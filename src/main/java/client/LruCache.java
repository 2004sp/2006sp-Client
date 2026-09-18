package client;

public final class LruCache {
   private final CacheableNode emptyNode = new CacheableNode();
   private final int capacity;
   private int remainingCapacity;
   private final NodeHashTable hashTable;
   private final CacheableNodeDeque deque = new CacheableNodeDeque();

   public LruCache(int capacity) {
      this.capacity = capacity;
      this.remainingCapacity = capacity;
      this.hashTable = new NodeHashTable();
   }

   public final CacheableNode get(long key) {
      CacheableNode node = (CacheableNode)this.hashTable.get(key);
      if (node != null) {
         this.deque.addLast(node);
      }

      return node;
   }

   public final void put(CacheableNode node, long key) {
      try {
         if (this.remainingCapacity == 0) {
            CacheableNode evicted = this.deque.removeFirst();
            evicted.unlink();
            evicted.unlinkCacheable();
            if (evicted == this.emptyNode) {
               evicted = this.deque.removeFirst();
               evicted.unlink();
               evicted.unlinkCacheable();
            }
         } else {
            this.remainingCapacity--;
         }

         this.hashTable.put(node, key);
         this.deque.addLast(node);
      } catch (RuntimeException exception) {
         SignLink.reporterror("47547, " + node + ", " + key + ", 2" + ", " + exception.toString());
         throw new RuntimeException();
      }
   }

   public final void clear() {
      CacheableNode node;
      while ((node = this.deque.removeFirst()) != null) {
         node.unlink();
         node.unlinkCacheable();
      }

      this.remainingCapacity = this.capacity;
   }
}
