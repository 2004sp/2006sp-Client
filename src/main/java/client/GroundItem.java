package client;
final class GroundItem extends Renderable {
   public int id;
   public int quantity;
   @Override
   public final Model getRotatedModel() {
      return ItemDefinition.lookup(this.id).getModel(this.quantity);
   }
}
