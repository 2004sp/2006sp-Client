package client;
public class Renderable extends CacheableNode {
   VertexNormal[] vertexNormals;
   public int modelHeight = 1000;
   public void renderAtPoint(int scalarArgument, int pitchSin, int pitchCos, int yawSin, int yawCos, int scalarArgument2, int heightOffset, int scalarArgument3, int pickedId) {
      Model model;
      if ((model = this.getRotatedModel()) != null) {
         this.modelHeight = model.modelHeight;
         model.renderAtPoint(scalarArgument, pitchSin, pitchCos, yawSin, yawCos, scalarArgument2, heightOffset, scalarArgument3, pickedId);
      }
   }
   Model getRotatedModel() {
      return null;
   }
}
