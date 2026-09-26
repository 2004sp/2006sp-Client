package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Builds static and varp-morphed revision-443 locs without consulting 377 configs. */
public final class SceneObjects {
    private static final int[] WALL_FLAGS = {1, 2, 4, 8};
    private static SceneObjects pendingObjects;
    private static RegionAssets pendingAssets;
    private static int pendingBaseX;
    private static int pendingBaseY;
    private static byte[][][] pendingTileSettings;
    private static int[][][] pendingTemplates;
    private static final CollisionMap[] pendingCollisionMaps = new CollisionMap[4];
    private static int pendingCollisionMapCount;
    private static final int[] DIAGONAL_WALL_FLAGS = {16, 32, 64, 128};
    private static final int[] WALL_DECORATION_X_OFFSETS = {1, 0, -1, 0};
    private static final int[] WALL_DECORATION_Y_OFFSETS = {0, -1, 0, 1};

    private final ObjectDefinitions definitions;
    private final Models models;
    private final Varbits varbits;
    private final Animations animations;
    private final Map<Long, Model> rawModels = new HashMap<Long, Model>();
    private final Map<Long, Model> builtModels = new HashMap<Long, Model>();
    private final Map<Long, Model> animatedBaseModels = new HashMap<Long, Model>();

    private static SceneObjects activeObjects;
    static synchronized boolean hasActiveDefinitions() {
        return activeObjects != null && "443".equals(System.getProperty("prs.clientRevision"));
    }

    static synchronized ObjectDefinitions.Definition definition(int id) {
        return hasActiveDefinitions() ? activeObjects.definitions.get(id) : null;
    }

    static synchronized ObjectDefinitions.Definition resolvedDefinition(int id) {
        ObjectDefinitions.Definition definition = definition(id);
        return definition == null ? null : activeObjects.resolveMorphedDefinition(definition);
    }
    private static SceneGraph activeScene;
    private static int[][][] activeTileHeights;
    private static byte[][][] activeTileSettings;
    private static CollisionMap[] activeCollisionMaps;
    private static final List<RuntimeTransition> runtimeTransitions =
            new ArrayList<RuntimeTransition>();

    private SceneObjects(ObjectDefinitions definitions,
                                    Models models,
                                    Varbits varbits,
                                    Animations animations) {
        this.definitions = definitions;
        this.models = models;
        this.varbits = varbits;
        this.animations = animations;
    }

    static synchronized void queueForRegion(SceneObjects objects,
                                            RegionAssets assets,
                                            RegionPacket region)
            throws IOException {
        pendingObjects = objects;
        pendingAssets = assets;
        activeObjects = null;
        activeScene = null;
        activeTileHeights = null;
        activeTileSettings = null;
        activeCollisionMaps = null;
        runtimeTransitions.clear();
        ObjectPacket.reset();
        pendingBaseX = (region.centerX - 6) << 3;
        pendingBaseY = (region.centerY - 6) << 3;
        pendingTemplates = region.templates;
        pendingTileSettings = region.templates == null
                ? decodeTileSettings(assets, pendingBaseX, pendingBaseY) : null;
        for (int i = 0; i < pendingCollisionMaps.length; i++) {
            pendingCollisionMaps[i] = null;
        }
        pendingCollisionMapCount = 0;
    }

    static synchronized void noteCollisionMapReset(CollisionMap collisionMap) {
        if (pendingObjects == null || collisionMap == null
                || pendingCollisionMapCount >= pendingCollisionMaps.length) {
            return;
        }
        pendingCollisionMaps[pendingCollisionMapCount++] = collisionMap;
    }

    static synchronized void setPendingTileSettings(byte[][][] settings) {
        if (pendingTemplates != null) pendingTileSettings = settings;
    }

    static synchronized void clearPending() {
        pendingObjects = null;
        pendingAssets = null;
        pendingTileSettings = null;
        pendingTemplates = null;
        pendingBaseX = 0;
        pendingBaseY = 0;
        for (int i = 0; i < pendingCollisionMaps.length; i++) {
            pendingCollisionMaps[i] = null;
        }
        pendingCollisionMapCount = 0;
    }

    static synchronized PlacementStats placePending(byte[][][] tileSettings,
                                                    int[][][] tileHeights,
                                                    CollisionMap[] collisionMaps,
                                                    SceneGraph scene)
            throws IOException {
        if (pendingObjects == null || pendingAssets == null) return null;
        if (!"443".equals(System.getProperty("prs.clientRevision"))) {
            clearPending();
            return null;
        }
        SceneObjects objects = pendingObjects;
        RegionAssets assets = pendingAssets;
        int[][][] templates = pendingTemplates;
        int baseX = pendingBaseX;
        int baseY = pendingBaseY;
        byte[][][] effectiveSettings = tileSettings != null
                ? tileSettings : pendingTileSettings;
        CollisionMap[] effectiveCollisionMaps = collisionMaps;
        if (effectiveCollisionMaps == null
                && pendingCollisionMapCount == pendingCollisionMaps.length) {
            effectiveCollisionMaps = pendingCollisionMaps.clone();
        }
        clearPending();
        return templates == null
                ? objects.placeRegion(assets, baseX, baseY, effectiveSettings,
                        tileHeights, effectiveCollisionMaps, scene)
                : objects.placeConstructedRegion(assets, templates, effectiveSettings,
                        tileHeights, effectiveCollisionMaps, scene);
    }

    static synchronized PlacementStats placePending(int[][][] tileHeights,
                                                    SceneGraph scene)
            throws IOException {
        return placePending(null, tileHeights, null, scene);
    }

