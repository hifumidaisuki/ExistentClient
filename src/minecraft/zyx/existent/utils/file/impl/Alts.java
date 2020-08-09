package zyx.existent.utils.file.impl;

import zyx.existent.gui.altmanager.Alt;
import zyx.existent.gui.altmanager.AltManager;
import zyx.existent.utils.file.FileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class Alts extends FileManager.CustomFile {
   public Alts(String name, boolean Module, boolean loadOnStart) {
      super(name, Module, loadOnStart);
   }

   public void loadFile() throws IOException {
      BufferedReader variable9 = new BufferedReader(new FileReader(this.getFile()));

      String line;
      while((line = variable9.readLine()) != null) {
         String[] arguments = line.split(":");

         for(int i = 0; i < 2; ++i) {
            arguments[i].replace(" ", "");
         }

         if (arguments.length > 2) {
            AltManager.registry.add(new Alt(arguments[0], arguments[1], arguments[2], arguments.length > 3 ? Alt.Status.valueOf(arguments[3]) : Alt.Status.Unchecked));
         } else {
            AltManager.registry.add(new Alt(arguments[0], arguments[1]));
         }
      }

      variable9.close();
      System.out.println("Loaded " + this.getName() + " File!");
   }

   public void saveFile() throws IOException {
      PrintWriter alts = new PrintWriter(new FileWriter(this.getFile()));
      Iterator var2 = AltManager.registry.iterator();

      while(var2.hasNext()) {
         Alt alt = (Alt)var2.next();
         if (alt.getMask().equals("")) {
            alts.println(alt.getUsername() + ":" + alt.getPassword() + ":" + alt.getUsername() + ":" + alt.getStatus());
         } else {
            alts.println(alt.getUsername() + ":" + alt.getPassword() + ":" + alt.getMask() + ":" + alt.getStatus());
         }
      }
      alts.close();
   }
}