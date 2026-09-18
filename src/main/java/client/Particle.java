package client;
public final class Particle {
   private int age = 0;
   private int color;
   private float size;
   private Vector3 velocity;
   private float alpha;
   private boolean dead = false;
   private ParticleDefinition definition = null;
   private Vector3 position;

   public final float getAlpha() {
      return this.alpha;
   }

   public final boolean isDead() {
      return this.dead;
   }
   public final void update() {
      if (this.definition != null) {
         this.age++;
         if (this.age >= this.definition.getLifetime()) {
            this.dead = true;
         } else {
            this.color = this.color + this.definition.getColorStep();
            this.size = this.size + this.definition.getSizeStep();
            this.position.add(this.velocity);
            this.velocity.add(this.definition.getVelocityStep());
            this.alpha = this.alpha + this.definition.getAlphaStep();
         }
      }
   }
   public final Vector3 getPosition() {
      return this.position;
   }

   public Particle(ParticleDefinition particleDefinition, Vector3 vector3, int scalarArgument, int scalarArgument2) {
      this(particleDefinition.getStartColor(), particleDefinition.getStartSize(), particleDefinition.getRandomizedStartVelocity(7).copy(), particleDefinition.getPositionGenerator().getVelocity().add(vector3), particleDefinition.getStartAlpha(), scalarArgument);
      this.definition = particleDefinition;
   }

   private Particle(int newColor, float newSize, Vector3 vector3, Vector3 newVector3, float newAlpha, int scalarArgument) {
      this.color = newColor;
      this.size = newSize;
      this.velocity = vector3;
      this.position = newVector3;
      this.alpha = newAlpha;
   }
   public final int getColor() {
      return this.color;
   }

   public final float getSize() {
      return this.size;
   }
}
