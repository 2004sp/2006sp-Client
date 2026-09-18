package worldmap;

import java.awt.Frame;
import java.awt.Graphics;
public final class WorldMapFrame extends Frame {
   private WorldMapGameShell gameShell;

   public WorldMapFrame(WorldMapGameShell worldMapGameShell, int scalarArgument, int scalarArgument2) {
      this.gameShell = worldMapGameShell;
      this.setTitle("World Map");
      this.setResizable(false);
      this.show();
      this.toFront();
      this.resize(scalarArgument + 8, scalarArgument2 + 28);
   }

   @Override
   public final void paint(Graphics graphics) {
      this.gameShell.paint(graphics);
   }

   @Override
   public final void update(Graphics graphics) {
      this.gameShell.update(graphics);
   }

   @Override
   public final Graphics getGraphics() {
      Graphics graphics;
      (graphics = super.getGraphics()).translate(4, 24);
      return graphics;
   }
}
