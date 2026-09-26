package client;

import java.io.File;

/** Prints searchable text from native interface groups for bridge research. */
public final class InterfaceSurvey {
   public static void main(String[] args) throws Exception {
      Widget.widgets = new Widget[20000];
      try (LocalCache source = new LocalCache(new File(args[0]))) {
         Cache cache = new Cache(source);
         Interfaces.initialize(cache, null);
         for (int group : cache.readReferenceTable(3).getGroupIds()) {
            if (args.length > 1 && !"all".equals(args[1]) && group != Integer.parseInt(args[1])) continue;
            int[] children = cache.readReferenceTable(3).getFileIds(group);
            if (children == null) continue;
            for (int child : children) {
               Widget widget = Interfaces.widget(group << 16 | child);
               if (widget == null) continue;
               if (args.length > 1) {
                  System.out.println(group + ":" + child + " type=" + widget.type
                     + " size=" + widget.width + "x" + widget.height
                     + " text=" + widget.message + " tooltip=" + widget.tooltip);
                  continue;
               }
               if (widget.message == null) continue;
               String message = widget.message.trim().replace('\n', ' ');
               if (!message.isEmpty()) System.out.println(group + ":" + child + " " + message);
            }
         }
      }
   }
}
