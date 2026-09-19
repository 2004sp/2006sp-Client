package client;
public final class Player extends Actor {
   public int privelage;
   public int accountMode = 0;
   public int donatorStatus = 0;
   private long lastModelHash = -1L;
   public NpcDefinition npcDefinition;
   boolean unanimated = false;
   final int[] appearanceColors = new int[5];
   public int team;
   private int gender;
   public String name;
   static LruCache appearanceModelCache = new LruCache(260);
   private static LruCache appearanceModelCacheMode1 = new LruCache(260);
   private static LruCache appearanceModelCacheMode2 = new LruCache(260);
   private static LruCache appearanceModelCacheMode3 = new LruCache(260);
   public int combatLevel;
   public int headIcon;
   public int skullIcon;
   public int attachedModelStartCycle;
   int attachedModelEndCycle;
   int attachedModelBaseHeight;
   boolean visible = false;
   int attachedModelX;
   int attachedModelHeight;
   int attachedModelY;
   Model attachedModel;
   public final int[] equipment = new int[12];
   private long appearanceHash;
   int attachedModelMinX;
   int attachedModelMinY;
   int attachedModelMaxX;
   int attachedModelMaxY;
   int skill;
   public int modelMode = 0;
   public static int mode1ScaleX = 110;
   public static int mode1ScaleY = 128;
   public static int mode1ScaleZ = 104;
   private static int mode2ScaleX = 180;
   private static int mode2ScaleY = 180;
   private static int mode2ScaleZ = 180;
   public static int mode3Rotation = 200;
   private boolean npcTransformActive = false;
   @Override
   public final Model getRotatedModel() {
      if (!this.visible) {
         return null;
      }

      Player player = this;
      int localFrameIds = -1;
      int localFrameLengths = -1;
      int localEmoteFrameCycle = -1;
      Model model;
      if (player.npcDefinition != null) {
         int frameId = -1;
         if (player.emoteAnimation >= 0 && player.animationDelay == 0) {
            AnimationSequence animationSequence;
            frameId = (animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(player.emoteAnimation)]).frameIds[player.emoteFrame];
            if (Client.smoothAnimations && player.nextEmoteFrame != -1) {
               localFrameIds = animationSequence.frameIds[player.emoteFrame];
               localFrameLengths = animationSequence.frameLengths[player.emoteFrame];
               localEmoteFrameCycle = player.emoteFrameCycle;
            }
         } else if (player.movementAnimation >= 0) {
            AnimationSequence animationSequence2;
            frameId = (animationSequence2 = AnimationSequence.sequences[AnimationSequence.remapId(player.movementAnimation)]).frameIds[player.movementFrame];
            if (Client.smoothAnimations && player.nextMovementFrame != -1) {
               localFrameIds = animationSequence2.frameIds[player.nextMovementFrame];
               localFrameLengths = animationSequence2.frameLengths[player.movementFrame];
               localEmoteFrameCycle = player.movementFrameCycle;
            }
         }

         model = player.npcDefinition.getAnimatedModelInterpolated(-1, frameId, localFrameIds, localFrameLengths, localEmoteFrameCycle, null);
      } else {
         buildAppearanceModel: {
            long lastModelHashOrAppearanceHash = player.appearanceHash;
            int frameIndex = -1;
            int frameIndex2 = -1;
            int localOffHandModel = -1;
            int localMainHandModel = -1;
            if (player.emoteAnimation >= 0 && player.animationDelay == 0) {
               AnimationSequence animationSequence3;
               frameIndex = (animationSequence3 = AnimationSequence.sequences[AnimationSequence.remapId(player.emoteAnimation)]).frameIds[player.emoteFrame];
               if (Client.smoothAnimations && player.nextEmoteFrame != -1) {
                  localFrameIds = animationSequence3.frameIds[player.nextEmoteFrame];
                  localFrameLengths = animationSequence3.frameLengths[player.emoteFrame];
                  localEmoteFrameCycle = player.emoteFrameCycle;
               }

               if (player.movementAnimation >= 0 && player.movementAnimation != player.idleAnimationId) {
                  frameIndex2 = AnimationSequence.sequences[AnimationSequence.remapId(player.movementAnimation)].frameIds[player.movementFrame];
               }

               if (animationSequence3.offHandModel >= 0) {
                  localOffHandModel = animationSequence3.offHandModel;
                  lastModelHashOrAppearanceHash += localOffHandModel - player.equipment[5] << 40;
               }

               if (animationSequence3.mainHandModel >= 0) {
                  localMainHandModel = animationSequence3.mainHandModel;
                  lastModelHashOrAppearanceHash += localMainHandModel - player.equipment[3] << 48;
               }
            } else if (player.movementAnimation >= 0) {
               AnimationSequence animationSequence4;
               frameIndex = (animationSequence4 = AnimationSequence.sequences[AnimationSequence.remapId(player.movementAnimation)]).frameIds[player.movementFrame];
               if (Client.smoothAnimations && player.nextMovementFrame != -1) {
                  localFrameIds = animationSequence4.frameIds[player.nextMovementFrame];
                  localFrameLengths = animationSequence4.frameLengths[player.movementFrame];
                  localEmoteFrameCycle = player.movementFrameCycle;
               }
            }

            Model node = null;
            if (player.modelMode == 0) {
               node = (Model)appearanceModelCache.get(lastModelHashOrAppearanceHash);
            } else if (player.modelMode == 1) {
               node = (Model)appearanceModelCacheMode1.get(lastModelHashOrAppearanceHash);
            } else if (player.modelMode == 2) {
               node = (Model)appearanceModelCacheMode2.get(lastModelHashOrAppearanceHash);
            } else if (player.modelMode == 3) {
               node = (Model)appearanceModelCacheMode3.get(lastModelHashOrAppearanceHash);
            }

            if (node == null) {
               boolean flag = false;

               for (int equipmentIndex2 = 0; equipmentIndex2 < 12; equipmentIndex2++) {
                  int equipmentEntry = player.equipment[equipmentIndex2];
                  if (localMainHandModel >= 0 && equipmentIndex2 == 3) {
                     equipmentEntry = localMainHandModel;
                  }

                  if (localOffHandModel >= 0 && equipmentIndex2 == 5) {
                     equipmentEntry = localOffHandModel;
                  }

                  if (equipmentEntry >= 256 && equipmentEntry < 512 && !IdentityKit.kits[equipmentEntry - 256].bodyLoaded()) {
                     flag = true;
                  }

                  if (equipmentEntry >= 512 && !ItemDefinition.lookup(equipmentEntry - 512).isEquippedModelCached(player.gender)) {
                     flag = true;
                  }
               }

               if (flag) {
                  if (player.lastModelHash != -1L) {
                     if (player.modelMode == 0) {
                        node = (Model)appearanceModelCache.get(player.lastModelHash);
                     } else if (player.modelMode == 1) {
                        node = (Model)appearanceModelCacheMode1.get(player.lastModelHash);
                     } else if (player.modelMode == 2) {
                        node = (Model)appearanceModelCacheMode2.get(player.lastModelHash);
                     } else if (player.modelMode == 3) {
                        node = (Model)appearanceModelCacheMode3.get(player.lastModelHash);
                     }
                  }

                  if (node == null) {
                     model = null;
                     break buildAppearanceModel;
                  }
               }
            }

            if (node == null) {
               Model[] values = new Model[12];
               int scalar = 0;

               for (int equipmentIndex = 0; equipmentIndex < 12; equipmentIndex++) {
                  int equipment2 = player.equipment[equipmentIndex];
                  if (localMainHandModel >= 0 && equipmentIndex == 3) {
                     equipment2 = localMainHandModel;
                  }

                  if (localOffHandModel >= 0 && equipmentIndex == 5) {
                     equipment2 = localOffHandModel;
                  }

                  Model model2;
                  if (equipment2 >= 256 && equipment2 < 512 && (model2 = IdentityKit.kits[equipment2 - 256].bodyModel()) != null) {
                     values[scalar++] = model2;
                  }

                  if (equipment2 >= 512 && (model2 = ItemDefinition.lookup(equipment2 - 512).getEquippedModel(player.gender)) != null) {
                     values[scalar++] = model2;
                  }
               }

               node = new Model(scalar, values);

               for (int appearanceColorIndex = 0; appearanceColorIndex < 5; appearanceColorIndex++) {
                  if (player.appearanceColors[appearanceColorIndex] != 0) {
                     node.recolor(Client.playerBodyColors[appearanceColorIndex][0], Client.playerBodyColors[appearanceColorIndex][player.appearanceColors[appearanceColorIndex]]);
                     if (appearanceColorIndex == 1) {
                        node.recolor(Client.secondaryPlayerBodyColors[0], Client.secondaryPlayerBodyColors[player.appearanceColors[appearanceColorIndex]]);
                     }
                  }
               }

               node.skin();
               node.light(64, 850, -30, -50, -30, true);
               if (player.modelMode == 1) {
                  node.scale(mode1ScaleX, mode1ScaleY, mode1ScaleZ);
               } else if (player.modelMode == 2) {
                  node.scale(mode2ScaleX, mode2ScaleY, mode2ScaleZ);
               }

               if (player.modelMode == 3) {
                  node.setTriangleAlpha(mode3Rotation);
               }

               if (player.modelMode == 0) {
                  appearanceModelCache.put(node, lastModelHashOrAppearanceHash);
               } else if (player.modelMode == 1) {
                  appearanceModelCacheMode1.put(node, lastModelHashOrAppearanceHash);
               } else if (player.modelMode == 2) {
                  appearanceModelCacheMode2.put(node, lastModelHashOrAppearanceHash);
               } else if (player.modelMode == 3) {
                  appearanceModelCacheMode3.put(node, lastModelHashOrAppearanceHash);
               }

               player.lastModelHash = lastModelHashOrAppearanceHash;
            }

            if (player.unanimated) {
               model = node;
            } else {
               Model model3;
               (model3 = Model.sharedModel).copyForAnimation(node, AnimationFrame.isNullFrame(frameIndex) & AnimationFrame.isNullFrame(frameIndex2));
               if (frameIndex != -1 && frameIndex2 != -1) {
                  model3.applyAnimationFrames(AnimationSequence.sequences[AnimationSequence.remapId(player.emoteAnimation)].interleaveOrder, frameIndex2, frameIndex);
               } else if (frameIndex != -1) {
                  if (Client.smoothAnimations) {
                     model3.applyInterpolatedAnimation(frameIndex, localFrameIds, localEmoteFrameCycle, localFrameLengths);
                  } else {
                     model3.applyAnimationFrame(frameIndex);
                  }
               }

               model3.calculateBoundsCylinder();
               model3.triangleSkin = null;
               model3.vectorSkin = null;
               model = model3;
            }
         }
      }

