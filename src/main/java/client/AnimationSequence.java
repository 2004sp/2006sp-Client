package client;
public final class AnimationSequence {
   public static AnimationSequence[] sequences;
   public int frameCount;
   public int[] frameIds;
   public int[] secondaryFrameIds;
   public int[] frameLengths;
   public int frameStep = -1;
   public int[] interleaveOrder;
   public boolean stretches = false;
   public int forcedPriority = 5;
   public int offHandModel = -1;
   public int mainHandModel = -1;
   public int maxLoops = 99;
   public int precedenceAnimating = -1;
   public int priority = -1;
   public int replyMode = 2;
   private static int[] animationScratch = new int[2800];
   public static void load(Archive archive) {
      for (int animationScratchIndex = 0; animationScratchIndex < animationScratch.length; animationScratchIndex++) {
         animationScratch[animationScratchIndex] = 0;
      }

      boolean flag = false;
      if (Client.extendedRevisionEnabled && !Client.hdModels) {
         if (Client.use2007Models) {
            flag = true;
         }

         Buffer buffer;
         int localLength = (buffer = new Buffer(archive.getFile("seq07.dat"))).readUnsignedShort();
         if (sequences == null) {
            sequences = new AnimationSequence[localLength];
         }

         for (int sequenceIndex = 0; sequenceIndex < localLength; sequenceIndex++) {
            if (sequences[sequenceIndex] == null) {
               sequences[sequenceIndex] = new AnimationSequence();
            }

            AnimationSequence animationSequence = sequences[sequenceIndex];
            Buffer sourceBuffer = buffer;
            AnimationSequence sourceAnimationSequence = animationSequence;

            int decodedUnsignedByte;
            while ((decodedUnsignedByte = sourceBuffer.readUnsignedByte()) != 0) {
               if (decodedUnsignedByte == 1) {
                  sourceAnimationSequence.frameCount = sourceBuffer.readUnsignedShort();
                  sourceAnimationSequence.frameIds = new int[sourceAnimationSequence.frameCount];
                  sourceAnimationSequence.secondaryFrameIds = new int[sourceAnimationSequence.frameCount];
                  sourceAnimationSequence.frameLengths = new int[sourceAnimationSequence.frameCount];

                  for (int frameLengthIndex = 0; frameLengthIndex < sourceAnimationSequence.frameCount; frameLengthIndex++) {
                     sourceAnimationSequence.frameLengths[frameLengthIndex] = sourceBuffer.readUnsignedShort();
                  }

                  for (int frameIdIndex = 0; frameIdIndex < sourceAnimationSequence.frameCount; frameIdIndex++) {
                     sourceAnimationSequence.frameIds[frameIdIndex] = sourceBuffer.readUnsignedShort();
                     sourceAnimationSequence.secondaryFrameIds[frameIdIndex] = -1;
                  }

                  for (int frameIndex = 0; frameIndex < sourceAnimationSequence.frameCount; frameIndex++) {
                     sourceAnimationSequence.frameIds[frameIndex] = sourceAnimationSequence.frameIds[frameIndex] + (sourceBuffer.readUnsignedShort() << 16);
                  }
               } else if (decodedUnsignedByte == 2) {
                  sourceAnimationSequence.frameStep = sourceBuffer.readUnsignedShort();
               } else if (decodedUnsignedByte == 3) {
                  decodedUnsignedByte = sourceBuffer.readUnsignedByte();
                  sourceAnimationSequence.interleaveOrder = new int[decodedUnsignedByte + 1];

                  for (int interleaveOrderIndex = 0; interleaveOrderIndex < decodedUnsignedByte; interleaveOrderIndex++) {
                     sourceAnimationSequence.interleaveOrder[interleaveOrderIndex] = sourceBuffer.readUnsignedByte();
                     if (sourceAnimationSequence.interleaveOrder[interleaveOrderIndex] == 127) {
                        sourceAnimationSequence.interleaveOrder[interleaveOrderIndex] = 9999999;
                     }
                  }
               } else if (decodedUnsignedByte == 4) {
                  sourceAnimationSequence.stretches = true;
               } else if (decodedUnsignedByte == 5) {
                  sourceAnimationSequence.forcedPriority = sourceBuffer.readUnsignedByte();
               } else if (decodedUnsignedByte == 6) {
                  sourceAnimationSequence.offHandModel = sourceBuffer.readUnsignedShort();
               } else if (decodedUnsignedByte == 7) {
                  sourceAnimationSequence.mainHandModel = sourceBuffer.readUnsignedShort();
               } else if (decodedUnsignedByte == 8) {
                  sourceAnimationSequence.maxLoops = sourceBuffer.readUnsignedByte();
               } else if (decodedUnsignedByte == 9) {
                  sourceAnimationSequence.precedenceAnimating = sourceBuffer.readUnsignedByte();
               } else if (decodedUnsignedByte == 10) {
                  sourceAnimationSequence.priority = sourceBuffer.readUnsignedByte();
               } else if (decodedUnsignedByte == 11) {
                  sourceAnimationSequence.replyMode = sourceBuffer.readUnsignedByte();
               } else if (decodedUnsignedByte != 12) {
                  if (decodedUnsignedByte == 13) {
                     decodedUnsignedByte = sourceBuffer.readUnsignedByte();

                     for (int loopIndex = 0; loopIndex < decodedUnsignedByte; loopIndex++) {
                        sourceBuffer.readUnsignedShort();
                        sourceBuffer.readUnsignedByte();
                     }
                  } else {
                     System.out.println("Error unrecognised seq config code: " + decodedUnsignedByte);
                  }
               } else {
                  decodedUnsignedByte = sourceBuffer.readUnsignedByte();

                  for (int loopIndex2 = 0; loopIndex2 < decodedUnsignedByte; loopIndex2++) {
                     sourceBuffer.readUnsignedShort();
                  }

                  for (int loopIndex3 = 0; loopIndex3 < decodedUnsignedByte; loopIndex3++) {
                     sourceBuffer.readUnsignedShort();
                  }
               }
            }

            if (sourceAnimationSequence.frameCount == 0) {
               sourceAnimationSequence.frameCount = 1;
               sourceAnimationSequence.frameIds = new int[1];
               sourceAnimationSequence.frameIds[0] = -1;
               sourceAnimationSequence.secondaryFrameIds = new int[1];
               sourceAnimationSequence.secondaryFrameIds[0] = -1;
               sourceAnimationSequence.frameLengths = new int[1];
               sourceAnimationSequence.frameLengths[0] = -1;
            }

            if (sourceAnimationSequence.precedenceAnimating == -1) {
               sourceAnimationSequence.precedenceAnimating = sourceAnimationSequence.interleaveOrder == null ? 0 : 2;
            }

            if (sourceAnimationSequence.priority == -1) {
               sourceAnimationSequence.priority = sourceAnimationSequence.interleaveOrder == null ? 0 : 2;
            }

            if (sequenceIndex == 7039) {
               for (int frameIndex2 = 0; frameIndex2 < sequences[sequenceIndex].frameCount; frameIndex2++) {
                  sequences[sequenceIndex].frameLengths[frameIndex2] = -1;
               }
            }
         }
      }

      if (!flag) {
         Buffer buffer2 = new Buffer(archive.getFile("seq.dat"));
         if (Client.hdModels) {
            buffer2 = new Buffer(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Configs/seq.dat"));
         }

         int readUnsignedShortOrLength = buffer2.readUnsignedShort();
         if (sequences == null) {
            sequences = new AnimationSequence[readUnsignedShortOrLength];
         }

         for (int sequenceIndex2 = 0; sequenceIndex2 < readUnsignedShortOrLength; sequenceIndex2++) {
            Client.isSequenceDecodeHookEnabled();
            if (sequences[sequenceIndex2] == null || Client.extendedRevisionEnabled && !Client.hdModels) {
               sequences[sequenceIndex2] = new AnimationSequence();
            }

            if (Client.hdModels) {
               sequences[sequenceIndex2].decodeHighDefinition(buffer2, 530);
               if (sequenceIndex2 == 7039) {
                  for (int frameIndex3 = 0; frameIndex3 < sequences[sequenceIndex2].frameCount; frameIndex3++) {
                     sequences[sequenceIndex2].frameLengths[frameIndex3] = -1;
                  }
               }
            } else {
               sequences[sequenceIndex2].decode(buffer2);
            }
         }
      }
   }
   public final int getFrameLength(int frameLengthIndex) {
      int localFrameLengths;
      AnimationFrame animationFrame;
      if ((localFrameLengths = this.frameLengths[frameLengthIndex]) == 0 && (animationFrame = AnimationFrame.get(this.frameIds[frameLengthIndex])) != null) {
         localFrameLengths = this.frameLengths[frameLengthIndex] = animationFrame.frameLength;
      }

      if (localFrameLengths == 0) {
         localFrameLengths = 1;
      }

      return localFrameLengths;
   }
   private void decode(Buffer buffer) {
      int interleaveOrderIndex;
      while ((interleaveOrderIndex = buffer.readUnsignedByte()) != 0) {
         if (interleaveOrderIndex == 1) {
            this.frameCount = buffer.readUnsignedByte();
            this.frameIds = new int[this.frameCount];
            this.secondaryFrameIds = new int[this.frameCount];
            this.frameLengths = new int[this.frameCount];

            for (int frameIdIndex = 0; frameIdIndex < this.frameCount; frameIdIndex++) {
               this.frameIds[frameIdIndex] = buffer.readUnsignedShort();
               this.secondaryFrameIds[frameIdIndex] = buffer.readUnsignedShort();
               if (this.secondaryFrameIds[frameIdIndex] == 65535) {
                  this.secondaryFrameIds[frameIdIndex] = -1;
               }

               this.frameLengths[frameIdIndex] = buffer.readUnsignedShort();
            }
         } else if (interleaveOrderIndex == 2) {
            this.frameStep = buffer.readUnsignedShort();
         } else if (interleaveOrderIndex != 3) {
            if (interleaveOrderIndex == 4) {
               this.stretches = true;
            } else if (interleaveOrderIndex == 5) {
               this.forcedPriority = buffer.readUnsignedByte();
            } else if (interleaveOrderIndex == 6) {
               this.offHandModel = buffer.readUnsignedShort();
            } else if (interleaveOrderIndex == 7) {
               this.mainHandModel = buffer.readUnsignedShort();
            } else if (interleaveOrderIndex == 8) {
               this.maxLoops = buffer.readUnsignedByte();
            } else if (interleaveOrderIndex == 9) {
               this.precedenceAnimating = buffer.readUnsignedByte();
            } else if (interleaveOrderIndex == 10) {
               this.priority = buffer.readUnsignedByte();
            } else if (interleaveOrderIndex == 11) {
               this.replyMode = buffer.readUnsignedByte();
            } else if (interleaveOrderIndex == 12) {
               buffer.readInt();
            } else {
               System.out.println("Error unrecognised seq config code: " + interleaveOrderIndex);
            }
         } else {
            interleaveOrderIndex = buffer.readUnsignedByte();
            this.interleaveOrder = new int[interleaveOrderIndex + 1];

            for (int loopIndex = 0; loopIndex < interleaveOrderIndex; loopIndex++) {
               this.interleaveOrder[loopIndex] = buffer.readUnsignedByte();
            }

            this.interleaveOrder[interleaveOrderIndex] = 9999999;
         }
      }

      if (this.frameCount == 0) {
         this.frameCount = 1;
         this.frameIds = new int[1];
         this.frameIds[0] = -1;
         this.secondaryFrameIds = new int[1];
         this.secondaryFrameIds[0] = -1;
         this.frameLengths = new int[1];
         this.frameLengths[0] = -1;
      }

      if (this.precedenceAnimating == -1) {
         if (this.interleaveOrder != null) {
            this.precedenceAnimating = 2;
         } else {
            this.precedenceAnimating = 0;
         }
      }

      if (this.priority == -1) {
         if (this.interleaveOrder != null) {
            this.priority = 2;
            return;
         }

         this.priority = 0;
      }
   }
   public static int remapId(int emoteAnimation) {
      return remapId(emoteAnimation, -1);
   }
   public static int remapId(int emoteAnimation, int id) {
      if (Animations.isActive()) return emoteAnimation;
      if (!Client.hdModels && !Client.use2007Models) {
         return emoteAnimation == 7033 ? 69 : emoteAnimation;
      }

      if (Client.hdModels) {
         if (emoteAnimation == 588) {
            return 9850;
         }

         if (emoteAnimation == 589) {
            return 9805;
         }

         if (emoteAnimation == 590) {
            return 9810;
         }

         if (emoteAnimation == 591) {
            return 9847;
         }

         if (emoteAnimation == 592) {
            return 9795;
         }

         if (emoteAnimation == 593) {
            return 9795;
         }

         if (emoteAnimation == 594) {
            return 9795;
         }

         if (emoteAnimation == 595) {
            return 9784;
         }

         if (emoteAnimation == 596) {
            return 9784;
         }

         if (emoteAnimation == 597) {
            return 9784;
         }

         if (emoteAnimation == 598) {
            return 9760;
         }

         if (emoteAnimation == 599) {
            return 9765;
         }

         if (emoteAnimation == 600) {
            return 9768;
         }

         if (emoteAnimation == 601) {
            return 9768;
         }

         if (emoteAnimation == 602) {
            return 9776;
         }

         if (emoteAnimation == 603) {
            return 9828;
         }

         if (emoteAnimation == 604) {
            return 9795;
         }

         if (emoteAnimation == 605) {
            return 9851;
         }

         if (emoteAnimation == 606) {
            return 9851;
         }

         if (emoteAnimation == 607) {
            return 9851;
         }

         if (emoteAnimation == 608) {
            return 9851;
         }

         if (emoteAnimation == 609) {
            return 9795;
         }

         if (emoteAnimation == 610) {
            return 9765;
         }

         if (emoteAnimation == 611) {
            return 9760;
         }

         if (emoteAnimation == 612) {
            return 9836;
         }

         if (emoteAnimation == 613) {
            return 9760;
         }

         if (emoteAnimation == 614) {
            return 9784;
         }

         if (emoteAnimation == 615) {
            return 9788;
         }

         if (emoteAnimation == 616) {
            return 9792;
         }

         if (emoteAnimation == 617) {
            return 9796;
         }
      }

      if (emoteAnimation == 309) {
         return 6185;
      }

      if (emoteAnimation == 312) {
         return 6183;
      }

      if (emoteAnimation == 313) {
         return 6182;
      }

      if (emoteAnimation == 143) {
         return 5327;
      }

      if (emoteAnimation == 144) {
         return 5328;
      }

      if (emoteAnimation == 146) {
         return 5329;
      }

      if (emoteAnimation == 280) {
         return 6249;
      }

      if (emoteAnimation == 281) {
         return 6250;
      }

      if (emoteAnimation == 282) {
         return 6251;
      }

      if (emoteAnimation != 422 || id != 2037 && id != 2036) {
         if (emoteAnimation != 404 || id != 2037 && id != 2036) {
            if (emoteAnimation != 2304 || id != 2037 && id != 2036) {
               if (emoteAnimation == 59) {
                  return 5849;
               }

               if (emoteAnimation == 60) {
                  return 5850;
               }

               if (emoteAnimation == 62) {
                  return 5851;
               }

               if (emoteAnimation == 55) {
                  return 5387;
               }

               if (emoteAnimation == 56) {
                  return 5388;
               }

               if (emoteAnimation == 57) {
                  return 5389;
               }

               if (emoteAnimation == 289) {
                  return 6376;
               }

               if (emoteAnimation == 290) {
                  return 6375;
               }

               if (emoteAnimation == 292) {
                  return 6377;
               }

               if (emoteAnimation == 138) {
                  return 4933;
               }

               if (emoteAnimation == 139) {
                  return 4934;
               }

               if (emoteAnimation == 141) {
                  return 4935;
               }

               if (emoteAnimation == 1777) {
                  return 6079;
               }

               if (emoteAnimation == 1775) {
                  return 6080;
               }

               if (emoteAnimation == 1778) {
                  return 6082;
               }

               if (emoteAnimation == 1785) {
                  return 6001;
               }

               if (emoteAnimation == 1786) {
                  return 6002;
               }

               if (emoteAnimation == 1787) {
                  return 6003;
               }

               if (emoteAnimation == 246) {
                  return 6254;
               }

               if (emoteAnimation == 247) {
                  return 6255;
               }

               if (emoteAnimation == 248) {
                  return 6256;
               }

               if (emoteAnimation == 41) {
                  return 4925;
               }

               if (emoteAnimation == 42) {
                  return 4927;
               }

               if (emoteAnimation == 44) {
                  return 4929;
               }

               if (emoteAnimation == 75) {
                  return 6559;
               }

               if (emoteAnimation == 76) {
                  return 6563;
               }

               if (emoteAnimation == 78) {
                  return 6558;
               }

               if (emoteAnimation == 1184) {
                  return 6223;
               }

               if (emoteAnimation == 1185) {
                  return 6223;
               }

               if (emoteAnimation == 1186) {
                  return 6232;
               }

               if (emoteAnimation == 1187) {
                  return 6230;
               }

               if (emoteAnimation == 1177) {
                  return 6235;
               }

               if (emoteAnimation == 1179) {
                  return 6234;
               }

               if (emoteAnimation == 1182) {
                  return 6233;
               }

               if (emoteAnimation == 1170) {
                  return 6235;
               }

               if (emoteAnimation == 260) {
                  return 5485;
               }

               if (emoteAnimation == 261) {
                  return 5489;
               }

               if (emoteAnimation == 263) {
                  return 5491;
               }

               if (emoteAnimation == 65000) {
                  return 263;
               }

               if (emoteAnimation == 1546) {
                  return 260;
               }

               if (emoteAnimation == 1547) {
                  return 261;
               }

               if (emoteAnimation == 1548) {
                  return 264;
               }

               if (emoteAnimation == 30) {
                  return 4915;
               }

               if (emoteAnimation == 31) {
                  return 4916;
               }

               if (emoteAnimation == 36) {
                  return 4918;
               }

               if (emoteAnimation == 299) {
                  return 5568;
               }

               if (emoteAnimation == 300) {
                  return 5567;
               }

               if (emoteAnimation == 302) {
                  return 5569;
               }

               if (emoteAnimation == 123 && id != 655) {
                  return 5540;
               }

               if (emoteAnimation == 124 && id != 655) {
                  return 5541;
               }

               if (emoteAnimation == 126 && id != 655) {
                  return 5542;
               }

               if (emoteAnimation == 123 && id == 655) {
                  return 5532;
               }

               if (emoteAnimation == 124 && id == 655) {
                  return 5533;
               }

               if (emoteAnimation == 126 && id == 655) {
                  return 5534;
               }

               if (emoteAnimation == 128 && id != 113 && id != 221) {
                  return 4666;
               }

               if (emoteAnimation == 129 && id != 113 && id != 221) {
                  return 4665;
               }

               if (emoteAnimation == 131 && id != 113 && id != 221) {
                  return 4668;
               }

               if (emoteAnimation == 158) {
                  return 6562;
               }

               if (emoteAnimation == 159) {
                  return 6563;
               }

               if (emoteAnimation == 161) {
                  return 6564;
               }

               if (emoteAnimation == 2733) {
                  return 2731;
               }

               if (emoteAnimation == 2730) {
                  return 2732;
               }

               if (emoteAnimation == 2732) {
                  return 2733;
               }

               if (Client.hdModels) {
                  if (emoteAnimation == 1562) {
                     return 7766;
                  }

                  if (emoteAnimation == 1560) {
                     return 7761;
                  }

                  if (emoteAnimation == 1563) {
                     return 7763;
                  }

                  if (emoteAnimation == 1582) {
                     return 8080;
                  }

                  if (emoteAnimation == 1581) {
                     return 8084;
                  }

                  if (emoteAnimation == 1580) {
                     return 8078;
                  }

                  if (emoteAnimation == 1586) {
                     return 8569;
                  }

                  if (emoteAnimation == 1585) {
                     return 8571;
                  }

                  if (emoteAnimation == 1587) {
                     return 8570;
                  }

                  if (emoteAnimation == 1595) {
                     return 9481;
                  }

                  if (emoteAnimation == 1596) {
                     return 9478;
                  }

                  if (emoteAnimation == 1597) {
                     return 9477;
                  }

                  if (emoteAnimation == 1512) {
                     return 9439;
                  }

                  if (emoteAnimation == 1514) {
                     return 9441;
                  }

                  if (emoteAnimation == 1513) {
                     return 9440;
                  }

                  if (emoteAnimation == 1592) {
                     return 9439;
                  }

                  if (emoteAnimation == 1591) {
                     return 9441;
                  }

                  if (emoteAnimation == 1590) {
                     return 9440;
                  }

                  if (emoteAnimation == 1523) {
                     return 9449;
                  }

                  if (emoteAnimation == 1525) {
                     return 9451;
                  }

                  if (emoteAnimation == 1524) {
                     return 9450;
                  }

                  if (emoteAnimation == 1552) {
                     return 9130;
                  }

                  if (emoteAnimation == 1550) {
                     return 9132;
                  }

                  if (emoteAnimation == 1553) {
                     return 9131;
                  }

                  if (emoteAnimation == 1507) {
                     return 9466;
                  }

                  if (emoteAnimation == 1509) {
                     return 9468;
                  }

                  if (emoteAnimation == 1508) {
                     return 9467;
                  }

                  if (emoteAnimation == 1519) {
                     return 9454;
                  }

                  if (emoteAnimation == 1515) {
                     return 9455;
                  }

                  if (emoteAnimation == 1528) {
                     return 9487;
                  }

                  if (emoteAnimation == 1527) {
                     return 9489;
                  }

                  if (emoteAnimation == 1530) {
                     return 9488;
                  }

                  if (emoteAnimation == 2612) {
                     return 9345;
                  }

                  if (emoteAnimation == 2605) {
                     return 9287;
                  }

                  if (emoteAnimation == 2608) {
                     return 9291;
                  }

                  if (emoteAnimation == 2613) {
                     return 9260;
                  }

                  if (emoteAnimation == 2607) {
                     return 9291;
                  }

                  if (emoteAnimation == 2610) {
                     return 9345;
                  }

                  if (emoteAnimation == 2307) {
                     return 9288;
                  }

                  if (emoteAnimation == 2606) {
                     return 9287;
                  }

                  if (emoteAnimation == 2621) {
                     return 9232;
                  }

                  if (emoteAnimation == 2622) {
                     return 9231;
                  }

                  if (emoteAnimation == 2620) {
                     return 9230;
                  }

                  if (emoteAnimation == 2625) {
                     return 9233;
                  }

                  if (emoteAnimation == 2626) {
                     return 9235;
                  }

                  if (emoteAnimation == 2627) {
                     return 9234;
                  }

                  if (emoteAnimation == 2628) {
                     return 9245;
                  }

                  if (emoteAnimation == 2633) {
                     return 9243;
                  }

                  if (emoteAnimation == 2629) {
                     return 9242;
                  }

                  if (emoteAnimation == 2630) {
                     return 9239;
                  }

                  if (emoteAnimation == 2637) {
                     return 9252;
                  }

                  if (emoteAnimation == 2635) {
                     return 9249;
                  }

                  if (emoteAnimation == 2638) {
                     return 9257;
                  }

                  if (emoteAnimation == 2644) {
                     return 9265;
                  }

                  if (emoteAnimation == 2647) {
                     return 9266;
                  }

                  if (emoteAnimation == 2645) {
                     return 9268;
                  }

                  if (emoteAnimation == 2646) {
                     return 9269;
                  }

                  if (emoteAnimation == 2655) {
                     return 9277;
                  }

                  if (emoteAnimation == 2652) {
                     return 9276;
                  }

                  if (emoteAnimation == 2656) {
                     return 9276;
                  }

                  if (emoteAnimation == 2653) {
                     return 9278;
                  }

                  if (emoteAnimation == 2654) {
                     return 9279;
                  }
               }

               return emoteAnimation;
            } else {
               return 5491;
            }
         } else {
            return 5489;
         }
      } else {
         return 5487;
      }
   }
   private void decodeHighDefinition(Buffer buffer, int newReadUnsignedByte) {
      while ((newReadUnsignedByte = buffer.readUnsignedByte()) != 0) {
         if (newReadUnsignedByte == 1) {
            this.frameCount = buffer.readUnsignedShort();
            this.frameIds = new int[this.frameCount];
            this.secondaryFrameIds = new int[this.frameCount];
            this.frameLengths = new int[this.frameCount];

            for (int frameIdIndex = 0; frameIdIndex < this.frameCount; frameIdIndex++) {
               this.frameIds[frameIdIndex] = buffer.readInt();
               this.secondaryFrameIds[frameIdIndex] = -1;
            }

            for (int frameLengthIndex = 0; frameLengthIndex < this.frameCount; frameLengthIndex++) {
               this.frameLengths[frameLengthIndex] = buffer.readUnsignedByte();
            }
         } else if (newReadUnsignedByte == 2) {
            this.frameStep = buffer.readUnsignedShort();
         } else if (newReadUnsignedByte != 3) {
            if (newReadUnsignedByte == 4) {
               this.stretches = true;
            } else if (newReadUnsignedByte == 5) {
               this.forcedPriority = buffer.readUnsignedByte();
            } else if (newReadUnsignedByte == 6) {
               this.offHandModel = buffer.readUnsignedShort();
            } else if (newReadUnsignedByte == 7) {
               this.mainHandModel = buffer.readUnsignedShort();
            } else if (newReadUnsignedByte == 8) {
               this.maxLoops = buffer.readUnsignedByte();
            } else if (newReadUnsignedByte == 9) {
               this.precedenceAnimating = buffer.readUnsignedByte();
            } else if (newReadUnsignedByte == 10) {
               this.priority = buffer.readUnsignedByte();
            } else if (newReadUnsignedByte == 11) {
               this.replyMode = buffer.readUnsignedByte();
            } else if (newReadUnsignedByte == 12) {
               buffer.readInt();
            } else {
               System.out.println("Error unrecognised seq config code: " + newReadUnsignedByte);
            }
         } else {
            newReadUnsignedByte = buffer.readUnsignedByte();
            this.interleaveOrder = new int[newReadUnsignedByte + 1];

            for (int interleaveOrderIndex = 0; interleaveOrderIndex < newReadUnsignedByte; interleaveOrderIndex++) {
               this.interleaveOrder[interleaveOrderIndex] = buffer.readUnsignedByte();
            }

            this.interleaveOrder[newReadUnsignedByte] = 9999999;
         }
      }

      if (this.frameCount == 0) {
         this.frameCount = 1;
         this.frameIds = new int[1];
         this.frameIds[0] = -1;
         this.secondaryFrameIds = new int[1];
         this.secondaryFrameIds[0] = -1;
         this.frameLengths = new int[1];
         this.frameLengths[0] = -1;
      }

      if (this.precedenceAnimating == -1) {
         if (this.interleaveOrder != null) {
            this.precedenceAnimating = 2;
         } else {
            this.precedenceAnimating = 0;
         }
      }

      if (this.priority == -1) {
         if (this.interleaveOrder != null) {
            this.priority = 2;
            return;
         }

         this.priority = 0;
      }

      if (this.offHandModel == 65535) {
         this.offHandModel = 0;
      }

      if (this.mainHandModel == 65535) {
         this.mainHandModel = 0;
      }
   }
}
