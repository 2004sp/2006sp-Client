package bootstrap;

import client.Client;
import client.ClientBootstrap;
public final class DefaultClientBootstrap extends ClientBootstrap {
   public DefaultClientBootstrap() {
      super(false);
      Client client = Client.getClient();
   }
}
