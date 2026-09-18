package worldmap;

public final class WorldMapTileExporter {
    private WorldMapTileExporter() {
    }

    public static void main(String[] args) {
        String cacheDirectory = args != null && args.length > 0 ? args[0] : "./cache";
        boolean success = WorldMapViewer.exportControlPanelMapTilesNow(cacheDirectory);
        if (!success) {
            System.exit(1);
        }
    }
}
