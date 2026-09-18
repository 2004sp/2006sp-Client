package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
public final class QuestEntry {
   private static ArrayList freeQuestWhitelist = new QuestNameWhitelist();
   public static ArrayList<QuestEntry> entries = new ArrayList<QuestEntry>();
   private static ArrayList freeQuests = new ArrayList();
   private static ArrayList memberQuests = new ArrayList();
   private int id;
   private int y;
   private String text;
   private boolean visible;
   private static int membersHeaderY = 0;
   public static int scrollHeight = 0;

   public QuestEntry(int newId) {
      this.id = newId;
      entries.add(this);
   }

   public QuestEntry(int newId, int newY) {
      this.id = newId;
      this.y = newY;
      entries.add(this);
   }
   public static void categorizeQuests() {
      int localY = 0;
      Iterator iterator = entries.iterator();

      while (iterator.hasNext()) {
         QuestEntry questEntry;
         if ((questEntry = (QuestEntry)iterator.next()).text != null && questEntry.text.toLowerCase().equals("members quests:")) {
            localY = questEntry.y;
            break;
         }
      }

      iterator = entries.iterator();

      while (iterator.hasNext()) {
         QuestEntry questEntry2;
         if ((questEntry2 = (QuestEntry)iterator.next()).visible && questEntry2.text != null) {
            if (!freeQuestWhitelist.contains(questEntry2.text.toLowerCase())) {
               char y2 = '\ufde8';
               questEntry2.y = y2;
            } else if (questEntry2.y < localY) {
               freeQuests.add(questEntry2);
            } else {
               memberQuests.add(questEntry2);
            }
         }
      }
   }
   public static void sortAndLayoutQuests() {
      Collections.sort(freeQuests, new FreeQuestComparator());
      int localMembersHeaderY = 0;

      for (int loopIndex = 0; loopIndex < freeQuests.size(); loopIndex++) {
         QuestEntry questEntry = (QuestEntry)freeQuests.get(loopIndex);
         localMembersHeaderY = 23 + loopIndex * 15;
         int sourceY = localMembersHeaderY;
         questEntry.y = sourceY;
      }

      Collections.sort(memberQuests, new MembersQuestComparator());
      membersHeaderY = localMembersHeaderY + 19;
      Iterator iterator = entries.iterator();

      while (iterator.hasNext()) {
         QuestEntry questEntry3;
         if ((questEntry3 = (QuestEntry)iterator.next()).text != null && questEntry3.text.toLowerCase().equals("members quests:")) {
            int yOrMembersHeaderY = membersHeaderY;
            questEntry3.y = yOrMembersHeaderY;
            break;
         }
      }

      for (int loopIndex2 = 0; loopIndex2 < memberQuests.size(); loopIndex2++) {
         QuestEntry questEntry2 = (QuestEntry)memberQuests.get(loopIndex2);
         localMembersHeaderY = membersHeaderY + loopIndex2 * 15 + 15;
         int y2 = localMembersHeaderY;
         questEntry2.y = y2;
      }

      scrollHeight = localMembersHeaderY + 23;
   }

   public final void setText(String newText) {
      this.text = newText;
   }
   public final void setVisible(boolean newVisible) {
      this.visible = newVisible;
   }

   public final int getId() {
      return this.id;
   }

   public final int getY() {
      return this.y;
   }
   public final void setY(int newY) {
      this.y = newY;
   }

   public final String getText() {
      return this.text;
   }
}
