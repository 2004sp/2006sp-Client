package client;

import java.util.Comparator;
final class FreeQuestComparator implements Comparator<QuestEntry> {
   @Override
   public int compare(QuestEntry first, QuestEntry second) {
      return Integer.compare(first.getY(), second.getY());
   }
}
