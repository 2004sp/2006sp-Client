package client;
final class BZip2Decompressor {
   private static final BZip2State state = new BZip2State();
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
         BZip2State bZip2State = state;
         boolean flag = false;
         BZip2State bZip2State3 = null;
         byte decodedBits8;
         bZip2State.blockSize100k = 1;
         if (BZip2State.tt == null) {
            BZip2State.tt = new int[bZip2State.blockSize100k * 100000];
         }

         boolean continueBlocks = true;

         while (continueBlocks) {
            BZip2State bZip2State2 = bZip2State;
            if ((byte)readBits(8, bZip2State2) == 23) {
               break;
            }

            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State.currentBlock++;
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            decodedBits8 = (byte)readBits(8, bZip2State2);
            bZip2State2 = bZip2State;
            byte localBlockRandomized = (byte)readBits(1, bZip2State2);
            bZip2State.blockRandomized = localBlockRandomized != 0;
            if (bZip2State.blockRandomized) {
               System.out.println("PANIC! RANDOMISED BLOCK!");
            }

            bZip2State.originalPointer = 0;
            bZip2State2 = bZip2State;
            byte decodedBits2 = (byte)readBits(8, bZip2State2);
            bZip2State.originalPointer = bZip2State.originalPointer << 8 | decodedBits2 & 255;
            bZip2State2 = bZip2State;
            byte decodedBits3 = (byte)readBits(8, bZip2State2);
            bZip2State.originalPointer = bZip2State.originalPointer << 8 | decodedBits3 & 255;
            bZip2State2 = bZip2State;
            byte decodedBits4 = (byte)readBits(8, bZip2State2);
            bZip2State.originalPointer = bZip2State.originalPointer << 8 | decodedBits4 & 255;

            for (int loopIndex = 0; loopIndex < 16; loopIndex++) {
               bZip2State2 = bZip2State;
               bZip2State.inUse16[loopIndex] = (byte)readBits(1, bZip2State2) == 1;
            }

            for (int inUseIndex = 0; inUseIndex < 256; inUseIndex++) {
               bZip2State.inUse[inUseIndex] = false;
            }

            for (int loopIndex2 = 0; loopIndex2 < 16; loopIndex2++) {
               if (bZip2State.inUse16[loopIndex2]) {
                  for (int loopIndex3 = 0; loopIndex3 < 16; loopIndex3++) {
                     bZip2State2 = bZip2State;
                     if ((byte)readBits(1, bZip2State2) == 1) {
                        bZip2State.inUse[(loopIndex2 << 4) + loopIndex3] = true;
                     }
                  }
               }
            }

            bZip2State3 = bZip2State;
            bZip2State.nInUse = 0;

            for (int inUseIndex2 = 0; inUseIndex2 < 256; inUseIndex2++) {
               if (bZip2State3.inUse[inUseIndex2]) {
                  bZip2State3.seqToUnseq[bZip2State3.nInUse] = (byte)inUseIndex2;
                  bZip2State3.nInUse++;
               }
            }

            int localNInUse = bZip2State.nInUse + 2;
            int groupCount = readBits(3, bZip2State);
            int decodedBits = readBits(15, bZip2State);

            for (int selectorMtfIndex = 0; selectorMtfIndex < decodedBits; selectorMtfIndex++) {
               int scalar = 0;

               while (true) {
                  bZip2State2 = bZip2State;
                  if ((byte)readBits(1, bZip2State2) == 0) {
                     bZip2State.selectorMtf[selectorMtfIndex] = (byte)scalar;
                     break;
                  }

                  scalar++;
               }
            }

            byte[] selectorMtf = new byte[6];
            int sourceOutputRunLength = 0;

            while (sourceOutputRunLength < groupCount) {
               selectorMtf[sourceOutputRunLength] = (byte)sourceOutputRunLength++;
            }

            for (int selectorMtfIndex2 = 0; selectorMtfIndex2 < decodedBits; selectorMtfIndex2++) {
               byte selectorMtfEntry = bZip2State.selectorMtf[selectorMtfIndex2];
               sourceOutputRunLength = selectorMtf[selectorMtfEntry];

               while (selectorMtfEntry > 0) {
                  selectorMtf[selectorMtfEntry] = selectorMtf[selectorMtfEntry - 1];
                  selectorMtfEntry--;
               }

               selectorMtf[0] = (byte)sourceOutputRunLength;
               bZip2State.selector[selectorMtfIndex2] = (byte)sourceOutputRunLength;
            }

            for (int codeLengthIndex = 0; codeLengthIndex < groupCount; codeLengthIndex++) {
               int readBits2 = readBits(5, bZip2State);

               for (int loopIndex4 = 0; loopIndex4 < localNInUse; loopIndex4++) {
                  while (true) {
                     bZip2State2 = bZip2State;
                     if ((byte)readBits(1, bZip2State2) == 0) {
                        bZip2State.codeLengths[codeLengthIndex][loopIndex4] = (byte)readBits2;
                        break;
                     }

                     bZip2State2 = bZip2State;
                     if ((byte)readBits(1, bZip2State2) == 0) {
                        readBits2++;
                     } else {
                        readBits2--;
                     }
                  }
               }
            }

            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
               byte minLenOrCodeLengths = 32;
               sourceOutputRunLength = (byte)0;

               for (int loopIndex5 = 0; loopIndex5 < localNInUse; loopIndex5++) {
                  if (bZip2State.codeLengths[groupIndex][loopIndex5] > sourceOutputRunLength) {
                     sourceOutputRunLength = bZip2State.codeLengths[groupIndex][loopIndex5];
                  }

                  if (bZip2State.codeLengths[groupIndex][loopIndex5] < minLenOrCodeLengths) {
                     minLenOrCodeLengths = bZip2State.codeLengths[groupIndex][loopIndex5];
                  }
               }

               int[] huffmanLimitEntry = bZip2State.huffmanLimit[groupIndex];
               int[] huffmanBaseEntry = bZip2State.huffmanBase[groupIndex];
               int[] huffmanPermEntry = bZip2State.huffmanPerm[groupIndex];
               byte[] codeLength = bZip2State.codeLengths[groupIndex];
               int sourceLocalNInUse = localNInUse;
               byte loopIndex6 = (byte)sourceOutputRunLength;
               byte position = minLenOrCodeLengths;
               byte[] sourceCodeLength = codeLength;
               int[] values = huffmanPermEntry;
               int[] huffmanBase = huffmanBaseEntry;
               int[] integerBuffer = huffmanLimitEntry;
               int scalar2 = 0;

               for (int loopIndex7 = position; loopIndex7 <= loopIndex6; loopIndex7++) {
                  for (int loopIndex8 = 0; loopIndex8 < sourceLocalNInUse; loopIndex8++) {
                     if (sourceCodeLength[loopIndex8] == loopIndex7) {
                        values[scalar2] = loopIndex8;
                        scalar2++;
                     }
                  }
               }

               for (int huffmanBaseIndex = 0; huffmanBaseIndex < 23; huffmanBaseIndex++) {
                  huffmanBase[huffmanBaseIndex] = 0;
               }

               for (int loopIndex9 = 0; loopIndex9 < sourceLocalNInUse; loopIndex9++) {
                  huffmanBase[sourceCodeLength[loopIndex9] + 1]++;
               }

               for (int huffmanBaseIndex2 = 1; huffmanBaseIndex2 < 23; huffmanBaseIndex2++) {
                  huffmanBase[huffmanBaseIndex2] += huffmanBase[huffmanBaseIndex2 - 1];
               }

               for (int loopIndex10 = 0; loopIndex10 < 23; loopIndex10++) {
                  integerBuffer[loopIndex10] = 0;
               }

               int scalar3 = 0;

               for (int huffmanBaseIndex3 = position; huffmanBaseIndex3 <= loopIndex6; huffmanBaseIndex3++) {
                  scalar3 += huffmanBase[huffmanBaseIndex3 + 1] - huffmanBase[huffmanBaseIndex3];
                  integerBuffer[huffmanBaseIndex3] = scalar3 - 1;
                  scalar3 <<= 1;
               }

               for (int huffmanBaseIndex4 = position + 1; huffmanBaseIndex4 <= loopIndex6; huffmanBaseIndex4++) {
                  huffmanBase[huffmanBaseIndex4] = (integerBuffer[huffmanBaseIndex4 - 1] + 1 << 1) - huffmanBase[huffmanBaseIndex4];
               }

               bZip2State.minLens[groupIndex] = minLenOrCodeLengths;
            }

