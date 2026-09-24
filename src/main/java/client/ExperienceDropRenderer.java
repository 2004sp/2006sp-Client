package client;

import java.text.NumberFormat;
import java.util.Iterator;

final class ExperienceDropRenderer {
   private ExperienceDropRenderer() {
   }

   static void draw(int xpDropPosition, int xpDropSize, int xpDropColor,
                    int screenMode, int clientWidth, int projectedEntityX,
                    int projectedEntityY, BitmapFont smallFont,
                    BitmapFont plainFont, BitmapFont boldFont,
                    Sprite[] skillIconSprites, Sprite[] customSprites,
                    NumberFormat numberFormat) {
      int scalar = -1;
      int localProjectedEntityX;
      int localProjectedEntityY;
      if (xpDropPosition == 2) {
         localProjectedEntityX = projectedEntityX;
         localProjectedEntityY = projectedEntityY;
         if (screenMode != 0) {
            scalar = localProjectedEntityY - 100;
         }
      } else {
         localProjectedEntityX = screenMode == 0 ? 510 : clientWidth - 250;
         localProjectedEntityY = 100;
      }

      int scalar2 = 0;
      BitmapFont bitmapFont = null;
      if (xpDropSize == 0) {
         bitmapFont = smallFont;
      } else if (xpDropSize == 1) {
         bitmapFont = plainFont;
      } else if (xpDropSize == 2) {
         bitmapFont = boldFont;
      }

      Iterator iterator = ExperienceDrop.drops.iterator();

      while (iterator.hasNext()) {
         ExperienceDrop experienceDrop;
         if ((experienceDrop = (ExperienceDrop)iterator.next()) != null) {
            if (experienceDrop.y == -1) {
               experienceDrop.y = localProjectedEntityY + scalar2 * 24;
            }

            String text = "+" + numberFormat.format(experienceDrop.experience) + " xp";
            int measuredTextWidth = bitmapFont.getTextWidth(text);
            int scalar3 = localProjectedEntityX - measuredTextWidth;
            int sourceMeasuredTextWidth = measuredTextWidth;
            int localSpriteWidth = 0;
            if (xpDropSize > 0) {
               scalar3 -= 5;
            }

            Sprite sprite;
            if (xpDropSize > 0) {
               sprite = skillIconSprites[experienceDrop.skillId];
            } else {
               sprite = customSprites[53 + experienceDrop.skillId];
            }

            if (sprite != null) {
               localSpriteWidth = sprite.spriteWidth + 3;
               if (xpDropPosition == 2 && xpDropSize > 0) {
                  localSpriteWidth = sprite.spriteWidth + 8;
               }

               sourceMeasuredTextWidth += localSpriteWidth;
               if (xpDropPosition == 1) {
                  sprite.drawOutlinedSprite(scalar3 - localSpriteWidth,
                        experienceDrop.y - sprite.spriteHeight, 0);
               } else {
                  sprite.drawOutlinedSprite(localProjectedEntityX - sourceMeasuredTextWidth / 2,
                        experienceDrop.y - sprite.spriteHeight, 0);
               }
            }

            if (xpDropPosition == 1) {
               bitmapFont.textLeftShadow(true, localProjectedEntityX - measuredTextWidth,
                     xpDropColor, text, experienceDrop.y);
            } else {
               bitmapFont.textLeftShadow(true,
                     localProjectedEntityX - sourceMeasuredTextWidth / 2 + localSpriteWidth,
                     xpDropColor, text, experienceDrop.y);
            }

            scalar2++;
            experienceDrop.y--;
            if (experienceDrop.y == scalar) {
               iterator.remove();
            }
         }
      }
   }
}
