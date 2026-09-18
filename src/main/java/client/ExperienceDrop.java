package client;

import java.util.ArrayList;
public final class ExperienceDrop {
   int skillId;
   int experience;
   int y;
   public static ArrayList drops = new ArrayList();

   public ExperienceDrop(int newSkillId, int newExperience) {
      this.skillId = newSkillId;
      this.experience = newExperience;
      this.y = -1;
      drops.add(this);
   }
}
