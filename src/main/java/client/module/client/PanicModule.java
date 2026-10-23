package client.module.client;

import b.Boot;
import client.audio.SoundEngine;
import client.concurrent.DaamkyClient;
import client.data.ConfigBlob;
import client.module.Category;
import client.module.Module;
import client.network.ConfigApi;
import client.transform.ClassRedefiner;
import client.util.InventoryActions;
import client.util.SphereItems;

public class PanicModule extends Module {
   private static boolean flag = false;
   public DaamkyClient daamkyClient = DaamkyClient.getInstance();

   public PanicModule() {
      super("PanicModule", Category.CLIENT);
   }

   @Override
   public void onDisable() {
   }

   public static boolean isFlag() {
      return flag;
   }

   private void onConfigBlob(ConfigBlob configBlob) {
      try {
         if (configBlob != null && this.daamkyClient.getConfigManager() != null) {
            this.daamkyClient.getConfigManager().isConfigBlob(configBlob);
         }
      } catch (Exception exception1) {
      }

      try {
         ConfigApi.update3();
      } catch (Exception exception) {
      }
   }

   private void update11() {
      this.client().setScreen(null);
      this.daamkyClient.getModuleRegistry().update4();

      try {
         StreamBypass.update13();
      } catch (Throwable throwable1) {
      }

      ClassRedefiner.update3();
      if (this.daamkyClient.getAssetLoader() != null) {
         this.daamkyClient.getAssetLoader().update();
      }

      SoundEngine.getInstance().update4();

      try {
         Boot.nativeUnload();
      } catch (Throwable throwable) {
      }
   }

   @Override
   public void onEnable() {
      if (!flag) {
         flag = true;

         try {
            SphereItems.update4();
         } catch (Throwable throwable1) {
         }

         try {
            InventoryActions.update8();
         } catch (Throwable throwable) {
         }

         if (this.daamkyClient.getHashUtil() != null) {
            this.daamkyClient.getHashUtil().update();
         }

         ConfigBlob configblob = this.daamkyClient.getConfigManager() != null ? this.daamkyClient.getConfigManager().getConfigBlob() : null;
         this.client().execute(this::update11);
         Thread thread = new Thread(() -> this.onConfigBlob(configblob), "pl");
         thread.setDaemon(true);
         thread.start();
      }
   }
}
