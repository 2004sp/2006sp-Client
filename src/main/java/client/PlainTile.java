package client;
final class PlainTile {
   final int cornerColor0;
   final int cornerColor1;
   final int cornerColor2;
   final int cornerColor3;
   final int textureId;
   boolean flat = true;
   final int minimapColor;

   public PlainTile(int newCornerColor0, int newCornerColor1, int newCornerColor2, int newCornerColor3, int newTextureId, int newMinimapColor, boolean newFlat) {
      this.cornerColor0 = newCornerColor0;
      this.cornerColor1 = newCornerColor1;
      this.cornerColor2 = newCornerColor2;
      this.cornerColor3 = newCornerColor3;
      this.textureId = newTextureId;
      this.minimapColor = newMinimapColor;
      this.flat = newFlat;
      if (Client.hdMinimap) {
         this.flat = this.textureId != -1;
      }
   }
}
