package client;
public final class OnDemandRequest extends CacheableNode {
   int dataType;
   byte[] buffer;
   int id;
   boolean highPriority = true;
   int requestAge;
}
