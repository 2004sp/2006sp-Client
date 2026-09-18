package client;

final class NodeList {
   Node head = new Node();
   private Node current;

   final Node last() {
      Node node = this.head.previous;
      if (this.head.previous == this.head) {
         this.current = null;
         return null;
      } else {
         this.current = node.previous;
         return node;
      }
   }

   final void addLast(Node node) {
      if (node.next != null) {
         node.unlink();
      }

      node.previous = this.head.previous;
      node.next = this.head;
      node.next.previous = node;
      node.previous.next = node;
   }

   final Node previous() {
      Node node = this.current;
      if (this.head == node) {
         this.current = null;
         return null;
      } else {
         this.current = node.previous;
         return node;
      }
   }

   public NodeList() {
      this.head.previous = this.head;
      this.head.next = this.head;
   }
}
