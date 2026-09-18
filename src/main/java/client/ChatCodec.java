package client;

final class ChatCodec {
   private static final char[] decodedChars = new char[100];
   private static final Buffer scratchBuffer = new Buffer(new byte[100]);
   private static final char[] characterTable = new char[]{
      ' ',
      'e',
      't',
      'a',
      'o',
      'i',
      'h',
      'n',
      's',
      'r',
      'd',
      'l',
      'u',
      'm',
      'w',
      'c',
      'y',
      'f',
      'g',
      'p',
      'b',
      'v',
      'k',
      'x',
      'j',
      'q',
      'z',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      ' ',
      '!',
      '?',
      '.',
      ',',
      ':',
      ';',
      '(',
      ')',
      '-',
      '&',
      '*',
      '\\',
      '\'',
      '@',
      '#',
      '+',
      '=',
      '£',
      '$',
      '%',
      '"',
      '[',
      ']'
   };

   public static String decode(int length, Buffer buffer) {
      int decodedLength = 0;
      int pendingNibble = -1;

      for (int loopIndex = 0; loopIndex < length; loopIndex++) {
         int packed = buffer.readUnsignedByte();
         int nibble = packed >> 4 & 15;
         if (pendingNibble == -1) {
            if (nibble < 13) {
               decodedChars[decodedLength++] = characterTable[nibble];
            } else {
               pendingNibble = nibble;
            }
         } else {
            decodedChars[decodedLength++] = characterTable[(pendingNibble << 4) + nibble - 195];
            pendingNibble = -1;
         }

         nibble = packed & 15;
         if (pendingNibble == -1) {
            if (nibble < 13) {
               decodedChars[decodedLength++] = characterTable[nibble];
            } else {
               pendingNibble = nibble;
            }
         } else {
            decodedChars[decodedLength++] = characterTable[(pendingNibble << 4) + nibble - 195];
            pendingNibble = -1;
         }
      }

      boolean capitalize = true;

      for (int decodedCharIndex = 0; decodedCharIndex < decodedLength; decodedCharIndex++) {
         char character = decodedChars[decodedCharIndex];
         if (capitalize && character >= 'a' && character <= 'z') {
            decodedChars[decodedCharIndex] = Character.toUpperCase(character);
            capitalize = false;
         }

         if (character == '.' || character == '!' || character == '?') {
            capitalize = true;
         }
      }

      return new String(decodedChars, 0, decodedLength);
   }

   public static void encode(String message, Buffer buffer) {
      if (message.length() > 80) {
         message = message.substring(0, 80);
      }

      message = message.toLowerCase();
      int pendingNibble = -1;

      for (int loopIndex = 0; loopIndex < message.length(); loopIndex++) {
         char character = message.charAt(loopIndex);
         int tableIndex = 0;

         for (int candidate = 0; candidate < characterTable.length; candidate++) {
            if (character == characterTable[candidate]) {
               tableIndex = candidate;
               break;
            }
         }

         if (tableIndex > 12) {
            tableIndex += 195;
         }

         if (pendingNibble == -1) {
            if (tableIndex < 13) {
               pendingNibble = tableIndex;
            } else {
               buffer.writeByte(tableIndex);
            }
         } else if (tableIndex < 13) {
            buffer.writeByte((pendingNibble << 4) + tableIndex);
            pendingNibble = -1;
         } else {
            buffer.writeByte((pendingNibble << 4) + (tableIndex >> 4));
            pendingNibble = tableIndex & 15;
         }
      }

      if (pendingNibble != -1) {
         buffer.writeByte(pendingNibble << 4);
      }
   }

   public static String normalize(String message) {
      scratchBuffer.currentPosition = 0;
      encode(message, scratchBuffer);
      int encodedLength = scratchBuffer.currentPosition;
      scratchBuffer.currentPosition = 0;
      return decode(encodedLength, scratchBuffer);
   }
}
