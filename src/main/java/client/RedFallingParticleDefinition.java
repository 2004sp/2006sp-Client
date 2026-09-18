package client;
final class RedFallingParticleDefinition extends ParticleDefinition {
   RedFallingParticleDefinition() {
      this.setStartVelocity(new Vector3(0, -3, 0));
      this.setEndVelocity(new Vector3(0, -3, 0));
      new Vector3(0, 0, 0);
      this.setLifetime(19);
      this.setStartColor(16713728);
      this.setSpawnCount(1);
      this.setStartSize(1.0F);
      this.setEndSize(0.0F);
      this.setStartAlpha(0.075F);
      this.calculateSteps();
      this.setColorStep(2304);
   }
}
