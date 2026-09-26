package client;

import java.io.IOException;

/** Supplies the container bytes returned by a revision 443 JS5 request. */
public interface ContainerSource {
    byte[] requestContainer(int archive, int group) throws IOException;

    int[] readMasterCrcs() throws IOException;
}
