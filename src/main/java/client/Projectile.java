package client;
final class Projectile extends Renderable {
   public final int startCycle;
   public final int endCycle;
   private double velocityX;
   private double velocityY;
   private double horizontalSpeed;
   private double velocityZ;
   private double verticalAcceleration;
   private boolean moving = false;
   private final int startX;
   private final int startY;
   private final int startHeight;
   public final int endHeight;
   public double currentX;
   public double currentY;
   public double currentHeight;
   private final int startSlope;
   private final int startDistance;
   public final int targetIndex;
   private final SpotAnimationDefinition spotAnimation;
   private int animationFrame;
   private int animationFrameCycle;
   public int yaw;
   private int pitch;
   public final int plane;
   public final void trackTarget(int gameCycle, int worldY, int endHeight, int worldX) {
      if (!this.moving) {
         double calculation = worldX - this.startX;
         double calculation2 = worldY - this.startY;
         double localSqrt = Math.sqrt(calculation * calculation + calculation2 * calculation2);
         this.currentX = this.startX + calculation * this.startDistance / localSqrt;
         this.currentY = this.startY + calculation2 * this.startDistance / localSqrt;
         this.currentHeight = this.startHeight;
      }

      double localEndCycle = this.endCycle + 1 - gameCycle;
      this.velocityX = (worldX - this.currentX) / localEndCycle;
      this.velocityY = (worldY - this.currentY) / localEndCycle;
      this.horizontalSpeed = Math.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY);
      if (!this.moving) {
         this.velocityZ = -this.horizontalSpeed * Math.tan(this.startSlope * 0.02454369);
      }

      this.verticalAcceleration = 2.0 * (endHeight - this.currentHeight - this.velocityZ * localEndCycle) / (localEndCycle * localEndCycle);
   }
   @Override
   public final Model getRotatedModel() {
      Model model;
      if ((model = this.spotAnimation.getModel()) == null) {
         return null;
      }

      int frameIndex = -1;
      if (this.spotAnimation.animationSequence != null) {
         frameIndex = this.spotAnimation.animationSequence.frameIds[this.animationFrame];
      }

      model = new Model(true, AnimationFrame.isNullFrame(frameIndex), false, model);
      if (frameIndex != -1) {
         model.skin();
         model.applyAnimationFrame(frameIndex);
         model.triangleSkin = null;
         model.vectorSkin = null;
      }

      if (this.spotAnimation.resizeX != 128 || this.spotAnimation.resizeY != 128) {
         model.scale(this.spotAnimation.resizeX, this.spotAnimation.resizeX, this.spotAnimation.resizeY);
      }

      int pitch = this.pitch;
      Model sourceModel = model;
      int sINEEntry = Model.SINE[pitch];
      pitch = Model.COSINE[pitch];

      for (int verticesYIndex = 0; verticesYIndex < sourceModel.vertexCount; verticesYIndex++) {
         int localVerticesY = sourceModel.verticesY[verticesYIndex] * pitch - sourceModel.verticesZ[verticesYIndex] * sINEEntry >> 16;
         sourceModel.verticesZ[verticesYIndex] = sourceModel.verticesY[verticesYIndex] * sINEEntry + sourceModel.verticesZ[verticesYIndex] * pitch >> 16;
         sourceModel.verticesY[verticesYIndex] = localVerticesY;
      }

      model.light(64 + this.spotAnimation.ambient, 850 + this.spotAnimation.contrast, -30, -50, -30, true);
      return model;
   }

   public Projectile(int newStartSlope, int newEndHeight, int newStartCycle, int newEndCycle, int newStartDistance, int newPlane, int newStartHeight, int newStartY, int newStartX, int newTargetIndex, int definitionIndex) {
      this.spotAnimation = SpotAnimationDefinition.definitions[definitionIndex];
      this.plane = newPlane;
      this.startX = newStartX;
      this.startY = newStartY;
      this.startHeight = newStartHeight;
      this.startCycle = newStartCycle;
      this.endCycle = newEndCycle;
      this.startSlope = newStartSlope;
      this.startDistance = newStartDistance;
      this.targetIndex = newTargetIndex;
      this.endHeight = newEndHeight;
      this.moving = false;
   }
   public final void move(int animationCycleDelta) {
      this.moving = true;
      this.currentX = this.currentX + this.velocityX * animationCycleDelta;
      this.currentY = this.currentY + this.velocityY * animationCycleDelta;
      this.currentHeight = this.currentHeight + (this.velocityZ * animationCycleDelta + 0.5 * this.verticalAcceleration * animationCycleDelta * animationCycleDelta);
      this.velocityZ = this.velocityZ + this.verticalAcceleration * animationCycleDelta;
      this.yaw = (int)(Math.atan2(this.velocityX, this.velocityY) * 325.949) + 1024 & 2047;
      this.pitch = (int)(Math.atan2(this.velocityZ, this.horizontalSpeed) * 325.949) & 2047;
      if (this.spotAnimation.animationSequence != null) {
         this.animationFrameCycle += animationCycleDelta;

         while (this.animationFrameCycle > this.spotAnimation.animationSequence.getFrameLength(this.animationFrame)) {
            this.animationFrameCycle = this.animationFrameCycle - (this.spotAnimation.animationSequence.getFrameLength(this.animationFrame) + 1);
            this.animationFrame++;
            if (this.animationFrame >= this.spotAnimation.animationSequence.frameCount) {
               this.animationFrame = 0;
            }
         }
      }
   }
}