    /**
     * Loads every static mesh that the supplied region can select, including
     * all non-animated children of morphed loc definitions. Morph selection is
     * resolved at render time from the revision-specific varp store.
     */
    public static SceneObjects prepare(Cache cache,
                                                   ObjectDefinitions definitions,
                                                   RegionAssets assets)
            throws IOException {
        Models models = Models.load(cache);
        models.initializeModelNamespace();
        Varbits varbits = Varbits.load(cache);
        Animations animations = new Animations(cache);
        int requiredVarps = varbits.getRequiredVarpCount();
        for (int objectId = 0; objectId < definitions.size(); objectId++) {
            ObjectDefinitions.Definition definition = definitions.get(objectId);
            if (definition != null && definition.configId >= requiredVarps) {
                requiredVarps = definition.configId + 1;
            }
        }
        Varps.ensureCapacity(requiredVarps);

        Set<Integer> requiredModels = new HashSet<Integer>();
        Set<Integer> requiredSequences = new HashSet<Integer>();
        for (RegionAssets.MapSquare square : assets.mapSquares) {
            if (square.locations == null) continue;
            LocationMap.Location[] locations =
                    LocationMap.decode(square.locations);
            for (LocationMap.Location location : locations) {
                ObjectDefinitions.Definition definition =
                        definitions.get(location.objectId);
                if (definition == null) {
                    throw new IOException("443 region references missing object "
                            + location.objectId);
                }
                collectStaticModels(definitions, definition, requiredModels,
                        requiredSequences, new HashSet<Integer>());
            }
        }
        for (Integer modelId : requiredModels) models.register(modelId.intValue());
        for (Integer sequenceId : requiredSequences) {
            animations.getSequence(sequenceId.intValue());
        }
        return new SceneObjects(definitions, models, varbits, animations);
    }

    private static void collectStaticModels(ObjectDefinitions definitions,
                                            ObjectDefinitions.Definition definition,
                                            Set<Integer> modelIds,
                                            Set<Integer> sequenceIds,
                                            Set<Integer> visited) {
        if (definition == null || !visited.add(definition.id)) {
            return;
        }
        if (definition.animationId != -1) {
            sequenceIds.add(Integer.valueOf(definition.animationId));
        }
        if (definition.childIds != null) {
            for (int childId : definition.childIds) {
                if (childId != -1) {
                    collectStaticModels(definitions, definitions.get(childId), modelIds,
                            sequenceIds, visited);
                }
            }
            return;
        }
        if (definition.modelIds != null) {
            for (int modelId : definition.modelIds) modelIds.add(modelId);
        }
    }

    private static byte[][][] decodeTileSettings(RegionAssets assets,
                                                  int baseX, int baseY)
            throws IOException {
        byte[][][] settings = new byte[4][104][104];
        for (RegionAssets.MapSquare square : assets.mapSquares) {
            if (square.terrain == null) continue;
            byte[] terrain = square.terrain;
            int offset = 0;
            int regionX = (square.x << 6) - baseX;
            int regionY = (square.y << 6) - baseY;
            for (int plane = 0; plane < 4; plane++) {
                for (int localX = 0; localX < 64; localX++) {
                    for (int localY = 0; localY < 64; localY++) {
                        byte setting = 0;
                        while (true) {
                            if (offset >= terrain.length) {
                                throw new IOException("Truncated 443 terrain while reading settings");
                            }
                            int opcode = terrain[offset++] & 255;
                            if (opcode == 0) break;
                            if (opcode == 1) {
                                if (offset >= terrain.length) {
                                    throw new IOException("Truncated 443 terrain height");
                                }
                                offset++;
                                break;
                            }
                            if (opcode <= 49) {
                                if (offset >= terrain.length) {
                                    throw new IOException("Truncated 443 terrain overlay");
                                }
                                offset++;
                            } else if (opcode <= 81) {
                                setting = (byte) (opcode - 49);
                            }
                        }
                        int x = regionX + localX;
                        int y = regionY + localY;
                        if (x >= 0 && x < 104 && y >= 0 && y < 104) {
                            settings[plane][x][y] = setting;
                        }
                    }
                }
            }
            if (offset != terrain.length) {
                throw new IOException("Trailing bytes in 443 terrain settings: "
                        + (terrain.length - offset));
            }
        }
        return settings;
    }

    public PlacementStats placeRegion(RegionAssets assets, int baseX, int baseY,
                                      byte[][][] tileSettings, int[][][] tileHeights,
                                      CollisionMap[] collisionMaps, SceneGraph scene)
            throws IOException {
        PlacementStats stats = new PlacementStats();
        for (RegionAssets.MapSquare square : assets.mapSquares) {
            if (square.locations == null) continue;
            int regionX = (square.x << 6) - baseX;
            int regionY = (square.y << 6) - baseY;
            LocationMap.Location[] locations =
                    LocationMap.decode(square.locations);
            for (LocationMap.Location location : locations) {
                int x = regionX + location.localX;
                int y = regionY + location.localY;
                if (x <= 0 || y <= 0 || x >= 103 || y >= 103) {
                    stats.outOfBounds++;
                    continue;
                }
                ObjectDefinitions.Definition definition =
                        definitions.get(location.objectId);
                if (definition == null) {
                    throw new IOException("443 region references missing object "
                            + location.objectId);
                }
                int collisionPlane = location.plane;
                if (tileSettings != null && (tileSettings[1][x][y] & 2) == 2) {
                    collisionPlane--;
                }
                CollisionMap collisionMap = collisionMaps != null && collisionPlane >= 0
                        ? collisionMaps[collisionPlane] : null;
                if (placeObject(scene, collisionMap, tileHeights, location.plane,
                        location.plane, x, y, definition, location.type,
                        location.orientation)) {
                    stats.placed++;
                    if (definition.childIds != null) stats.morphedPlaced++;
                } else {
                    stats.noModel++;
                }
            }
        }
        activeObjects = this;
        activeScene = scene;
        activeTileHeights = tileHeights;
        activeTileSettings = tileSettings;
        activeCollisionMaps = collisionMaps;
        return stats;
    }

