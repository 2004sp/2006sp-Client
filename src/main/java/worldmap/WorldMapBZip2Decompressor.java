package worldmap;
public final class WorldMapBZip2Decompressor {
   private static WorldMapBZip2State state = new WorldMapBZip2State();
   public static int decompress(byte[] newOutputData, int newOutputRemaining, byte[] newInputData, int newInputRemaining, int newInputOffset) {
      synchronized (state) {
         state.inputData = newInputData;
         state.inputOffset = newInputOffset;
         state.outputData = newOutputData;
         state.outputOffset = 0;
         state.inputRemaining = newInputRemaining;
         state.outputRemaining = newOutputRemaining;
         state.liveBits = 0;
         state.bitBuffer = 0;
         state.totalInLo32 = 0;
         state.totalInHi32 = 0;
         state.totalOutLo32 = 0;
         state.totalOutHi32 = 0;
         state.currentBlock = 0;
         WorldMapBZip2State worldMapBZip2State = state;
         boolean flag = false;
         WorldMapBZip2State worldMapBZip2State3 = null;
         worldMapBZip2State.blockSize100k = 1;
         if (WorldMapBZip2State.tt == null) {
            WorldMapBZip2State.tt = new int[worldMapBZip2State.blockSize100k * 100000];
         }

         boolean localFlag = true;

         while (localFlag) {
            WorldMapBZip2State worldMapBZip2State2 = worldMapBZip2State;
            if ((byte)readBits(8, worldMapBZip2State2) == 23) {
               break;
            }

            worldMapBZip2State2 = worldMapBZip2State;
            byte decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State.currentBlock++;
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            decodedBits2 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State2 = worldMapBZip2State;
            if ((byte)readBits(1, worldMapBZip2State2) != 0) {
               worldMapBZip2State.blockRandomized = true;
            } else {
               worldMapBZip2State.blockRandomized = false;
            }

            if (worldMapBZip2State.blockRandomized) {
               System.out.println("PANIC! RANDOMISED BLOCK!");
            }

            worldMapBZip2State.originalPointer = 0;
            worldMapBZip2State2 = worldMapBZip2State;
            byte decodedBits3 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State.originalPointer = worldMapBZip2State.originalPointer << 8 | decodedBits3 & 255;
            worldMapBZip2State2 = worldMapBZip2State;
            byte decodedBits4 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State.originalPointer = worldMapBZip2State.originalPointer << 8 | decodedBits4 & 255;
            worldMapBZip2State2 = worldMapBZip2State;
            byte decodedBits5 = (byte)readBits(8, worldMapBZip2State2);
            worldMapBZip2State.originalPointer = worldMapBZip2State.originalPointer << 8 | decodedBits5 & 255;

            for (int loopIndex = 0; loopIndex < 16; loopIndex++) {
               worldMapBZip2State2 = worldMapBZip2State;
               if ((byte)readBits(1, worldMapBZip2State2) == 1) {
                  worldMapBZip2State.inUse16[loopIndex] = true;
               } else {
                  worldMapBZip2State.inUse16[loopIndex] = false;
               }
            }

            for (int inUseIndex = 0; inUseIndex < 256; inUseIndex++) {
               worldMapBZip2State.inUse[inUseIndex] = false;
            }

            for (int loopIndex2 = 0; loopIndex2 < 16; loopIndex2++) {
               if (worldMapBZip2State.inUse16[loopIndex2]) {
                  for (int loopIndex3 = 0; loopIndex3 < 16; loopIndex3++) {
                     worldMapBZip2State2 = worldMapBZip2State;
                     if ((byte)readBits(1, worldMapBZip2State2) == 1) {
                        worldMapBZip2State.inUse[(loopIndex2 << 4) + loopIndex3] = true;
                     }
                  }
               }
            }

            worldMapBZip2State3 = worldMapBZip2State;
            worldMapBZip2State.nInUse = 0;

            for (int inUseIndex2 = 0; inUseIndex2 < 256; inUseIndex2++) {
               if (worldMapBZip2State3.inUse[inUseIndex2]) {
                  worldMapBZip2State3.seqToUnseq[worldMapBZip2State3.nInUse] = (byte)inUseIndex2;
                  worldMapBZip2State3.nInUse++;
               }
            }

            int localNInUse = worldMapBZip2State.nInUse + 2;
            int groupCount = readBits(3, worldMapBZip2State);
            int decodedBits = readBits(15, worldMapBZip2State);

            for (int selectorMtfIndex = 0; selectorMtfIndex < decodedBits; selectorMtfIndex++) {
               int scalar = 0;

               while (true) {
                  worldMapBZip2State2 = worldMapBZip2State;
                  if ((byte)readBits(1, worldMapBZip2State2) == 0) {
                     worldMapBZip2State.selectorMtf[selectorMtfIndex] = (byte)scalar;
                     break;
                  }

                  scalar++;
               }
            }

            byte[] selectorMtf = new byte[6];
            int nInUse2 = 0;

            while (nInUse2 < groupCount) {
               selectorMtf[nInUse2] = (byte)nInUse2++;
            }

            for (int selectorMtfIndex2 = 0; selectorMtfIndex2 < decodedBits; selectorMtfIndex2++) {
               byte selectorMtfEntry = worldMapBZip2State.selectorMtf[selectorMtfIndex2];
               byte selectorMtfEntry2 = selectorMtf[selectorMtfEntry];

               while (selectorMtfEntry > 0) {
                  selectorMtf[selectorMtfEntry] = selectorMtf[selectorMtfEntry - 1];
                  selectorMtfEntry--;
               }

               selectorMtf[0] = selectorMtfEntry2;
               worldMapBZip2State.selector[selectorMtfIndex2] = selectorMtfEntry2;
            }

            for (int codeLengthIndex = 0; codeLengthIndex < groupCount; codeLengthIndex++) {
               int readBits2 = readBits(5, worldMapBZip2State);

               for (int loopIndex4 = 0; loopIndex4 < localNInUse; loopIndex4++) {
                  while (true) {
                     worldMapBZip2State2 = worldMapBZip2State;
                     if ((byte)readBits(1, worldMapBZip2State2) == 0) {
                        worldMapBZip2State.codeLengths[codeLengthIndex][loopIndex4] = (byte)readBits2;
                        break;
                     }

                     worldMapBZip2State2 = worldMapBZip2State;
                     if ((byte)readBits(1, worldMapBZip2State2) == 0) {
                        readBits2++;
                     } else {
                        readBits2--;
                     }
                  }
               }
            }

            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
               byte minLenOrCodeLengths = 32;
               byte localCodeLengths = 0;

               for (int loopIndex5 = 0; loopIndex5 < localNInUse; loopIndex5++) {
                  if (worldMapBZip2State.codeLengths[groupIndex][loopIndex5] > localCodeLengths) {
                     localCodeLengths = worldMapBZip2State.codeLengths[groupIndex][loopIndex5];
                  }

                  if (worldMapBZip2State.codeLengths[groupIndex][loopIndex5] < minLenOrCodeLengths) {
                     minLenOrCodeLengths = worldMapBZip2State.codeLengths[groupIndex][loopIndex5];
                  }
               }

               int[] huffmanLimitEntry = worldMapBZip2State.huffmanLimit[groupIndex];
               int[] huffmanBaseEntry = worldMapBZip2State.huffmanBase[groupIndex];
               int[] huffmanPermEntry = worldMapBZip2State.huffmanPerm[groupIndex];
               byte[] codeLengths2 = worldMapBZip2State.codeLengths[groupIndex];
               int sourceLocalNInUse = localNInUse;
               byte sourceLocalCodeLengths = localCodeLengths;
               byte position = minLenOrCodeLengths;
               byte[] byteBuffer = codeLengths2;
               int[] values = huffmanPermEntry;
               int[] huffmanBase = huffmanBaseEntry;
               int[] integerBuffer = huffmanLimitEntry;
               int scalar2 = 0;

               for (int loopIndex6 = position; loopIndex6 <= sourceLocalCodeLengths; loopIndex6++) {
                  for (int loopIndex7 = 0; loopIndex7 < sourceLocalNInUse; loopIndex7++) {
                     if (byteBuffer[loopIndex7] == loopIndex6) {
                        values[scalar2] = loopIndex7;
                        scalar2++;
                     }
                  }
               }

               for (int huffmanBaseIndex = 0; huffmanBaseIndex < 23; huffmanBaseIndex++) {
                  huffmanBase[huffmanBaseIndex] = 0;
               }

               for (int loopIndex8 = 0; loopIndex8 < sourceLocalNInUse; loopIndex8++) {
                  huffmanBase[byteBuffer[loopIndex8] + 1]++;
               }

               for (int huffmanBaseIndex2 = 1; huffmanBaseIndex2 < 23; huffmanBaseIndex2++) {
                  huffmanBase[huffmanBaseIndex2] += huffmanBase[huffmanBaseIndex2 - 1];
               }

               for (int loopIndex9 = 0; loopIndex9 < 23; loopIndex9++) {
                  integerBuffer[loopIndex9] = 0;
               }

               int scalar3 = 0;

               for (int huffmanBaseIndex3 = position; huffmanBaseIndex3 <= sourceLocalCodeLengths; huffmanBaseIndex3++) {
                  int scalar4 = scalar3 + (huffmanBase[huffmanBaseIndex3 + 1] - huffmanBase[huffmanBaseIndex3]);
                  integerBuffer[huffmanBaseIndex3] = scalar4 - 1;
                  scalar3 = scalar4 << 1;
               }

               for (int huffmanBaseIndex4 = position + 1; huffmanBaseIndex4 <= sourceLocalCodeLengths; huffmanBaseIndex4++) {
                  huffmanBase[huffmanBaseIndex4] = (integerBuffer[huffmanBaseIndex4 - 1] + 1 << 1) - huffmanBase[huffmanBaseIndex4];
               }

               worldMapBZip2State.minLens[groupIndex] = minLenOrCodeLengths;
            }

            nInUse2 = worldMapBZip2State.nInUse + 1;
            int selectorIndex = -1;
            int sourceOutputRunLength = 0;

            for (int unzftabIndex = 0; unzftabIndex <= 255; unzftabIndex++) {
               worldMapBZip2State.unzftab[unzftabIndex] = 0;
            }

            int mtfaIndex = 4095;

            for (int mtfbaseIndex = 15; mtfbaseIndex >= 0; mtfbaseIndex--) {
               for (int loopIndex10 = 15; loopIndex10 >= 0; loopIndex10--) {
                  worldMapBZip2State.mtfa[mtfaIndex] = (byte)((mtfbaseIndex << 4) + loopIndex10);
                  mtfaIndex--;
               }

               worldMapBZip2State.mtfbase[mtfbaseIndex] = mtfaIndex + 1;
            }

            int nblockOrNblockUsed = 0;
            selectorIndex++;
            sourceOutputRunLength = 50;
            int selectorEntry = worldMapBZip2State.selector[0];
            int minLen = worldMapBZip2State.minLens[selectorEntry];
            int[] huffmanLimit = worldMapBZip2State.huffmanLimit[selectorEntry];
            int[] huffmanPerm = worldMapBZip2State.huffmanPerm[selectorEntry];
            int[] huffmanBase2 = worldMapBZip2State.huffmanBase[selectorEntry];
            sourceOutputRunLength--;
            selectorEntry = minLen;
            int decodedBits6 = readBits(minLen, worldMapBZip2State);

            while (decodedBits6 > huffmanLimit[selectorEntry]) {
               selectorEntry++;
               worldMapBZip2State2 = worldMapBZip2State;
               byte decodedBits7 = (byte)readBits(1, worldMapBZip2State2);
               decodedBits6 = decodedBits6 << 1 | decodedBits7;
            }

            selectorEntry = (int)huffmanPerm[decodedBits6 - huffmanBase2[selectorEntry]];

            while (selectorEntry != nInUse2) {
               if (selectorEntry != 0 && selectorEntry != 1) {
                  byte localMtfa;
                  if ((decodedBits6 = selectorEntry - 1) < 16) {
                     int mtfbaseEntry = worldMapBZip2State.mtfbase[0];
                     localMtfa = worldMapBZip2State.mtfa[mtfbaseEntry + decodedBits6];

                     while (decodedBits6 > 3) {
                        selectorEntry = mtfbaseEntry + decodedBits6;
                        worldMapBZip2State.mtfa[selectorEntry] = worldMapBZip2State.mtfa[selectorEntry - 1];
                        worldMapBZip2State.mtfa[selectorEntry - 1] = worldMapBZip2State.mtfa[selectorEntry - 2];
                        worldMapBZip2State.mtfa[selectorEntry - 2] = worldMapBZip2State.mtfa[selectorEntry - 3];
                        worldMapBZip2State.mtfa[selectorEntry - 3] = worldMapBZip2State.mtfa[selectorEntry - 4];
                        decodedBits6 -= 4;
                     }

                     while (decodedBits6 > 0) {
                        worldMapBZip2State.mtfa[mtfbaseEntry + decodedBits6] = worldMapBZip2State.mtfa[mtfbaseEntry + decodedBits6 - 1];
                        decodedBits6--;
                     }

                     worldMapBZip2State.mtfa[mtfbaseEntry] = localMtfa;
                  } else {
                     int mtfbaseIndex2 = decodedBits6 / 16;
                     selectorEntry = decodedBits6 % 16;
                     int mtfaIndex2 = worldMapBZip2State.mtfbase[mtfbaseIndex2] + selectorEntry;
                     localMtfa = worldMapBZip2State.mtfa[mtfaIndex2];

                     while (mtfaIndex2 > worldMapBZip2State.mtfbase[mtfbaseIndex2]) {
                        worldMapBZip2State.mtfa[mtfaIndex2] = worldMapBZip2State.mtfa[mtfaIndex2 - 1];
                        mtfaIndex2--;
                     }

                     worldMapBZip2State.mtfbase[mtfbaseIndex2]++;

                     while (mtfbaseIndex2 > 0) {
                        worldMapBZip2State.mtfbase[mtfbaseIndex2]--;
                        worldMapBZip2State.mtfa[worldMapBZip2State.mtfbase[mtfbaseIndex2]] = worldMapBZip2State.mtfa[worldMapBZip2State.mtfbase[mtfbaseIndex2 - 1] + 16 - 1];
                        mtfbaseIndex2--;
                     }

                     worldMapBZip2State.mtfbase[0]--;
                     worldMapBZip2State.mtfa[worldMapBZip2State.mtfbase[0]] = localMtfa;
                     if (worldMapBZip2State.mtfbase[0] == 0) {
                        selectorEntry = 4095;

                        for (int mtfbaseIndex3 = 15; mtfbaseIndex3 >= 0; mtfbaseIndex3--) {
                           for (int loopIndex11 = 15; loopIndex11 >= 0; loopIndex11--) {
                              worldMapBZip2State.mtfa[selectorEntry] = worldMapBZip2State.mtfa[worldMapBZip2State.mtfbase[mtfbaseIndex3] + loopIndex11];
                              selectorEntry--;
                           }

                           worldMapBZip2State.mtfbase[mtfbaseIndex3] = selectorEntry + 1;
                        }
                     }
                  }

                  worldMapBZip2State.unzftab[worldMapBZip2State.seqToUnseq[localMtfa & 0xFF] & 0xFF]++;
                  WorldMapBZip2State.tt[nblockOrNblockUsed] = worldMapBZip2State.seqToUnseq[localMtfa & 0xFF] & 255;
                  nblockOrNblockUsed++;
                  if (sourceOutputRunLength == 0) {
                     selectorIndex++;
                     sourceOutputRunLength = 50;
                     byte selectorEntry2 = worldMapBZip2State.selector[selectorIndex];
                     minLen = worldMapBZip2State.minLens[selectorEntry2];
                     huffmanLimit = worldMapBZip2State.huffmanLimit[selectorEntry2];
                     huffmanPerm = worldMapBZip2State.huffmanPerm[selectorEntry2];
                     huffmanBase2 = worldMapBZip2State.huffmanBase[selectorEntry2];
                  }

                  sourceOutputRunLength--;
                  int huffmanLimitIndex = minLen;
                  selectorEntry = readBits(minLen, worldMapBZip2State);

                  while (selectorEntry > huffmanLimit[huffmanLimitIndex]) {
                     huffmanLimitIndex++;
                     worldMapBZip2State2 = worldMapBZip2State;
                     byte decodedBits8 = (byte)readBits(1, worldMapBZip2State2);
                     selectorEntry = selectorEntry << 1 | decodedBits8;
                  }

                  selectorEntry = (int)huffmanPerm[selectorEntry - huffmanBase2[huffmanLimitIndex]];
               } else {
                  decodedBits6 = -1;
                  int runLengthMultiplier = 1;

                  int huffmanLimitIndex2;
                  do {
                     if (selectorEntry == 0) {
                        decodedBits6 += runLengthMultiplier;
                     } else if (selectorEntry == 1) {
                        decodedBits6 += 2 * runLengthMultiplier;
                     }

                     runLengthMultiplier <<= 1;
                     if (sourceOutputRunLength == 0) {
                        selectorIndex++;
                        sourceOutputRunLength = 50;
                        byte selectorEntry3 = worldMapBZip2State.selector[selectorIndex];
                        minLen = worldMapBZip2State.minLens[selectorEntry3];
                        huffmanLimit = worldMapBZip2State.huffmanLimit[selectorEntry3];
                        huffmanPerm = worldMapBZip2State.huffmanPerm[selectorEntry3];
                        huffmanBase2 = worldMapBZip2State.huffmanBase[selectorEntry3];
                     }

                     sourceOutputRunLength--;
                     huffmanLimitIndex2 = minLen;
                     selectorEntry = readBits(minLen, worldMapBZip2State);

                     while (selectorEntry > huffmanLimit[huffmanLimitIndex2]) {
                        huffmanLimitIndex2++;
                        worldMapBZip2State2 = worldMapBZip2State;
                        byte decodedBits9 = (byte)readBits(1, worldMapBZip2State2);
                        selectorEntry = selectorEntry << 1 | decodedBits9;
                     }
                  } while ((selectorEntry = (int)huffmanPerm[selectorEntry - huffmanBase2[huffmanLimitIndex2]]) == 0 || selectorEntry == 1);

                  decodedBits6++;
                  byte seqToUnseqEntry = worldMapBZip2State.seqToUnseq[worldMapBZip2State.mtfa[worldMapBZip2State.mtfbase[0]] & 0xFF];

                  for (worldMapBZip2State.unzftab[seqToUnseqEntry & 0xFF] = worldMapBZip2State.unzftab[seqToUnseqEntry & 0xFF] + decodedBits6; decodedBits6 > 0; decodedBits6--) {
                     WorldMapBZip2State.tt[nblockOrNblockUsed] = seqToUnseqEntry & 255;
                     nblockOrNblockUsed++;
                  }
               }
            }

            worldMapBZip2State.outputRunLength = 0;
            worldMapBZip2State.outputByte = 0;
            worldMapBZip2State.cftab[0] = 0;

            for (int cftabIndex = 1; cftabIndex <= 256; cftabIndex++) {
               worldMapBZip2State.cftab[cftabIndex] = worldMapBZip2State.unzftab[cftabIndex - 1];
            }

            for (int cftabIndex2 = 1; cftabIndex2 <= 256; cftabIndex2++) {
               worldMapBZip2State.cftab[cftabIndex2] = worldMapBZip2State.cftab[cftabIndex2] + worldMapBZip2State.cftab[cftabIndex2 - 1];
            }

            for (int ttIndex = 0; ttIndex < nblockOrNblockUsed; ttIndex++) {
               byte ttEntry = (byte)WorldMapBZip2State.tt[ttIndex];
               WorldMapBZip2State.tt[worldMapBZip2State.cftab[ttEntry & 0xFF]] = WorldMapBZip2State.tt[worldMapBZip2State.cftab[ttEntry & 0xFF]] | ttIndex << 8;
               worldMapBZip2State.cftab[ttEntry & 0xFF]++;
            }

            worldMapBZip2State.tPos = WorldMapBZip2State.tt[worldMapBZip2State.originalPointer] >> 8;
            worldMapBZip2State.nblockUsed = 0;
            worldMapBZip2State.tPos = WorldMapBZip2State.tt[worldMapBZip2State.tPos];
            worldMapBZip2State.k0 = (byte)worldMapBZip2State.tPos;
            worldMapBZip2State.tPos >>= 8;
            worldMapBZip2State.nblockUsed++;
            worldMapBZip2State.nblock = nblockOrNblockUsed;
            worldMapBZip2State3 = worldMapBZip2State;
            int outputByte = worldMapBZip2State.outputByte;
            sourceOutputRunLength = worldMapBZip2State3.outputRunLength;
            nblockOrNblockUsed = worldMapBZip2State3.nblockUsed;
            selectorEntry = worldMapBZip2State3.k0;
            int[] tt = WorldMapBZip2State.tt;
            int tPos = worldMapBZip2State3.tPos;
            byte[] outputData = worldMapBZip2State3.outputData;
            int outputOffset = worldMapBZip2State3.outputOffset;
            int outputRemaining = worldMapBZip2State3.outputRemaining;
            int outputRemaining2 = worldMapBZip2State3.outputRemaining;
            int end = worldMapBZip2State3.nblock + 1;

            decompressionOutputLoop:
            while (true) {
               if (sourceOutputRunLength > 0) {
                  while (true) {
                     if (outputRemaining == 0) {
                        break decompressionOutputLoop;
                     }

                     if (sourceOutputRunLength == 1) {
                        if (outputRemaining == 0) {
                           sourceOutputRunLength = 1;
                           break decompressionOutputLoop;
                        }

                        outputData[outputOffset] = (byte)outputByte;
                        outputOffset++;
                        outputRemaining--;
                        break;
                     }

                     outputData[outputOffset] = (byte)outputByte;
                     sourceOutputRunLength--;
                     outputOffset++;
                     outputRemaining--;
                  }
               }

               boolean continueScan = true;
               byte runByte;

               while (continueScan) {
                  continueScan = false;
                  if (nblockOrNblockUsed == end) {
                     sourceOutputRunLength = 0;
                     break decompressionOutputLoop;
                  }

                  outputByte = (byte)selectorEntry;
                  int ttEntry2;
                  byte byteCode2 = (byte)(ttEntry2 = tt[tPos]);
                  tPos = ttEntry2 >> 8;
                  nblockOrNblockUsed++;
                  if (byteCode2 != selectorEntry) {
                     selectorEntry = byteCode2;
                     if (outputRemaining == 0) {
                        sourceOutputRunLength = 1;
                        break decompressionOutputLoop;
                     }

                     outputData[outputOffset] = (byte)outputByte;
                     outputOffset++;
                     outputRemaining--;
                     continueScan = true;
                  } else if (nblockOrNblockUsed == end) {
                     if (outputRemaining == 0) {
                        sourceOutputRunLength = 1;
                        break decompressionOutputLoop;
                     }

                     outputData[outputOffset] = (byte)outputByte;
                     outputOffset++;
                     outputRemaining--;
                     continueScan = true;
                  }
               }

               sourceOutputRunLength = 2;
               int ttEntry3;
               byte byteCode3 = (byte)(ttEntry3 = tt[tPos]);
               tPos = ttEntry3 >> 8;
               if (++nblockOrNblockUsed != end) {
                  if (byteCode3 != selectorEntry) {
                     selectorEntry = byteCode3;
                  } else {
                     sourceOutputRunLength = 3;
                     int ttEntry4;
                     runByte = (byte)(ttEntry4 = tt[tPos]);
                     tPos = ttEntry4 >> 8;
                     if (++nblockOrNblockUsed != end) {
                        if (runByte != selectorEntry) {
                           selectorEntry = runByte;
                        } else {
                           int ttEntry5;
                           runByte = (byte)(ttEntry5 = tt[tPos]);
                           tPos = ttEntry5 >> 8;
                           nblockOrNblockUsed++;
                           sourceOutputRunLength = (runByte & 255) + 4;
                           int ttEntry6;
                           selectorEntry = (byte)(ttEntry6 = tt[tPos]);
                           tPos = ttEntry6 >> 8;
                           nblockOrNblockUsed++;
                        }
                     }
                  }
               }
            }

            nInUse2 = worldMapBZip2State3.totalOutLo32;
            worldMapBZip2State3.totalOutLo32 += outputRemaining2 - outputRemaining;
            if (worldMapBZip2State3.totalOutLo32 < nInUse2) {
               worldMapBZip2State3.totalOutHi32++;
            }

            worldMapBZip2State3.outputByte = (byte)outputByte;
            worldMapBZip2State3.outputRunLength = sourceOutputRunLength;
            worldMapBZip2State3.nblockUsed = nblockOrNblockUsed;
            worldMapBZip2State3.k0 = selectorEntry;
            WorldMapBZip2State.tt = tt;
            worldMapBZip2State3.tPos = tPos;
            worldMapBZip2State3.outputData = outputData;
            worldMapBZip2State3.outputOffset = outputOffset;
            worldMapBZip2State3.outputRemaining = outputRemaining;
            if (worldMapBZip2State.nblockUsed == worldMapBZip2State.nblock + 1 && worldMapBZip2State.outputRunLength == 0) {
               localFlag = true;
            } else {
               localFlag = false;
            }
         }

         int scalar5;
         return scalar5 = newOutputRemaining - state.outputRemaining;
      }
   }
   private static int readBits(int minLen, WorldMapBZip2State worldMapBZip2State) {
      while (worldMapBZip2State.liveBits < minLen) {
         worldMapBZip2State.bitBuffer = worldMapBZip2State.bitBuffer << 8 | worldMapBZip2State.inputData[worldMapBZip2State.inputOffset] & 255;
         worldMapBZip2State.liveBits += 8;
         worldMapBZip2State.inputOffset++;
         worldMapBZip2State.inputRemaining--;
         worldMapBZip2State.totalInLo32++;
         if (worldMapBZip2State.totalInLo32 == 0) {
            worldMapBZip2State.totalInHi32++;
         }
      }

      int localBitBuffer = worldMapBZip2State.bitBuffer >> worldMapBZip2State.liveBits - minLen & (1 << minLen) - 1;
      worldMapBZip2State.liveBits -= minLen;
      return localBitBuffer;
   }
}