      Model model4 = model;
      if (model == null) {
         return null;
      }

      super.height = model4.modelHeight;
      model4.singleTile = true;
      if (this.unanimated) {
         return model4;
      }

      SpotAnimationDefinition spotAnimationDefinition;
      Model model5;
      if (super.graphicId != -1 && super.graphicFrame != -1 && (model5 = (spotAnimationDefinition = SpotAnimationDefinition.definitions[super.graphicId]).getModel()) != null) {
         Model model6 = new Model(true, AnimationFrame.isNullFrame(super.graphicFrame), false, model5);
         localEmoteFrameCycle = spotAnimationDefinition.animationSequence.frameIds[super.nextGraphicFrame];
         int animationSequence5 = spotAnimationDefinition.animationSequence.frameLengths[super.graphicFrame];
         int graphicFrameCycle = super.graphicFrameCycle;
         model6.translate(0, -super.graphicHeight, 0);
         model6.skin();
         if (Client.smoothAnimations && localEmoteFrameCycle != -1) {
            model6.applyInterpolatedAnimation(spotAnimationDefinition.animationSequence.frameIds[super.graphicFrame], localEmoteFrameCycle, graphicFrameCycle, animationSequence5);
         } else {
            model6.applyAnimationFrame(spotAnimationDefinition.animationSequence.frameIds[super.graphicFrame]);
         }

         model6.triangleSkin = null;
         model6.vectorSkin = null;
         if (spotAnimationDefinition.resizeX != 128 || spotAnimationDefinition.resizeY != 128) {
            model6.scale(spotAnimationDefinition.resizeX, spotAnimationDefinition.resizeX, spotAnimationDefinition.resizeY);
         }

         model6.light(64 + spotAnimationDefinition.ambient, 850 + spotAnimationDefinition.contrast, -30, -50, -30, true);
         if (this.modelMode == 1) {
            model6.scale(mode1ScaleX, mode1ScaleY, mode1ScaleZ);
         } else if (this.modelMode == 2) {
            model6.scale(mode2ScaleX, mode2ScaleY, mode2ScaleZ);
         }

         Model[] model8 = new Model[]{model4, model6};
         model4 = new Model(model8);
      }

