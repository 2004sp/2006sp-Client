package client;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.CRC32;

/** Verifies that a local revision 443 cache can supply its reference tables. */
public final class CacheProbe {
    private static final Pattern SERVER_SEND_CONFIG_LITERAL = Pattern.compile(
            "\\bsendConfig\\s*\\(\\s*(\\d+)\\s*,");
    private static final Pattern SERVER_CONFIG_STATE_LITERAL = Pattern.compile(
            "\\bconfigStates\\s*\\[\\s*(\\d+)\\s*\\]");
    private static final Pattern SERVER_NAMED_CONFIG_LITERAL = Pattern.compile(
            "(?i)\\b[A-Za-z_$][A-Za-z0-9_$]*(?:config_?id|config_?index)\\s*=\\s*(\\d+)\\b");
    private static final Pattern SERVER_CONFIG_ARRAY = Pattern.compile(
            "(?i)\\b[A-Za-z_$][A-Za-z0-9_$]*(?:config_?ids|config_?indexes)\\s*=\\s*new\\s+int\\s*\\[\\s*\\]\\s*\\{([^}]*)\\}");
    private static final Pattern INTEGER_LITERAL = Pattern.compile("\\b(\\d+)\\b");

    private CacheProbe() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: client.CacheProbe <revision 443 cache directory> [server source root]");
            System.exit(2);
        }
        validateVarpPackets();
        validateObjectPackets();
        validateZonePackets();
        try (LocalCache source = new LocalCache(new File(args[0]))) {
            Cache cache = new Cache(source);
            int[] crcs = cache.getArchiveCrcs();
            for (int archive = 0; archive < crcs.length; archive++) {
                Cache.ReferenceTable table = cache.readReferenceTable(archive);
                System.out.println("Archive " + archive + ": "
                        + table.getGroupIds().length + " groups, CRC "
                        + Integer.toHexString(crcs[archive]));
            }
            FloorDefinition.loadRevision443(cache);
            if (FloorDefinition.underlayDefinitions.length != 150
                    || FloorDefinition.overlayDefinitions.length != 174
                    || FloorDefinition.overlayDefinitions[4].textureId != 3) {
                throw new IllegalStateException("Revision 443 floor configs failed to decode");
            }
            System.out.println("443 floors: 76 underlays + 114 overlays decoded");

            Varbits varbits = Varbits.load(cache);
            VarbitDefinition.loadRevision443(cache);
            if (VarbitDefinition.definitions.length != varbits.size()) {
                throw new IllegalStateException("443 runtime varbit table size mismatch");
            }
            if (varbits.size() == 0) {
                throw new IllegalStateException("Revision 443 varbit config group is empty");
            }
            int requiredVarps = varbits.getRequiredVarpCount();
            for (int varbitId = 0; varbitId < varbits.size(); varbitId++) {
                Varbits.Definition definition = varbits.get(varbitId);
                if (definition == null) continue;
                VarbitDefinition runtimeDefinition = VarbitDefinition.definitions[varbitId];
                if (runtimeDefinition == null || runtimeDefinition.index != definition.varpIndex
                        || runtimeDefinition.leastSignificantBit != definition.leastSignificantBit
                        || runtimeDefinition.mostSignificantBit != definition.mostSignificantBit) {
                    throw new IllegalStateException("443 runtime varbit mismatch: " + varbitId);
                }
                int width = definition.mostSignificantBit
                        - definition.leastSignificantBit + 1;
                int expectedMask = width >= 32 ? -1 : (1 << width) - 1;
                if (varbits.getValue(varbitId, -1) != expectedMask) {
                    throw new IllegalStateException("443 varbit extraction failed for "
                            + varbitId);
                }
            }
            System.out.println("443 varbits: " + varbits.size() + " definitions decoded; "
                    + requiredVarps + " base varps required");
            VarpDefinition.loadRevision443(cache);
            if (VarpDefinition.definitions.length < requiredVarps) {
                throw new IllegalStateException("443 varp table cannot cover varbit base indices");
            }
            System.out.println("443 varps: " + VarpDefinition.definitions.length
                    + " runtime definitions decoded");
            IdentityKit.loadRevision443(cache);
            if (IdentityKit.length == 0) {
                throw new IllegalStateException("443 identity kit table is empty");
            }
            System.out.println("443 identity kits: " + IdentityKit.length
                    + " runtime definitions decoded");
            Animations.loadRuntime(cache);
            if (AnimationSequence.sequences.length == 0) {
                throw new IllegalStateException("443 runtime sequence table is empty");
            }
            System.out.println("443 sequences: " + AnimationSequence.sequences.length
                    + " runtime definitions decoded");
            SpotAnimationDefinition.loadRevision443(cache);
            if (SpotAnimationDefinition.definitions.length == 0) {
                throw new IllegalStateException("443 spot animation table is empty");
            }
            System.out.println("443 spot animations: "
                    + SpotAnimationDefinition.definitions.length + " runtime definitions decoded");
            ItemDefinition.loadRevision443(cache);
            System.out.println("443 items: " + ItemDefinition.definitionCount
                    + " runtime definitions decoded");
            if (!"Dwarf remains".equals(ItemDefinition.lookup(0).name)) {
                throw new IllegalStateException("443 runtime item lookup failed");
            }
            if (ItemDefinition.lookup(0).getModel(1) == null) {
                throw new IllegalStateException("443 runtime item model failed");
            }
            NpcDefinition.loadRevision443(cache);
            System.out.println("443 NPCs: " + NpcDefinition.definitionCount
                    + " runtime definitions decoded");
            if (NpcDefinition.lookup(0).name == null) {
                throw new IllegalStateException("443 runtime NPC lookup failed");
            }
            if (NpcDefinition.lookup(0).getAnimatedModelInterpolated(
                    -1, -1, -1, 0, 0, null) == null) {
                throw new IllegalStateException("443 runtime NPC model failed");
            }

            ObjectDefinitions objectDefinitions =
                    ObjectDefinitions.load(cache);
            if (objectDefinitions.size() != 20099) {
                throw new IllegalStateException("Unexpected revision 443 object count: "
                        + objectDefinitions.size());
            }
            System.out.println("443 objects: " + objectDefinitions.size()
                    + " definitions, " + objectDefinitions.countModelReferences()
                    + " model references decoded");
            int morphedObjects = 0;
            int varbitMorphedObjects = 0;
            int configMorphedObjects = 0;
            int fallbackMorphs = 0;
            int maxDirectVarp = -1;
            Map<Integer, MorphVarpUsage> morphVarps = new TreeMap<Integer, MorphVarpUsage>();
            for (int objectId = 0; objectId < objectDefinitions.size(); objectId++) {
                ObjectDefinitions.Definition definition = objectDefinitions.get(objectId);
                if (definition == null || definition.childIds == null) continue;
                morphedObjects++;
                if (definition.hasMorphFallback) fallbackMorphs++;
                for (int childId : definition.childIds) {
                    if (childId != -1 && objectDefinitions.get(childId) == null) {
                        throw new IllegalStateException("443 object " + objectId
                                + " references missing morph child " + childId);
                    }
                }
                if (definition.varbitId != -1) {
                    Varbits.Definition varbit = varbits.get(definition.varbitId);
                    if (varbit == null) {
                        throw new IllegalStateException("443 object " + objectId
                                + " references missing varbit " + definition.varbitId);
                    }
                    MorphVarpUsage usage = getMorphVarpUsage(morphVarps, varbit.varpIndex);
                    usage.varbitMorphs++;
                    usage.varbitIds.add(Integer.valueOf(definition.varbitId));
                    usage.objectIds.add(Integer.valueOf(objectId));
                    usage.objectLabels.add(objectLabel(definition));
                    varbitMorphedObjects++;
                } else if (definition.configId != -1) {
                    MorphVarpUsage usage = getMorphVarpUsage(morphVarps, definition.configId);
                    usage.directMorphs++;
                    usage.objectIds.add(Integer.valueOf(objectId));
                    usage.objectLabels.add(objectLabel(definition));
                    maxDirectVarp = Math.max(maxDirectVarp, definition.configId);
                    configMorphedObjects++;
                }
            }
            requiredVarps = Math.max(requiredVarps, maxDirectVarp + 1);
            Varps.ensureCapacity(requiredVarps);
            Varps.reset();
            if (requiredVarps > 0) {
                Varps.set(requiredVarps - 1, 0x443);
                if (Varps.get(requiredVarps - 1) != 0x443) {
                    throw new IllegalStateException("443 varp store failed round-trip");
                }
                Varps.reset();
            }
            int zeroStateStaticMorphs = 0;
            int zeroStateAnimatedMorphs = 0;
            int zeroStateNullMorphs = 0;
            for (int objectId = 0; objectId < objectDefinitions.size(); objectId++) {
                ObjectDefinitions.Definition definition = objectDefinitions.get(objectId);
                if (definition == null || definition.childIds == null) continue;
                ObjectDefinitions.Definition resolved =
                        SceneObjects.resolveMorphedDefinition(
                                objectDefinitions, varbits, definition);
                if (resolved == null) {
                    zeroStateNullMorphs++;
                } else if (resolved.animationId != -1) {
                    zeroStateAnimatedMorphs++;
                } else {
                    zeroStateStaticMorphs++;
                }
            }
            System.out.println("443 object morphs: " + morphedObjects + " definitions ("
                    + varbitMorphedObjects + " varbit-backed, " + configMorphedObjects
                    + " direct-varp, " + fallbackMorphs + " with fallback); "
                    + requiredVarps + " varp slots allocated; zero state="
                    + zeroStateStaticMorphs + " static/" + zeroStateAnimatedMorphs
                    + " animated/" + zeroStateNullMorphs + " null");
            System.out.println("443 morph base varps: " + morphVarps.size()
                    + " unique indices " + morphVarps.keySet());
            if (args.length == 2) {
                auditServerConfigUsage(new File(args[1]).toPath(), morphVarps);
                return;
            }

            Models models = Models.load(cache);
            Set<Integer> referencedModelIds = new HashSet<Integer>();
            for (int objectId = 0; objectId < objectDefinitions.size(); objectId++) {
                ObjectDefinitions.Definition definition = objectDefinitions.get(objectId);
                if (definition == null || definition.modelIds == null) continue;
                for (int modelId : definition.modelIds) {
                    referencedModelIds.add(modelId);
                    if (!models.contains(modelId)) {
                        throw new IllegalStateException("443 object " + objectId
                                + " references missing archive-7 model " + modelId);
                    }
                }
            }
            System.out.println("443 models: archive 7 has " + models.getGroupCount()
                    + " groups; all " + referencedModelIds.size()
                    + " unique object model ids are present");

            int fontGroup = cache.readReferenceTable(8).getGroupId("p11_full");
            byte[] font = cache.readFile(8, fontGroup, 0);
            System.out.println("p11_full: " + font.length + " bytes");
            Rasterizer3D.loadRevision443Textures(cache);
            for (int textureId : cache.readReferenceTable(9).getFileIds(0)) {
                if (Rasterizer3D.textures[textureId] == null) {
                    throw new IllegalStateException("443 texture missing: " + textureId);
                }
            }
            System.out.println("443 textures: " + cache.readReferenceTable(9).getFileIds(0).length
                    + " loaded into rasterizer");
            SoundEffect.loadRevision443(cache);
            int soundsChecked = 0;
            for (int soundId : cache.readReferenceTable(4).getGroupIds()) {
                if (SoundEffect.get(soundId) == null) {
                    throw new IllegalStateException("443 sound missing: " + soundId);
                }
                if (++soundsChecked == 20) break;
            }
            System.out.println("443 sound effects: " + soundsChecked + " decoded on demand");
            for (String spriteGroup : new String[]{"p11_full", "compass", "mapback",
                    "mapscene", "mapfunction", "hitmarks", "sideicons", "titlebox", "logo"}) {
                Sprites.DecodedSprite[] sprites =
                        Sprites.load(cache, spriteGroup);
                if (sprites.length == 0 || sprites[0].toSprite().pixels.length
                        != sprites[0].width * sprites[0].height) {
                    throw new IllegalStateException("443 sprite group failed: " + spriteGroup);
                }
                sprites[0].toIndexedSprite();
                System.out.println("443 sprites " + spriteGroup + ": " + sprites.length);
            }
            for (String fontName : new String[]{"p11_full", "p12_full", "b12_full", "q8_full"}) {
                BitmapFont bitmapFont = new BitmapFont(cache, fontName);
                RichTextFont richTextFont = new RichTextFont(cache, fontName);
                if (bitmapFont.lineHeight <= 0 || richTextFont.lineHeight <= 0) {
                    throw new IllegalStateException("443 font has no line height: " + fontName);
                }
                System.out.println("443 font " + fontName + ": " + bitmapFont.lineHeight);
            }
            int spriteGroups = 0;
            Cache.ReferenceTable spriteIndex = cache.readReferenceTable(8);
            for (int group : spriteIndex.getGroupIds()) {
                int[] fileIds = spriteIndex.getFileIds(group);
                if (fileIds == null || fileIds.length != 1) {
                    throw new IllegalStateException("Unexpected 443 sprite group " + group);
                }
                Sprites.decode(cache.readFile(8, group, fileIds[0]));
                spriteGroups++;
            }
            System.out.println("443 sprite archive: " + spriteGroups + " groups decoded");

            // The restored revision 443 cache validates this location group and XTEA key.
            int[] key = {1152832719, 1703327384, -664371384, 1662584632};
            byte[] locations = cache.readFile(5, 609, 0, key);
            CRC32 locationCrc = new CRC32();
            locationCrc.update(locations);
            if (locations.length != 8466 || locationCrc.getValue() != 0x7B1A3C82L) {
                throw new IllegalStateException("Encrypted 443 location group failed to decode");
            }
            LocationMap.Location[] decodedLocations =
                    LocationMap.decode(locations);
            validateLocations(decodedLocations, objectDefinitions);
            System.out.println("l48_48: " + locations.length + " decoded bytes, "
                    + decodedLocations.length + " object placements");

            int[][] spawnKeys = {
                    {339960494, -1350930341, -272469140, 318766784},
                    {-1938428459, -1838574783, 131585942, -1297008627},
                    {-387260899, -2132243418, 654609855, -1092457792},
                    key
            };
            byte[] regionPayload = new byte[73];
            regionPayload[0] = (byte) 128;
            regionPayload[1] = 1;
            regionPayload[2] = 51;
            regionPayload[4] = (byte) 178;
            regionPayload[6] = (byte) 128;
            for (int keyIndex = 0; keyIndex < spawnKeys.length; keyIndex++) {
                for (int word = 0; word < 4; word++) {
                    int value = spawnKeys[keyIndex][word];
                    int offset = 7 + keyIndex * 16 + word * 4;
                    regionPayload[offset] = (byte) (value >>> 24);
                    regionPayload[offset + 1] = (byte) (value >>> 16);
                    regionPayload[offset + 2] = (byte) (value >>> 8);
                    regionPayload[offset + 3] = (byte) value;
                }
            }
            regionPayload[71] = 1;
            RegionAssets assets = RegionAssets.load(cache,
                    RegionPacket.decode(regionPayload));
            int terrainGroups = 0;
            int locationGroups = 0;
            int locationCount = 0;
            Set<Integer> spawnModelIds = new HashSet<Integer>();
            for (RegionAssets.MapSquare square : assets.mapSquares) {
                if (square.terrain != null && square.terrain.length > 0) terrainGroups++;
                if (square.locations != null && square.locations.length > 0) {
                    locationGroups++;
                    LocationMap.Location[] squareLocations =
                            LocationMap.decode(square.locations);
                    validateLocations(squareLocations, objectDefinitions);
                    collectModelIds(squareLocations, objectDefinitions, spawnModelIds);
                    locationCount += squareLocations.length;
                }
            }
            if (terrainGroups != 4 || locationGroups != 4 || locationCount == 0) {
                throw new IllegalStateException("Spawn-region 443 map groups did not load");
            }
            System.out.println("443 spawn region: 4 terrain + 4 location groups, "
                    + locationCount + " object placements decoded");

            Animations animations = new Animations(cache);
            Set<Integer> spawnSequenceIds = new HashSet<Integer>();
            for (RegionAssets.MapSquare square : assets.mapSquares) {
                if (square.locations == null || square.locations.length == 0) continue;
                LocationMap.Location[] squareLocations =
                        LocationMap.decode(square.locations);
                for (LocationMap.Location location : squareLocations) {
                    collectSequenceIds(objectDefinitions,
                            objectDefinitions.get(location.objectId), spawnSequenceIds,
                            new HashSet<Integer>());
                }
            }
            int spawnFrames = 0;
            for (Integer sequenceId : spawnSequenceIds) {
                AnimationSequence sequence = animations.getSequence(sequenceId.intValue());
                spawnFrames += sequence.frameCount;
            }
            System.out.println("443 spawn animations: " + spawnSequenceIds.size()
                    + " sequences, " + spawnFrames + " sequence frames decoded");

            models.initializeModelNamespace();
            int lazyModelId = -1;
            for (int id = 0; id < models.getModelCapacity(); id++) {
                if (models.contains(id) && !spawnModelIds.contains(Integer.valueOf(id))) {
                    lazyModelId = id;
                    break;
                }
            }
            if (lazyModelId < 0 || !Model.isCached(models.getRegisteredModelId(lazyModelId))
                    || Model.getModel(models.getRegisteredModelId(lazyModelId)) == null) {
                throw new IllegalStateException("443 lazy model registration failed");
            }
            System.out.println("443 lazy model: " + lazyModelId + " registered on demand");
            models.initializeModelCache();
            long vertexCount = 0;
            long triangleCount = 0;
            for (Integer modelId : spawnModelIds) {
                Model model = models.registerAndDecode(modelId);
                vertexCount += model.vertexCount;
                triangleCount += model.triangleCount;
            }
            System.out.println("443 spawn models: " + spawnModelIds.size()
                    + " meshes decoded, " + vertexCount + " vertices, "
                    + triangleCount + " triangles");
            validateTerrainFloors(assets);
        }
    }
    private static MorphVarpUsage getMorphVarpUsage(
            Map<Integer, MorphVarpUsage> usages, int varpId) {
        Integer key = Integer.valueOf(varpId);
        MorphVarpUsage usage = usages.get(key);
        if (usage == null) {
            usage = new MorphVarpUsage(varpId);
            usages.put(key, usage);
        }
        return usage;
    }

    private static void auditServerConfigUsage(Path serverSourceRoot,
                                               Map<Integer, MorphVarpUsage> morphVarps)
            throws IOException {
        if (!Files.isDirectory(serverSourceRoot)) {
            throw new IOException("Server source root is not a directory: " + serverSourceRoot);
        }

        Map<Integer, Set<String>> serverUsage = scanServerConfigUsage(serverSourceRoot);
        Set<Integer> verified = readVerifiedMorphVarps(serverSourceRoot);
        TreeSet<Integer> overlap = new TreeSet<Integer>(morphVarps.keySet());
        overlap.retainAll(serverUsage.keySet());
        TreeSet<Integer> missing = new TreeSet<Integer>(morphVarps.keySet());
        missing.removeAll(serverUsage.keySet());

        System.out.println("443/server morph-varp audit: " + overlap.size() + "/"
                + morphVarps.size() + " morph base varps have numeric server config evidence; "
                + serverUsage.size() + " numeric server config IDs discovered");
        for (Integer varpId : morphVarps.keySet()) {
            MorphVarpUsage usage = morphVarps.get(varpId);
            boolean hasServerReference = serverUsage.containsKey(varpId);
            String classification = verified.contains(varpId) ? "verified"
                    : hasServerReference ? "numeric-candidate" : "no-known-server-writer";
            String morphKind = usage.varbitMorphs > 0 ? "varbit-backed" : "object-only";
            System.out.println("  varp " + varpId + " [" + classification + ", "
                    + morphKind + "]: " + usage.describe());
            if (!hasServerReference) continue;
            int shown = 0;
            for (String evidence : serverUsage.get(varpId)) {
                if (shown == 6) {
                    System.out.println("    ... more server references omitted");
                    break;
                }
                System.out.println("    " + evidence);
                shown++;
            }
        }
        System.out.println("  morph varps with no numeric server evidence: " + missing);
        System.out.println("  NOTE: matching numeric IDs are candidates only; semantic compatibility "
                + "must be verified from the feature/object behavior before initial-state sync.");
        System.out.println("  Scanner evidence covers literal sendConfig IDs, literal configStates indexes, "
                + "numeric config-id declarations, and config-id arrays; dynamically computed IDs may be absent.");
        System.out.println("  no-known-server-writer means no matching numeric writer was found; "
                + "different-ID equivalence requires manual gameplay evidence.");
    }

    private static Set<Integer> readVerifiedMorphVarps(Path serverSourceRoot) throws IOException {
        Path source = serverSourceRoot.resolve("com/rs2/net/packet/InitialVarps.java");
        Set<Integer> verified = new TreeSet<Integer>();
        if (!Files.isRegularFile(source)) return verified;
        String contents = new String(Files.readAllBytes(source), StandardCharsets.UTF_8);
        Matcher array = Pattern.compile("VERIFIED_VARPS\\s*=\\s*\\{([^}]*)\\}", Pattern.DOTALL)
                .matcher(contents);
        if (!array.find()) return verified;
        String literals = array.group(1).replaceAll("(?m)//[^\\r\\n]*", "");
        Matcher numbers = INTEGER_LITERAL.matcher(literals);
        while (numbers.find()) verified.add(Integer.valueOf(numbers.group(1)));
        return verified;
    }

    private static Map<Integer, Set<String>> scanServerConfigUsage(Path root) throws IOException {
        Map<Integer, Set<String>> usage = new TreeMap<Integer, Set<String>>();
        try (Stream<Path> paths = Files.walk(root)) {
            Iterator<Path> iterator = paths.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                if (!Files.isRegularFile(path) || !path.toString().endsWith(".java")) continue;
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);
                    String location = root.relativize(path).toString() + ":" + (lineIndex + 1);
                    recordLiteralMatches(usage, SERVER_SEND_CONFIG_LITERAL, line,
                            location, "sendConfig");
                    recordLiteralMatches(usage, SERVER_CONFIG_STATE_LITERAL, line,
                            location, "configStates");
                    recordLiteralMatches(usage, SERVER_NAMED_CONFIG_LITERAL, line,
                            location, "named-config");

                    Matcher arrayMatcher = SERVER_CONFIG_ARRAY.matcher(line);
                    while (arrayMatcher.find()) {
                        Matcher numberMatcher = INTEGER_LITERAL.matcher(arrayMatcher.group(1));
                        while (numberMatcher.find()) {
                            recordServerUsage(usage, Integer.parseInt(numberMatcher.group(1)),
                                    location + " [config-array] " + abbreviate(line.trim()));
                        }
                    }
                }
            }
        }
        return usage;
    }

    private static void recordLiteralMatches(Map<Integer, Set<String>> usage,
                                             Pattern pattern,
                                             String line,
                                             String location,
                                             String kind) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            recordServerUsage(usage, Integer.parseInt(matcher.group(1)),
                    location + " [" + kind + "] " + abbreviate(line.trim()));
        }
    }

    private static void recordServerUsage(Map<Integer, Set<String>> usage,
                                          int configId,
                                          String evidence) {
        Integer key = Integer.valueOf(configId);
        Set<String> references = usage.get(key);
        if (references == null) {
            references = new TreeSet<String>();
            usage.put(key, references);
        }
        references.add(evidence);
    }

    private static String abbreviate(String value) {
        return value.length() <= 180 ? value : value.substring(0, 177) + "...";
    }

    private static String objectLabel(ObjectDefinitions.Definition definition) {
        return definition.id + ":" + (definition.name == null ? "<unnamed>" : definition.name);
    }

    private static String summarizeStrings(Set<String> values) {
        if (values.size() <= 12) return values.toString();
        List<String> list = new ArrayList<String>(values);
        return list.subList(0, 12).toString().replace("]", ", ...]");
    }

    private static String summarizeIds(Set<Integer> ids) {
        if (ids.size() <= 12) return ids.toString();
        List<Integer> values = new ArrayList<Integer>(ids);
        return values.subList(0, 12).toString().replace("]", ", ...]");
    }

    private static final class MorphVarpUsage {
        final int varpId;
        int varbitMorphs;
        int directMorphs;
        final Set<Integer> varbitIds = new TreeSet<Integer>();
        final Set<Integer> objectIds = new TreeSet<Integer>();
        final Set<String> objectLabels = new TreeSet<String>();

        MorphVarpUsage(int varpId) {
            this.varpId = varpId;
        }

        String describe() {
            return (varbitMorphs + directMorphs) + " object morphs ("
                    + varbitMorphs + " varbit-backed, " + directMorphs + " direct); varbits="
                    + summarizeIds(varbitIds) + "; objects=" + summarizeStrings(objectLabels);
        }
    }


    private static void validateVarpPackets() throws Exception {
        VarpPacket.Update small = VarpPacket.decode(
                VarpPacket.SMALL_OPCODE,
                new byte[]{5, 1, (byte) 196});
        if (small.id != 452 || small.value != -5) {
            throw new IllegalStateException("443 small varp packet decode failed");
        }

        VarpPacket.Update large = VarpPacket.decode(
                VarpPacket.LARGE_OPCODE,
                new byte[]{0x34, 0x12, 0x78, 0x56, 1, (byte) 196});
        if (large.id != 452 || large.value != 0x12345678) {
            throw new IllegalStateException("443 large varp packet decode failed");
        }

        System.out.println("443 varp packets: opcodes 62/74 decoded");
    }

    private static void validateObjectPackets() throws Exception {
        ObjectPacket.reset();
        byte[] zonePayload = new byte[]{(byte) (128 - 48), (byte) (128 - 49)};
        ObjectPacket.Update zone = ObjectPacket.decode(
                ObjectPacket.ZONE_BASE_OPCODE, zonePayload);
        if (!zone.zoneBase || zone.x != 48 || zone.y != 49) {
            throw new IllegalStateException("443 object zone-base packet decode failed");
        }
        ObjectPacket.apply(ObjectPacket.ZONE_BASE_OPCODE,
                zonePayload, 0);

        int packedType = 10 << 2 | 2;
        ObjectPacket.Update add = ObjectPacket.decode(
                ObjectPacket.ADD_OPCODE,
                new byte[]{0, 0x08, (byte) 0xA5, (byte) (packedType + 128)});
        if (add.zoneBase || add.x != 48 || add.y != 49 || add.type != 10
                || add.orientation != 2 || add.objectId != 2213) {
            throw new IllegalStateException("443 add-loc packet decode failed");
        }

        ObjectPacket.Update remove = ObjectPacket.decode(
                ObjectPacket.REMOVE_OPCODE,
                new byte[]{(byte) packedType, (byte) 128});
        if (remove.zoneBase || remove.x != 48 || remove.y != 49 || remove.type != 10
                || remove.orientation != 2 || remove.objectId != -1) {
            throw new IllegalStateException("443 remove-loc packet decode failed");
        }

        ObjectPacket.Update animate = ObjectPacket.decode(
                ObjectPacket.ANIMATE_OPCODE,
                new byte[]{(byte) 255, 0, (byte) (packedType + 128), (byte) 128});
        if (animate.zoneBase || animate.x != 48 || animate.y != 49 || animate.type != 10
                || animate.orientation != 2 || animate.sequenceId != 127) {
            throw new IllegalStateException("443 animate-loc packet decode failed");
        }
        ObjectPacket.reset();
        System.out.println("443 object packets: opcodes 82/69/122/170 decoded");
    }

    private static void validateZonePackets() throws Exception {
        ObjectPacket.reset();
        byte[] zonePayload = new byte[]{(byte) (128 - 48), (byte) (128 - 49)};
        ObjectPacket.apply(ObjectPacket.ZONE_BASE_OPCODE,
                zonePayload, 0);

        ZonePacket.Update add = ZonePacket.decode(
                ZonePacket.GROUND_ADD_OPCODE,
                new byte[]{1, (byte) 172, (byte) 0xE3, 0x03, 0x23});
        if (add.x != 50 || add.y != 52 || add.id != 995 || add.amount != 300) {
            throw new IllegalStateException("443 ground-item add packet decode failed");
        }

        ZonePacket.Update remove = ZonePacket.decode(
                ZonePacket.GROUND_REMOVE_OPCODE,
                new byte[]{(byte) 0xE3, 0x03, 0x23});
        if (remove.x != 50 || remove.y != 52 || remove.id != 995) {
            throw new IllegalStateException("443 ground-item remove packet decode failed");
        }

        ZonePacket.Update amount = ZonePacket.decode(
                ZonePacket.GROUND_AMOUNT_OPCODE,
                new byte[]{0x23, 0x03, (byte) 0xE3, 0x01, 0x2C, 0x01, (byte) 0xC2});
        if (amount.x != 50 || amount.y != 52 || amount.id != 995
                || amount.oldAmount != 300 || amount.amount != 450) {
            throw new IllegalStateException("443 ground-item amount packet decode failed");
        }

        ZonePacket.Update addExcept = ZonePacket.decode(
                ZonePacket.GROUND_ADD_EXCEPT_OPCODE,
                new byte[]{0, (byte) 133, 1, (byte) 172, (byte) -35, 3, 99});
        if (addExcept.x != 50 || addExcept.y != 52 || addExcept.id != 995
                || addExcept.amount != 300 || addExcept.sourcePlayerIndex != 5) {
            throw new IllegalStateException("443 ground-item exclusion packet decode failed");
        }

        ZonePacket.Update projectile = ZonePacket.decode(
                ZonePacket.PROJECTILE_OPCODE,
                new byte[]{0, 1, (byte) -2, (byte) 0xFF, (byte) 0xFF,
                        0, 100, 10, 20, 0, 5, 0, 15, 45, 64});
        if (projectile.x != 48 || projectile.y != 49 || projectile.endX != 49
                || projectile.endY != 47 || projectile.id != 100
                || projectile.targetIndex != -1 || projectile.startHeight != 40
                || projectile.endHeight != 80 || projectile.startDelay != 5
                || projectile.endDelay != 15 || projectile.slope != 45
                || projectile.startDistance != 64) {
            throw new IllegalStateException("443 projectile packet decode failed");
        }

        ZonePacket.Update sound = ZonePacket.decode(
                ZonePacket.AREA_SOUND_OPCODE,
                new byte[]{0, 1, 62, 0x21, 7});
        if (sound.x != 48 || sound.y != 49 || sound.id != 318
                || sound.radius != 2 || sound.loops != 1 || sound.delay != 7) {
            throw new IllegalStateException("443 area-sound packet decode failed");
        }

        ZonePacket.Update graphic = ZonePacket.decode(
                ZonePacket.SPOT_ANIMATION_OPCODE,
                new byte[]{0, 0, 100, 5, 0, 12});
        if (graphic.x != 48 || graphic.y != 49 || graphic.id != 100
                || graphic.startHeight != 5 || graphic.delay != 12) {
            throw new IllegalStateException("443 spot-animation packet decode failed");
        }

        ZonePacket.Update attached = ZonePacket.decode(
                ZonePacket.PLAYER_OBJECT_OPCODE,
                new byte[]{(byte) -3, (byte) 129, 0x08, 0x25, 126, 126,
                        0, (byte) 131, (byte) -42, (byte) 128, 0, 8,
                        (byte) 133, 0});
        if (attached.x != 48 || attached.y != 49 || attached.id != 2213
                || attached.type != 10 || attached.orientation != 2
                || attached.sourcePlayerIndex != 5 || attached.startDelay != 3
                || attached.endDelay != 8 || attached.minXOffset != -1
                || attached.minYOffset != -2 || attached.maxXOffset != 2
                || attached.maxYOffset != 3) {
            throw new IllegalStateException("443 player-object packet decode failed");
        }

        ObjectPacket.reset();
        System.out.println("443 zone packets: opcodes 79/84/94/101/109/115/129/207 decoded");
    }

    private static void validateLocations(LocationMap.Location[] locations,
                                          ObjectDefinitions definitions) {
        for (LocationMap.Location location : locations) {
            if (definitions.get(location.objectId) == null) {
                throw new IllegalStateException("443 map references missing object "
                        + location.objectId);
            }
        }
    }

    private static void collectSequenceIds(
            ObjectDefinitions definitions,
            ObjectDefinitions.Definition definition,
            Set<Integer> sequenceIds,
            Set<Integer> visited) {
        if (definition == null || !visited.add(Integer.valueOf(definition.id))) return;
        if (definition.animationId != -1) {
            sequenceIds.add(Integer.valueOf(definition.animationId));
        }
        if (definition.childIds == null) return;
        for (int childId : definition.childIds) {
            if (childId != -1) {
                collectSequenceIds(definitions, definitions.get(childId),
                        sequenceIds, visited);
            }
        }
    }

    private static void collectModelIds(LocationMap.Location[] locations,
                                        ObjectDefinitions definitions,
                                        Set<Integer> modelIds) {
        for (LocationMap.Location location : locations) {
            ObjectDefinitions.Definition definition =
                    definitions.get(location.objectId);
            if (definition == null || definition.modelIds == null) continue;
            for (int modelId : definition.modelIds) modelIds.add(modelId);
        }
    }

    private static void validateTerrainFloors(RegionAssets assets) {
        int maxTexture = -1;
        for (RegionAssets.MapSquare square : assets.mapSquares) {
            byte[] terrain = square.terrain;
            if (terrain == null) continue;
            int offset = 0;
            for (int tile = 0; tile < 4 * 64 * 64; tile++) {
                while (true) {
                    if (offset >= terrain.length) {
                        throw new IllegalStateException("Truncated 443 terrain map");
                    }
                    int opcode = terrain[offset++] & 255;
                    if (opcode == 0) break;
                    if (opcode == 1) {
                        offset++;
                        break;
                    }
                    if (opcode <= 49) {
                        int overlay = terrain[offset++] & 255;
                        if (overlay > 0) {
                            FloorDefinition definition = FloorDefinition.overlayDefinitions[overlay - 1];
                            if (definition == null) throw new IllegalStateException("Missing 443 overlay " + (overlay - 1));
                            maxTexture = Math.max(maxTexture, definition.textureId);
                        }
                    } else if (opcode > 81) {
                        int underlay = opcode - 81;
                        if (FloorDefinition.underlayDefinitions[underlay - 1] == null) {
                            throw new IllegalStateException("Missing 443 underlay " + (underlay - 1));
                        }
                    }
                }
            }
            if (offset != terrain.length) {
                throw new IllegalStateException("Trailing 443 terrain bytes");
            }
        }
        System.out.println("443 spawn terrain floors valid; max texture=" + maxTexture);
    }
}
