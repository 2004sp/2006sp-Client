package client;
final class BlackFallingParticleDefinition extends ParticleDefinition {
   BlackFallingParticleDefinition() {
      this.setStartVelocity(new Vector3(0, -1, 0));
      this.setEndVelocity(new Vector3(0, -1, 0));
      new Vector3(0, 0, 0);
      this.setLifetime(19);
      this.setStartColor(0);
      this.setSpawnCount(3);
      this.setStartSize(1.0F);
      this.setEndSize(0.05F);
      this.setStartAlpha(0.015F);
      this.calculateSteps();
      this.setColorStep(0);
   }
}
