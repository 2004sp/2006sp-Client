package client;

/** Client copy of the server's explicit legacy-component to 443 widget bridge. */
final class WidgetIds {
   private static final java.util.Map<Integer, Integer> COMPONENTS = new java.util.HashMap<Integer, Integer>();
   private static final java.util.Map<Integer, Integer> LEGACY_COMPONENTS = new java.util.HashMap<Integer, Integer>();
   private static final java.util.Set<Integer> WARNED_UNMAPPED_COMPONENTS = new java.util.HashSet<Integer>();
   private static final java.util.Set<Integer> WARNED_UNMAPPED_INTERFACES = new java.util.HashSet<Integer>();
   static {
      COMPONENTS.put(191, 17956989);
      COMPONENTS.put(198, 24903680);
      COMPONENTS.put(199, 24903681);
      COMPONENTS.put(200, 24903682);
      COMPONENTS.put(202, 25493504);
      COMPONENTS.put(249, 17956979);
      COMPONENTS.put(331, 5898240);
      COMPONENTS.put(428, 4980736);
      COMPONENTS.put(668, 17956966);
      COMPONENTS.put(673, 17956910);
      COMPONENTS.put(779, 5636096);
      COMPONENTS.put(1675, 30474348);
      COMPONENTS.put(1676, 30474349);
      COMPONENTS.put(1677, 30474350);
      COMPONENTS.put(1678, 30474351);
      COMPONENTS.put(1679, 30474352);
      COMPONENTS.put(1680, 30474353);
      COMPONENTS.put(1681, 30474354);
      COMPONENTS.put(1682, 30474355);
      COMPONENTS.put(1683, 30474356);
      COMPONENTS.put(1684, 30474357);
      COMPONENTS.put(1686, 30474359);
      COMPONENTS.put(1687, 30474360);
      COMPONENTS.put(1688, 25362457);
      COMPONENTS.put(1701, 4915200);
      COMPONENTS.put(1740, 17956980);
      COMPONENTS.put(1752, 5046272);
      COMPONENTS.put(1767, 5177344);
      COMPONENTS.put(2279, 5832704);
      COMPONENTS.put(2426, 5111808);
      COMPONENTS.put(3214, 9764864);
      COMPONENTS.put(3278, 17956982);
      COMPONENTS.put(3799, 5767168);
      COMPONENTS.put(3984, 20971661);
      COMPONENTS.put(3985, 17956888);
      COMPONENTS.put(4449, 5963776);
      COMPONENTS.put(4508, 17956943);
      COMPONENTS.put(4536, 1572864);
      COMPONENTS.put(4537, 1572865);
      COMPONENTS.put(4538, 1572866);
      COMPONENTS.put(4539, 1572867);
      COMPONENTS.put(4540, 1572868);
      COMPONENTS.put(4541, 1572869);
      COMPONENTS.put(4542, 1572870);
      COMPONENTS.put(4682, 5701632);
      COMPONENTS.put(4708, 5373952);
      COMPONENTS.put(5064, 983040);
      COMPONENTS.put(5573, 5439488);
      COMPONENTS.put(5382, 786521);
      COMPONENTS.put(5383, 12 << 16 | 90); // bank title updated by legacy caller
      COMPONENTS.put(5385, 12 << 16 | 91); // bank scrolling contents
      COMPONENTS.put(5386, 786524);
      COMPONENTS.put(5387, 786525);
      COMPONENTS.put(8130, 786530);
      COMPONENTS.put(8131, 786531);
      COMPONENTS.put(3900, 19660875);
      COMPONENTS.put(3901, 19660876);
      COMPONENTS.put(3823, 19726336);
      COMPONENTS.put(5857, 6029312);
      COMPONENTS.put(6024, 17956991);
      COMPONENTS.put(6027, 17956955);
      COMPONENTS.put(6180, 24379392);
      COMPONENTS.put(6181, 24379393);
      COMPONENTS.put(6182, 24379394);
      COMPONENTS.put(6183, 24379395);
      COMPONENTS.put(6184, 24379396);
      COMPONENTS.put(6518, 17956984);
      COMPONENTS.put(6987, 17956924);
      COMPONENTS.put(7332, 17956890);
      COMPONENTS.put(7333, 17956891);
      COMPONENTS.put(7334, 17956892);
      COMPONENTS.put(7335, 17956907);
      COMPONENTS.put(7336, 17956893);
      COMPONENTS.put(7337, 17956895);
      COMPONENTS.put(7338, 17956896);
      COMPONENTS.put(7339, 17956897);
      COMPONENTS.put(7340, 17956898);
      COMPONENTS.put(7341, 17956899);
      COMPONENTS.put(7342, 17956900);
      COMPONENTS.put(7343, 17956901);
      COMPONENTS.put(7344, 17956902);
      COMPONENTS.put(7345, 17956903);
      COMPONENTS.put(7346, 17956904);
      COMPONENTS.put(7347, 17956905);
      COMPONENTS.put(7348, 17956906);
      COMPONENTS.put(7349, 17956992);
      COMPONENTS.put(7350, 17956993);
      COMPONENTS.put(7351, 17956994);
      COMPONENTS.put(7352, 17956911);
      COMPONENTS.put(7353, 17956913);
      COMPONENTS.put(7354, 17956918);
      COMPONENTS.put(7355, 17956919);
      COMPONENTS.put(7356, 17956920);
      COMPONENTS.put(7357, 17956925);
      COMPONENTS.put(7358, 17956927);
      COMPONENTS.put(7359, 17956928);
      COMPONENTS.put(7360, 17956932);
      COMPONENTS.put(7361, 17956936);
      COMPONENTS.put(7362, 17956939);
      COMPONENTS.put(7363, 17956940);
      COMPONENTS.put(7364, 17956941);
      COMPONENTS.put(7365, 17956945);
      COMPONENTS.put(7366, 17956946);
      COMPONENTS.put(7367, 17956947);
      COMPONENTS.put(7368, 17956950);
      COMPONENTS.put(7369, 17956952);
      COMPONENTS.put(7370, 17956957);
      COMPONENTS.put(7371, 17956959);
      COMPONENTS.put(7372, 17956961);
      COMPONENTS.put(7373, 17956971);
      COMPONENTS.put(7374, 17956972);
      COMPONENTS.put(7375, 17956975);
      COMPONENTS.put(7376, 17956976);
      COMPONENTS.put(7378, 17956983);
      COMPONENTS.put(7379, 17956985);
      COMPONENTS.put(7380, 17956986);
      COMPONENTS.put(7381, 17956987);
      COMPONENTS.put(7383, 17956894);
      COMPONENTS.put(7459, 17956922);
      COMPONENTS.put(7462, 4980744);
      COMPONENTS.put(7474, 4980746);
      COMPONENTS.put(7475, 4980747);
      COMPONENTS.put(7476, 4980748);
      COMPONENTS.put(7477, 4980749);
      COMPONENTS.put(7478, 4980750);
      COMPONENTS.put(7479, 4980751);
      COMPONENTS.put(7480, 4980752);
      COMPONENTS.put(7481, 4980753);
      COMPONENTS.put(7482, 4980754);
      COMPONENTS.put(7483, 4980755);
      COMPONENTS.put(7484, 4980756);
      COMPONENTS.put(7485, 4980757);
      COMPONENTS.put(7486, 4980758);
      COMPONENTS.put(7487, 4915210);
      COMPONENTS.put(7499, 4915212);
      COMPONENTS.put(7500, 4915213);
      COMPONENTS.put(7501, 4915214);
      COMPONENTS.put(7502, 4915215);
      COMPONENTS.put(7503, 4915216);
      COMPONENTS.put(7504, 4915217);
      COMPONENTS.put(7505, 4915218);
      COMPONENTS.put(7506, 4915219);
      COMPONENTS.put(7507, 4915220);
      COMPONENTS.put(7508, 4915221);
      COMPONENTS.put(7509, 4915222);
      COMPONENTS.put(7510, 4915223);
      COMPONENTS.put(7511, 4915224);
      COMPONENTS.put(7512, 5046280);
      COMPONENTS.put(7524, 5046282);
      COMPONENTS.put(7525, 5046283);
      COMPONENTS.put(7526, 5046284);
      COMPONENTS.put(7527, 5046285);
      COMPONENTS.put(7528, 5046286);
      COMPONENTS.put(7529, 5046287);
      COMPONENTS.put(7530, 5046288);
      COMPONENTS.put(7531, 5046289);
      COMPONENTS.put(7532, 5046290);
      COMPONENTS.put(7533, 5046291);
      COMPONENTS.put(7534, 5046292);
      COMPONENTS.put(7535, 5046293);
      COMPONENTS.put(7536, 5046294);
      COMPONENTS.put(7537, 5177352);
      COMPONENTS.put(7549, 5177354);
      COMPONENTS.put(7550, 5177355);
      COMPONENTS.put(7551, 5177356);
      COMPONENTS.put(7552, 5177357);
      COMPONENTS.put(7553, 5177358);
      COMPONENTS.put(7554, 5177359);
      COMPONENTS.put(7555, 5177360);
      COMPONENTS.put(7556, 5177361);
      COMPONENTS.put(7557, 5177362);
      COMPONENTS.put(7558, 5177363);
      COMPONENTS.put(7559, 5177364);
      COMPONENTS.put(7560, 5177365);
      COMPONENTS.put(7561, 5177366);
      COMPONENTS.put(7562, 5832714);
      COMPONENTS.put(7574, 5832716);
      COMPONENTS.put(7575, 5832717);
      COMPONENTS.put(7576, 5832718);
      COMPONENTS.put(7577, 5832719);
      COMPONENTS.put(7578, 5832720);
      COMPONENTS.put(7579, 5832721);
      COMPONENTS.put(7580, 5832722);
      COMPONENTS.put(7581, 5832723);
      COMPONENTS.put(7582, 5832724);
      COMPONENTS.put(7583, 5832725);
      COMPONENTS.put(7584, 5832726);
      COMPONENTS.put(7585, 5832727);
      COMPONENTS.put(7586, 5832728);
      COMPONENTS.put(7587, 5111818);
      COMPONENTS.put(7599, 5111820);
      COMPONENTS.put(7600, 5111821);
      COMPONENTS.put(7601, 5111822);
      COMPONENTS.put(7602, 5111823);
      COMPONENTS.put(7603, 5111824);
      COMPONENTS.put(7604, 5111825);
      COMPONENTS.put(7605, 5111826);
      COMPONENTS.put(7606, 5111827);
      COMPONENTS.put(7607, 5111828);
      COMPONENTS.put(7608, 5111829);
      COMPONENTS.put(7609, 5111830);
      COMPONENTS.put(7610, 5111831);
      COMPONENTS.put(7611, 5111832);
      COMPONENTS.put(7612, 5767178);
      COMPONENTS.put(7624, 5767180);
      COMPONENTS.put(7625, 5767181);
      COMPONENTS.put(7626, 5767182);
      COMPONENTS.put(7627, 5767183);
      COMPONENTS.put(7628, 5767184);
      COMPONENTS.put(7629, 5767185);
      COMPONENTS.put(7630, 5767186);
      COMPONENTS.put(7631, 5767187);
      COMPONENTS.put(7632, 5767188);
      COMPONENTS.put(7633, 5767189);
      COMPONENTS.put(7634, 5767190);
      COMPONENTS.put(7635, 5767191);
      COMPONENTS.put(7636, 5767192);
      COMPONENTS.put(7637, 5963784);
      COMPONENTS.put(7649, 5963786);
      COMPONENTS.put(7650, 5963787);
      COMPONENTS.put(7651, 5963788);
      COMPONENTS.put(7652, 5963789);
      COMPONENTS.put(7653, 5963790);
      COMPONENTS.put(7654, 5963791);
      COMPONENTS.put(7655, 5963792);
      COMPONENTS.put(7656, 5963793);
      COMPONENTS.put(7657, 5963794);
      COMPONENTS.put(7658, 5963795);
      COMPONENTS.put(7659, 5963796);
      COMPONENTS.put(7660, 5963797);
      COMPONENTS.put(7661, 5963798);
      COMPONENTS.put(7662, 5701642);
      COMPONENTS.put(7674, 5701644);
      COMPONENTS.put(7675, 5701645);
      COMPONENTS.put(7676, 5701646);
      COMPONENTS.put(7677, 5701647);
      COMPONENTS.put(7678, 5701648);
      COMPONENTS.put(7679, 5701649);
      COMPONENTS.put(7680, 5701650);
      COMPONENTS.put(7681, 5701651);
      COMPONENTS.put(7682, 5701652);
      COMPONENTS.put(7683, 5701653);
      COMPONENTS.put(7684, 5701654);
      COMPONENTS.put(7685, 5701655);
      COMPONENTS.put(7686, 5701656);
      COMPONENTS.put(7687, 5373962);
      COMPONENTS.put(7699, 5373964);
      COMPONENTS.put(7700, 5373965);
      COMPONENTS.put(7701, 5373966);
      COMPONENTS.put(7702, 5373967);
      COMPONENTS.put(7703, 5373968);
      COMPONENTS.put(7704, 5373969);
      COMPONENTS.put(7705, 5373970);
      COMPONENTS.put(7706, 5373971);
      COMPONENTS.put(7707, 5373972);
      COMPONENTS.put(7708, 5373973);
      COMPONENTS.put(7709, 5373974);
      COMPONENTS.put(7710, 5373975);
      COMPONENTS.put(7711, 5373976);
      COMPONENTS.put(7765, 5308416);
      COMPONENTS.put(7788, 5308426);
      COMPONENTS.put(7800, 5308428);
      COMPONENTS.put(7801, 5308429);
      COMPONENTS.put(7802, 5308430);
      COMPONENTS.put(7803, 5308431);
      COMPONENTS.put(7804, 5308432);
      COMPONENTS.put(7805, 5308433);
      COMPONENTS.put(7806, 5308434);
      COMPONENTS.put(7807, 5308435);
      COMPONENTS.put(7808, 5308436);
      COMPONENTS.put(7809, 5308437);
      COMPONENTS.put(7810, 5308438);
      COMPONENTS.put(7811, 5308439);
      COMPONENTS.put(7812, 5308440);
      COMPONENTS.put(8115, 17956962);
      COMPONENTS.put(8137, 17956958);
      COMPONENTS.put(8438, 17956915);
      COMPONENTS.put(8463, 5505024);
      COMPONENTS.put(8481, 5505032);
      COMPONENTS.put(8493, 5505034);
      COMPONENTS.put(8494, 5505035);
      COMPONENTS.put(8495, 5505036);
      COMPONENTS.put(8496, 5505037);
      COMPONENTS.put(8497, 5505038);
      COMPONENTS.put(8498, 5505039);
      COMPONENTS.put(8499, 5505040);
      COMPONENTS.put(8500, 5505041);
      COMPONENTS.put(8501, 5505042);
      COMPONENTS.put(8502, 5505043);
      COMPONENTS.put(8503, 5505044);
      COMPONENTS.put(8504, 5505045);
      COMPONENTS.put(8505, 5505046);
      COMPONENTS.put(8576, 17956967);
      COMPONENTS.put(8679, 17956921);
      COMPONENTS.put(8969, 17956973);
      COMPONENTS.put(9927, 17956990);
      COMPONENTS.put(10115, 17956930);
      COMPONENTS.put(10135, 17956942);
      COMPONENTS.put(11132, 17956951);
      COMPONENTS.put(11857, 17956938);
      COMPONENTS.put(11858, 17956988);
      COMPONENTS.put(11907, 17956944);
      COMPONENTS.put(12129, 17956914);
      COMPONENTS.put(12139, 17956968);
      COMPONENTS.put(12161, 24313876);
      COMPONENTS.put(12224, 24313877);
      COMPONENTS.put(12225, 24313878);
      COMPONENTS.put(12226, 24313879);
      COMPONENTS.put(12227, 24313880);
      COMPONENTS.put(12228, 24313881);
      COMPONENTS.put(12282, 17956933);
      COMPONENTS.put(12293, 6094848);
      COMPONENTS.put(12311, 6094856);
      COMPONENTS.put(12323, 6094858);
      COMPONENTS.put(12324, 6094859);
      COMPONENTS.put(12325, 6094860);
      COMPONENTS.put(12326, 6094861);
      COMPONENTS.put(12327, 6094862);
      COMPONENTS.put(12328, 6094863);
      COMPONENTS.put(12329, 6094864);
      COMPONENTS.put(12330, 6094865);
      COMPONENTS.put(12331, 6094866);
      COMPONENTS.put(12332, 6094867);
      COMPONENTS.put(12333, 6094868);
      COMPONENTS.put(12334, 6094869);
      COMPONENTS.put(12335, 6094870);
      COMPONENTS.put(12345, 17956960);
      COMPONENTS.put(12389, 17956953);
      COMPONENTS.put(12415, 6291456);
      COMPONENTS.put(12417, 6356992);
      COMPONENTS.put(12419, 6422528);
      COMPONENTS.put(12772, 17956909);
      COMPONENTS.put(12836, 17956926);
      COMPONENTS.put(12839, 17956935);
      COMPONENTS.put(12852, 17956916);
      COMPONENTS.put(13104, 20512768);
      COMPONENTS.put(13105, 20512769);
      COMPONENTS.put(13106, 20512770);
      COMPONENTS.put(13356, 17956995);
      COMPONENTS.put(13389, 17956948);
      COMPONENTS.put(13577, 17956934);
      COMPONENTS.put(13974, 17956954);
      COMPONENTS.put(14169, 17956929);
      COMPONENTS.put(14604, 17956931);
      COMPONENTS.put(14912, 17956970);
      COMPONENTS.put(15098, 17956977);
      COMPONENTS.put(15235, 17956981);
      COMPONENTS.put(15352, 17956974);
      COMPONENTS.put(15487, 17956949);
      COMPONENTS.put(15499, 17956964);
      COMPONENTS.put(15592, 17956978);
      COMPONENTS.put(15841, 17956917);
      COMPONENTS.put(16128, 17956937);
      COMPONENTS.put(16149, 17956923);
      COMPONENTS.put(17510, 17956912);
      COMPONENTS.put(18306, 17956965);
      COMPONENTS.put(18517, 17956956);
      COMPONENTS.put(18684, 17956963);
      COMPONENTS.put(3322, 336 << 16); // inventory while trading or duelling
      COMPONENTS.put(3417, 335 << 16 | 93); // trading partner text
      COMPONENTS.put(8144, 275 << 16 | 2); // quest title
      COMPONENTS.put(8145, 275 << 16 | 3); // first journal line
      // Spell pickers and caller-owned production interfaces.
      COMPONENTS.put(352, 90 << 16 | 105);
      COMPONENTS.put(353, 90 << 16 | 4);
      for (int spell = 0; spell < 16; spell++) COMPONENTS.put(1830 + spell, 319 << 16 | spell);
      COMPONENTS.put(2004, 319 << 16 | 174);
      COMPONENTS.put(12051, 310 << 16 | 0);
      COMPONENTS.put(12052, 310 << 16 | 61);
      for (int spell = 0; spell < 4; spell++) COMPONENTS.put(12053 + spell, 310 << 16 | spell + 1);
      COMPONENTS.put(12101, 310 << 16 | 49);
      int[] ancientButtons = {6162, 13114, 13125, 13136, 13147, 13158, 13167, 13178,
         13189, 13202, 13215, 13228, 13241, 13254, 13267, 13280};
      int[] ancientChildren = {7, 18, 29, 40, 51, 62, 71, 82, 93, 106, 119, 132, 145, 158, 171, 184};
      for (int spell = 0; spell < ancientButtons.length; spell++)
         COMPONENTS.put(ancientButtons[spell], 388 << 16 | ancientChildren[spell]);
      COMPONENTS.put(6161, 388 << 16 | 6);
      COMPONENTS.put(4226, 446 << 16 | 64);
      COMPONENTS.put(13716, 307 << 16 | 2);
      COMPONENTS.put(8716, 308 << 16 | 1);
      COMPONENTS.put(8717, 308 << 16 | 2);
      COMPONENTS.put(8847, 308 << 16 | 132);
      COMPONENTS.put(8849, 308 << 16 | 133);
      COMPONENTS.put(12560, 326 << 16 | 91);
      COMPONENTS.put(12145, 277 << 16 | 0);
      COMPONENTS.put(12150, 277 << 16 | 8);
      COMPONENTS.put(12151, 277 << 16 | 9);
      COMPONENTS.put(12152, 277 << 16 | 10);
      COMPONENTS.put(12153, 277 << 16 | 11);
      COMPONENTS.put(12154, 277 << 16 | 12);
      COMPONENTS.put(12155, 277 << 16 | 13);
      COMPONENTS.put(14165, 49 << 16 | 109);
      COMPONENTS.put(14166, 49 << 16 | 110);
      COMPONENTS.put(15948, 197 << 16 | 3);
      COMPONENTS.put(15896, 194 << 16 | 3);
      for (int id = 15902; id <= 15906; id++) COMPONENTS.put(id, 194 << 16 | 9 + id - 15902);
      COMPONENTS.put(15921, 195 << 16 | 3);
      COMPONENTS.put(15935, 196 << 16 | 3);
      COMPONENTS.put(15966, 198 << 16 | 3);
      COMPONENTS.put(15968, 198 << 16 | 5);
      COMPONENTS.put(1734, 332 << 16 | 98);
      COMPONENTS.put(1735, 332 << 16 | 99);
      COMPONENTS.put(1736, 332 << 16 | 100);
      COMPONENTS.put(1737, 332 << 16 | 101);
      COMPONENTS.put(1738, 332 << 16 | 102);
      COMPONENTS.put(15348, 332 << 16 | 103);
      int[] smeltWidgets = {2405, 2406, 2407, 2409, 2410, 2411, 2412, 2413};
      for (int i = 0; i < smeltWidgets.length; i++) COMPONENTS.put(smeltWidgets[i], 311 << 16 | 4 + i);
      COMPONENTS.put(2248, 265 << 16 | 91);
      COMPONENTS.put(3431, 335 << 16 | 107);
      COMPONENTS.put(3535, 334 << 16 | 91);
      COMPONENTS.put(6571, 106 << 16 | 133);
      COMPONENTS.put(6839, 110 << 16 | 104);
      COMPONENTS.put(6840, 110 << 16 | 105);
      COMPONENTS.put(8112, 272 << 16 | 94);
      COMPONENTS.put(8966, 154 << 16 | 88);
      COMPONENTS.put(15902, 194 << 16 | 9);
      for (int legacyId = 8147; legacyId <= 8195; legacyId++)
         COMPONENTS.put(legacyId, 275 << 16 | legacyId - 8142);
      for (int legacyId = 12174; legacyId <= 12223; legacyId++)
         COMPONENTS.put(legacyId, 275 << 16 | legacyId - 12120);
      for (java.util.Map.Entry<Integer, Integer> entry : COMPONENTS.entrySet()) {
         Integer previous = LEGACY_COMPONENTS.put(entry.getValue(), entry.getKey());
         if (previous != null && previous.intValue() != entry.getKey().intValue()) {
            throw new IllegalStateException("Duplicate revision 443 component mapping "
               + entry.getValue() + " -> " + previous + "/" + entry.getKey());
         }
      }
   }
   private WidgetIds() {}

