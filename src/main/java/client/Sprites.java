package client;

import java.io.IOException;

/** Decodes the palette sprite groups in revision 443 JS5 archive 8. */
public final class Sprites {
   private Sprites() {
   }

   public static DecodedSprite[] load(Cache cache, String name) throws IOException {
      int group = cache.readReferenceTable(8).getGroupId(name);
      if (group < 0) throw new IOException("Missing 443 sprite group " + name);
      return load(cache, group);
   }

   public static DecodedSprite[] load(Cache cache, int group) throws IOException {
      int[] files = cache.readReferenceTable(8).getFileIds(group);
      if (files == null || files.length != 1) {
         throw new IOException("Unexpected 443 sprite file count for group " + group);
      }
      return decode(cache.readFile(8, group, files[0]));
   }

   public static DecodedSprite[] decode(byte[] data) throws IOException {
      if (data == null || data.length < 7) throw new IOException("Truncated 443 sprite group");
      int count = unsignedShort(data, data.length - 2);
      long footerPosition = (long)data.length - 7L - (long)count * 8L;
      if (count == 0 || footerPosition < 0) throw new IOException("Invalid 443 sprite count");
      int footer = (int)footerPosition;
      int canvasWidth = unsignedShort(data, footer);
      int canvasHeight = unsignedShort(data, footer + 2);
      int paletteLength = (data[footer + 4] & 255) + 1;
      int palettePosition = footer - (paletteLength - 1) * 3;
      if (palettePosition < 0) throw new IOException("Invalid 443 sprite palette");
      int[] palette = new int[paletteLength];
      for (int i = 1; i < paletteLength; i++) {
         int value = (data[palettePosition++] & 255) << 16
               | (data[palettePosition++] & 255) << 8
               | data[palettePosition++] & 255;
         palette[i] = value == 0 ? 1 : value;
      }
      int[] x = new int[count], y = new int[count], width = new int[count], height = new int[count];
      int position = footer + 5;
      for (int i = 0; i < count; i++, position += 2) x[i] = unsignedShort(data, position);
      for (int i = 0; i < count; i++, position += 2) y[i] = unsignedShort(data, position);
      for (int i = 0; i < count; i++, position += 2) width[i] = unsignedShort(data, position);
      for (int i = 0; i < count; i++, position += 2) height[i] = unsignedShort(data, position);
      DecodedSprite[] sprites = new DecodedSprite[count];
      position = 0;
      for (int i = 0; i < count; i++) {
         long area = (long)width[i] * height[i];
         if (area > Integer.MAX_VALUE || x[i] + width[i] > canvasWidth
               || y[i] + height[i] > canvasHeight || position + 1L + area > footerPosition) {
            throw new IOException("Invalid 443 sprite dimensions or pixel length at " + i);
         }
         int packing = data[position++] & 255;
         if (packing != 0 && packing != 1) {
            throw new IOException("Unsupported 443 sprite packing " + packing + " at " + i);
         }
         byte[] indices = new byte[(int)area];
         if (packing == 0) {
            System.arraycopy(data, position, indices, 0, indices.length);
            position += indices.length;
         } else {
            for (int column = 0; column < width[i]; column++) {
               for (int row = 0; row < height[i]; row++) {
                  indices[column + row * width[i]] = data[position++];
               }
            }
         }
         for (byte index : indices) {
            if ((index & 255) >= paletteLength) {
               throw new IOException("Invalid 443 sprite palette index at " + i);
            }
         }
         sprites[i] = new DecodedSprite(canvasWidth, canvasHeight, x[i], y[i],
               width[i], height[i], indices, palette);
      }
      if (position != footer - (paletteLength - 1) * 3) {
         throw new IOException("443 sprite pixel/palette boundary mismatch");
      }
      return sprites;
   }

   private static int unsignedShort(byte[] bytes, int offset) {
      return (bytes[offset] & 255) << 8 | bytes[offset + 1] & 255;
   }

   public static final class DecodedSprite {
      public final int canvasWidth, canvasHeight, xOffset, yOffset, width, height;
      public final byte[] indices;
      public final int[] palette;

      private DecodedSprite(int canvasWidth, int canvasHeight, int xOffset, int yOffset,
                            int width, int height, byte[] indices, int[] palette) {
         this.canvasWidth = canvasWidth;
         this.canvasHeight = canvasHeight;
         this.xOffset = xOffset;
         this.yOffset = yOffset;
         this.width = width;
         this.height = height;
         this.indices = indices;
         this.palette = palette;
      }

      public Sprite toSprite() {
         Sprite sprite = new Sprite(width, height);
         sprite.canvasWidth = canvasWidth;
         sprite.canvasHeight = canvasHeight;
         sprite.xOffset = xOffset;
         sprite.yOffset = yOffset;
         for (int i = 0; i < indices.length; i++) sprite.pixels[i] = palette[indices[i] & 255];
         return sprite;
      }

      public IndexedSprite toIndexedSprite() {
         return new IndexedSprite(canvasWidth, canvasHeight, xOffset, yOffset,
               width, height, indices.clone(), palette.clone());
      }

      public byte[] toFontMask() {
         byte[] mask = new byte[indices.length];
         for (int i = 0; i < indices.length; i++) {
            // Font sprites use palette color 1 for the background inside each glyph rectangle.
            mask[i] = (byte)(palette[indices[i] & 255] > 1 ? 1 : 0);
         }
         return mask;
      }
   }
}