    private PlacementStats placeConstructedRegion(RegionAssets assets,
            int[][][] templates, byte[][][] tileSettings, int[][][] tileHeights,
            CollisionMap[] collisionMaps, SceneGraph scene) throws IOException {
        PlacementStats stats = new PlacementStats();
        for (int plane = 0; plane < 4; plane++) {
            for (int chunkX = 0; chunkX < 13; chunkX++) {
                for (int chunkY = 0; chunkY < 13; chunkY++) {
                    int template = templates[plane][chunkX][chunkY];
                    if (template == -1) continue;
                    int sourcePlane = template >> 24 & 3;
                    int rotation = template >> 1 & 3;
                    int sourceChunkX = template >> 14 & 1023;
                    int sourceChunkY = template >> 3 & 2047;
                    int squareX = sourceChunkX / 8;
                    int squareY = sourceChunkY / 8;
                    for (RegionAssets.MapSquare square : assets.mapSquares) {
                        if (square.x != squareX || square.y != squareY
                                || square.locations == null) continue;
                        LocationMap.Location[] locations =
                                LocationMap.decode(square.locations);
                        for (LocationMap.Location location : locations) {
                            if (location.plane != sourcePlane
                                    || location.localX >> 3 != (sourceChunkX & 7)
                                    || location.localY >> 3 != (sourceChunkY & 7)) continue;
                            ObjectDefinitions.Definition definition =
                                    definitions.get(location.objectId);
                            if (definition == null) {
                                throw new IOException("443 region references missing object "
                                        + location.objectId);
                            }
                            int localX = location.localX & 7;
                            int localY = location.localY & 7;
                            int x;
                            int y;
                            if (rotation == 0) {
                                x = localX;
                                y = localY;
                            } else if (rotation == 1) {
                                x = localY;
                                y = 7 - localX - (definition.sizeX - 1);
                            } else if (rotation == 2) {
                                x = 7 - localX - (definition.sizeX - 1);
                                y = 7 - localY - (definition.sizeY - 1);
                            } else {
                                x = 7 - localY - (definition.sizeY - 1);
                                y = localX;
                            }
                            x += chunkX << 3;
                            y += chunkY << 3;
                            if (x <= 0 || y <= 0 || x >= 103 || y >= 103) {
                                stats.outOfBounds++;
                                continue;
                            }
                            int collisionPlane = plane;
                            if (tileSettings != null && (tileSettings[1][x][y] & 2) == 2) {
                                collisionPlane--;
                            }
                            CollisionMap collisionMap = collisionMaps != null
                                    && collisionPlane >= 0 ? collisionMaps[collisionPlane] : null;
                            if (placeObject(scene, collisionMap, tileHeights, plane, plane,
                                    x, y, definition, location.type,
                                    location.orientation + rotation & 3)) {
                                stats.placed++;
                                if (definition.childIds != null) stats.morphedPlaced++;
                            } else {
                                stats.noModel++;
                            }
                        }
                        break;
                    }
                }
            }
        }
        activeObjects = this;
        activeScene = scene;
        activeTileHeights = tileHeights;
        activeTileSettings = tileSettings;
        activeCollisionMaps = collisionMaps;
        return stats;
    }

    static synchronized boolean updateRuntimeObject(int plane, int x, int y,
                                                    int type, int orientation,
                                                    int objectId) throws IOException {
        if (activeObjects == null || activeScene == null || activeTileHeights == null
                || x <= 0 || y <= 0 || x >= 103 || y >= 103
                || plane < 0 || plane >= activeTileHeights.length) {
            return false;
        }
        int heightPlane = plane;
        if (plane < 3 && activeTileSettings != null
                && (activeTileSettings[1][x][y] & 2) == 2) {
            heightPlane++;
        }
        activeObjects.removeObject(activeScene, activeCollisionMaps, plane,
                plane, x, y, type);
        if (objectId < 0) return true;

        ObjectDefinitions.Definition definition =
                activeObjects.definitions.get(objectId);
        if (definition == null) {
            throw new IOException("443 runtime packet references missing object " + objectId);
        }
        activeObjects.ensureRuntimeModels(definition, new HashSet<Integer>());
        CollisionMap collisionMap = activeCollisionMaps != null
                && plane < activeCollisionMaps.length
                ? activeCollisionMaps[plane] : null;
        return activeObjects.placeObject(activeScene, collisionMap, activeTileHeights,
                plane, heightPlane, x, y, definition, type, orientation);
    }