            int nInUse2 = bZip2State.nInUse + 1;
            int selectorIndex = -1;

            for (int unzftabIndex = 0; unzftabIndex <= 255; unzftabIndex++) {
               bZip2State.unzftab[unzftabIndex] = 0;
            }

            int mtfaIndex = 4095;

            for (int mtfbaseIndex = 15; mtfbaseIndex >= 0; mtfbaseIndex--) {
               for (int loopIndex11 = 15; loopIndex11 >= 0; loopIndex11--) {
                  bZip2State.mtfa[mtfaIndex] = (byte)((mtfbaseIndex << 4) + loopIndex11);
                  mtfaIndex--;
               }

               bZip2State.mtfbase[mtfbaseIndex] = mtfaIndex + 1;
            }

            int nblockOrNblockUsed = 0;
            selectorIndex++;
            int selectorEntry = bZip2State.selector[0];
            int minLen = bZip2State.minLens[selectorEntry];
            int[] huffmanLimit = bZip2State.huffmanLimit[selectorEntry];
            int[] huffmanPerm = bZip2State.huffmanPerm[selectorEntry];
            int[] huffmanBase2 = bZip2State.huffmanBase[selectorEntry];
            sourceOutputRunLength = 49;
            int huffmanLimitIndex = minLen;
            selectorEntry = readBits(minLen, bZip2State);

