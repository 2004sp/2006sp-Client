package client;
public class ParticleVelocity {
   private Vector3 velocity;
   public Vector3 getVelocity() {
      return this.velocity.copy();
   }

   public ParticleVelocity(Vector3 vector3) {
      this.velocity = vector3;
   }
}