    static synchronized boolean animateRuntimeObject(int plane, int x, int y,
                                                     int type, int orientation,
                                                     int sequenceId) throws IOException {
        if (activeObjects == null || activeScene == null || activeTileHeights == null
                || x < 0 || y < 0 || x >= 103 || y >= 103
                || plane < 0 || plane >= activeTileHeights.length) {
            return false;
        }
        activeObjects.animations.getSequence(sequenceId);
        int southWest = activeTileHeights[plane][x][y];
        int southEast = activeTileHeights[plane][x + 1][y];
        int northEast = activeTileHeights[plane][x + 1][y + 1];
        int northWest = activeTileHeights[plane][x][y + 1];
        int group = sceneGroup(type);

        if (group == 0) {
            WallObject wall = activeScene.getWall(plane, x, y);
            if (wall == null) return false;
            ObjectDefinitions.Definition definition =
                    activeObjects.definitions.get(wall.hash >> 14 & 32767);
            if (definition == null) return false;
            activeObjects.ensureRuntimeModels(definition, new HashSet<Integer>());
            if (type == 2) {
                wall.primary = new AnimatedRenderable(activeObjects, definition, 2,
                        orientation + 4, southWest, southEast, northEast, northWest,
                        sequenceId, false);
                wall.secondary = new AnimatedRenderable(activeObjects, definition, 2,
                        orientation + 1 & 3, southWest, southEast, northEast, northWest,
                        sequenceId, false);
            } else {
                wall.primary = new AnimatedRenderable(activeObjects, definition, type,
                        orientation, southWest, southEast, northEast, northWest,
                        sequenceId, false);
            }
            return true;
        }

        if (group == 1) {
            GroundDecoration decoration = activeScene.getWallDecoration(x, y, plane);
            if (decoration == null) return false;
            ObjectDefinitions.Definition definition =
                    activeObjects.definitions.get(decoration.hash >> 14 & 32767);
            if (definition == null) return false;
            activeObjects.ensureRuntimeModels(definition, new HashSet<Integer>());
            decoration.renderable = new AnimatedRenderable(activeObjects, definition, 4, 0,
                    southWest, southEast, northEast, northWest, sequenceId, false);
            return true;
        }

        if (group == 2) {
            InteractiveObject object = activeScene.getInteractiveObject(x, y, plane);
            if (object == null) return false;
            ObjectDefinitions.Definition definition =
                    activeObjects.definitions.get(object.hash >> 14 & 32767);
            if (definition == null) return false;
            activeObjects.ensureRuntimeModels(definition, new HashSet<Integer>());
            int requestedType = type == 11 ? 10 : type;
            object.renderable = new AnimatedRenderable(activeObjects, definition,
                    requestedType, orientation, southWest, southEast, northEast,
                    northWest, sequenceId, false);
            return true;
        }

        if (group == 3) {
            WallDecoration floor = activeScene.getFloorDecoration(y, x, plane);
            if (floor == null) return false;
            ObjectDefinitions.Definition definition =
                    activeObjects.definitions.get(floor.hash >> 14 & 32767);
            if (definition == null) return false;
            activeObjects.ensureRuntimeModels(definition, new HashSet<Integer>());
            floor.renderable = new AnimatedRenderable(activeObjects, definition, 22,
                    orientation, southWest, southEast, northEast, northWest,
                    sequenceId, false);
            return true;
        }
        return false;
    }

    static synchronized void scheduleRuntimeAttachmentTransition(int plane, int x, int y,
                                                                 int type, int startDelay,
                                                                 int endDelay) {
        if (activeScene == null || activeObjects == null || x < 0 || y < 0
                || x >= 104 || y >= 104) {
            return;
        }
        int group = sceneGroup(type);
        int tag = 0;
        if (group == 0) tag = activeScene.getWallHash(plane, x, y);
        else if (group == 1) tag = activeScene.getWallDecorationHash(plane, x, y);
        else if (group == 2) tag = activeScene.getInteractiveObjectHash(plane, x, y);
        else if (group == 3) tag = activeScene.getFloorDecorationHash(plane, x, y);
        if (tag == 0) return;

        int arrangement = activeScene.getArrangement(plane, x, y, tag);
        RuntimeTransition transition = new RuntimeTransition();
        transition.plane = plane;
        transition.x = x;
        transition.y = y;
        transition.groupType = type;
        transition.objectId = tag >> 14 & 32767;
        transition.objectType = arrangement & 31;
        transition.orientation = arrangement >> 6 & 3;
        transition.startCycle = Client.gameCycle + Math.max(0, startDelay);
        transition.endCycle = Client.gameCycle + Math.max(startDelay, endDelay);
        runtimeTransitions.add(transition);
    }

    static synchronized void processRuntimeTransitions() {
        if (runtimeTransitions.isEmpty()) return;
        for (Iterator<RuntimeTransition> iterator = runtimeTransitions.iterator();
             iterator.hasNext();) {
            RuntimeTransition transition = iterator.next();
            try {
                if (!transition.removed && Client.gameCycle >= transition.startCycle) {
                    updateRuntimeObject(transition.plane, transition.x, transition.y,
                            transition.groupType, 0, -1);
                    transition.removed = true;
                }
                if (transition.removed && Client.gameCycle >= transition.endCycle) {
                    updateRuntimeObject(transition.plane, transition.x, transition.y,
                            transition.objectType, transition.orientation,
                            transition.objectId);
                    iterator.remove();
                }
            } catch (IOException exception) {
                System.err.println("443 runtime object transition failed: "
                        + exception.getMessage());
                iterator.remove();
            }
        }
    }

    private static final class RuntimeTransition {
        int plane;
        int x;
        int y;
        int groupType;
        int objectId;
        int objectType;
        int orientation;
        int startCycle;
        int endCycle;
        boolean removed;
    }

    static synchronized RuntimeAttachment buildRuntimeAttachment(int plane, int x, int y,
                                                                 int objectId, int type,
                                                                 int orientation) throws IOException {
        if (activeObjects == null || activeTileHeights == null
                || plane < 0 || plane >= activeTileHeights.length
                || x < 0 || y < 0 || x >= 103 || y >= 103) {
            return null;
        }
        ObjectDefinitions.Definition definition =
                activeObjects.definitions.get(objectId);
        if (definition == null) {
            throw new IOException("443 attachment packet references missing object " + objectId);
        }
        activeObjects.ensureRuntimeModels(definition, new HashSet<Integer>());
        int southWest = activeTileHeights[plane][x][y];
        int southEast = activeTileHeights[plane][x + 1][y];
        int northEast = activeTileHeights[plane][x + 1][y + 1];
        int northWest = activeTileHeights[plane][x][y + 1];
        Model model = activeObjects.modelAt(definition, type, orientation,
                southWest, southEast, northEast, northWest);
        if (model == null) return null;
        int sizeX = definition.sizeX;
        int sizeY = definition.sizeY;
        if (orientation == 1 || orientation == 3) {
            int swap = sizeX;
            sizeX = sizeY;
            sizeY = swap;
        }
        return new RuntimeAttachment(model, sizeX, sizeY);
    }