            while (selectorEntry > huffmanLimit[huffmanLimitIndex]) {
               huffmanLimitIndex++;
               bZip2State2 = bZip2State;
               byte decodedBits5 = (byte)readBits(1, bZip2State2);
               selectorEntry = selectorEntry << 1 | decodedBits5;
            }

            huffmanLimitIndex = (int)huffmanPerm[selectorEntry - huffmanBase2[huffmanLimitIndex]];

            while (huffmanLimitIndex != nInUse2) {
               if (huffmanLimitIndex != 0 && huffmanLimitIndex != 1) {
                  byte localMtfa;
                  if ((selectorEntry = huffmanLimitIndex - 1) < 16) {
                     int mtfbaseEntry = bZip2State.mtfbase[0];
                     localMtfa = bZip2State.mtfa[mtfbaseEntry + selectorEntry];

                     while (selectorEntry > 3) {
                        huffmanLimitIndex = mtfbaseEntry + selectorEntry;
                        bZip2State.mtfa[huffmanLimitIndex] = bZip2State.mtfa[huffmanLimitIndex - 1];
                        bZip2State.mtfa[huffmanLimitIndex - 1] = bZip2State.mtfa[huffmanLimitIndex - 2];
                        bZip2State.mtfa[huffmanLimitIndex - 2] = bZip2State.mtfa[huffmanLimitIndex - 3];
                        bZip2State.mtfa[huffmanLimitIndex - 3] = bZip2State.mtfa[huffmanLimitIndex - 4];
                        selectorEntry -= 4;
                     }

                     while (selectorEntry > 0) {
                        bZip2State.mtfa[mtfbaseEntry + selectorEntry] = bZip2State.mtfa[mtfbaseEntry + selectorEntry - 1];
                        selectorEntry--;
                     }

                     bZip2State.mtfa[mtfbaseEntry] = localMtfa;
                  } else {
                     int mtfbaseIndex2 = selectorEntry / 16;
                     huffmanLimitIndex = selectorEntry % 16;
                     huffmanLimitIndex = bZip2State.mtfbase[mtfbaseIndex2] + huffmanLimitIndex;
                     localMtfa = bZip2State.mtfa[huffmanLimitIndex];

                     while (huffmanLimitIndex > bZip2State.mtfbase[mtfbaseIndex2]) {
                        bZip2State.mtfa[huffmanLimitIndex] = bZip2State.mtfa[huffmanLimitIndex - 1];
                        huffmanLimitIndex--;
                     }

                     bZip2State.mtfbase[mtfbaseIndex2]++;

                     while (mtfbaseIndex2 > 0) {
                        bZip2State.mtfbase[mtfbaseIndex2]--;
                        bZip2State.mtfa[bZip2State.mtfbase[mtfbaseIndex2]] = bZip2State.mtfa[bZip2State.mtfbase[mtfbaseIndex2 - 1] + 16 - 1];
                        mtfbaseIndex2--;
                     }

                     bZip2State.mtfbase[0]--;
                     bZip2State.mtfa[bZip2State.mtfbase[0]] = localMtfa;
                     if (bZip2State.mtfbase[0] == 0) {
                        huffmanLimitIndex = 4095;

                        for (int mtfbaseIndex3 = 15; mtfbaseIndex3 >= 0; mtfbaseIndex3--) {
                           for (int loopIndex12 = 15; loopIndex12 >= 0; loopIndex12--) {
                              bZip2State.mtfa[huffmanLimitIndex] = bZip2State.mtfa[bZip2State.mtfbase[mtfbaseIndex3] + loopIndex12];
                              huffmanLimitIndex--;
                           }

                           bZip2State.mtfbase[mtfbaseIndex3] = huffmanLimitIndex + 1;
                        }
                     }
                  }

                  bZip2State.unzftab[bZip2State.seqToUnseq[localMtfa & 0xFF] & 0xFF]++;
                  BZip2State.tt[nblockOrNblockUsed] = bZip2State.seqToUnseq[localMtfa & 0xFF] & 255;
                  nblockOrNblockUsed++;
                  if (sourceOutputRunLength == 0) {
                     selectorIndex++;
                     sourceOutputRunLength = 50;
                     byte selectorEntry2 = bZip2State.selector[selectorIndex];
                     minLen = bZip2State.minLens[selectorEntry2];
                     huffmanLimit = bZip2State.huffmanLimit[selectorEntry2];
                     huffmanPerm = bZip2State.huffmanPerm[selectorEntry2];
                     huffmanBase2 = bZip2State.huffmanBase[selectorEntry2];
                  }

                  sourceOutputRunLength--;
                  int sourceMinLen = minLen;
                  huffmanLimitIndex = readBits(minLen, bZip2State);

                  while (huffmanLimitIndex > huffmanLimit[sourceMinLen]) {
                     sourceMinLen++;
                     bZip2State2 = bZip2State;
                     byte decodedBits6 = (byte)readBits(1, bZip2State2);
                     huffmanLimitIndex = huffmanLimitIndex << 1 | decodedBits6;
                  }

                  huffmanLimitIndex = (int)huffmanPerm[huffmanLimitIndex - huffmanBase2[sourceMinLen]];
               } else {
                  selectorEntry = -1;
                  int scalar4 = 1;

                  int huffmanLimitIndex2;
                  do {
                     if (huffmanLimitIndex == 0) {
                        selectorEntry += scalar4;
                     } else if (huffmanLimitIndex == 1) {
                        selectorEntry += 2 * scalar4;
                     }

                     scalar4 <<= 1;
                     if (sourceOutputRunLength == 0) {
                        selectorIndex++;
                        sourceOutputRunLength = 50;
                        byte selectorEntry3 = bZip2State.selector[selectorIndex];
                        minLen = bZip2State.minLens[selectorEntry3];
                        huffmanLimit = bZip2State.huffmanLimit[selectorEntry3];
                        huffmanPerm = bZip2State.huffmanPerm[selectorEntry3];
                        huffmanBase2 = bZip2State.huffmanBase[selectorEntry3];
                     }

                     sourceOutputRunLength--;
                     huffmanLimitIndex2 = minLen;
                     huffmanLimitIndex = readBits(minLen, bZip2State);

                     while (huffmanLimitIndex > huffmanLimit[huffmanLimitIndex2]) {
                        huffmanLimitIndex2++;
                        bZip2State2 = bZip2State;
                        byte decodedBits7 = (byte)readBits(1, bZip2State2);
                        huffmanLimitIndex = huffmanLimitIndex << 1 | decodedBits7;
                     }
                  } while ((huffmanLimitIndex = (int)huffmanPerm[huffmanLimitIndex - huffmanBase2[huffmanLimitIndex2]]) == 0 || huffmanLimitIndex == 1);

                  selectorEntry++;
                  byte seqToUnseqEntry = bZip2State.seqToUnseq[bZip2State.mtfa[bZip2State.mtfbase[0]] & 0xFF];

                  for (bZip2State.unzftab[seqToUnseqEntry & 0xFF] = bZip2State.unzftab[seqToUnseqEntry & 0xFF] + selectorEntry; selectorEntry > 0; selectorEntry--) {
                     BZip2State.tt[nblockOrNblockUsed] = seqToUnseqEntry & 255;
                     nblockOrNblockUsed++;
                  }
               }
            }