   static int packed(int legacyId) {
      int nativePacked = Interfaces.packedForRenderId(legacyId);
      if (nativePacked >= 0) return nativePacked;
      if (isLegacyPassthrough(legacyId)) return legacyId;
      Integer packed = COMPONENTS.get(legacyId);
      return packed == null ? -1 : packed.intValue();
   }

   /** Resolves a packed revision-443 component id to the flat legacy widget id. */
   static int legacyComponent(int packedId) {
      Widget nativeWidget = Interfaces.widget(packedId);
      if (nativeWidget != null) return nativeWidget.id;
      if (isLegacyPassthrough(packedId)) return packedId;
      Integer legacy = LEGACY_COMPONENTS.get(packedId);
      if (legacy != null) return legacy.intValue();
      warnOnce(WARNED_UNMAPPED_COMPONENTS, packedId,
         "Unmapped revision 443 component 0x" + Integer.toHexString(packedId)
            + " (group=" + (packedId >>> 16) + ", child=" + (packedId & 65535) + ")");
      return -1;
   }

   /**
    * Resolves a native revision-443 interface group to the legacy root widget.
    * The bridge intentionally derives roots from component mappings instead of
    * maintaining a second hand-written root table. This keeps packet-opened
    * interfaces and component update packets on the same source of truth.
    */
   static int legacyInterface(int groupId) {
      if (groupId < 0 || groupId == 65535) return -1;
      int nativeRoot = Interfaces.rootRenderId(groupId);
      if (nativeRoot >= 0) return nativeRoot;
      if (isLegacyPassthrough(groupId)) return groupId;

      Integer childZero = LEGACY_COMPONENTS.get(groupId << 16);
      int resolved = childZero == null ? -1 : legacyRoot(childZero.intValue());
      if (resolved >= 0) return resolved;

      int candidateRoot = -1;
      for (java.util.Map.Entry<Integer, Integer> entry : LEGACY_COMPONENTS.entrySet()) {
         if ((entry.getKey().intValue() >>> 16) != groupId) continue;
         int root = legacyRoot(entry.getValue().intValue());
         if (root < 0) continue;
         if (candidateRoot == -1) {
            candidateRoot = root;
         } else if (candidateRoot != root) {
            warnOnce(WARNED_UNMAPPED_INTERFACES, groupId,
               "Ambiguous revision 443 interface group " + groupId
                  + " maps to legacy roots " + candidateRoot + " and " + root);
            return candidateRoot;
         }
      }
      if (candidateRoot >= 0) return candidateRoot;

      warnOnce(WARNED_UNMAPPED_INTERFACES, groupId,
         "Unmapped revision 443 interface group " + groupId);
      return -1;
   }