    static final class RuntimeAttachment {
        final Model model;
        final int sizeX;
        final int sizeY;

        RuntimeAttachment(Model model, int sizeX, int sizeY) {
            this.model = model;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }
    }

    private void ensureRuntimeModels(ObjectDefinitions.Definition definition,
                                     Set<Integer> visited) throws IOException {
        if (definition == null || !visited.add(definition.id)) return;
        if (definition.animationId != -1) animations.getSequence(definition.animationId);
        if (definition.childIds != null) {
            for (int childId : definition.childIds) {
                if (childId != -1) ensureRuntimeModels(definitions.get(childId), visited);
            }
            return;
        }
        if (definition.modelIds == null) return;
        for (int modelId : definition.modelIds) models.register(modelId);
    }

    private void removeObject(SceneGraph scene, CollisionMap[] collisionMaps,
                              int plane, int collisionPlane, int x, int y, int type) {
        int group = sceneGroup(type);
        int tag = 0;
        if (group == 0) tag = scene.getWallHash(plane, x, y);
        else if (group == 1) tag = scene.getWallDecorationHash(plane, x, y);
        else if (group == 2) tag = scene.getInteractiveObjectHash(plane, x, y);
        else if (group == 3) tag = scene.getFloorDecorationHash(plane, x, y);
        if (tag == 0) return;

        int arrangement = scene.getArrangement(plane, x, y, tag);
        int oldType = arrangement & 31;
        int oldOrientation = arrangement >> 6 & 3;
        ObjectDefinitions.Definition oldDefinition =
                definitions.get(tag >> 14 & 32767);

        if (group == 0) scene.removeWall(x, plane, y, (byte) -119);
        else if (group == 1) scene.removeWallDecoration(y, plane, x);
        else if (group == 2) scene.removeInteractiveObject(plane, x, y);
        else scene.removeFloorDecoration(plane, y, x);

        if (oldDefinition == null || !oldDefinition.solid || collisionMaps == null
                || collisionPlane < 0 || collisionPlane >= collisionMaps.length
                || collisionMaps[collisionPlane] == null) {
            return;
        }
        CollisionMap collisionMap = collisionMaps[collisionPlane];
        if (group == 0) {
            collisionMap.removeWall(oldOrientation, oldType, oldDefinition.walkable, x, y);
        } else if (group == 2) {
            collisionMap.removeObject(oldOrientation, oldDefinition.sizeX, x, y,
                    oldDefinition.sizeY, oldDefinition.walkable);
        } else if (group == 3 && oldDefinition.hasActions) {
            collisionMap.removeBlocked(y, x);
        }
    }

    private static int sceneGroup(int type) {
        if (type >= 0 && type <= 3) return 0;
        if (type >= 4 && type <= 8) return 1;
        if (type >= 9 && type <= 21) return 2;
        return type == 22 ? 3 : -1;
    }

