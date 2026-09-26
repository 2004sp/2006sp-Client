package client;
final class ChatFilter {
   private static int[] encodedFragments;
   private static char[][] badWords;
   private static byte[][][] badWordContexts;
   private static char[][] domains;
   private static char[][] topLevelDomains;
   private static int[] topLevelDomainTypes;
   private static final String[] exceptions = new String[]{"cook", "cook's", "cooks", "seeks", "sheet", "woop", "woops", "faq", "noob", "noobs"};
   public static void load(Archive archive) {
      Buffer buffer = new Buffer(archive.getFile("fragmentsenc.txt"));
      Buffer buffer10 = new Buffer(archive.getFile("badenc.txt"));
      Buffer buffer2 = new Buffer(archive.getFile("domainenc.txt"));
      Buffer buffer3 = new Buffer(archive.getFile("tldlist.txt"));
      Buffer buffer4 = buffer3;
      Buffer buffer5 = buffer2;
      Buffer buffer6 = buffer10;
      Buffer buffer7 = buffer;
      int readIntOrLength;
      badWords = new char[readIntOrLength = (buffer = buffer6).readInt()][];
      badWordContexts = new byte[readIntOrLength][][];
      byte[][][] sourceBadWordContexts = badWordContexts;
      char[][] sourceBadWords = badWords;
      Buffer buffer8 = buffer;

      for (int loopIndex = 0; loopIndex < sourceBadWords.length; loopIndex++) {
         char[] characters = new char[buffer8.readUnsignedByte()];

         for (int characterIndex = 0; characterIndex < characters.length; characterIndex++) {
            characters[characterIndex] = (char)buffer8.readUnsignedByte();
         }

         sourceBadWords[loopIndex] = characters;
         byte[][] workingArray = new byte[buffer8.readUnsignedByte()][2];

         for (int loopIndex2 = 0; loopIndex2 < workingArray.length; loopIndex2++) {
            workingArray[loopIndex2][0] = (byte)buffer8.readUnsignedByte();
            workingArray[loopIndex2][1] = (byte)buffer8.readUnsignedByte();
         }

         if (workingArray.length > 0) {
            sourceBadWordContexts[loopIndex] = workingArray;
         }
      }

      buffer = buffer5;
      char[][] localDomains = domains = new char[buffer5.readInt()][];
      Buffer buffer9 = buffer;
      char[][] localCharacters = localDomains;

      for (int localCharacterIndex = 0; localCharacterIndex < localCharacters.length; localCharacterIndex++) {
         char[] characters2 = new char[buffer9.readUnsignedByte()];

         for (int loopIndex3 = 0; loopIndex3 < characters2.length; loopIndex3++) {
            characters2[loopIndex3] = (char)buffer9.readUnsignedByte();
         }

         localCharacters[localCharacterIndex] = characters2;
      }

      buffer = buffer7;
      encodedFragments = new int[buffer7.readInt()];

      for (int encodedFragmentIndex = 0; encodedFragmentIndex < encodedFragments.length; encodedFragmentIndex++) {
         encodedFragments[encodedFragmentIndex] = buffer.readUnsignedShort();
      }

      buffer = buffer4;
      topLevelDomains = new char[readIntOrLength = buffer4.readInt()][];
      topLevelDomainTypes = new int[readIntOrLength];

      for (int topLevelDomainTypeIndex = 0; topLevelDomainTypeIndex < readIntOrLength; topLevelDomainTypeIndex++) {
         topLevelDomainTypes[topLevelDomainTypeIndex] = buffer.readUnsignedByte();
         char[] topLevelDomain = new char[buffer.readUnsignedByte()];

         for (int topLevelDomainIndex = 0; topLevelDomainIndex < topLevelDomain.length; topLevelDomainIndex++) {
            topLevelDomain[topLevelDomainIndex] = (char)buffer.readUnsignedByte();
         }

         topLevelDomains[topLevelDomainTypeIndex] = topLevelDomain;
      }
   }
   public static String apply(String text) {
      // Revision 443 uses its JS5 Huffman wordpack and server-side chat rules.
      // The 377 wordenc tables do not describe this revision's filtering data.
      if (badWords == null) return text;
      char[] characters2;
      char[] characters = characters2 = text.toCharArray();
      int characterIndex2 = 0;

      for (int characterIndex = 0; characterIndex < characters.length; characterIndex++) {
         char character;
         if ((character = characters[characterIndex]) >= ' ' && character <= 127 || character == ' ' || character == '\n' || character == '\t' || character == 163 || character == 8364) {
            characters[characterIndex2] = characters[characterIndex];
         } else {
            characters[characterIndex2] = ' ';
         }

         if (characterIndex2 == 0 || characters[characterIndex2] != ' ' || characters[characterIndex2 - 1] != ' ') {
            characterIndex2++;
         }
      }

      for (int characterIndex3 = characterIndex2; characterIndex3 < characters.length; characterIndex3++) {
         characters[characterIndex3] = ' ';
      }

      String localText;
      characters2 = (localText = new String(characters2).trim()).toLowerCase().toCharArray();
      String text2 = localText.toLowerCase();
      censorTlds(characters2);
      censorBadWords(characters2);
      censorDomains(characters2);
      censorIpAddresses(characters2);
      String[] sourceExceptions = exceptions;

      for (int loopIndex = 0; loopIndex < 10; loopIndex++) {
         String text3 = sourceExceptions[loopIndex];
         int position = -1;

         while ((position = text2.indexOf(text3, position + 1)) != -1) {
            char[] lengthOrToCharArray;
            System.arraycopy(lengthOrToCharArray = text3.toCharArray(), 0, characters2, position, lengthOrToCharArray.length);
         }
      }

      copyCase(localText.toCharArray(), characters2);
      capitalize(characters2);
      return text;
   }
   private static void copyCase(char[] characters, char[] newCharacters) {
      for (int characterIndex = 0; characterIndex < characters.length; characterIndex++) {
         if (newCharacters[characterIndex] != '*' && isUpperCase(characters[characterIndex])) {
            newCharacters[characterIndex] = characters[characterIndex];
         }
      }
   }
   private static void capitalize(char[] characters) {
      boolean flag = true;

      for (int characterIndex = 0; characterIndex < characters.length; characterIndex++) {
         char character;
         if (isLetter(character = characters[characterIndex])) {
            if (flag) {
               if ((character = character) >= 'a' && character <= 'z') {
                  flag = false;
               }
            } else if (isUpperCase(character)) {
               characters[characterIndex] = (char)(character + 'a' - 65);
            }
         } else {
            flag = true;
         }
      }
   }
   private static void censorBadWords(char[] characters) {
      for (int loopIndex = 0; loopIndex < 2; loopIndex++) {
         for (int badWordContextIndex = badWords.length - 1; badWordContextIndex >= 0; badWordContextIndex--) {
            censorPattern(badWordContexts[badWordContextIndex], characters, badWords[badWordContextIndex]);
         }
      }
   }
   private static void censorDomains(char[] characters) {
      char[] copy = (char[])characters.clone();
      char[] characters2 = new char[]{'(', 'a', ')'};
      censorPattern(null, copy, characters2);
      characters2 = (char[])characters.clone();
      char[] characters3 = new char[]{'d', 'o', 't'};
      censorPattern(null, characters2, characters3);

      for (int domainIndex = domains.length - 1; domainIndex >= 0; domainIndex--) {
         char[] domain = domains[domainIndex];
         char[] characters4 = copy;
         char[] characters5 = characters2;
         char[] characters6 = domain;
         char[] characters7 = characters;
         if (characters6.length <= characters7.length) {
            int position = 0;

            while (position <= characters7.length - characters6.length) {
               int loopIndex = position;
               int scalar = 0;
               int scalar2 = 1;

               while (loopIndex < characters7.length) {
                  char character = characters7[loopIndex];
                  char localCharacter = 0;
                  if (loopIndex + 1 < characters7.length) {
                     localCharacter = characters7[loopIndex + 1];
                  }

                  int domainMatchLength;
                  if (scalar < characters6.length && (domainMatchLength = matchDomainCharacter(character, characters6[scalar], localCharacter)) > 0) {
                     loopIndex += domainMatchLength;
                     scalar++;
                  } else {
                     if (scalar == 0) {
                        break;
                     }

                     if ((domainMatchLength = matchDomainCharacter(character, characters6[scalar - 1], localCharacter)) > 0) {
                        loopIndex += domainMatchLength;
                        if (scalar == 1) {
                           scalar2++;
                        }
                     } else {
                        if (scalar >= characters6.length || !isNotAlphanumeric(character)) {
                           break;
                        }

                        loopIndex++;
                     }
                  }
               }

               if (scalar >= characters6.length) {
                  boolean flag = false;
                  int position2 = position;
                  char[] characters8 = characters4;
                  char[] characters9 = characters7;
                  byte byteCode;
                  if (position2 == 0) {
                     byteCode = 2;
                  } else {
                     classifyLeftBoundary: {
                        for (int position3 = position2 - 1; position3 >= 0 && isNotAlphanumeric(characters9[position3]); position3--) {
                           if (characters9[position3] == '@') {
                              byteCode = 3;
                              break classifyLeftBoundary;
                           }
                        }

                        int scalar3 = 0;

                        for (int position4 = position2 - 1; position4 >= 0 && isNotAlphanumeric(characters8[position4]); position4--) {
                           if (characters8[position4] == '*') {
                              scalar3++;
                           }
                        }

                        byteCode = (byte)(scalar3 >= 3 ? 4 : (!isNotAlphanumeric(characters9[position2 - 1]) ? 0 : 1));
                     }
                  }

                  byte byteCode2 = byteCode;
                  int scalar4 = loopIndex - 1;
                  char[] characters10 = characters7;
                  int position5 = scalar4;
                  char[] characters11 = characters5;
                  if (position5 + 1 == characters10.length) {
                     byteCode = 2;
                  } else {
                     int position6 = position5 + 1;

                     while (true) {
                        if (position6 < characters10.length && isNotAlphanumeric(characters10[position6])) {
                           if (characters10[position6] != '.' && characters10[position6] != ',') {
                              position6++;
                              continue;
                           }

                           byteCode = 3;
                           break;
                        }

                        position6 = 0;

                        for (int position7 = position5 + 1; position7 < characters10.length && isNotAlphanumeric(characters11[position7]); position7++) {
                           if (characters11[position7] == '*') {
                              position6++;
                           }
                        }

                        byteCode = (byte)(position6 >= 3 ? 4 : (!isNotAlphanumeric(characters10[position5 + 1]) ? 0 : 1));
                        break;
                     }
                  }

                  byte byteCode3 = byteCode;
                  if (byteCode2 > 2 || byteCode3 > 2) {
                     flag = true;
                  }

                  if (flag) {
                     for (int loopIndex2 = position; loopIndex2 < loopIndex; loopIndex2++) {
                        characters7[loopIndex2] = '*';
                     }
                  }
               }

               position += scalar2;
            }
         }
      }
   }
   private static void censorTlds(char[] characters) {
      char[] copy = (char[])characters.clone();
      char[] characters2 = new char[]{'d', 'o', 't'};
      censorPattern(null, copy, characters2);
      characters2 = (char[])characters.clone();
      char[] characters3 = new char[]{'s', 'l', 'a', 's', 'h'};
      censorPattern(null, characters2, characters3);

      for (int topLevelDomainIndex = 0; topLevelDomainIndex < topLevelDomains.length; topLevelDomainIndex++) {
         char[] topLevelDomain = topLevelDomains[topLevelDomainIndex];
         int topLevelDomainType = topLevelDomainTypes[topLevelDomainIndex];
         char[] characters4 = characters;
         char[] characters5 = copy;
         int sourceTopLevelDomainType = topLevelDomainType;
         char[] characters6 = topLevelDomain;
         char[] characters7 = characters2;
         if (characters6.length <= characters4.length) {
            int position = 0;

            while (position <= characters4.length - characters6.length) {
               int loopIndex = position;
               int position2 = 0;
               int scalar = 1;

               while (loopIndex < characters4.length) {
                  char character = characters4[loopIndex];
                  char localCharacter = 0;
                  if (loopIndex + 1 < characters4.length) {
                     localCharacter = characters4[loopIndex + 1];
                  }

                  int domainMatchLength;
                  if (position2 < characters6.length && (domainMatchLength = matchDomainCharacter(character, characters6[position2], localCharacter)) > 0) {
                     loopIndex += domainMatchLength;
                     position2++;
                  } else {
                     if (position2 == 0) {
                        break;
                     }

                     if ((domainMatchLength = matchDomainCharacter(character, characters6[position2 - 1], localCharacter)) > 0) {
                        loopIndex += domainMatchLength;
                        if (position2 == 1) {
                           scalar++;
                        }
                     } else {
                        if (position2 >= characters6.length || !isNotAlphanumeric(character)) {
                           break;
                        }

                        loopIndex++;
                     }
                  }
               }

               if (position2 >= characters6.length) {
                  boolean flag = false;
                  char[] characters8 = characters5;
                  int position3 = position;
                  char[] characters9 = characters4;
                  byte byteCode;
                  if (position3 == 0) {
                     byteCode = 2;
                  } else {
                     int position4 = position3 - 1;

                     while (true) {
                        if (position4 >= 0 && isNotAlphanumeric(characters9[position4])) {
                           if (characters9[position4] != ',' && characters9[position4] != '.') {
                              position4--;
                              continue;
                           }

                           byteCode = 3;
                           break;
                        }

                        position4 = 0;

                        for (int position5 = position3 - 1; position5 >= 0 && isNotAlphanumeric(characters8[position5]); position5--) {
                           if (characters8[position5] == '*') {
                              position4++;
                           }
                        }

                        byteCode = (byte)(position4 >= 3 ? 4 : (!isNotAlphanumeric(characters9[position3 - 1]) ? 0 : 1));
                        break;
                     }
                  }

                  byte byteCode2 = byteCode;
                  int position6 = loopIndex - 1;
                  char[] characters10 = characters7;
                  char[] characters11 = characters4;
                  if (position6 + 1 == characters11.length) {
                     byteCode = 2;
                  } else {
                     int position7 = position6 + 1;

                     while (true) {
                        if (position7 < characters11.length && isNotAlphanumeric(characters11[position7])) {
                           if (characters11[position7] != '\\' && characters11[position7] != '/') {
                              position7++;
                              continue;
                           }

                           byteCode = 3;
                           break;
                        }

                        position7 = 0;

                        for (int position8 = position6 + 1; position8 < characters11.length && isNotAlphanumeric(characters10[position8]); position8++) {
                           if (characters10[position8] == '*') {
                              position7++;
                           }
                        }

                        byteCode = (byte)(position7 >= 5 ? 4 : (!isNotAlphanumeric(characters11[position6 + 1]) ? 0 : 1));
                        break;
                     }
                  }

                  byte byteCode3 = byteCode;
                  if (sourceTopLevelDomainType == 1 && byteCode2 > 0 && byteCode3 > 0) {
                     flag = true;
                  }

                  if (sourceTopLevelDomainType == 2 && (byteCode2 > 2 && byteCode3 > 0 || byteCode2 > 0 && byteCode3 > 2)) {
                     flag = true;
                  }

                  if (sourceTopLevelDomainType == 3 && byteCode2 > 0 && byteCode3 > 2) {
                     flag = true;
                  }

                  if (flag) {
                     position2 = position;
                     loopIndex--;
                     if (byteCode2 > 2) {
                        if (byteCode2 == 4) {
                           flag = false;

                           for (int loopIndex2 = position - 1; loopIndex2 >= 0; loopIndex2--) {
                              if (flag) {
                                 if (characters5[loopIndex2] != '*') {
                                    break;
                                 }

                                 position2 = loopIndex2;
                              } else if (characters5[loopIndex2] == '*') {
                                 position2 = loopIndex2;
                                 flag = true;
                              }
                           }
                        }

                        flag = false;

                        for (int loopIndex3 = position2 - 1; loopIndex3 >= 0; loopIndex3--) {
                           if (flag) {
                              if (isNotAlphanumeric(characters4[loopIndex3])) {
                                 break;
                              }

                              position2 = loopIndex3;
                           } else if (!isNotAlphanumeric(characters4[loopIndex3])) {
                              flag = true;
                              position2 = loopIndex3;
                           }
                        }
                     }

                     if (byteCode3 > 2) {
                        if (byteCode3 == 4) {
                           flag = false;

                           for (int loopIndex4 = loopIndex + 1; loopIndex4 < characters4.length; loopIndex4++) {
                              if (flag) {
                                 if (characters7[loopIndex4] != '*') {
                                    break;
                                 }

                                 loopIndex = loopIndex4;
                              } else if (characters7[loopIndex4] == '*') {
                                 loopIndex = loopIndex4;
                                 flag = true;
                              }
                           }
                        }

                        flag = false;

                        for (int loopIndex5 = loopIndex + 1; loopIndex5 < characters4.length; loopIndex5++) {
                           if (flag) {
                              if (isNotAlphanumeric(characters4[loopIndex5])) {
                                 break;
                              }

                              loopIndex = loopIndex5;
                           } else if (!isNotAlphanumeric(characters4[loopIndex5])) {
                              flag = true;
                              loopIndex = loopIndex5;
                           }
                        }
                     }

                     for (int loopIndex6 = position2; loopIndex6 <= loopIndex; loopIndex6++) {
                        characters4[loopIndex6] = '*';
                     }
                  }
               }

               position += scalar;
            }
         }
      }
   }
   private static void censorPattern(byte[][] data, char[] characters, char[] newCharacters) {
      if (newCharacters.length <= characters.length) {
         int position = 0;

         while (position <= characters.length - newCharacters.length) {
            int characterIndex = position;
            int newCharacterIndex = 0;
            int scalar = 0;
            int scalar2 = 1;
            int scalar3 = 0;
            int scalar4 = 0;
            byte localRightContext = 0;
            boolean hasSeparator = false;
            boolean matchedDigit = false;
            boolean skippedDigit = false;

            while (characterIndex < characters.length && (!matchedDigit || !skippedDigit)) {
               char character = characters[characterIndex];
               char character2 = 0;
               if (characterIndex + 1 < characters.length) {
                  character2 = characters[characterIndex + 1];
               }

               int badWordMatchLength;
               if (newCharacterIndex < newCharacters.length && (badWordMatchLength = matchBadWordCharacter(character2, character, newCharacters[newCharacterIndex])) > 0) {
                  if (badWordMatchLength == 1 && isDigit(character)) {
                     matchedDigit = true;
                  }

                  if (badWordMatchLength == 2 && (isDigit(character) || isDigit(character2))) {
                     matchedDigit = true;
                  }

                  characterIndex += badWordMatchLength;
                  newCharacterIndex++;
               } else {
                  if (newCharacterIndex == 0) {
                     break;
                  }

                  if ((badWordMatchLength = matchBadWordCharacter(character2, character, newCharacters[newCharacterIndex - 1])) > 0) {
                     characterIndex += badWordMatchLength;
                     if (newCharacterIndex == 1) {
                        scalar2++;
                     }
                  } else {
                     if (newCharacterIndex >= newCharacters.length || !isSkippableCharacter(character)) {
                        break;
                     }

                     if (isNotAlphanumeric(character) && character != '\'') {
                        hasSeparator = true;
                     }

                     if (isDigit(character)) {
                        skippedDigit = true;
                     }

                     characterIndex++;
                     if (++scalar * 100 / (characterIndex - position) > 90) {
                        break;
                     }
                  }
               }
            }

            if (newCharacterIndex >= newCharacters.length && (!matchedDigit || !skippedDigit)) {
               boolean flag = true;
               if (!hasSeparator) {
                  int character3 = 32;
                  if (position - 1 >= 0) {
                     character3 = characters[position - 1];
                  }

                  int localLength = 32;
                  if (characterIndex < characters.length) {
                     localLength = characters[characterIndex];
                  }

                  byte leftContextCode = encodeContextCharacter((char)character3);
                  byte rightContext = encodeContextCharacter((char)localLength);
                  if (data != null) {
                     localRightContext = rightContext;
                     byte[][] workingArray = data;
                     character3 = 0;
                     boolean localFlag;
                     if (workingArray[0][0] == leftContextCode && workingArray[0][1] == localRightContext) {
                        localFlag = true;
                     } else {
                        localLength = workingArray.length - 1;
                        if (workingArray[localLength][0] == leftContextCode && workingArray[localLength][1] == localRightContext) {
                           localFlag = true;
                        } else {
                           while (true) {
                              int sourceLocalLength = (character3 + localLength) / 2;
                              if (workingArray[sourceLocalLength][0] == leftContextCode && workingArray[sourceLocalLength][1] == localRightContext) {
                                 localFlag = true;
                                 break;
                              }

                              if (leftContextCode < workingArray[sourceLocalLength][0] || leftContextCode == workingArray[sourceLocalLength][0] && localRightContext < workingArray[sourceLocalLength][1]) {
                                 localLength = sourceLocalLength;
                              } else {
                                 character3 = sourceLocalLength;
                              }

                              if (character3 == localLength || character3 + 1 == localLength) {
                                 localFlag = false;
                                 break;
                              }
                           }
                        }
                     }

                     if (localFlag) {
                        flag = false;
                     }
                  }
               } else {
                  boolean flag2 = false;
                  boolean rightBoundary = false;
                  int localCharacterIndex = 0;
                  if (position - 1 < 0 || isNotAlphanumeric(characters[position - 1]) && characters[position - 1] != '\'') {
                     flag2 = true;
                  }

                  if (characterIndex >= characters.length || isNotAlphanumeric(characters[characterIndex]) && characters[characterIndex] != '\'') {
                     rightBoundary = true;
                  }

                  if (!flag2 || !rightBoundary) {
                     boolean foundFragment = false;
                     newCharacterIndex = position - 2;
                     if (flag2) {
                        newCharacterIndex = position;
                     }

                     for (; !foundFragment && newCharacterIndex < characterIndex; newCharacterIndex++) {
                        if (newCharacterIndex >= 0 && (!isNotAlphanumeric(characters[newCharacterIndex]) || characters[newCharacterIndex] == '\'')) {
                           char[] localCharacters = new char[3];

                           for (localCharacterIndex = 0; localCharacterIndex < 3 && newCharacterIndex + localCharacterIndex < characters.length && (!isNotAlphanumeric(characters[newCharacterIndex + localCharacterIndex]) || characters[newCharacterIndex + localCharacterIndex] == '\''); localCharacterIndex++) {
                              localCharacters[localCharacterIndex] = characters[newCharacterIndex + localCharacterIndex];
                           }

                           int scalar5 = 1;
                           if (localCharacterIndex == 0) {
                              scalar5 = 0;
                           }

                           if (localCharacterIndex < 3 && newCharacterIndex - 1 >= 0 && (!isNotAlphanumeric(characters[newCharacterIndex - 1]) || characters[newCharacterIndex - 1] == '\'')) {
                              scalar5 = 0;
                           }

                           if (scalar5 != 0) {
                              char[] characters2 = localCharacters;
                              int scalar6 = 1;

                              for (int loopIndex = 0; loopIndex < 3; loopIndex++) {
                                 if (!isDigit(characters2[loopIndex]) && characters2[loopIndex] != 0) {
                                    scalar6 = 0;
                                 }
                              }

                              int scalar7;
                              checkEncodedFragment: {
                                 if (scalar6 == 0) {
                                    char[] characters3;
                                    if ((characters3 = characters2).length > 6) {
                                       scalar7 = 0;
                                    } else {
                                       scalar6 = 0;
                                       scalar5 = 0;

                                       while (true) {
                                          if (scalar5 >= characters3.length) {
                                             scalar7 = scalar6;
                                             break;
                                          }

                                          char length2;
                                          if ((length2 = characters3[characters3.length - scalar5 - 1]) >= 'a' && length2 <= 'z') {
                                             scalar6 = scalar6 * 38 + length2 - 97 + 1;
                                          } else if (length2 == '\'') {
                                             scalar6 = scalar6 * 38 + 27;
                                          } else if (length2 >= '0' && length2 <= '9') {
                                             scalar6 = scalar6 * 38 + length2 - 48 + 28;
                                          } else if (length2 != 0) {
                                             scalar7 = 0;
                                             break;
                                          }

                                          scalar5++;
                                       }
                                    }

                                    scalar5 = scalar7;
                                    int sourceEncodedFragmentIndex = 0;
                                    int encodedFragmentsLengthOrLength = encodedFragments.length - 1;
                                    if (scalar5 != encodedFragments[0] && scalar5 != encodedFragments[encodedFragmentsLengthOrLength]) {
                                       while (true) {
                                          int encodedFragmentIndex = (sourceEncodedFragmentIndex + encodedFragmentsLengthOrLength) / 2;
                                          if (scalar5 == encodedFragments[encodedFragmentIndex]) {
                                             break;
                                          }

                                          if (scalar5 < encodedFragments[encodedFragmentIndex]) {
                                             encodedFragmentsLengthOrLength = encodedFragmentIndex;
                                          } else {
                                             sourceEncodedFragmentIndex = encodedFragmentIndex;
                                          }

                                          if (sourceEncodedFragmentIndex == encodedFragmentsLengthOrLength || sourceEncodedFragmentIndex + 1 == encodedFragmentsLengthOrLength) {
                                             scalar7 = 0;
                                             break checkEncodedFragment;
                                          }
                                       }
                                    }
                                 }

                                 scalar7 = 1;
                              }

                              if (scalar7 == 0) {
                                 foundFragment = true;
                              }
                           }
                        }
                     }

                     if (!foundFragment) {
                        flag = false;
                     }
                  }
               }

               if (flag) {
                  scalar = 0;
                  scalar3 = 0;
                  scalar4 = -1;

                  for (int characterIndex2 = position; characterIndex2 < characterIndex; characterIndex2++) {
                     if (isDigit(characters[characterIndex2])) {
                        scalar++;
                     } else if (isLetter(characters[characterIndex2])) {
                        scalar3++;
                        scalar4 = characterIndex2;
                     }
                  }

                  if (scalar4 >= 0) {
                     scalar -= characterIndex - 1 - scalar4;
                  }

                  if (scalar <= scalar3) {
                     for (int characterIndex3 = position; characterIndex3 < characterIndex; characterIndex3++) {
                        characters[characterIndex3] = '*';
                     }
                  } else {
                     scalar2 = 1;
                  }
               }
            }

            position += scalar2;
         }
      }
   }
   private static int matchDomainCharacter(char character, char newCharacter, char character2) {
      if (newCharacter == character) {
         return 1;
      }

      if (newCharacter == 'o' && character == '0') {
         return 1;
      }

      if (newCharacter == 'o' && character == '(' && character2 == ')') {
         return 2;
      }

      if (newCharacter != 'c' || character != '(' && character != '<' && character != '[') {
         if (newCharacter == 'e' && character == 8364) {
            return 1;
         } else if (newCharacter == 's' && character == '$') {
            return 1;
         } else {
            return newCharacter == 108 && character == 105 ? 1 : 0;
         }
      } else {
         return 1;
      }
   }
   private static int matchBadWordCharacter(char character, char newCharacter, char character2) {
      if (character2 == newCharacter) {
         return 1;
      }

      if (character2 >= 'a' && character2 <= 'm') {
         if (character2 == 'a') {
            if (newCharacter != '4' && newCharacter != '@' && newCharacter != '^') {
               if (newCharacter == '/' && character == '\\') {
                  return 2;
               }

               return 0;
            }

            return 1;
         }

         if (character2 == 'b') {
            if (newCharacter != '6' && newCharacter != '8') {
               if (newCharacter == '1' && character == '3' || newCharacter == 'i' && character == '3') {
                  return 2;
               }

               return 0;
            }

            return 1;
         }

         if (character2 == 'c') {
            if (newCharacter != '(' && newCharacter != '<' && newCharacter != '{' && newCharacter != '[') {
               return 0;
            }

            return 1;
         }

         if (character2 == 'd') {
            if (newCharacter == '[' && character == ')' || newCharacter == 'i' && character == ')') {
               return 2;
            }

            return 0;
         }

         if (character2 == 'e') {
            if (newCharacter != '3' && newCharacter != 8364) {
               return 0;
            }

            return 1;
         }

         if (character2 == 'f') {
            if (newCharacter == 'p' && character == 'h') {
               return 2;
            }

            if (newCharacter != 163) {
               return 0;
            }

            return 1;
         }

         if (character2 == 'g') {
            if (newCharacter != '9' && newCharacter != '6' && newCharacter != 'q') {
               return 0;
            }

            return 1;
         }

         if (character2 == 'h') {
            if (newCharacter != '#') {
               return 0;
            }

            return 1;
         }

         if (character2 == 'i') {
            if (newCharacter != 'y' && newCharacter != 'l' && newCharacter != 'j' && newCharacter != '1' && newCharacter != '!' && newCharacter != ':' && newCharacter != ';' && newCharacter != '|') {
               return 0;
            }

            return 1;
         }

         if (character2 == 'j') {
            return 0;
         }

         if (character2 == 'k') {
            return 0;
         }

         if (character2 == 'l') {
            if (newCharacter != '1' && newCharacter != '|' && newCharacter != 'i') {
               return 0;
            }

            return 1;
         }

         if (character2 == 'm') {
            return 0;
         }
      }

      if (character2 >= 'n' && character2 <= 'z') {
         if (character2 == 'n') {
            return 0;
         }

         if (character2 == 'o') {
            if (newCharacter != '0' && newCharacter != '*') {
               if (newCharacter == '(' && character == ')' || newCharacter == '[' && character == ']' || newCharacter == '{' && character == '}' || newCharacter == '<' && character == '>') {
                  return 2;
               }

               return 0;
            }

            return 1;
         }

         if (character2 == 'p') {
            return 0;
         }

         if (character2 == 'q') {
            return 0;
         }

         if (character2 == 'r') {
            return 0;
         }

         if (character2 == 's') {
            if (newCharacter != '5' && newCharacter != 'z' && newCharacter != '$' && newCharacter != '2') {
               return 0;
            }

            return 1;
         }

         if (character2 == 't') {
            if (newCharacter != '7' && newCharacter != '+') {
               return 0;
            }

            return 1;
         }

         if (character2 == 'u') {
            if (newCharacter == 'v') {
               return 1;
            }

            if (newCharacter == '\\' && character == '/' || newCharacter == '\\' && character == '|' || newCharacter == '|' && character == '/') {
               return 2;
            }

            return 0;
         }

         if (character2 == 'v') {
            if (newCharacter == '\\' && character == '/' || newCharacter == '\\' && character == '|' || newCharacter == '|' && character == '/') {
               return 2;
            }

            return 0;
         }

         if (character2 == 'w') {
            if (newCharacter == 'v' && character == 'v') {
               return 2;
            }

            return 0;
         }

         if (character2 == 'x') {
            if (newCharacter == ')' && character == '(' || newCharacter == '}' && character == '{' || newCharacter == ']' && character == '[' || newCharacter == '>' && character == '<') {
               return 2;
            }

            return 0;
         }

         if (character2 == 'y') {
            return 0;
         }

         if (character2 == 'z') {
            return 0;
         }
      }

      if (character2 >= '0' && character2 <= '9') {
         if (character2 == '0') {
            if (newCharacter != 'o' && newCharacter != 'O') {
               if (newCharacter == '(' && character == ')' || newCharacter == '{' && character == '}' || newCharacter == '[' && character == ']') {
                  return 2;
               }

               return 0;
            }

            return 1;
         }

         if (character2 != '1') {
            return 0;
         }

         if (newCharacter == 'l') {
            return 1;
         }
      } else if (character2 == ',') {
         if (newCharacter == '.') {
            return 1;
         }
      } else if (character2 == '.') {
         if (newCharacter == ',') {
            return 1;
         }
      } else if (character2 == '!' && newCharacter == 'i') {
         return 1;
      }

      return 0;
   }
   private static byte encodeContextCharacter(char character) {
      if (character >= 'a' && character <= 'z') {
         return (byte)(character - 'a' + 1);
      } else if (character == '\'') {
         return 28;
      } else {
         return character >= '0' && character <= '9' ? (byte)(character - '0' + 29) : 27;
      }
   }
   private static void censorIpAddresses(char[] characters) {
      int sourceLocalLength4 = 0;
      int scalar = 0;
      int position = 0;

      while (true) {
         int sourceLocalLength = sourceLocalLength4;
         char[] sourceCharacters = characters;

         int localLength;
         findNextDigit: {
            for (int sourceLocalLength2 = sourceLocalLength; sourceLocalLength2 < sourceCharacters.length && sourceLocalLength2 >= 0; sourceLocalLength2++) {
               if (sourceCharacters[sourceLocalLength2] >= '0' && sourceCharacters[sourceLocalLength2] <= '9') {
                  localLength = sourceLocalLength2;
                  break findNextDigit;
               }
            }

            localLength = -1;
         }

         int sourceLocalLength3 = localLength;
         if (localLength == -1) {
            return;
         }

         boolean flag = false;

         for (int characterIndex = sourceLocalLength4; characterIndex >= 0 && characterIndex < sourceLocalLength3 && !flag; characterIndex++) {
            if (!isNotAlphanumeric(characters[characterIndex]) && !isSkippableCharacter(characters[characterIndex])) {
               flag = true;
            }
         }

         if (flag) {
            scalar = 0;
         }

         if (scalar == 0) {
            position = sourceLocalLength3;
         }

         sourceLocalLength = sourceLocalLength3;
         sourceCharacters = characters;

         while (true) {
            if (sourceLocalLength < sourceCharacters.length && sourceLocalLength >= 0) {
               if (sourceCharacters[sourceLocalLength] >= '0' && sourceCharacters[sourceLocalLength] <= '9') {
                  sourceLocalLength++;
                  continue;
               }

               localLength = sourceLocalLength;
               break;
            }

            localLength = sourceCharacters.length;
            break;
         }

         sourceLocalLength4 = localLength;
         sourceLocalLength = 0;

         for (int characterIndex2 = sourceLocalLength3; characterIndex2 < sourceLocalLength4; characterIndex2++) {
            sourceLocalLength = sourceLocalLength * 10 + characters[characterIndex2] - 48;
         }

         if (sourceLocalLength <= 255 && sourceLocalLength4 - sourceLocalLength3 <= 8) {
            scalar++;
         } else {
            scalar = 0;
         }

         if (scalar == 4) {
            for (int characterIndex3 = position; characterIndex3 < sourceLocalLength4; characterIndex3++) {
               characters[characterIndex3] = '*';
            }

            scalar = 0;
         }
      }
   }
   private static boolean isNotAlphanumeric(char character) {
      return !isLetter(character) && !isDigit(character);
   }
   private static boolean isSkippableCharacter(char character) {
      return character < 'a' || character > 'z' || character == 'v' || character == 'x' || character == 'j' || character == 'q' || character == 'z';
   }

   private static boolean isLetter(char character) {
      return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z';
   }

   private static boolean isDigit(char character) {
      return character >= '0' && character <= '9';
   }
   private static boolean isUpperCase(char character) {
      return character >= 'A' && character <= 'Z';
   }
}
