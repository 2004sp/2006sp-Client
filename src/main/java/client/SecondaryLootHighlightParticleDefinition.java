package client;
final class SecondaryLootHighlightParticleDefinition extends ParticleDefinition {
   SecondaryLootHighlightParticleDefinition() {
      this.setStartVelocity(new Vector3(0, 5, 0));
      this.setEndVelocity(new Vector3(0, 10, 0));
      new Vector3(0, 0, 0);
      this.setLifetime(20);
      this.setStartColor(16776693);
      this.setSpawnCount(1);
      this.setStartSize(3.0F);
      this.setEndSize(0.8F);
      this.setStartAlpha(0.45F);
      this.setEndAlpha(0.045F);
      this.calculateSteps();
      this.setColorStep(16776693);
   }
}
