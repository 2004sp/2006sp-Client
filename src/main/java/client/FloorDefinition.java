package client;
import java.io.IOException;
import java.util.Map;
public final class FloorDefinition {
   public static FloorDefinition[] definitions;
   public static FloorDefinition[] underlayDefinitions;
   public static FloorDefinition[] overlayDefinitions;
   public int rgbColor;
   public int textureId = -1;
   public boolean occlude = true;
   public int hue;
   public int saturation;
   public int lightness;
   public int weightedHue;
   public int hueMultiplier;
   public int packedHsl;
   public int secondaryRgbColor;
   public static void unpackConfig(Archive archive) {
      Buffer buffer;
      int length = (buffer = new Buffer(archive.getFile("flo.dat"))).readUnsignedShort();
      if (definitions == null) {
         definitions = new FloorDefinition[length];
      }

      for (int definitionIndex = 0; definitionIndex < length; definitionIndex++) {
         if (definitions[definitionIndex] == null) {
            definitions[definitionIndex] = new FloorDefinition();
         }

         FloorDefinition floorDefinition = definitions[definitionIndex];
         Buffer sourceBuffer = buffer;
         int sourceDefinitionIndex = definitionIndex;
         FloorDefinition sourceFloorDefinition = floorDefinition;

         int hueOrReadUnsignedByte;
         while ((hueOrReadUnsignedByte = sourceBuffer.readUnsignedByte()) != 0) {
            if (hueOrReadUnsignedByte == 1) {
               sourceFloorDefinition.rgbColor = sourceBuffer.readUnsignedMedium();
               if (Client.christmasEventActive) {
                  if (sourceDefinitionIndex == 28 || sourceDefinitionIndex == 47 || sourceDefinitionIndex == 48 || sourceDefinitionIndex == 49 || sourceDefinitionIndex == 50 || sourceDefinitionIndex == 51) {
                     sourceFloorDefinition.rgbColor = 16777215;
                  }
               } else if (Client.halloweenEventActive) {
                  if (sourceDefinitionIndex == 28) {
                     sourceFloorDefinition.rgbColor = 1579032;
                  }

                  if (sourceDefinitionIndex == 47) {
                     sourceFloorDefinition.rgbColor = 2105376;
                  }

                  if (sourceDefinitionIndex == 48) {
                     sourceFloorDefinition.rgbColor = 2631720;
                  }

                  if (sourceDefinitionIndex == 49) {
                     sourceFloorDefinition.rgbColor = 3158064;
                  }

                  if (sourceDefinitionIndex == 50) {
                     sourceFloorDefinition.rgbColor = 3684408;
                  }

                  if (sourceDefinitionIndex == 51) {
                     sourceFloorDefinition.rgbColor = 4210752;
                  }
               }

               sourceFloorDefinition.setHslFromRgb(sourceFloorDefinition.rgbColor);
            } else if (hueOrReadUnsignedByte == 2) {
               sourceFloorDefinition.textureId = sourceBuffer.readUnsignedByte();
               if (Client.halloweenEventActive && sourceDefinitionIndex == 5) {
                  sourceFloorDefinition.textureId = 25;
               }
            } else if (hueOrReadUnsignedByte != 3) {
               if (hueOrReadUnsignedByte == 5) {
                  sourceFloorDefinition.occlude = false;
               } else if (hueOrReadUnsignedByte == 6) {
                  sourceBuffer.readString();
               } else if (hueOrReadUnsignedByte == 7) {
                  hueOrReadUnsignedByte = sourceFloorDefinition.hue;
                  int saturation = sourceFloorDefinition.saturation;
                  int lightness = sourceFloorDefinition.lightness;
                  int weightedHue = sourceFloorDefinition.weightedHue;
                  int secondaryRgbColorOrReadUnsignedMedium = sourceBuffer.readUnsignedMedium();
                  sourceFloorDefinition.secondaryRgbColor = secondaryRgbColorOrReadUnsignedMedium;
                  sourceFloorDefinition.setHslFromRgb(secondaryRgbColorOrReadUnsignedMedium);
                  sourceFloorDefinition.hue = hueOrReadUnsignedByte;
                  sourceFloorDefinition.saturation = saturation;
                  sourceFloorDefinition.lightness = lightness;
                  sourceFloorDefinition.weightedHue = weightedHue;
                  sourceFloorDefinition.hueMultiplier = weightedHue;
               } else {
                  System.out.println("Error unrecognised config code: " + hueOrReadUnsignedByte);
               }
            }
         }
      }
      underlayDefinitions = definitions;
      overlayDefinitions = definitions;
   }
   public static void loadRevision443(Cache cache) throws IOException {
      underlayDefinitions = decodeRevision443Group(cache.readFiles(2, 1));
      overlayDefinitions = decodeRevision443Group(cache.readFiles(2, 4));
   }

