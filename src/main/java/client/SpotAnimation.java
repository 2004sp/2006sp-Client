package client;
final class SpotAnimation extends Renderable {
   public final int plane;
   public final int modelId;
   public final int animationId;
   public final int resizeXY;
   public final int resizeZ;
   public boolean finished = false;
   private final SpotAnimationDefinition definition;
   private int rotation;
   private int modelBrightness;
   private int modelShadow;

   public SpotAnimation(int newPlane, int newResizeZ, int scalarArgument, int definitionIndex, int newResizeXY, int newAnimationId, int newModelId) {
      this.definition = SpotAnimationDefinition.definitions[definitionIndex];
      this.plane = newPlane;
      this.modelId = newModelId;
      this.animationId = newAnimationId;
      this.resizeXY = newResizeXY;
      this.resizeZ = newResizeZ + scalarArgument;
      this.finished = false;
   }
   @Override
   public final Model getRotatedModel() {
      Model model;
      if ((model = this.definition.getModel()) == null) {
         return null;
      }

      int frameId = this.definition.animationSequence.frameIds[this.rotation];
      model = new Model(true, AnimationFrame.isNullFrame(frameId), false, model);
      if (!this.finished) {
         model.skin();
         if (Client.smoothAnimations && this.modelShadow != -1) {
            model.applyInterpolatedAnimation(frameId, this.definition.animationSequence.frameIds[this.modelShadow], this.modelBrightness, this.definition.animationSequence.frameLengths[this.rotation]);
         } else {
            model.applyAnimationFrame(frameId);
         }

         model.triangleSkin = null;
         model.vectorSkin = null;
      }

      if (this.definition.resizeX != 128 || this.definition.resizeY != 128) {
         model.scale(this.definition.resizeX, this.definition.resizeX, this.definition.resizeY);
      }

      if (this.definition.rotation != 0) {
         if (this.definition.rotation == 90) {
            model.rotateY90();
         }

         if (this.definition.rotation == 180) {
            model.rotateY90();
            model.rotateY90();
         }

         if (this.definition.rotation == 270) {
            model.rotateY90();
            model.rotateY90();
            model.rotateY90();
         }
      }

      model.light(64 + this.definition.ambient, 850 + this.definition.contrast, -30, -50, -30, true);
      return model;
   }
   public final void advanceAnimation(int animationCycleDelta) {
      this.modelBrightness += animationCycleDelta;

      while (this.modelBrightness > this.definition.animationSequence.getFrameLength(this.rotation)) {
         this.modelBrightness = this.modelBrightness - (this.definition.animationSequence.getFrameLength(this.rotation) + 1);
         this.rotation++;
         if (this.rotation >= this.definition.animationSequence.frameCount && (this.rotation < 0 || this.rotation >= this.definition.animationSequence.frameCount)) {
            this.rotation = 0;
            this.finished = true;
         }

         if (Client.smoothAnimations) {
            this.modelShadow = this.rotation + 1;
            if (this.modelShadow >= this.definition.animationSequence.frameCount) {
               this.modelShadow = -1;
            }
         }
      }
   }
}
