package client;
public class Node {
   public long key;
   public Node next;
   public Node previous;

   public final void unlink() {
      if (this.previous != null) {
         this.previous.next = this.next;
         this.next.previous = this.previous;
         this.next = null;
         this.previous = null;
      }
   }
}
