package client;

import java.io.IOException;

/** Access to the revision 443 model index (JS5 archive 7). */
public final class Models {
    private static final int MODEL_ARCHIVE = 7;
    private static int liveNamespaceBase = -1;
    private static int liveNamespaceCapacity;
    private static Models liveModels;

    private final Cache cache;
    private final Cache.ReferenceTable referenceTable;
    private final boolean[] present;
    private final int groupCount;
    private int modelNamespaceBase = -1;

    private Models(Cache cache,
                              Cache.ReferenceTable referenceTable,
                              boolean[] present, int groupCount) {
        this.cache = cache;
        this.referenceTable = referenceTable;
        this.present = present;
        this.groupCount = groupCount;
    }

    public static Models load(Cache cache) throws IOException {
        Cache.ReferenceTable table = cache.readReferenceTable(MODEL_ARCHIVE);
        int[] groupIds = table.getGroupIds();
        int maxId = -1;
        for (int groupId : groupIds) {
            if (groupId > maxId) maxId = groupId;
        }
        boolean[] present = new boolean[maxId + 1];
        for (int groupId : groupIds) {
            int[] fileIds = table.getFileIds(groupId);
            if (fileIds == null || fileIds.length != 1) {
                throw new IOException("Unexpected revision 443 model group " + groupId
                        + " file count: " + (fileIds == null ? 0 : fileIds.length));
            }
            present[groupId] = true;
        }
        return new Models(cache, table, present, groupIds.length);
    }

    public int getGroupCount() {
        return groupCount;
    }

    public int getModelCapacity() {
        return present.length;
    }

    public boolean contains(int modelId) {
        return modelId >= 0 && modelId < present.length && present[modelId];
    }

    public byte[] readModel(int modelId) throws IOException {
        if (!contains(modelId)) {
            throw new IOException("Missing revision 443 model " + modelId);
        }
        int[] fileIds = referenceTable.getFileIds(modelId);
        if (fileIds == null || fileIds.length != 1) {
            throw new IOException("Unexpected revision 443 model group " + modelId
                    + " file count: " + (fileIds == null ? 0 : fileIds.length));
        }
        return cache.readFile(MODEL_ARCHIVE, modelId, fileIds[0]);
    }

    /** Replaces the model table for standalone cache probes. */
    public void initializeModelCache() {
        Model.initializeModelCache(present.length, new OnDemandFetcherBase());
        modelNamespaceBase = 0;
        liveNamespaceBase = 0;
        liveNamespaceCapacity = present.length;
        liveModels = this;
    }

    /**
     * Reserves a separate id range for live 443 models without overwriting the
     * legacy models still used by unported client systems.
     */
    public synchronized int initializeModelNamespace() {
        if (modelNamespaceBase >= 0) return modelNamespaceBase;
        if (liveNamespaceBase < 0) {
            liveNamespaceBase = Model.reserveModelNamespace(present.length);
            liveNamespaceCapacity = present.length;
        } else if (liveNamespaceCapacity != present.length) {
            throw new IllegalStateException("Revision 443 model namespace size changed");
        }
        modelNamespaceBase = liveNamespaceBase;
        liveModels = this;
        return modelNamespaceBase;
    }

    static boolean registerIfNeeded(int registeredId) {
        Models models = liveModels;
        if (models == null || registeredId < liveNamespaceBase
                || registeredId >= liveNamespaceBase + liveNamespaceCapacity) return false;
        int modelId = registeredId - liveNamespaceBase;
        if (!models.contains(modelId)) return false;
        try {
            models.register(modelId);
            return true;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to fetch 443 model " + modelId, exception);
        }
    }

    public int getRegisteredModelId(int modelId) {
        if (!contains(modelId)) {
            throw new IllegalArgumentException("Missing revision 443 model " + modelId);
        }
        if (modelNamespaceBase < 0) {
            throw new IllegalStateException("Revision 443 model namespace is not initialized");
        }
        return modelNamespaceBase + modelId;
    }

    public void register(int modelId) throws IOException {
        int registeredId = getRegisteredModelId(modelId);
        Model.registerRevision443Model(readModel(modelId), registeredId);
    }

    public Model registerAndDecode(int modelId) throws IOException {
        register(modelId);
        int registeredId = getRegisteredModelId(modelId);
        Model model = Model.getModel(registeredId);
        if (model == null) {
            throw new IOException("Revision 443 model did not register: " + modelId);
        }
        return model;
    }
}