            bZip2State.outputRunLength = 0;
            bZip2State.outputByte = 0;
            bZip2State.cftab[0] = 0;

            for (int cftabIndex = 1; cftabIndex <= 256; cftabIndex++) {
               bZip2State.cftab[cftabIndex] = bZip2State.unzftab[cftabIndex - 1];
            }

            for (int cftabIndex2 = 1; cftabIndex2 <= 256; cftabIndex2++) {
               bZip2State.cftab[cftabIndex2] = bZip2State.cftab[cftabIndex2] + bZip2State.cftab[cftabIndex2 - 1];
            }

            for (int ttIndex = 0; ttIndex < nblockOrNblockUsed; ttIndex++) {
               byte ttEntry = (byte)BZip2State.tt[ttIndex];
               BZip2State.tt[bZip2State.cftab[ttEntry & 0xFF]] = BZip2State.tt[bZip2State.cftab[ttEntry & 0xFF]] | ttIndex << 8;
               bZip2State.cftab[ttEntry & 0xFF]++;
            }

            bZip2State.tPos = BZip2State.tt[bZip2State.originalPointer] >> 8;
            bZip2State.nblockUsed = 0;
            bZip2State.tPos = BZip2State.tt[bZip2State.tPos];
            bZip2State.k0 = (byte)bZip2State.tPos;
            bZip2State.tPos >>= 8;
            bZip2State.nblockUsed++;
            bZip2State.nblock = nblockOrNblockUsed;
            bZip2State3 = bZip2State;
            int outputByte = bZip2State.outputByte;
            sourceOutputRunLength = bZip2State3.outputRunLength;
            nblockOrNblockUsed = bZip2State3.nblockUsed;
            huffmanLimitIndex = bZip2State3.k0;
            int[] tt = BZip2State.tt;
            int tPos = bZip2State3.tPos;
            byte[] outputData = bZip2State3.outputData;
            int outputOffset = bZip2State3.outputOffset;
            int initialRemainingOrOutputRemaining = bZip2State3.outputRemaining;
            int initialRemaining = initialRemainingOrOutputRemaining;
            int end = bZip2State3.nblock + 1;

