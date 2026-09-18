package client;
public class Actor extends Renderable {
   public int nextEmoteFrame;
   public int nextMovementFrame;
   public int nextGraphicFrame;
   public int index = -1;
   public final int[] pathX = new int[10];
   public final int[] pathY = new int[10];
   public int interactingEntity = -1;
   int updateCounter;
   int turnSpeed = 32;
   int runAnimationId = -1;
   public String spokenText;
   public int height = 200;
   public int targetOrientation;
   int idleAnimationId = -1;
   int standTurnAnimationId = -1;
   int textColor;
   final int[] hitDamages = new int[4];
   final int[] hitMarkTypes = new int[4];
   final int[] hitsLoopCycle = new int[4];
   final int[] hitIcons = new int[4];
   int movementAnimation = -1;
   int movementFrame;
   int movementFrameCycle;
   int graphicId = -1;
   int graphicFrame;
   int graphicFrameCycle;
   int graphicDelay;
   int graphicHeight;
   int pathLength;
   public int emoteAnimation = -1;
   int emoteFrame;
   int emoteFrameCycle;
   int animationDelay;
   int animationLoopCount;
   int textEffect;
   public int healthBarEndCycle = -1000;
   public int currentHealth;
   public int maxHealth;
   int textCycle = 100;
   int lastUpdateCycle;
   int faceX;
   int faceY;
   int size = 1;
   boolean animationStretches = false;
   int emotePathLength;
   int forceMoveStartX;
   int forceMoveEndX;
   int forceMoveStartY;
   int forceMoveEndY;
   int forceMoveStartCycle;
   int forceMoveEndCycle;
   int forceMoveFaceDirection;
   public int worldX;
   public int worldY;
   int orientation;
   final boolean[] pathRun = new boolean[10];
   int walkAnimationId = -1;
   int turnAroundAnimationId = -1;
   int turnRightAnimationId = -1;
   int turnLeftAnimationId = -1;
   public final void setPosition(int x, int y, boolean teleport) {
      int npcIdOrDeltaX = -1;
      if (this instanceof Npc) {
         Npc npc;
         npcIdOrDeltaX = (int)(npc = (Npc)this).definition.id;
      }

      if (this.emoteAnimation != -1 && AnimationSequence.sequences[AnimationSequence.remapId(this.emoteAnimation, npcIdOrDeltaX)].priority == 1) {
         this.emoteAnimation = -1;
      }

      if (!teleport) {
         npcIdOrDeltaX = x - this.pathX[0];
         int deltaY = y - this.pathY[0];
         if (npcIdOrDeltaX >= -8 && npcIdOrDeltaX <= 8 && deltaY >= -8 && deltaY <= 8) {
            if (this.pathLength < 9) {
               this.pathLength++;
            }

            for (int pathIndex = this.pathLength; pathIndex > 0; pathIndex--) {
               this.pathX[pathIndex] = this.pathX[pathIndex - 1];
               this.pathY[pathIndex] = this.pathY[pathIndex - 1];
               this.pathRun[pathIndex] = this.pathRun[pathIndex - 1];
            }

            this.pathX[0] = x;
            this.pathY[0] = y;
            this.pathRun[0] = false;
            return;
         }
      }

      this.pathLength = 0;
      this.emotePathLength = 0;
      this.updateCounter = 0;
      this.pathX[0] = x;
      this.pathY[0] = y;
      this.worldX = (this.pathX[0] << 7) + (this.size << 6);
      this.worldY = (this.pathY[0] << 7) + (this.size << 6);
   }
   public final void resetPath() {
      this.pathLength = 0;
      this.emotePathLength = 0;
   }
   public final void addHit(int hitType, int damage, int hitIcon, int cycle) {
      for (int hitsLoopCycleIndex = 0; hitsLoopCycleIndex < 4; hitsLoopCycleIndex++) {
         if (this.hitsLoopCycle[hitsLoopCycleIndex] <= cycle) {
            this.hitDamages[hitsLoopCycleIndex] = damage;
            this.hitMarkTypes[hitsLoopCycleIndex] = hitType;
            this.hitIcons[hitsLoopCycleIndex] = hitIcon;
            this.hitsLoopCycle[hitsLoopCycleIndex] = cycle + 70;
            return;
         }
      }
   }
   public final void moveInDirection(boolean running, int direction) {
      int npcId = -1;
      if (this instanceof Npc) {
         Npc npc;
         npcId = (int)(npc = (Npc)this).definition.id;
      }

      int x = this.pathX[0];
      int y = this.pathY[0];
      if (direction == 0) {
         x--;
         y++;
      }

      if (direction == 1) {
         y++;
      }

      if (direction == 2) {
         x++;
         y++;
      }

      if (direction == 3) {
         x--;
      }

      if (direction == 4) {
         x++;
      }

      if (direction == 5) {
         x--;
         y--;
      }

      if (direction == 6) {
         y--;
      }

      if (direction == 7) {
         x++;
         y--;
      }

      if (this.emoteAnimation != -1 && AnimationSequence.sequences[AnimationSequence.remapId(this.emoteAnimation, npcId)].priority == 1) {
         this.emoteAnimation = -1;
      }

      if (this.pathLength < 9) {
         this.pathLength++;
      }

      for (int pathIndex = this.pathLength; pathIndex > 0; pathIndex--) {
         this.pathX[pathIndex] = this.pathX[pathIndex - 1];
         this.pathY[pathIndex] = this.pathY[pathIndex - 1];
         this.pathRun[pathIndex] = this.pathRun[pathIndex - 1];
      }

      this.pathX[0] = x;
      this.pathY[0] = y;
      this.pathRun[0] = running;
   }

   public boolean isVisible() {
      return false;
   }
}
