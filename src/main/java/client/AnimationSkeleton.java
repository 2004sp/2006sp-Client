package client;
public class AnimationSkeleton {
   public final int[] transformTypes;
   public final int[][] labels;
   public final int transformCount;
   static final void copyBytes(byte[] buffer, int positionArgument, byte[] newData, int newIndex, int length) {
      if (buffer != newData) {
         length -= 7;

         while (positionArgument < length) {
            newData[newIndex++] = buffer[positionArgument++];
            newData[newIndex++] = buffer[positionArgument++];
            newData[newIndex++] = buffer[positionArgument++];
            newData[newIndex++] = buffer[positionArgument++];
            newData[newIndex++] = buffer[positionArgument++];
            newData[newIndex++] = buffer[positionArgument++];
            newData[newIndex++] = buffer[positionArgument++];
            newData[newIndex++] = buffer[positionArgument++];
         }

         length += 7;

         while (positionArgument < length) {
            newData[newIndex++] = buffer[positionArgument++];
         }
      }
   }

   public AnimationSkeleton(Buffer buffer, int sourceTransformCount) {
      if (sourceTransformCount == 474) {
         this.transformCount = buffer.readUnsignedByte();
         this.transformTypes = new int[this.transformCount];
         this.labels = new int[this.transformCount][];

         for (int transformIndex = 0; transformIndex < this.transformCount; transformIndex++) {
            this.transformTypes[transformIndex] = buffer.readUnsignedByte();
         }

         for (int labelIndex = 0; labelIndex < this.transformCount; labelIndex++) {
            this.labels[labelIndex] = new int[buffer.readUnsignedByte()];
         }

         for (int transformIndex2 = 0; transformIndex2 < this.transformCount; transformIndex2++) {
            for (int loopIndex = 0; loopIndex < this.labels[transformIndex2].length; loopIndex++) {
               this.labels[transformIndex2][loopIndex] = buffer.readUnsignedByte();
            }
         }
      } else {
         sourceTransformCount = buffer.readUnsignedShort();
         this.transformCount = sourceTransformCount;
         this.transformTypes = new int[sourceTransformCount];
         this.labels = new int[sourceTransformCount][];

         for (int transformTypeIndex = 0; transformTypeIndex < sourceTransformCount; transformTypeIndex++) {
            this.transformTypes[transformTypeIndex] = buffer.readUnsignedShort();
         }

         for (int labelIndex2 = 0; labelIndex2 < sourceTransformCount; labelIndex2++) {
            this.labels[labelIndex2] = new int[buffer.readUnsignedShort()];
         }

         for (int labelIndex3 = 0; labelIndex3 < sourceTransformCount; labelIndex3++) {
            for (int loopIndex2 = 0; loopIndex2 < this.labels[labelIndex3].length; loopIndex2++) {
               this.labels[labelIndex3][loopIndex2] = buffer.readUnsignedShort();
            }
         }
      }
   }

   public AnimationSkeleton(Buffer buffer) {
      this.transformCount = buffer.readUnsignedByte();
      this.transformTypes = new int[this.transformCount];
      this.labels = new int[this.transformCount][];

      for (int transformTypeIndex = 0; transformTypeIndex < this.transformCount; transformTypeIndex++) {
         this.transformTypes[transformTypeIndex] = buffer.readUnsignedByte();
      }

      for (int labelIndex = 0; labelIndex < this.transformCount; labelIndex++) {
         int readUnsignedByteOrLength = buffer.readUnsignedByte();
         this.labels[labelIndex] = new int[readUnsignedByteOrLength];

         for (int loopIndex = 0; loopIndex < readUnsignedByteOrLength; loopIndex++) {
            this.labels[labelIndex][loopIndex] = buffer.readUnsignedByte();
         }
      }
   }
}
