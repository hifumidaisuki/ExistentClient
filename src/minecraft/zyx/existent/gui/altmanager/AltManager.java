package zyx.existent.gui.altmanager;

import java.util.ArrayList;

public class AltManager {
   public static Alt lastAlt;
   public static ArrayList registry = new ArrayList();

   public ArrayList getRegistry() {
      return registry;
   }

   public void setLastAlt(Alt alt) {
      lastAlt = alt;
   }
}