    private boolean placeObject(SceneGraph scene, CollisionMap collisionMap,
                                int[][][] heights, int plane, int heightPlane,
                                int x, int y,
                                ObjectDefinitions.Definition definition,
                                int type, int orientation) {
        int width = definition.sizeX;
        int length = definition.sizeY;
        if (orientation == 1 || orientation == 3) {
            width = definition.sizeY;
            length = definition.sizeX;
        }

        int x1;
        int x2;
        if (x + width <= 104) {
            x1 = x + (width >> 1);
            x2 = x + (width + 1 >> 1);
        } else {
            x1 = x;
            x2 = x + 1;
        }
        int y1;
        int y2;
        if (y + length <= 104) {
            y1 = y + (length >> 1);
            y2 = y + (length + 1 >> 1);
        } else {
            y1 = y;
            y2 = y + 1;
        }

        int southWest = heights[heightPlane][x1][y1];
        int southEast = heights[heightPlane][x2][y1];
        int northEast = heights[heightPlane][x2][y2];
        int northWest = heights[heightPlane][x1][y2];
        int averageHeight = southWest + southEast + northEast + northWest >> 2;
        int tag = x + (y << 7) + (definition.id << 14) + 1073741824;
        if (!definition.hasActions) tag -= Integer.MIN_VALUE;
        byte config = (byte) ((orientation << 6) + type);

        if (type == 22) {
            if (RegionBuilder.lowMemory && !definition.hasActions
                    && !definition.obstructsGround) {
                return true;
            }
            Renderable model = renderableAt(definition, 22, orientation,
                    southWest, southEast, northEast, northWest);
            if (model == null) return false;
            scene.addGroundDecoration(plane, averageHeight, y, model, config, tag, x);
            if (definition.solid && definition.hasActions && collisionMap != null) {
                collisionMap.setBlocked(y, x);
            }
            return true;
        }

        if (type == 10 || type == 11) {
            Renderable model = renderableAt(definition, 10, orientation,
                    southWest, southEast, northEast, northWest);
            if (model == null) return false;
            int rotationFlag = type == 11 ? 256 : 0;
            scene.addEntity(tag, config, averageHeight, length, model, width,
                    plane, rotationFlag, y, x);
            if (definition.solid && collisionMap != null) {
                collisionMap.addObject(definition.walkable, definition.sizeX,
                        definition.sizeY, x, y, orientation);
            }
            return true;
        }

        if (type >= 12) {
            Renderable model = renderableAt(definition, type, orientation,
                    southWest, southEast, northEast, northWest);
            if (model == null) return false;
            scene.addEntity(tag, config, averageHeight, 1, model, 1,
                    plane, 0, y, x);
            if (definition.solid && collisionMap != null) {
                collisionMap.addObject(definition.walkable, definition.sizeX,
                        definition.sizeY, x, y, orientation);
            }
            return true;
        }

        if (type == 0) {
            Renderable model = renderableAt(definition, 0, orientation,
                    southWest, southEast, northEast, northWest);
            if (model == null) return false;
            scene.addWall(WALL_FLAGS[orientation], model, tag, y, config, x,
                    null, averageHeight, 0, plane);
            if (definition.solid && collisionMap != null) {
                collisionMap.addWall(y, orientation, x, type, definition.walkable);
            }
            if (definition.decorDisplacement != 16) {
                scene.setWallDecorationOffset(y, definition.decorDisplacement, x, plane);
            }
            return true;
        }

        if (type == 1 || type == 3) {
            Renderable model = renderableAt(definition, type, orientation,
                    southWest, southEast, northEast, northWest);
            if (model == null) return false;
            scene.addWall(DIAGONAL_WALL_FLAGS[orientation], model, tag, y,
                    config, x, null, averageHeight, 0, plane);
            if (definition.solid && collisionMap != null) {
                collisionMap.addWall(y, orientation, x, type, definition.walkable);
            }
            return true;
        }

        if (type == 2) {
            int nextOrientation = orientation + 1 & 3;
            Renderable first = renderableAt(definition, 2, orientation + 4,
                    southWest, southEast, northEast, northWest);
            Renderable second = renderableAt(definition, 2, nextOrientation,
                    southWest, southEast, northEast, northWest);
            if (first == null || second == null) return false;
            scene.addWall(WALL_FLAGS[orientation], first, tag, y, config, x,
                    second, averageHeight, WALL_FLAGS[nextOrientation], plane);
            if (definition.solid && collisionMap != null) {
                collisionMap.addWall(y, orientation, x, type, definition.walkable);
            }
            if (definition.decorDisplacement != 16) {
                scene.setWallDecorationOffset(y, definition.decorDisplacement, x, plane);
            }
            return true;
        }

        if (type == 9) {
            Renderable model = renderableAt(definition, 9, orientation,
                    southWest, southEast, northEast, northWest);
            if (model == null) return false;
            scene.addEntity(tag, config, averageHeight, 1, model, 1,
                    plane, 0, y, x);
            if (definition.solid && collisionMap != null) {
                collisionMap.addObject(definition.walkable, definition.sizeX,
                        definition.sizeY, x, y, orientation);
            }
            return true;
        }

        if (type >= 4 && type <= 8) {
            int decorSouthWest = southWest;
            int decorSouthEast = southEast;
            int decorNorthEast = northEast;
            int decorNorthWest = northWest;
            if (definition.adjustToTerrain) {
                if (orientation == 1) {
                    decorSouthWest = northWest;
                    decorSouthEast = southWest;
                    decorNorthEast = southEast;
                    decorNorthWest = northEast;
                } else if (orientation == 2) {
                    decorSouthWest = northEast;
                    decorSouthEast = northWest;
                    decorNorthEast = southWest;
                    decorNorthWest = southEast;
                } else if (orientation == 3) {
                    decorSouthWest = southEast;
                    decorSouthEast = northEast;
                    decorNorthEast = northWest;
                    decorNorthWest = southWest;
                }
            }
            Renderable model = renderableAt(definition, 4, 0,
                    decorSouthWest, decorSouthEast, decorNorthEast, decorNorthWest);
            if (model == null) return false;
            if (type == 4) {
                scene.addWallDecoration(tag, y, orientation << 9, plane, 0,
                        averageHeight, model, x, config, 0, WALL_FLAGS[orientation]);
            } else if (type == 5) {
                int displacement = 16;
                int wallTag = scene.getWallHash(plane, x, y);
                if (wallTag > 0) {
                    ObjectDefinitions.Definition wallDefinition =
                            definitions.get(wallTag >> 14 & 32767);
                    if (wallDefinition != null) displacement = wallDefinition.decorDisplacement;
                }
                scene.addWallDecoration(tag, y, orientation << 9, plane,
                        WALL_DECORATION_X_OFFSETS[orientation] * displacement,
                        averageHeight, model, x, config,
                        WALL_DECORATION_Y_OFFSETS[orientation] * displacement,
                        WALL_FLAGS[orientation]);
            } else if (type == 6) {
                scene.addWallDecoration(tag, y, orientation, plane, 0,
                        averageHeight, model, x, config, 0, 256);
            } else if (type == 7) {
                scene.addWallDecoration(tag, y, orientation, plane, 0,
                        averageHeight, model, x, config, 0, 512);
            } else {
                scene.addWallDecoration(tag, y, orientation, plane, 0,
                        averageHeight, model, x, config, 0, 768);
            }
            return true;
        }
        return false;
    }

    private Renderable renderableAt(ObjectDefinitions.Definition definition,
                                    int requestedType, int orientation,
                                    int southWest, int southEast, int northEast,
                                    int northWest) {
        if (definition.childIds == null) {
            if (definition.animationId != -1) {
                return new AnimatedRenderable(this, definition, requestedType, orientation,
                        southWest, southEast, northEast, northWest,
                        definition.animationId, true);
            }
            return modelAt(definition, requestedType, orientation,
                    southWest, southEast, northEast, northWest);
        }
        return new MorphedRenderable(this, definition, requestedType, orientation,
                southWest, southEast, northEast, northWest);
    }

    private ObjectDefinitions.Definition resolveMorphedDefinition(
            ObjectDefinitions.Definition definition) {
        return resolveMorphedDefinition(definitions, varbits, definition);
    }

