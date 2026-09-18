package client;

import java.util.ArrayList;
import java.util.List;
public final class BankTab {
   List searchResultSlots = new ArrayList();
   int tabIndex;
   int containerWidgetId;
   int actionWidgetId;
   int toggleWidgetId;
   int itemCount;
   int filteredItemCount;
   boolean hasSearchResults;

   BankTab(int newTabIndex, int newContainerWidgetId, int newActionWidgetId, int newToggleWidgetId, int newItemCount, int newFilteredItemCount, boolean newHasSearchResults) {
      this.tabIndex = newTabIndex;
      this.containerWidgetId = newContainerWidgetId;
      this.actionWidgetId = newActionWidgetId;
      this.toggleWidgetId = newToggleWidgetId;
      this.itemCount = newItemCount;
      this.filteredItemCount = newFilteredItemCount;
      this.hasSearchResults = newHasSearchResults;
   }
   public final int getContainerWidgetId() {
      return this.containerWidgetId;
   }
   public final int getActionWidgetId() {
      return this.actionWidgetId;
   }
   public final int getDisplayedItemCount() {
      return Client.fetchMusic ? this.filteredItemCount : this.itemCount;
   }
   public final boolean hasSearchResults() {
      return Client.server == "" ? false : this.hasSearchResults;
   }
   public final List getSearchResultSlots() {
      return this.searchResultSlots;
   }
}