   private static FloorDefinition[] decodeRevision443Group(Map<Integer, byte[]> files)
         throws IOException {
      int maxId = -1;
      for (Integer id : files.keySet()) {
         if (id > maxId) maxId = id;
      }
      FloorDefinition[] decoded = new FloorDefinition[maxId + 1];
      for (Map.Entry<Integer, byte[]> entry : files.entrySet()) {
         FloorDefinition definition = new FloorDefinition();
         Buffer buffer = new Buffer(entry.getValue());
         while (true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0) break;
            if (opcode == 1) {
               definition.rgbColor = buffer.readUnsignedMedium();
               definition.setHslFromRgb(definition.rgbColor);
            } else if (opcode == 2) {
               definition.textureId = buffer.readUnsignedByte();
            } else if (opcode == 5) {
               definition.occlude = false;
            } else if (opcode == 7) {
               int hue = definition.hue;
               int saturation = definition.saturation;
               int lightness = definition.lightness;
               int weightedHue = definition.weightedHue;
               int hueMultiplier = definition.hueMultiplier;
               definition.secondaryRgbColor = buffer.readUnsignedMedium();
               definition.setHslFromRgb(definition.secondaryRgbColor);
               definition.hue = hue;
               definition.saturation = saturation;
               definition.lightness = lightness;
               definition.weightedHue = weightedHue;
               definition.hueMultiplier = hueMultiplier;
            } else {
               throw new IOException("Unsupported revision 443 floor opcode " + opcode);
            }
         }
         decoded[entry.getKey()] = definition;
      }
      return decoded;
   }
   private void setHslFromRgb(int newHue) {
      double calculation = (newHue >> 16 & 0xFF) / 256.0;
      double calculation2 = (newHue >> 8 & 0xFF) / 256.0;
      double calculation3 = (newHue & 0xFF) / 256.0;
      double calculation4 = calculation;
      if (calculation2 < calculation) {
         calculation4 = calculation2;
      }

      if (calculation3 < calculation4) {
         calculation4 = calculation3;
      }

      double calculation5 = calculation;
      if (calculation2 > calculation) {
         calculation5 = calculation2;
      }

      if (calculation3 > calculation5) {
         calculation5 = calculation3;
      }

      double calculation6 = 0.0;
      double calculation7 = 0.0;
      double calculation8 = (calculation4 + calculation5) / 2.0;
      if (calculation4 != calculation5) {
         if (calculation8 < 0.5) {
            calculation7 = (calculation5 - calculation4) / (calculation5 + calculation4);
         }

         if (calculation8 >= 0.5) {
            calculation7 = (calculation5 - calculation4) / (2.0 - calculation5 - calculation4);
         }

         if (calculation == calculation5) {
            calculation6 = (calculation2 - calculation3) / (calculation5 - calculation4);
         } else if (calculation2 == calculation5) {
            calculation6 = 2.0 + (calculation3 - calculation) / (calculation5 - calculation4);
         } else if (calculation3 == calculation5) {
            calculation6 = 4.0 + (calculation - calculation2) / (calculation5 - calculation4);
         }
      }

      calculation6 /= 6.0;
      this.hue = (int)(calculation6 * 256.0);
      this.saturation = (int)(calculation7 * 256.0);
      this.lightness = (int)(calculation8 * 256.0);
      if (this.saturation < 0) {
         this.saturation = 0;
      } else if (this.saturation > 255) {
         this.saturation = 255;
      }

      if (this.lightness < 0) {
         this.lightness = 0;
      } else if (this.lightness > 255) {
         this.lightness = 255;
      }

      if (calculation8 > 0.5) {
         this.hueMultiplier = (int)((1.0 - calculation8) * calculation7 * 512.0);
      } else {
         this.hueMultiplier = (int)(calculation8 * calculation7 * 512.0);
      }

      if (this.hueMultiplier <= 0) {
         this.hueMultiplier = 1;
      }

      this.weightedHue = (int)(calculation6 * this.hueMultiplier);
      if ((newHue = this.hue + (int)(Math.random() * 16.0) - 8) < 0) {
         newHue = 0;
      } else if (newHue > 255) {
         newHue = 255;
      }

      int localSaturation;
      if ((localSaturation = this.saturation + (int)(Math.random() * 48.0) - 24) < 0) {
         localSaturation = 0;
      } else if (localSaturation > 255) {
         localSaturation = 255;
      }

      int localLightness;
      if ((localLightness = this.lightness + (int)(Math.random() * 48.0) - 24) < 0) {
         localLightness = 0;
      } else if (localLightness > 255) {
         localLightness = 255;
      }

      int sourceLocalSaturation = localSaturation;
      if (localLightness > 179) {
         sourceLocalSaturation /= 2;
      }

      if (localLightness > 192) {
         sourceLocalSaturation /= 2;
      }

      if (localLightness > 217) {
         sourceLocalSaturation /= 2;
      }

      if (localLightness > 243) {
         sourceLocalSaturation /= 2;
      }

      this.packedHsl = (newHue / 4 << 10) + (sourceLocalSaturation / 32 << 7) + localLightness / 2;
   }
}