            writeOutputRuns:
            while (true) {
               if (sourceOutputRunLength > 0) {
                  while (true) {
                     if (initialRemainingOrOutputRemaining == 0) {
                        break writeOutputRuns;
                     }

                     if (sourceOutputRunLength == 1) {
                        if (initialRemainingOrOutputRemaining == 0) {
                           sourceOutputRunLength = 1;
                           break writeOutputRuns;
                        }

                        outputData[outputOffset] = (byte)outputByte;
                        outputOffset++;
                        initialRemainingOrOutputRemaining--;
                        break;
                     }

                     outputData[outputOffset] = (byte)outputByte;
                     sourceOutputRunLength--;
                     outputOffset++;
                     initialRemainingOrOutputRemaining--;
                  }
               }

               boolean continueScan = true;
               byte runByte;

               while (continueScan) {
                  continueScan = false;
                  if (nblockOrNblockUsed == end) {
                     sourceOutputRunLength = 0;
                     break writeOutputRuns;
                  }

                  outputByte = (byte)huffmanLimitIndex;
                  int ttEntry2;
                  byte sourceHuffmanLimitIndex = (byte)(ttEntry2 = tt[tPos]);
                  tPos = ttEntry2 >> 8;
                  nblockOrNblockUsed++;
                  if (sourceHuffmanLimitIndex != huffmanLimitIndex) {
                     huffmanLimitIndex = sourceHuffmanLimitIndex;
                     if (initialRemainingOrOutputRemaining == 0) {
                        sourceOutputRunLength = 1;
                        break writeOutputRuns;
                     }

                     outputData[outputOffset] = (byte)outputByte;
                     outputOffset++;
                     initialRemainingOrOutputRemaining--;
                     continueScan = true;
                  } else if (nblockOrNblockUsed == end) {
                     if (initialRemainingOrOutputRemaining == 0) {
                        sourceOutputRunLength = 1;
                        break writeOutputRuns;
                     }

                     outputData[outputOffset] = (byte)outputByte;
                     outputOffset++;
                     initialRemainingOrOutputRemaining--;
                     continueScan = true;
                  }
               }

               sourceOutputRunLength = 2;
               int ttEntry3;
               byte sourceHuffmanLimitIndex2 = (byte)(ttEntry3 = tt[tPos]);
               tPos = ttEntry3 >> 8;
               if (++nblockOrNblockUsed != end) {
                  if (sourceHuffmanLimitIndex2 != huffmanLimitIndex) {
                     huffmanLimitIndex = sourceHuffmanLimitIndex2;
                  } else {
                     sourceOutputRunLength = 3;
                     int ttEntry4;
                     runByte = (byte)(ttEntry4 = tt[tPos]);
                     tPos = ttEntry4 >> 8;
                     if (++nblockOrNblockUsed != end) {
                        if (runByte != huffmanLimitIndex) {
                           huffmanLimitIndex = runByte;
                        } else {
                           int ttEntry5;
                           runByte = (byte)(ttEntry5 = tt[tPos]);
                           int ttIndex2 = ttEntry5 >> 8;
                           nblockOrNblockUsed++;
                           sourceOutputRunLength = (runByte & 255) + 4;
                           int ttEntry6;
                           huffmanLimitIndex = (byte)(ttEntry6 = tt[ttIndex2]);
                           tPos = ttEntry6 >> 8;
                           nblockOrNblockUsed++;
                        }
                     }
                  }
               }
            }

