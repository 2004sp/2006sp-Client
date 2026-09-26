package client;

import java.io.IOException;
import java.util.LinkedHashSet;

/** Loads the map-square groups referenced by a revision 443 static region packet. */
final class RegionAssets {
    private static final int MAP_ARCHIVE = 5;

    final MapSquare[] mapSquares;

    private RegionAssets(MapSquare[] mapSquares) {
        this.mapSquares = mapSquares;
    }

    static RegionAssets load(Cache cache,
                                        RegionPacket region)
            throws IOException {
        Request[] requests = requests(region);
        MapSquare[] squares = new MapSquare[requests.length];
        Cache.ReferenceTable table = cache.readReferenceTable(MAP_ARCHIVE);
        for (int i = 0; i < requests.length; i++) {
            Request request = requests[i];
            byte[] terrain = readMapGroup(cache, table,
                    "m" + request.x + "_" + request.y, null);
            byte[] locations = null;
            locations = readMapGroup(cache, table,
                    "l" + request.x + "_" + request.y, request.xteaKey);
            if (locations != null) LocationMap.decode(locations);
            squares[i] = new MapSquare(request.x, request.y, terrain, locations);
        }
        RegionAssets assets = new RegionAssets(squares);
        if ("443".equals(System.getProperty("prs.clientRevision"))) {
            ObjectDefinitions definitions =
                    ObjectDefinitions.load(cache);
            SceneObjects objects = SceneObjects.prepare(
                    cache, definitions, assets);
            SceneObjects.queueForRegion(objects, assets, region);
        }
        return assets;
    }


    static Request[] requests(RegionPacket region) throws IOException {
        if (region.templates != null) {
            LinkedHashSet<Integer> ids = new LinkedHashSet<Integer>();
            for (int plane = 0; plane < 4; plane++) {
                for (int x = 0; x < 13; x++) {
                    for (int y = 0; y < 13; y++) {
                        int template = region.templates[plane][x][y];
                        if (template != -1) {
                            int sourceX = template >> 14 & 1023;
                            int sourceY = template >> 3 & 2047;
                            ids.add(Integer.valueOf((sourceX / 8 << 8) | sourceY / 8));
                        }
                    }
                }
            }
            if (region.xteaKeys.length != ids.size()) {
                throw new IOException("Revision 443 constructed key count mismatch: expected "
                        + ids.size() + ", got " + region.xteaKeys.length);
            }
            Request[] requests = new Request[ids.size()];
            int index = 0;
            for (Integer id : ids) {
                requests[index] = new Request(id.intValue() >> 8,
                        id.intValue() & 255, region.xteaKeys[index].clone());
                index++;
            }
            return requests;
        }
        int count = RegionPacket.expectedKeyCount(region.centerX, region.centerY);
        if (region.xteaKeys.length != count) {
            throw new IOException("Revision 443 region key count mismatch: expected "
                    + count + ", got " + region.xteaKeys.length);
        }
        Request[] requests = new Request[count];
        boolean special = RegionPacket.isSpecialRegion(region.centerX,
                region.centerY);
        int index = 0;
        for (int x = (region.centerX - 6) / 8; x <= (region.centerX + 6) / 8; x++) {
            for (int y = (region.centerY - 6) / 8; y <= (region.centerY + 6) / 8; y++) {
                if (special && RegionPacket.isSkippedSpecialSquare(x, y)) {
                    continue;
                }
                requests[index] = new Request(x, y, region.xteaKeys[index].clone());
                index++;
            }
        }
        return requests;
    }

    private static byte[] readMapGroup(Cache cache,
                                       Cache.ReferenceTable table,
                                       String name, int[] xteaKey) throws IOException {
        int group = table.getGroupId(name);
        if (group < 0) {
            return null;
        }
        int[] fileIds = table.getFileIds(group);
        if (fileIds == null || fileIds.length != 1) {
            throw new IOException("Revision 443 map group " + name
                    + " should contain one file");
        }
        try {
            return cache.readFile(MAP_ARCHIVE, group, fileIds[0], xteaKey);
        } catch (IOException exception) {
            throw new IOException("Unable to load revision 443 map group " + name,
                    exception);
        }
    }

    static final class Request {
        final int x;
        final int y;
        final int[] xteaKey;

        Request(int x, int y, int[] xteaKey) {
            this.x = x;
            this.y = y;
            this.xteaKey = xteaKey;
        }
    }

    static final class MapSquare {
        final int x;
        final int y;
        final byte[] terrain;
        final byte[] locations;
        MapSquare(int x, int y, byte[] terrain, byte[] locations) {
            this.x = x;
            this.y = y;
            this.terrain = terrain;
            this.locations = locations;
        }
    }
}