      if (this.attachedModel != null) {
         if (Client.gameCycle >= this.attachedModelEndCycle) {
            this.attachedModel = null;
         }

         if (Client.gameCycle >= this.attachedModelStartCycle && Client.gameCycle < this.attachedModelEndCycle) {
            Model model7 = this.attachedModel;
            this.attachedModel.translate(this.attachedModelX - super.worldX, this.attachedModelHeight - this.attachedModelBaseHeight, this.attachedModelY - super.worldY);
            if (super.targetOrientation == 512) {
               model7.rotateY90();
               model7.rotateY90();
               model7.rotateY90();
            } else if (super.targetOrientation == 1024) {
               model7.rotateY90();
               model7.rotateY90();
            } else if (super.targetOrientation == 1536) {
               model7.rotateY90();
            }

            Model[] values2 = new Model[]{model4, model7};
            model4 = new Model(values2);
            if (super.targetOrientation == 512) {
               model7.rotateY90();
            } else if (super.targetOrientation == 1024) {
               model7.rotateY90();
               model7.rotateY90();
            } else if (super.targetOrientation == 1536) {
               model7.rotateY90();
               model7.rotateY90();
               model7.rotateY90();
            }

            model7.translate(super.worldX - this.attachedModelX, this.attachedModelBaseHeight - this.attachedModelHeight, super.worldY - this.attachedModelY);
         }
      }

