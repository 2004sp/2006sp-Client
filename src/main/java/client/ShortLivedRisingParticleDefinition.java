package client;
final class ShortLivedRisingParticleDefinition extends ParticleDefinition {
   ShortLivedRisingParticleDefinition() {
      this.setStartVelocity(new Vector3(0, 2, 0));
      this.setEndVelocity(new Vector3(0, 2, 0));
      new Vector3(0, 0, 0);
      this.setLifetime(15);
      this.setStartAlpha(0.035F);
      this.setEndAlpha(0.0F);
      this.setSpawnCount(1);
      this.setStartSize(0.2F);
      this.calculateSteps();
   }
}