    static ObjectDefinitions.Definition resolveMorphedDefinition(
            ObjectDefinitions definitions, Varbits varbits,
            ObjectDefinitions.Definition definition) {
        ObjectDefinitions.Definition current = definition;
        for (int depth = 0; depth < 16 && current != null; depth++) {
            if (current.childIds == null) return current;
            int childIndex = -1;
            if (current.varbitId != -1) {
                Varbits.Definition varbit = varbits.get(current.varbitId);
                if (varbit != null) {
                    childIndex = varbits.getValue(current.varbitId,
                            Varps.get(varbit.varpIndex));
                }
            } else if (current.configId != -1) {
                childIndex = Varps.get(current.configId);
            }

            int selectableCount = current.childIds.length
                    - (current.hasMorphFallback ? 1 : 0);
            int childId;
            if (childIndex >= 0 && childIndex < selectableCount) {
                childId = current.childIds[childIndex];
            } else {
                childId = current.hasMorphFallback ? current.morphFallbackId : -1;
            }
            if (childId == -1) return null;
            current = definitions.get(childId);
        }
        return null;
    }

    private static final class MorphedRenderable extends Renderable {
        private final SceneObjects owner;
        private final ObjectDefinitions.Definition definition;
        private final int requestedType;
        private final int orientation;
        private final int southWest;
        private final int southEast;
        private final int northEast;
        private final int northWest;
        private int animatedChildId = -1;
        private AnimatedRenderable animatedChild;

        private MorphedRenderable(SceneObjects owner,
                                  ObjectDefinitions.Definition definition,
                                  int requestedType, int orientation,
                                  int southWest, int southEast, int northEast,
                                  int northWest) {
            this.owner = owner;
            this.definition = definition;
            this.requestedType = requestedType;
            this.orientation = orientation;
            this.southWest = southWest;
            this.southEast = southEast;
            this.northEast = northEast;
            this.northWest = northWest;
        }

        @Override
        final Model getRotatedModel() {
            ObjectDefinitions.Definition resolved =
                    owner.resolveMorphedDefinition(definition);
            if (resolved == null) {
                animatedChild = null;
                animatedChildId = -1;
                return null;
            }
            if (resolved.animationId != -1) {
                if (animatedChild == null || animatedChildId != resolved.id) {
                    animatedChild = new AnimatedRenderable(owner, resolved, requestedType,
                            orientation, southWest, southEast, northEast, northWest,
                            resolved.animationId, true);
                    animatedChildId = resolved.id;
                }
                return animatedChild.getRotatedModel();
            }
            animatedChild = null;
            animatedChildId = -1;
            return owner.modelAt(resolved, requestedType, orientation,
                    southWest, southEast, northEast, northWest);
        }
    }

    private static final class AnimatedRenderable extends Renderable {
        private final SceneObjects owner;
        private final ObjectDefinitions.Definition definition;
        private final int requestedType;
        private final int orientation;
        private final int southWest;
        private final int southEast;
        private final int northEast;
        private final int northWest;
        private AnimationSequence sequence;
        private int animationFrame;
        private int frameStartCycle;

        private AnimatedRenderable(SceneObjects owner,
                                   ObjectDefinitions.Definition definition,
                                   int requestedType, int orientation,
                                   int southWest, int southEast, int northEast,
                                   int northWest, int sequenceId, boolean randomize) {
            this.owner = owner;
            this.definition = definition;
            this.requestedType = requestedType;
            this.orientation = orientation;
            this.southWest = southWest;
            this.southEast = southEast;
            this.northEast = northEast;
            this.northWest = northWest;
            this.sequence = owner.animations.getLoadedSequence(sequenceId);
            this.animationFrame = 0;
            this.frameStartCycle = Client.gameCycle - 1;
            if (randomize && this.sequence != null && this.sequence.frameStep != -1
                    && this.sequence.frameCount > 0) {
                this.animationFrame = (int) (Math.random() * this.sequence.frameCount);
                this.frameStartCycle -= (int) (Math.random()
                        * this.sequence.getFrameLength(this.animationFrame));
            }
        }

        @Override
        final Model getRotatedModel() {
            int frameId = -1;
            if (sequence != null) {
                int elapsed = Client.gameCycle - frameStartCycle;
                if (elapsed > 100 && sequence.frameStep > 0) elapsed = 100;
                while (elapsed > sequence.getFrameLength(animationFrame)) {
                    elapsed -= sequence.getFrameLength(animationFrame);
                    animationFrame++;
                    if (animationFrame >= sequence.frameCount) {
                        animationFrame -= sequence.frameStep;
                        if (animationFrame < 0 || animationFrame >= sequence.frameCount) {
                            sequence = null;
                            break;
                        }
                    }
                }
                frameStartCycle = Client.gameCycle - elapsed;
                if (sequence != null) frameId = sequence.frameIds[animationFrame];
            }
            return frameId == -1
                    ? owner.modelAt(definition, requestedType, orientation,
                            southWest, southEast, northEast, northWest)
                    : owner.animatedModelAt(definition, requestedType, orientation,
                            southWest, southEast, northEast, northWest, frameId);
        }
    }

