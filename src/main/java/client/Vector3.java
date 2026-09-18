package client;
public final class Vector3 {
   public static final Vector3 ZERO = new Vector3(0, 0, 0);
   private int x;
   private int y;
   private int z;

   public Vector3(int newX, int newY, int newZ) {
      this.x = newX;
      this.y = newY;
      this.z = newZ;
   }

   public final int getX() {
      return this.x;
   }

   public final int getY() {
      return this.y;
   }
   public final int getZ() {
      return this.z;
   }
   public final Vector3 subtract(Vector3 vector3) {
      return new Vector3(this.x - vector3.x, this.y - vector3.y, this.z - vector3.z);
   }
   public final Vector3 divide(float lifetime) {
      return new Vector3((int)(this.x / lifetime), (int)(this.y / lifetime), (int)(this.z / lifetime));
   }
   public final Vector3 add(Vector3 vector3) {
      this.x = this.x + vector3.x;
      this.y = this.y + vector3.y;
      this.z = this.z + vector3.z;
      return this;
   }
   public final Vector3 copy() {
      return new Vector3(this.x, this.y, this.z);
   }

   @Override
   public final String toString() {
      return "Vector{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }
}
