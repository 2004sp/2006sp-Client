package client;

final class CacheableNodeDeque {
   private final CacheableNode sentinel = new CacheableNode();
   private CacheableNode iterator;

   public CacheableNodeDeque() {
      this.sentinel.nextCacheable = this.sentinel;
      this.sentinel.previousCacheable = this.sentinel;
   }

   public final void addLast(CacheableNode node) {
      if (node.previousCacheable != null) {
         node.unlinkCacheable();
      }

      node.previousCacheable = this.sentinel.previousCacheable;
      node.nextCacheable = this.sentinel;
      node.previousCacheable.nextCacheable = node;
      node.nextCacheable.previousCacheable = node;
   }

   public final CacheableNode removeFirst() {
      CacheableNode node = this.sentinel.nextCacheable;
      if (this.sentinel.nextCacheable == this.sentinel) {
         return null;
      }

      node.unlinkCacheable();
      return node;
   }

   public final CacheableNode first() {
      CacheableNode node = this.sentinel.nextCacheable;
      if (this.sentinel.nextCacheable == this.sentinel) {
         this.iterator = null;
         return null;
      } else {
         this.iterator = node.nextCacheable;
         return node;
      }
   }

   public final CacheableNode next() {
      CacheableNode node = this.iterator;
      if (this.iterator == this.sentinel) {
         this.iterator = null;
         return null;
      } else {
         this.iterator = node.nextCacheable;
         return node;
      }
   }

   public final int size() {
      int size = 0;

      for (CacheableNode node = this.sentinel.nextCacheable; node != this.sentinel; node = node.nextCacheable) {
         size++;
      }

      return size;
   }
}
