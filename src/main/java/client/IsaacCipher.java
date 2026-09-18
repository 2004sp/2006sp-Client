package client;
public final class IsaacCipher {
   private int count;
   private final int[] results = new int[256];
   private final int[] memory = new int[256];
   private int accumulator;
   private int lastResult;
   private int counter;

   public IsaacCipher(int[] values) {
      System.arraycopy(values, 0, this.results, 0, 4);
      this.initialize();
   }
   public final int nextInt() {
      if (this.count-- == 0) {
         this.generateResults();
         this.count = 255;
      }

      return this.results[this.count];
   }
   private void generateResults() {
      this.lastResult = this.lastResult + ++this.counter;

      for (int memoryIndex = 0; memoryIndex < 256; memoryIndex++) {
         int memoryEntry = this.memory[memoryIndex];
         if ((memoryIndex & 3) == 0) {
            this.accumulator = this.accumulator ^ this.accumulator << 13;
         } else if ((memoryIndex & 3) == 1) {
            this.accumulator = this.accumulator ^ this.accumulator >>> 6;
         } else if ((memoryIndex & 3) == 2) {
            this.accumulator = this.accumulator ^ this.accumulator << 2;
         } else if ((memoryIndex & 3) == 3) {
            this.accumulator = this.accumulator ^ this.accumulator >>> 16;
         }

         this.accumulator = this.accumulator + this.memory[memoryIndex + 128 & 0xFF];
         int memory2;
         this.memory[memoryIndex] = memory2 = this.memory[(memoryEntry & 1020) >> 2] + this.accumulator + this.lastResult;
         this.results[memoryIndex] = this.lastResult = this.memory[(memory2 >> 8 & 1020) >> 2] + memoryEntry;
      }
   }
   private void initialize() {
      int localMemory = -1640531527;
      int memory2 = -1640531527;
      int memory3 = -1640531527;
      int memory4 = -1640531527;
      int memory5 = -1640531527;
      int memory6 = -1640531527;
      int memory7 = -1640531527;
      int memory8 = -1640531527;

      for (int loopIndex = 0; loopIndex < 4; loopIndex++) {
         memory8 ^= memory7 << 11;
         int scalar = memory5 + memory8;
         int scalar2;
         int scalar3 = (scalar2 = memory7 + memory6) ^ memory6 >>> 2;
         int scalar4 = memory4 + scalar3;
         int scalar5;
         memory6 = (scalar5 = memory6 + scalar) ^ scalar << 8;
         int scalar6 = memory3 + memory6;
         int scalar7;
         memory5 = (scalar7 = scalar + scalar4) ^ scalar4 >>> 16;
         int scalar8 = memory2 + memory5;
         int scalar9;
         memory4 = (scalar9 = scalar4 + scalar6) ^ scalar6 << 10;
         int scalar10 = localMemory + memory4;
         int scalar11;
         memory3 = (scalar11 = scalar6 + scalar8) ^ scalar8 >>> 4;
         memory8 += memory3;
         int scalar12;
         memory2 = (scalar12 = scalar8 + scalar10) ^ scalar10 << 8;
         memory7 = scalar3 + memory2;
         int scalar13;
         localMemory = (scalar13 = scalar10 + memory8) ^ memory8 >>> 9;
         memory6 += localMemory;
         memory8 += memory7;
      }

      for (int resultIndex = 0; resultIndex < 256; resultIndex += 8) {
         int scalar14 = memory8 + this.results[resultIndex];
         int scalar15 = memory7 + this.results[resultIndex + 1];
         int scalar16 = memory6 + this.results[resultIndex + 2];
         int scalar17 = memory5 + this.results[resultIndex + 3];
         int scalar18 = memory4 + this.results[resultIndex + 4];
         int scalar19 = memory3 + this.results[resultIndex + 5];
         int scalar20 = memory2 + this.results[resultIndex + 6];
         int scalar21 = localMemory + this.results[resultIndex + 7];
         int scalar22 = scalar14 ^ scalar15 << 11;
         int scalar23 = scalar17 + scalar22;
         int scalar24;
         int scalar25 = (scalar24 = scalar15 + scalar16) ^ scalar16 >>> 2;
         int scalar26 = scalar18 + scalar25;
         int scalar27;
         int scalar28 = (scalar27 = scalar16 + scalar23) ^ scalar23 << 8;
         int scalar29 = scalar19 + scalar28;
         int scalar30;
         memory5 = (scalar30 = scalar23 + scalar26) ^ scalar26 >>> 16;
         int scalar31 = scalar20 + memory5;
         int scalar32;
         memory4 = (scalar32 = scalar26 + scalar29) ^ scalar29 << 10;
         int scalar33 = scalar21 + memory4;
         int scalar34;
         memory3 = (scalar34 = scalar29 + scalar31) ^ scalar31 >>> 4;
         int scalar35 = scalar22 + memory3;
         int scalar36;
         memory2 = (scalar36 = scalar31 + scalar33) ^ scalar33 << 8;
         memory7 = scalar25 + memory2;
         int scalar37;
         localMemory = (scalar37 = scalar33 + scalar35) ^ scalar35 >>> 9;
         memory6 = scalar28 + localMemory;
         memory8 = scalar35 + memory7;
         this.memory[resultIndex] = memory8;
         this.memory[resultIndex + 1] = memory7;
         this.memory[resultIndex + 2] = memory6;
         this.memory[resultIndex + 3] = memory5;
         this.memory[resultIndex + 4] = memory4;
         this.memory[resultIndex + 5] = memory3;
         this.memory[resultIndex + 6] = memory2;
         this.memory[resultIndex + 7] = localMemory;
      }

      for (int memoryIndex = 0; memoryIndex < 256; memoryIndex += 8) {
         int scalar38 = memory8 + this.memory[memoryIndex];
         int scalar39 = memory7 + this.memory[memoryIndex + 1];
         int scalar40 = memory6 + this.memory[memoryIndex + 2];
         int scalar41 = memory5 + this.memory[memoryIndex + 3];
         int scalar42 = memory4 + this.memory[memoryIndex + 4];
         int scalar43 = memory3 + this.memory[memoryIndex + 5];
         int scalar44 = memory2 + this.memory[memoryIndex + 6];
         int scalar45 = localMemory + this.memory[memoryIndex + 7];
         int scalar46 = scalar38 ^ scalar39 << 11;
         int scalar47 = scalar41 + scalar46;
         int scalar48;
         int scalar49 = (scalar48 = scalar39 + scalar40) ^ scalar40 >>> 2;
         int scalar50 = scalar42 + scalar49;
         int scalar51;
         int scalar52 = (scalar51 = scalar40 + scalar47) ^ scalar47 << 8;
         int scalar53 = scalar43 + scalar52;
         int scalar54;
         memory5 = (scalar54 = scalar47 + scalar50) ^ scalar50 >>> 16;
         int scalar55 = scalar44 + memory5;
         int scalar56;
         memory4 = (scalar56 = scalar50 + scalar53) ^ scalar53 << 10;
         int scalar57 = scalar45 + memory4;
         int scalar58;
         memory3 = (scalar58 = scalar53 + scalar55) ^ scalar55 >>> 4;
         int scalar59 = scalar46 + memory3;
         int scalar60;
         memory2 = (scalar60 = scalar55 + scalar57) ^ scalar57 << 8;
         memory7 = scalar49 + memory2;
         int scalar61;
         localMemory = (scalar61 = scalar57 + scalar59) ^ scalar59 >>> 9;
         memory6 = scalar52 + localMemory;
         memory8 = scalar59 + memory7;
         this.memory[memoryIndex] = memory8;
         this.memory[memoryIndex + 1] = memory7;
         this.memory[memoryIndex + 2] = memory6;
         this.memory[memoryIndex + 3] = memory5;
         this.memory[memoryIndex + 4] = memory4;
         this.memory[memoryIndex + 5] = memory3;
         this.memory[memoryIndex + 6] = memory2;
         this.memory[memoryIndex + 7] = localMemory;
      }

      this.generateResults();
      this.count = 256;
   }
}
