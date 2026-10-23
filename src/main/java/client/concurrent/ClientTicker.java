package client.concurrent;

import client.audio.SoundEngine;
import client.module.client.PanicModule;
import client.network.ConfigApi;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientTicker {
   private final DaamkyClient daamkyClient = DaamkyClient.getInstance();
   private final ScheduledExecutorService scheduledExecutorService;
   private boolean flag = false;

   public ClientTicker(ScheduledExecutorService scheduledExecutorService2) {
      this.scheduledExecutorService = scheduledExecutorService2;
      Thread thread = Thread.ofPlatform().name("e").unstarted(this::update);
      Runtime.getRuntime().addShutdownHook(thread);
      this.update3();
   }

   private void update() {
      if (!this.flag) {
         this.flag = true;
         if (!PanicModule.isFlag()) {
            try {
               if (this.daamkyClient.getHashUtil() != null) {
                  this.daamkyClient.getHashUtil().update4();
               }
            } catch (Exception exception1) {
            }

            try {
               ConfigApi.update2();
            } catch (Exception exception) {
            }

            try {
               this.daamkyClient.getScheduledExecutorService().shutdown();
               if (!this.daamkyClient.getScheduledExecutorService().awaitTermination(5L, TimeUnit.SECONDS)) {
                  this.daamkyClient.getScheduledExecutorService().shutdownNow();
               }
            } catch (InterruptedException interruptedexception) {
               this.daamkyClient.getScheduledExecutorService().shutdownNow();
            }

            SoundEngine.getInstance().update4();
         }
      }
   }

   public void update3() {
      this.scheduledExecutorService.scheduleAtFixedRate(() -> {
         if (!PanicModule.isFlag()) {
            try {
               ConfigApi.update();
            } catch (Exception exception1) {
            }

            try {
               if (this.daamkyClient.getHashUtil() != null) {
                  this.daamkyClient.getHashUtil().update4();
               }
            } catch (Exception exception) {
            }
         }
      }, 3L, 3L, TimeUnit.MINUTES);
   }
}
