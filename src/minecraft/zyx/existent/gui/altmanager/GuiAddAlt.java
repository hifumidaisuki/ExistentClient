package zyx.existent.gui.altmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.net.Proxy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import zyx.existent.Existent;
import zyx.existent.gui.altmanager.althening.api.AltService;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.file.impl.Alts;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiAddAlt extends GuiScreen {
   private final GuiAltManager manager;
   private PasswordField password;
   private String status;
   private GuiTextField username;

   GuiAddAlt(GuiAltManager manager) {
      this.status = TextFormatting.GRAY + "Idle...";
      this.manager = manager;
   }

   protected void actionPerformed(GuiButton button) {
      switch (button.id) {
         case 0:
            AddAltThread login = new AddAltThread(this.username.getText(), this.password.getText());
            login.start();
            break;
         case 1:
            this.mc.displayGuiScreen(this.manager);
            break;
         case 2:
            String data;
            try {
               data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (Exception var4) {
               return;
            }

            if (data.contains(":")) {
               String[] credentials = data.split(":");
               this.username.setText(credentials[0]);
               this.password.setText(credentials[1]);
            }
         case 1337:
             String data1;
             try {
                data1 = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
             } catch (Exception var4) {
                return;
             }
             ArrayList<String> list = new ArrayList<String>(Arrays.asList(data1.replace("\r\n", "\n").split("\n")));
             for (String string : list) {
            	 String user = "";
            	 String pass = "";
            	 if(string.contains("@alt.com")&&!string.contains(":")) {
            		 user = string;
            		 pass = "Existent";
            	 }else {
            		  user = string.split(":")[0];
                	  pass = string.split(":")[1];
            	 }
            	 AltManager.registry.add(new Alt(user, pass, "LOGIN NOW", Alt.Status.Working));
			}
             this.mc.displayGuiScreen(this.manager);
             break;
      }

   }

   public void drawScreen(int i, int j, float f) {
      RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
      CFontRenderer fonts = Fonts.default18;
      this.username.drawTextBox();
      this.password.drawTextBox();
      fonts.drawCenteredString("Add Alt", this.width / 2, 20, -1);
      if (this.username.getText().isEmpty() && !this.username.isFocused()) {
         this.drawString(this.mc.fontRendererObj, "Username / E-Mail", this.width / 2 - 96, 66, -7829368);
      }

      if (this.password.getText().isEmpty() && !this.password.isFocused()) {
         this.drawString(this.mc.fontRendererObj, "Password", this.width / 2 - 96, 106, -7829368);
      }

      fonts.drawCenteredString(this.status, this.width / 2F, 30, -1);
      super.drawScreen(i, j, f);
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiExButton(0, this.width / 2 - 100, this.height / 4 + 92 + 12, "Login"));
      this.buttonList.add(new GuiExButton(1, this.width / 2 - 100, this.height / 4 + 116 + 12, "Back"));
      this.buttonList.add(new GuiExButton(2, this.width / 2 - 100, this.height / 4 + 92 - 12, "Import user:pass"));
      this.buttonList.add(new GuiExButton(1337, this.width / 2 - 100, this.height / 4 + 92 - 24 - 12, "Import AltList(ClipBoard)"));
      this.username = new GuiTextField(this.eventButton, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
      this.password = new PasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
   }

   protected void keyTyped(char par1, int par2) {
      this.username.textboxKeyTyped(par1, par2);
      this.password.textboxKeyTyped(par1, par2);
      if (par1 == '\t' && (this.username.isFocused() || this.password.isFocused())) {
         this.username.setFocused(!this.username.isFocused());
         this.password.setFocused(!this.password.isFocused());
      }

      if (par1 == '\r') {
         this.actionPerformed(this.buttonList.get(0));
      }
   }

   protected void mouseClicked(int par1, int par2, int par3) {
      try {
         super.mouseClicked(par1, par2, par3);
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      this.username.mouseClicked(par1, par2, par3);
      this.password.mouseClicked(par1, par2, par3);
   }

   private static void setStatus(GuiAddAlt guiAddAlt, String status) {
      guiAddAlt.status = status;
   }

   private class AddAltThread extends Thread {
      private final String password;
      private final String username;

      AddAltThread(String username, String password) {
         this.username = username;
         this.password = password;
         GuiAddAlt.setStatus(GuiAddAlt.this, TextFormatting.GRAY + "Idle...");
      }

      private void checkAndAddAlt(String username, String password) {
         try {
            GuiAltManager.altService.switchService(username.contains("@alt.com") ? AltService.EnumAltService.THEALTENING : AltService.EnumAltService.MOJANG);

            YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(username);
            auth.setPassword(password);

            try {
               auth.logIn();
               AltManager.registry.add(new Alt(username, password, auth.getSelectedProfile().getName(), Alt.Status.Working));

               try {
                  Existent.getFileManager().getFile(Alts.class).saveFile();
               } catch (Exception var6) {
                  ;
               }

               GuiAddAlt.setStatus(GuiAddAlt.this, "Alt added. (" + username + ")");
            } catch (AuthenticationException var7) {
               GuiAddAlt.setStatus(GuiAddAlt.this, TextFormatting.RED + "Alt failed!");
               var7.printStackTrace();
            }
         } catch (Throwable e) {
            GuiAddAlt.setStatus(GuiAddAlt.this, TextFormatting.RED + "Error 1 :(");
            e.printStackTrace();;
         }
      }

      public void run() {
         if (this.password.equals("")) {
            AltManager.registry.add(new Alt(this.username, ""));
            GuiAddAlt.setStatus(GuiAddAlt.this, TextFormatting.GREEN + "Alt added. (" + this.username + " - offline name)");
         } else {
            GuiAddAlt.setStatus(GuiAddAlt.this, TextFormatting.AQUA + "Trying alt...");
            this.checkAndAddAlt(this.username, this.password);
         }
      }
   }
}
