package client;

public class CacheableNode extends Node {
   public CacheableNode nextCacheable;
   CacheableNode previousCacheable;

   public final void unlinkCacheable() {
      if (this.previousCacheable != null) {
         this.previousCacheable.nextCacheable = this.nextCacheable;
         this.nextCacheable.previousCacheable = this.previousCacheable;
         this.nextCacheable = null;
         this.previousCacheable = null;
      }
   }
}
