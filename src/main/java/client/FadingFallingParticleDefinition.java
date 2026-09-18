package client;
final class FadingFallingParticleDefinition extends ParticleDefinition {
   FadingFallingParticleDefinition() {
      this.setStartVelocity(new Vector3(0, -1, 0));
      this.setEndVelocity(new Vector3(0, -1, 0));
      new Vector3(0, 0, 0);
      this.setLifetime(19);
      this.setStartAlpha(0.5F);
      this.setSpawnCount(3);
      this.calculateSteps();
   }
}
