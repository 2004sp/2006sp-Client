package client;

final class NodeHashTable {
   private final int size = 1024;
   private final Node[] buckets = new Node[1024];

   public NodeHashTable() {
      for (int bucketIndex = 0; bucketIndex < this.buckets.length; bucketIndex++) {
         Node sentinel = this.buckets[bucketIndex] = new Node();
         sentinel.next = sentinel;
         sentinel.previous = sentinel;
      }
   }

   public final Node get(long key) {
      Node bucket = this.buckets[(int)(key & this.size - 1)];
      for (Node node = bucket.next; node != bucket; node = node.next) {
         if (node.key == key) {
            return node;
         }
      }

      return null;
   }

   public final void put(Node node, long key) {
      try {
         if (node.previous != null) {
            node.unlink();
         }

         Node bucket = this.buckets[(int)(key & this.size - 1)];
         node.previous = bucket.previous;
         node.next = bucket;
         node.previous.next = node;
         node.next.previous = node;
         node.key = key;
      } catch (RuntimeException exception) {
         SignLink.reporterror("91499, " + node + ", " + key + ", 7" + ", " + exception.toString());
         throw new RuntimeException();
      }
   }
}
