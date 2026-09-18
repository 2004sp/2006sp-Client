package client;

final class NameUtils {
   private static final char[] base37Characters = new char[]{
      '_',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
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
      '9'
   };

   public static long encodeBase37(String name) {
      long encoded = 0L;

      for (int position = 0; position < name.length() && position < 12; position++) {
         char character = name.charAt(position);
         encoded *= 37L;
         if (character >= 'A' && character <= 'Z') {
            encoded += character + 1 - 'A';
         } else if (character >= 'a' && character <= 'z') {
            encoded += character + 1 - 'a';
         } else if (character >= '0' && character <= '9') {
            encoded += character + 27 - '0';
         }
      }

      while (encoded % 37L == 0L && encoded != 0L) {
         encoded /= 37L;
      }

      return encoded;
   }

   public static String decodeBase37(long encoded) {
      try {
         if (encoded > 0L && encoded < 6582952005840035281L) {
            if (encoded % 37L == 0L) {
               return "invalid_name";
            }

            int length = 0;
            char[] characters = new char[12];

            while (encoded != 0L) {
               long previous = encoded;
               encoded /= 37L;
               characters[11 - length++] = base37Characters[(int)(previous - encoded * 37L)];
            }

            return new String(characters, 12 - length, length);
         } else {
            return "invalid_name";
         }
      } catch (RuntimeException exception) {
         SignLink.reporterror("81570, " + encoded + ", -99" + ", " + exception.toString());
         throw new RuntimeException();
      }
   }

   public static long hashUsername(String username) {
      username = username.toUpperCase();
      long hash = 0L;

      for (int loopIndex = 0; loopIndex < username.length(); loopIndex++) {
         long nextHash = hash * 61L + username.charAt(loopIndex) - 32L;
         hash = nextHash + (nextHash >> 56) & 72057594037927935L;
      }

      return hash;
   }

   public static String formatIpv4Address(int address) {
      return (address >>> 24) + "." + (address >> 16 & 0xFF) + "." + (address >> 8 & 0xFF) + "." + (address & 0xFF);
   }

   public static String formatDisplayName(String name) {
      if (name.length() > 0) {
         char[] characters = name.toCharArray();

         for (int characterIndex = 0; characterIndex < characters.length; characterIndex++) {
            if (characters[characterIndex] == '_') {
               characters[characterIndex] = ' ';
               if (characterIndex + 1 < characters.length && characters[characterIndex + 1] >= 'a' && characters[characterIndex + 1] <= 'z') {
                  characters[characterIndex + 1] = Character.toUpperCase(characters[characterIndex + 1]);
               }
            }
         }

         if (characters[0] >= 'a' && characters[0] <= 'z') {
            characters[0] = Character.toUpperCase(characters[0]);
         }

         return new String(characters);
      } else {
         return name;
      }
   }

   public static String mask(String password) {
      StringBuilder masked = new StringBuilder(password.length());

      for (int loopIndex = 0; loopIndex < password.length(); loopIndex++) {
         masked.append('*');
      }

      return masked.toString();
   }
}
