package client;
final class BlackSmokeParticleDefinition extends ParticleDefinition {
   BlackSmokeParticleDefinition() {
      this.setStartVelocity(new Vector3(0, 2, 0));
      this.setEndVelocity(new Vector3(0, 2, 0));
      new Vector3(0, 16, 0);
      this.setLifetime(19);
      this.setStartColor(0);
      this.setSpawnCount(3);
      this.setStartSize(0.7F);
      this.setEndSize(0.5F);
      this.setStartAlpha(0.0F);
      this.setEndAlpha(0.035F);
      this.calculateSteps();
      this.setColorStep(0);
   }
}
