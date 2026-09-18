package client;
final class DynamicObject extends Renderable {
   private int animationFrame;
   private final int[] childIds;
   private final int varbitId;
   private final int configId;
   private final int tileHeight00;
   private final int tileHeight10;
   private final int tileHeight11;
   private final int tileHeight01;
   private AnimationSequence animationSequence;
   private int frameStartCycle;
   public static Client clientInstance;
   private final int objectId;
   private final int objectType;
   private final int orientation;
   private boolean interpolateAnimation = true;
   @Override
   public final Model getRotatedModel() {
      if (this.interpolateAnimation) {
         DynamicObject dynamicObject = this;
         int localAnimationSequence = -1;
         int animationSequence2 = -1;
         int animationSequence3 = -1;
         int sourceLocalGameCycle = -1;
         int frameIdIndex = dynamicObject.animationFrame + 1;
         if (dynamicObject.animationSequence != null) {
            int localGameCycle;
            if ((localGameCycle = Client.gameCycle - dynamicObject.frameStartCycle) > 100 && dynamicObject.animationSequence.frameStep > 0) {
               localGameCycle = 100;
            }

            while (localGameCycle > dynamicObject.animationSequence.getFrameLength(dynamicObject.animationFrame)) {
               localGameCycle -= dynamicObject.animationSequence.getFrameLength(dynamicObject.animationFrame);
               dynamicObject.animationFrame++;
               frameIdIndex++;
               if (dynamicObject.animationFrame >= dynamicObject.animationSequence.frameCount) {
                  dynamicObject.animationFrame = dynamicObject.animationFrame - dynamicObject.animationSequence.frameStep;
                  frameIdIndex -= dynamicObject.animationSequence.frameStep;
                  if (dynamicObject.animationFrame < 0 || dynamicObject.animationFrame >= dynamicObject.animationSequence.frameCount) {
                     dynamicObject.animationSequence = null;
                     break;
                  }
               }
            }

            dynamicObject.frameStartCycle = Client.gameCycle - localGameCycle;
            if (dynamicObject.animationSequence != null) {
               localAnimationSequence = dynamicObject.animationSequence.frameIds[dynamicObject.animationFrame];
               if (frameIdIndex > dynamicObject.animationSequence.frameIds.length - 1) {
                  frameIdIndex = dynamicObject.animationFrame;
               }

               animationSequence2 = dynamicObject.animationSequence.frameIds[frameIdIndex];
               animationSequence3 = dynamicObject.animationSequence.frameLengths[dynamicObject.animationFrame];
               sourceLocalGameCycle = localGameCycle;
            }
         }

         ObjectDefinition objectDefinition;
         if (dynamicObject.childIds != null) {
            objectDefinition = dynamicObject.resolveChildDefinition();
         } else {
            objectDefinition = ObjectDefinition.lookup(dynamicObject.objectId);
         }

         return objectDefinition == null
            ? null
            : objectDefinition.modelAt(
               dynamicObject.objectType,
               dynamicObject.orientation,
               dynamicObject.tileHeight00,
               dynamicObject.tileHeight10,
               dynamicObject.tileHeight11,
               dynamicObject.tileHeight01,
               localAnimationSequence,
               localAnimationSequence == animationSequence2 ? -1 : animationSequence2,
               animationSequence3,
               sourceLocalGameCycle
            );
      } else {
         int animationSequence4 = -1;
         if (this.animationSequence != null) {
            int gameCycle2;
            if ((gameCycle2 = Client.gameCycle - this.frameStartCycle) > 100 && this.animationSequence.frameStep > 0) {
               gameCycle2 = 100;
            }

            while (gameCycle2 > this.animationSequence.getFrameLength(this.animationFrame)) {
               gameCycle2 -= this.animationSequence.getFrameLength(this.animationFrame);
               this.animationFrame++;
               if (this.animationFrame >= this.animationSequence.frameCount) {
                  this.animationFrame = this.animationFrame - this.animationSequence.frameStep;
                  if (this.animationFrame < 0 || this.animationFrame >= this.animationSequence.frameCount) {
                     this.animationSequence = null;
                     break;
                  }
               }
            }

            this.frameStartCycle = Client.gameCycle - gameCycle2;
            if (this.animationSequence != null) {
               animationSequence4 = this.animationSequence.frameIds[this.animationFrame];
            }
         }

         ObjectDefinition objectDefinition2;
         if (this.childIds != null) {
            objectDefinition2 = this.resolveChildDefinition();
         } else {
            objectDefinition2 = ObjectDefinition.lookup(this.objectId);
         }

         return objectDefinition2 == null
            ? null
            : objectDefinition2.modelAt(this.objectType, this.orientation, this.tileHeight00, this.tileHeight10, this.tileHeight11, this.tileHeight01, animationSequence4, -1, -1, -1);
      }
   }
   private ObjectDefinition resolveChildDefinition() {
      int childIdIndex = -1;
      if (this.varbitId != -1) {
         VarbitDefinition varbitDefinition;
         int varpIndex = (varbitDefinition = VarbitDefinition.definitions[this.varbitId]).index;
         int leastSignificantBit = varbitDefinition.leastSignificantBit;
         childIdIndex = varbitDefinition.mostSignificantBit;
         childIdIndex = Client.bitMasks[childIdIndex - leastSignificantBit];
         childIdIndex = clientInstance.varps[varpIndex] >> leastSignificantBit & childIdIndex;
      } else if (this.configId != -1) {
         childIdIndex = clientInstance.varps[this.configId];
      }

      return childIdIndex >= 0 && childIdIndex < this.childIds.length && this.childIds[childIdIndex] != -1 ? ObjectDefinition.lookup(this.childIds[childIdIndex]) : null;
   }

   public DynamicObject(int newObjectId, int newOrientation, int newObjectType, int newTileHeight10, int newTileHeight11, int newTileHeight00, int newTileHeight01, int emoteAnimation, boolean flag) {
      this.objectId = newObjectId;
      this.objectType = newObjectType;
      this.orientation = newOrientation;
      this.tileHeight00 = newTileHeight00;
      this.tileHeight10 = newTileHeight10;
      this.tileHeight11 = newTileHeight11;
      this.tileHeight01 = newTileHeight01;
      if (emoteAnimation != -1) {
         this.animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(emoteAnimation)];
         this.animationFrame = 0;
         this.frameStartCycle = Client.gameCycle;
         if (flag && this.animationSequence.frameStep != -1) {
            this.animationFrame = (int)(Math.random() * this.animationSequence.frameCount);
            this.frameStartCycle = this.frameStartCycle - (int)(Math.random() * this.animationSequence.getFrameLength(this.animationFrame));
         }
      }

      ObjectDefinition objectDefinition = ObjectDefinition.lookup(this.objectId);
      this.varbitId = objectDefinition.varbitId;
      this.configId = objectDefinition.configId;
      this.childIds = objectDefinition.childIds;
   }
}
