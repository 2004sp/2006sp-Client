package client;
final class MouseRecorder implements Runnable {
   private Client client;
   public final Object lock = new Object();
   public final int[] mouseY = new int[500];
   public boolean running = true;
   public final int[] mouseX = new int[500];
   public int sampleCount;
   @Override
   public final void run() {
      while (this.running) {
         synchronized (this.lock) {
            if (this.sampleCount < 500) {
               this.mouseX[this.sampleCount] = this.client.mouseX;
               this.mouseY[this.sampleCount] = this.client.mouseY;
               this.sampleCount++;
            }
         }

         try {
            Thread.sleep(50L);
         } catch (Exception exception) {
         }
      }
   }

   public MouseRecorder(Client newClient) {
      this.client = newClient;
   }
}
