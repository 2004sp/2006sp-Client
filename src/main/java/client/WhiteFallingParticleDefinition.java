package client;
final class WhiteFallingParticleDefinition extends ParticleDefinition {
   WhiteFallingParticleDefinition() {
      this.setStartVelocity(new Vector3(0, -3, 0));
      this.setEndVelocity(new Vector3(0, -3, 0));
      new Vector3(0, 0, 0);
      this.setLifetime(19);
      this.setStartColor(16777215);
      this.setSpawnCount(4);
      this.setStartSize(0.75F);
      this.setEndSize(0.0F);
      this.setStartAlpha(0.035F);
      this.calculateSteps();
      this.setColorStep(0);
   }
}