   static Widget widget(int packedId) {
      int legacyId = legacyComponent(packedId);
      return legacyId >= 0 && Widget.widgets != null && legacyId < Widget.widgets.length
         ? Widget.widgets[legacyId] : null;
   }

   static void validateLoadedWidgets() {
      if (Widget.widgets == null) return;
      int missing = 0;
      for (Integer legacyId : COMPONENTS.keySet()) {
         int id = legacyId.intValue();
         if (id < 0 || id >= Widget.widgets.length || Widget.widgets[id] == null) missing++;
      }
      if (missing != 0) {
         System.err.println("Revision 443 widget bridge: " + missing + "/" + COMPONENTS.size()
            + " mapped legacy components are absent from the loaded interface archive");
      }
   }

   private static int legacyRoot(int legacyId) {
      if (Widget.widgets == null || legacyId < 0 || legacyId >= Widget.widgets.length
            || Widget.widgets[legacyId] == null) return -1;
      int current = legacyId;
      for (int depth = 0; depth < 32; depth++) {
         Widget widget = current >= 0 && current < Widget.widgets.length
            ? Widget.widgets[current] : null;
         if (widget == null || widget.parentId < 0 || widget.parentId == current
               || widget.parentId >= Widget.widgets.length || Widget.widgets[widget.parentId] == null) {
            return current;
         }
         current = widget.parentId;
      }
      return current;
   }

   private static boolean isLegacyPassthrough(int id) {
      return id >= 18890 && id <= 19103
              || id == 19497
              || id >= 19508 && id <= 19565
              || id >= 19580 && id <= 19584
              || id >= 19600 && id <= 19640;
   }

   private static void warnOnce(java.util.Set<Integer> warned, int id, String message) {
      if (warned.add(id)) System.err.println(message);
   }
}
