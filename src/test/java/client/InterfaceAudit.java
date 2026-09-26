package client;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.InvalidMidiDataException;

/** Exhaustive archive-3 decode audit against a local revision-443 cache. */
public final class InterfaceAudit {
   public static void main(String[] args) throws Exception {
      if (args.length != 1) {
         throw new IllegalArgumentException("Usage: InterfaceAudit <cache dir>");
      }
      Widget.widgets = new Widget[20000];
      try (LocalCache source = new LocalCache(new File(args[0]))) {
         Cache cache = new Cache(source);
         RichTextFont[] fonts = {
            new RichTextFont(cache, "p11_full"), new RichTextFont(cache, "p12_full"),
            new RichTextFont(cache, "b12_full"), new RichTextFont(cache, "q8_full")
         };
         Interfaces.initialize(cache, fonts);
         Interfaces.auditAllGroups();
         for (String name : new String[]{"p11_full", "p12_full", "b12_full", "q8_full"}) {
            System.out.println("font " + name + " sprite=" + cache.readReferenceTable(8).getGroupId(name)
               + " metrics=" + cache.readReferenceTable(13).getGroupId(name));
         }
         for (int id : new int[]{494, 495, 496, 497, 645, 646, 647, 648}) {
            Sprites.DecodedSprite[] glyphs = Sprites.load(cache, id);
            System.out.println("font sprite " + id + " glyphs=" + glyphs.length
               + " canvas=" + glyphs[0].canvasWidth + "x" + glyphs[0].canvasHeight
               + " A=" + glyphs[65].width + "x" + glyphs[65].height
               + " a=" + glyphs[97].width + "x" + glyphs[97].height);
         }
         String[] media = {"titlebox","titlebutton","runes","logo","title_mute",
            "invback","chatback","mapback","backbase1","backbase2","backhmid1",
            "sideicons","compass","mapedge","overlay_multiway","mapscene","mapfunction","hitmarks",
            "headicons_hint","headicons_prayer","headicons_pk","tradebacking","steelborder","steelborder2",
            "miscgraphics","mapmarker","cross","staticons","staticons2","stat_icons","skill_icons","skills","mapdots","mod_icons","scrollbar",
            "redstone1","redstone2","redstone3","gamemode_icons","backleft1","backleft2","backright1",
            "backright2","backtop1","backvmid1","backvmid2","backvmid3","backhmid2"};
         for (String name : media) {
            int group = cache.readReferenceTable(8).getGroupId(name);
            int count = group < 0 ? 0 : Sprites.load(cache, group).length;
            System.out.println("media " + name + "=" + group + " count=" + count);
         }
         for (String name : new String[]{"title.jpg", "title", "logo"}) {
            System.out.println("binary10 " + name + "=" + cache.readReferenceTable(10).getGroupId(name));
         }
         byte[] title = cache.readFile(10, "title.jpg", "");
         System.out.println("title.jpg bytes=" + title.length);
         if (title.length == 0) throw new IllegalStateException("Empty revision 443 title.jpg");
         for (int archive : new int[]{6, 11}) {
            int[] groups = cache.readReferenceTable(archive).getGroupIds();
            int decoded = 0;
            int missing = 0;
            int invalid = 0;
            for (int group : groups) {
               int[] files = cache.readReferenceTable(archive).getFileIds(group);
               if (files.length != 1) throw new IllegalStateException("Invalid music group " + archive + ":" + group);
               try {
                  byte[] midi = cache.readFile(archive, group, files[0]);
                  MidiSystem.getSequence(new ByteArrayInputStream(midi));
                  decoded++;
               } catch (IOException exception) {
                  missing++;
                  System.out.println("music missing " + archive + ":" + group
                     + " - " + exception.getMessage());
               } catch (InvalidMidiDataException exception) {
                  invalid++;
                  System.out.println("music invalid " + archive + ":" + group
                     + " - " + exception.getMessage());
               }
            }
            System.out.println("music archive " + archive + ": " + decoded + "/" + groups.length
               + " MIDI tracks decoded, missing=" + missing + ", invalid=" + invalid);
         }
      }
   }
}
