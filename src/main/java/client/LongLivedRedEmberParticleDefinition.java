package client;
final class LongLivedRedEmberParticleDefinition extends ParticleDefinition {
   LongLivedRedEmberParticleDefinition() {
      this.setStartVelocity(new Vector3(0, 2, 0));
      this.setEndVelocity(new Vector3(0, 2, 0));
      new Vector3(0, 1, 0);
      this.setLifetime(19);
      this.setStartColor(16713728);
      this.setSpawnCount(4);
      this.setStartSize(2.0F);
      this.setEndSize(0.5F);
      this.setStartAlpha(0.0F);
      this.setEndAlpha(0.045F);
      this.calculateSteps();
      this.setColorStep(2304);
   }
}
