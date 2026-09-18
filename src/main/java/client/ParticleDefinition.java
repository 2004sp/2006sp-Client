package client;

import java.util.Random;
public class ParticleDefinition {
   public static final Random random = new Random(System.currentTimeMillis());
   public static ParticleDefinition[] definitions = new ParticleDefinition[]{
      new PrimaryLootHighlightParticleDefinition(),
      new SecondaryLootHighlightParticleDefinition(),
      new FadingFallingParticleDefinition(),
      new RedFallingParticleDefinition(),
      new BlackFallingParticleDefinition(),
      new WhiteFallingParticleDefinition(),
      new BlackSmokeParticleDefinition(),
      new LongLivedRedEmberParticleDefinition(),
      new RedEmberParticleDefinition(),
      new AlternateRisingParticleDefinition(),
      new BlueEmberParticleDefinition(),
      new ShortLivedRisingParticleDefinition()
   };
   private float startSize = 1.0F;
   private float endSize = 1.0F;
   private int startColor = -1;
   private int endColor = -1;
   private Vector3 startVelocity = Vector3.ZERO;
   private Vector3 endVelocity = Vector3.ZERO;
   private int lifetime = 1;
   private int spawnCount = 1;
   private float startAlpha = 1.0F;
   private float endAlpha = 0.05F;
   private ParticleVelocity positionGenerator = new ParticleVelocity(Vector3.ZERO);
   private Vector3 velocityStep;
   private int colorStep;
   private float sizeStep;
   private float alphaStep;
   public final float getStartAlpha() {
      return this.startAlpha;
   }
   public final void setStartAlpha(float newStartAlpha) {
      this.startAlpha = newStartAlpha;
   }
   public final void setEndAlpha(float newEndAlpha) {
      this.endAlpha = newEndAlpha;
   }
   public final float getAlphaStep() {
      return this.alphaStep;
   }
   public final ParticleVelocity getPositionGenerator() {
      return this.positionGenerator;
   }
   private static int randomInt(int scalarArgument, int scalarArgument2) {
      scalarArgument2 = scalarArgument2 - scalarArgument + 1;
      return (int)(Math.random() * scalarArgument2) + scalarArgument;
   }
   public final int getSpawnCount() {
      return this.spawnCount;
   }
   public final void setSpawnCount(int newSpawnCount) {
      this.spawnCount = newSpawnCount;
   }
   public final float getStartSize() {
      return this.startSize;
   }
   public final void setStartSize(float newStartSize) {
      this.startSize = newStartSize;
   }
   public final void setEndSize(float newEndSize) {
      this.endSize = newEndSize;
   }
   public final int getStartColor() {
      return this.startColor;
   }
   public final void setStartColor(int newStartColor) {
      this.startColor = newStartColor;
   }
   public final Vector3 getRandomizedStartVelocity(int scalarArgument) {
      switch (scalarArgument) {
         case 7:
            return new Vector3(this.startVelocity.getX(), this.startVelocity.getY() + randomInt(0, 1), this.startVelocity.getZ());
         default:
            return new Vector3(
               this.startVelocity.getX() + randomInt(-3, 3), this.startVelocity.getY() + randomInt(0, 9), this.startVelocity.getZ() + randomInt(-3, 3)
            );
      }
   }
   public final void setStartVelocity(Vector3 vector3) {
      this.startVelocity = vector3;
   }
   public final void setEndVelocity(Vector3 vector3) {
      this.endVelocity = vector3;
   }
   public final int getLifetime() {
      return this.lifetime;
   }
   public final void setLifetime(int newLifetime) {
      this.lifetime = newLifetime;
   }
   public final void setColorStep(int newColorStep) {
      this.colorStep = newColorStep;
   }
   public final float getSizeStep() {
      return this.sizeStep;
   }
   public final Vector3 getVelocityStep() {
      return this.velocityStep;
   }
   public final int getColorStep() {
      return this.colorStep;
   }
   public final void calculateSteps() {
      this.sizeStep = (this.endSize - this.startSize) / this.lifetime;
      this.colorStep = (this.endColor - this.startColor) / this.lifetime;
      this.velocityStep = this.endVelocity.subtract(this.startVelocity).divide(this.lifetime);
      this.alphaStep = (this.endAlpha - this.startAlpha) / this.lifetime;
   }
}
