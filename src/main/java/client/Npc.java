package client;
public final class Npc extends Actor {
   public NpcDefinition definition;
   @Override
   public final Model getRotatedModel() {
      if (this.definition == null) {
         return null;
      }

      Npc npc = this;
      Model model;
      if (super.emoteAnimation >= 0 && npc.animationDelay == 0) {
         AnimationSequence animationSequence;
         int npcId = (animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(npc.emoteAnimation, (int)npc.definition.id)]).frameIds[npc.emoteFrame];
         int frameId = animationSequence.frameIds[npc.nextEmoteFrame];
         int frameLength = animationSequence.frameLengths[npc.emoteFrame];
         int emoteFrameCycle = npc.emoteFrameCycle;
         int npcIdOrSequences = -1;
         if (npc.movementAnimation >= 0 && npc.movementAnimation != npc.idleAnimationId) {
            npcIdOrSequences = AnimationSequence.sequences[AnimationSequence.remapId(npc.movementAnimation, (int)npc.definition.id)].frameIds[npc.movementFrame];
         }

         model = npc.definition
            .getAnimatedModelInterpolated(npcIdOrSequences, npcId, frameId, frameLength, emoteFrameCycle, AnimationSequence.sequences[AnimationSequence.remapId(npc.emoteAnimation, (int)npc.definition.id)].interleaveOrder);
      } else {
         int localNpcId = -1;
         int frameIds2 = -1;
         int frameLengths2 = 0;
         int localMovementFrameCycle = 0;
         if (npc.movementAnimation >= 0) {
            AnimationSequence animationSequence2;
            localNpcId = (animationSequence2 = AnimationSequence.sequences[AnimationSequence.remapId(npc.movementAnimation, (int)npc.definition.id)]).frameIds[npc.movementFrame];
            frameIds2 = animationSequence2.frameIds[npc.nextMovementFrame];
            frameLengths2 = animationSequence2.frameLengths[npc.movementFrame];
            localMovementFrameCycle = npc.movementFrameCycle;
         }

         model = npc.definition.getAnimatedModelInterpolated(-1, localNpcId, frameIds2, frameLengths2, localMovementFrameCycle, null);
      }

      Model sourceModel = model;
      if (model == null) {
         return null;
      }

      super.height = sourceModel.modelHeight;
      SpotAnimationDefinition spotAnimationDefinition;
      Model model2;
      if (super.graphicId != -1 && super.graphicFrame != -1 && (model2 = (spotAnimationDefinition = SpotAnimationDefinition.definitions[super.graphicId]).getModel()) != null) {
         int animationSequence3 = spotAnimationDefinition.animationSequence.frameIds[super.graphicFrame];
         int animationSequence4 = spotAnimationDefinition.animationSequence.frameIds[super.nextGraphicFrame];
         int animationSequence5 = spotAnimationDefinition.animationSequence.frameLengths[super.graphicFrame];
         int graphicFrameCycle = super.graphicFrameCycle;
         Model model3;
         (model3 = new Model(true, AnimationFrame.isNullFrame(animationSequence3), false, model2)).translate(0, -super.graphicHeight, 0);
         model3.skin();
         if (Client.smoothAnimations && animationSequence4 != -1) {
            model3.applyInterpolatedAnimation(animationSequence3, animationSequence4, graphicFrameCycle, animationSequence5);
         } else {
            model3.applyAnimationFrame(animationSequence3);
         }

         model3.triangleSkin = null;
         model3.vectorSkin = null;
         if (spotAnimationDefinition.resizeX != 128 || spotAnimationDefinition.resizeY != 128) {
            model3.scale(spotAnimationDefinition.resizeX, spotAnimationDefinition.resizeX, spotAnimationDefinition.resizeY);
         }

         model3.light(64 + spotAnimationDefinition.ambient, 850 + spotAnimationDefinition.contrast, -30, -50, -30, true);
         Model[] values = new Model[]{sourceModel, model3};
         sourceModel = new Model(values);
      }

      if (this.definition.size == 1) {
         sourceModel.singleTile = true;
      }

      return sourceModel;
   }

   @Override
   public final boolean isVisible() {
      return this.definition != null;
   }
}
