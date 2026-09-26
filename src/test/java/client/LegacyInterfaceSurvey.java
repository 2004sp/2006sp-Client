package client;

import java.io.File;
import java.io.RandomAccessFile;

/** Prints legacy interface metadata for comparing gameplay callers with JS5 widgets. */
public final class LegacyInterfaceSurvey {
   public static void main(String[] args) throws Exception {
      File directory = new File(args[0]);
      try (RandomAccessFile data = new RandomAccessFile(new File(directory, "main_file_cache.dat"), "r");
           RandomAccessFile index = new RandomAccessFile(new File(directory, "main_file_cache.idx0"), "r")) {
         byte[] raw = new CacheStore(data, index, 1).read(3);
         if (raw == null) throw new IllegalStateException("Legacy interface archive missing");
         Client.getClient();
         Client.miscInterfaceSprites = new Sprite[20];
         for (int i = 0; i < Client.miscInterfaceSprites.length; i++)
            Client.miscInterfaceSprites[i] = new Sprite(1, 1);
         Widget.load(new Archive(raw), new RichTextFont[4], null);
         for (Widget widget : Widget.widgets) {
            if (widget == null) continue;
            if (args.length > 1 && widget.id != Integer.parseInt(args[1])
                  && widget.parentId != Integer.parseInt(args[1])) continue;
            System.out.println(widget.id + " parent=" + widget.parentId + " type=" + widget.type
               + " option=" + widget.optionType + " size=" + widget.width + "x" + widget.height
               + " text=" + widget.message + " tooltip=" + widget.tooltip);
         }
      }
   }
}