      model4.singleTile = true;
      return model4;
   }
   public final void updatePlayer(Buffer buffer) {
      buffer.currentPosition = 0;
      this.gender = buffer.readUnsignedByte();
      this.headIcon = buffer.readUnsignedByte();
      this.skullIcon = buffer.readUnsignedByte();
      this.npcDefinition = null;
      this.team = 0;

      for (int equipmentIndex2 = 0; equipmentIndex2 < 12; equipmentIndex2++) {
         int teamOrReadUnsignedByte;
         if ((teamOrReadUnsignedByte = buffer.readUnsignedByte()) == 0) {
            this.equipment[equipmentIndex2] = 0;
         } else {
            int decodedUnsignedByte = buffer.readUnsignedByte();
            this.equipment[equipmentIndex2] = (teamOrReadUnsignedByte << 8) + decodedUnsignedByte;
            if (equipmentIndex2 == 0 && this.equipment[0] == 65535) {
               teamOrReadUnsignedByte = buffer.readUnsignedShort();
               this.npcDefinition = NpcDefinition.lookup(teamOrReadUnsignedByte);
               this.npcTransformActive = true;
               super.idleAnimationId = this.npcDefinition.idleAnimationId;
               super.walkAnimationId = this.npcDefinition.walkAnimationId;
               super.turnAroundAnimationId = this.npcDefinition.turnAroundAnimationId;
               super.standTurnAnimationId = this.npcDefinition.walkAnimationId;
               super.turnRightAnimationId = this.npcDefinition.turnRightAnimationId;
               super.turnLeftAnimationId = this.npcDefinition.turnLeftAnimationId;
               super.runAnimationId = this.npcDefinition.walkAnimationId;
               super.turnSpeed = this.npcDefinition.turnSpeed;
               break;
            }

            if ((equipmentIndex2 != 0 || this.equipment[0] != 65535) && this.npcTransformActive) {
               this.npcTransformActive = false;
            }

            if (this.equipment[equipmentIndex2] >= 512) {
               int itemId = this.equipment[equipmentIndex2] - 512;
               if (itemId == 4513 || itemId == 4514) {
                  this.team = 1;
               } else if (itemId == 4515 || itemId == 4516) {
                  this.team = 2;
               } else if (itemId < ItemDefinition.definitionCount
                  && (teamOrReadUnsignedByte = ItemDefinition.lookup(itemId).teamIndex) != 0) {
                  this.team = teamOrReadUnsignedByte;
               }
            }
         }
      }

      for (int playerBodyColorIndex = 0; playerBodyColorIndex < 5; playerBodyColorIndex++) {
         int appearanceColorOrReadUnsignedByte;
         if ((appearanceColorOrReadUnsignedByte = buffer.readUnsignedByte()) < 0 || appearanceColorOrReadUnsignedByte >= Client.playerBodyColors[playerBodyColorIndex].length) {
            appearanceColorOrReadUnsignedByte = 0;
         }

         this.appearanceColors[playerBodyColorIndex] = appearanceColorOrReadUnsignedByte;
      }

      if (this.npcTransformActive) {
         buffer.readUnsignedShort();
         buffer.readUnsignedShort();
         buffer.readUnsignedShort();
         buffer.readUnsignedShort();
         buffer.readUnsignedShort();
         buffer.readUnsignedShort();
         buffer.readUnsignedShort();
      } else {
         super.idleAnimationId = buffer.readUnsignedShort();
         if (super.idleAnimationId == 65535) {
            super.idleAnimationId = -1;
         }

         super.standTurnAnimationId = buffer.readUnsignedShort();
         if (super.standTurnAnimationId == 65535) {
            super.standTurnAnimationId = -1;
         }

         super.walkAnimationId = buffer.readUnsignedShort();
         if (super.walkAnimationId == 65535) {
            super.walkAnimationId = -1;
         }

         super.turnAroundAnimationId = buffer.readUnsignedShort();
         if (super.turnAroundAnimationId == 65535) {
            super.turnAroundAnimationId = -1;
         }

         super.turnRightAnimationId = buffer.readUnsignedShort();
         if (super.turnRightAnimationId == 65535) {
            super.turnRightAnimationId = -1;
         }

         super.turnLeftAnimationId = buffer.readUnsignedShort();
         if (super.turnLeftAnimationId == 65535) {
            super.turnLeftAnimationId = -1;
         }

         super.runAnimationId = buffer.readUnsignedShort();
         if (super.runAnimationId == 65535) {
            super.runAnimationId = -1;
         }
      }

      this.name = NameUtils.formatDisplayName(NameUtils.decodeBase37(buffer.readLong()));
      this.combatLevel = buffer.readUnsignedByte();
      this.privelage = buffer.readUnsignedByte();
      this.donatorStatus = buffer.readUnsignedByte();
      this.accountMode = buffer.readUnsignedByte();
      this.modelMode = 0;
      if (Client.customPlayerModelModeEnabled) {
         this.modelMode = buffer.readUnsignedByte();
      }

      this.skill = buffer.readUnsignedShort();
      this.visible = true;
      this.appearanceHash = 0L;

      for (int equipmentIndex = 0; equipmentIndex < 12; equipmentIndex++) {
         this.appearanceHash <<= 4;
         if (this.equipment[equipmentIndex] >= 256) {
            this.appearanceHash = this.appearanceHash + (this.equipment[equipmentIndex] - 256);
         }
      }

      if (this.equipment[0] >= 256) {
         this.appearanceHash = this.appearanceHash + (this.equipment[0] - 256 >> 4);
      }

      if (this.equipment[1] >= 256) {
         this.appearanceHash = this.appearanceHash + (this.equipment[1] - 256 >> 8);
      }

      for (int appearanceColorIndex = 0; appearanceColorIndex < 5; appearanceColorIndex++) {
         this.appearanceHash <<= 3;
         this.appearanceHash = this.appearanceHash + this.appearanceColors[appearanceColorIndex];
      }

      this.appearanceHash <<= 1;
      this.appearanceHash = this.appearanceHash + this.gender;
   }

   @Override
   public final boolean isVisible() {
      return this.visible;
   }
   public final Model getHeadModel() {
      if (!this.visible) {
         return null;
      }

      if (this.npcDefinition != null) {
         return this.npcDefinition.model();
      }

      boolean flag = false;

      for (int equipmentIndex2 = 0; equipmentIndex2 < 12; equipmentIndex2++) {
         int localEquipment;
         if ((localEquipment = this.equipment[equipmentIndex2]) >= 256 && localEquipment < 512 && !IdentityKit.kits[localEquipment - 256].headLoaded()) {
            flag = true;
         }

         if (localEquipment >= 512 && !ItemDefinition.lookup(localEquipment - 512).isDialogueModelCached(this.gender)) {
            flag = true;
         }
      }

      if (flag) {
         return null;
      }

      Model[] values = new Model[12];
      int scalar = 0;

      for (int equipmentIndex = 0; equipmentIndex < 12; equipmentIndex++) {
         int equipment2;
         if ((equipment2 = this.equipment[equipmentIndex]) >= 256 && equipment2 < 512) {
            Model model = IdentityKit.kits[equipment2 - 256].headModel();
            values[scalar++] = model;
         }

         Model model3;
         if (equipment2 >= 512 && (model3 = ItemDefinition.lookup(equipment2 - 512).getChatEquipModel(this.gender)) != null) {
            values[scalar++] = model3;
         }
      }

      Model model2 = new Model(scalar, values);

      for (int appearanceColorIndex = 0; appearanceColorIndex < 5; appearanceColorIndex++) {
         if (this.appearanceColors[appearanceColorIndex] != 0) {
            model2.recolor(Client.playerBodyColors[appearanceColorIndex][0], Client.playerBodyColors[appearanceColorIndex][this.appearanceColors[appearanceColorIndex]]);
            if (appearanceColorIndex == 1) {
               model2.recolor(Client.secondaryPlayerBodyColors[0], Client.secondaryPlayerBodyColors[this.appearanceColors[appearanceColorIndex]]);
            }
         }
      }

      if (this.modelMode == 3) {
         model2.setTriangleAlpha(mode3Rotation);
      }

      return model2;
   }
}
