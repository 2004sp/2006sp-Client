package client;
public final class SceneTile extends Node {
   int plane;
   final int x;
   final int y;
   final int originalPlane;
   public PlainTile plainTile;
   public ShapedTile shapedTile;
   public WallObject wall;
   public GroundDecoration wallDecoration;
   public WallDecoration floorDecoration;
   public GroundItemPile groundItemPile;
   int interactiveObjectCount;
   public final InteractiveObject[] interactiveObjects = new InteractiveObject[5];
   final int[] interactiveObjectFlags = new int[5];
   int interactiveObjectFlagsOr;
   int drawLevel;
   boolean draw;
   boolean visible;
   boolean drawEntities;
   int wallCullMask;
   int wallCullDirection;
   int wallCullComplement;
   int delayedWallMask;
   public SceneTile linkedBelowTile;

   public SceneTile(int newPlane, int newX, int newY) {
      this.originalPlane = this.plane = newPlane;
      this.x = newX;
      this.y = newY;
   }
}
