package client;

final class NodeDeque {
   private final Node sentinel = new Node();
   private Node iterator;

   public NodeDeque() {
      this.sentinel.next = this.sentinel;
      this.sentinel.previous = this.sentinel;
   }

   public final void addLast(Node node) {
      if (node.previous != null) {
         node.unlink();
      }

      node.previous = this.sentinel.previous;
      node.next = this.sentinel;
      node.previous.next = node;
      node.next.previous = node;
   }

   public final void addFirst(Node node) {
      if (node.previous != null) {
         node.unlink();
      }

      node.previous = this.sentinel;
      node.next = this.sentinel.next;
      node.previous.next = node;
      node.next.previous = node;
   }

   public final Node removeFirst() {
      Node node = this.sentinel.next;
      if (this.sentinel.next == this.sentinel) {
         return null;
      }

      node.unlink();
      return node;
   }

   public final Node first() {
      Node node = this.sentinel.next;
      if (this.sentinel.next == this.sentinel) {
         this.iterator = null;
         return null;
      } else {
         this.iterator = node.next;
         return node;
      }
   }

   public final Node last() {
      Node node = this.sentinel.previous;
      if (this.sentinel.previous == this.sentinel) {
         this.iterator = null;
         return null;
      } else {
         this.iterator = node.previous;
         return node;
      }
   }

   public final Node next() {
      Node node = this.iterator;
      if (this.iterator == this.sentinel) {
         this.iterator = null;
         return null;
      } else {
         this.iterator = node.next;
         return node;
      }
   }

   public final Node previous() {
      Node node = this.iterator;
      if (this.iterator == this.sentinel) {
         this.iterator = null;
         return null;
      } else {
         this.iterator = node.previous;
         return node;
      }
   }

   public final void removeAll() {
      if (this.sentinel.next != this.sentinel) {
         while (true) {
            Node node = this.sentinel.next;
            if (this.sentinel.next == this.sentinel) {
               return;
            }

            node.unlink();
         }
      }
   }
}
