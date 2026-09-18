package client;
final class BlueEmberParticleDefinition extends ParticleDefinition {
   BlueEmberParticleDefinition() {
      this.setStartVelocity(new Vector3(0, 2, 0));
      this.setEndVelocity(new Vector3(0, 2, 0));
      new Vector3(0, 1, 0);
      this.setLifetime(15);
      this.setStartColor(21196);
      this.setSpawnCount(3);
      this.setStartSize(2.0F);
      this.setEndSize(0.5F);
      this.setStartAlpha(0.0F);
      this.setEndAlpha(0.065F);
      this.calculateSteps();
      this.setColorStep(2304);
   }
}