            int previousTotalOut = bZip2State3.totalOutLo32;
            bZip2State3.totalOutLo32 += initialRemaining - initialRemainingOrOutputRemaining;
            if (bZip2State3.totalOutLo32 < previousTotalOut) {
               bZip2State3.totalOutHi32++;
            }

            bZip2State3.outputByte = (byte)outputByte;
            bZip2State3.outputRunLength = sourceOutputRunLength;
            bZip2State3.nblockUsed = nblockOrNblockUsed;
            bZip2State3.k0 = huffmanLimitIndex;
            BZip2State.tt = tt;
            bZip2State3.tPos = tPos;
            bZip2State3.outputData = outputData;
            bZip2State3.outputOffset = outputOffset;
            bZip2State3.outputRemaining = initialRemainingOrOutputRemaining;
            continueBlocks = bZip2State.nblockUsed == bZip2State.nblock + 1 && bZip2State.outputRunLength == 0;
         }

         int scalar5;
         return scalar5 = newOutputRemaining - state.outputRemaining;
      }
   }
   private static int readBits(int minLen, BZip2State bZip2State) {
      while (bZip2State.liveBits < minLen) {
         bZip2State.bitBuffer = bZip2State.bitBuffer << 8 | bZip2State.inputData[bZip2State.inputOffset] & 255;
         bZip2State.liveBits += 8;
         bZip2State.inputOffset++;
         bZip2State.inputRemaining--;
         bZip2State.totalInLo32++;
         if (bZip2State.totalInLo32 == 0) {
            bZip2State.totalInHi32++;
         }
      }

      int localBitBuffer = bZip2State.bitBuffer >> bZip2State.liveBits - minLen & (1 << minLen) - 1;
      bZip2State.liveBits -= minLen;
      return localBitBuffer;
   }
}