    private Model modelAt(ObjectDefinitions.Definition definition,
                          int requestedType, int orientation,
                          int southWest, int southEast, int northEast, int northWest) {
        long builtKey = ((long) definition.id << 32)
                | ((long) (requestedType & 255) << 8) | (orientation & 255L);
        Model model = builtModels.get(builtKey);
        if (model == null) {
            Model raw = getRawModel(definition, requestedType, orientation);
            if (raw == null) return null;
            boolean resize = definition.modelSizeX != 128
                    || definition.modelSizeHeight != 128 || definition.modelSizeY != 128;
            boolean translate = definition.offsetX != 0 || definition.offsetHeight != 0
                    || definition.offsetY != 0;
            boolean shareColors = definition.recolorFrom == null
                    && definition.retextureFrom == null;
            boolean shareVertices = orientation == 0 && !resize && !translate;
            model = new Model(shareColors, true, shareVertices, raw);
            int rotations = orientation;
            while (rotations-- > 0) model.rotateY90();
            if (definition.recolorFrom != null) {
                for (int i = 0; i < definition.recolorFrom.length; i++) {
                    model.recolor(definition.recolorFrom[i], definition.recolorTo[i]);
                }
            }
            if (definition.retextureFrom != null) {
                for (int i = 0; i < definition.retextureFrom.length; i++) {
                    model.retexture(definition.retextureFrom[i], definition.retextureTo[i]);
                }
            }
            if (resize) {
                model.scale(definition.modelSizeX, definition.modelSizeY,
                        definition.modelSizeHeight);
            }
            if (translate) {
                model.translate(definition.offsetX, definition.offsetHeight,
                        definition.offsetY);
            }
            model.light(64 + definition.ambient, 768 + definition.contrast * 5,
                    -50, -10, -50, !definition.nonFlatShading);
            if (definition.supportsItems == 1) model.sceneHeightOffset = model.modelHeight;
            builtModels.put(builtKey, model);
        }

        if (!definition.adjustToTerrain && !definition.nonFlatShading) return model;
        Model adjusted = new Model(definition.adjustToTerrain,
                definition.nonFlatShading, model);
        if (definition.adjustToTerrain) {
            int average = southWest + southEast + northEast + northWest >> 2;
            for (int vertex = 0; vertex < adjusted.vertexCount; vertex++) {
                int vx = adjusted.verticesX[vertex];
                int vz = adjusted.verticesZ[vertex];
                int south = southWest + (southEast - southWest) * (vx + 64) / 128;
                int north = northWest + (northEast - northWest) * (vx + 64) / 128;
                int height = south + (north - south) * (vz + 64) / 128;
                adjusted.verticesY[vertex] += height - average;
            }
            adjusted.computeSphericalBounds();
        }
        return adjusted;
    }

    private Model animatedModelAt(ObjectDefinitions.Definition definition,
                                  int requestedType, int orientation,
                                  int southWest, int southEast, int northEast,
                                  int northWest, int frameId) {
        long key = ((long) definition.id << 32)
                | ((long) (requestedType & 255) << 8) | (orientation & 255L);
        Model base = animatedBaseModels.get(key);
        if (base == null) {
            Model raw = getRawModel(definition, requestedType, orientation);
            if (raw == null) return null;
            boolean resize = definition.modelSizeX != 128
                    || definition.modelSizeHeight != 128 || definition.modelSizeY != 128;
            boolean translate = definition.offsetX != 0 || definition.offsetHeight != 0
                    || definition.offsetY != 0;
            boolean shareColors = definition.recolorFrom == null
                    && definition.retextureFrom == null;
            base = new Model(shareColors, false, false, raw);
            int rotations = orientation;
            while (rotations-- > 0) base.rotateY90();
            if (definition.recolorFrom != null) {
                for (int i = 0; i < definition.recolorFrom.length; i++) {
                    base.recolor(definition.recolorFrom[i], definition.recolorTo[i]);
                }
            }
            if (definition.retextureFrom != null) {
                for (int i = 0; i < definition.retextureFrom.length; i++) {
                    base.retexture(definition.retextureFrom[i], definition.retextureTo[i]);
                }
            }
            if (resize) {
                base.scale(definition.modelSizeX, definition.modelSizeY,
                        definition.modelSizeHeight);
            }
            if (translate) {
                base.translate(definition.offsetX, definition.offsetHeight,
                        definition.offsetY);
            }
            base.skin();
            base.light(64 + definition.ambient, 768 + definition.contrast * 5,
                    -50, -10, -50, !definition.nonFlatShading);
            if (definition.supportsItems == 1) base.sceneHeightOffset = base.modelHeight;
            animatedBaseModels.put(key, base);
        }

        Model model = new Model(true, false, false, base);
        int turns = orientation & 3;
        int reverseTurns = 4 - turns & 3;
        for (int i = 0; i < reverseTurns; i++) model.rotateY90();
        model.applyAnimationFrame(frameId);
        for (int i = 0; i < turns; i++) model.rotateY90();
        model.triangleSkin = null;
        model.vectorSkin = null;

        if (definition.adjustToTerrain) {
            int average = southWest + southEast + northEast + northWest >> 2;
            for (int vertex = 0; vertex < model.vertexCount; vertex++) {
                int vx = model.verticesX[vertex];
                int vz = model.verticesZ[vertex];
                int south = southWest + (southEast - southWest) * (vx + 64) / 128;
                int north = northWest + (northEast - northWest) * (vx + 64) / 128;
                int height = south + (north - south) * (vz + 64) / 128;
                model.verticesY[vertex] += height - average;
            }
        }
        model.computeSphericalBounds();
        return model;
    }

    private Model getRawModel(ObjectDefinitions.Definition definition,
                              int requestedType, int orientation) {
        if (definition.modelIds == null) return null;
        boolean mirror = definition.rotated ^ orientation > 3;
        if (definition.modelTypes == null) {
            if (requestedType != 10) return null;
            Model[] parts = new Model[definition.modelIds.length];
            for (int i = 0; i < definition.modelIds.length; i++) {
                parts[i] = getRawPart(definition.modelIds[i], mirror);
                if (parts[i] == null) return null;
            }
            return parts.length == 1 ? parts[0] : new Model(parts.length, parts);
        }
        for (int i = 0; i < definition.modelTypes.length; i++) {
            if (definition.modelTypes[i] == requestedType) {
                return getRawPart(definition.modelIds[i], mirror);
            }
        }
        return null;
    }

    private Model getRawPart(int modelId, boolean mirror) {
        long key = ((long) modelId << 1) | (mirror ? 1L : 0L);
        Model cached = rawModels.get(key);
        if (cached != null) return cached;
        Model model = Model.getModel(models.getRegisteredModelId(modelId));
        if (model == null) return null;
        if (mirror) model.mirror();
        rawModels.put(key, model);
        return model;
    }

    public static final class PlacementStats {
        public int placed;
        public int morphedPlaced;
        public int animatedSkipped;
        public int noModel;
        public int outOfBounds;
    }
}
