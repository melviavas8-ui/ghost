package recovered.fabric.mixin.profile;

import client.data.AnimatedFloat;
import client.module.Feature;
import client.module.client.PanicModule;
import client.util.ModuleDispatcher;
import client.util.UnsafeAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pure-Java tick driver. Replaces GameMenuHooks + part of TargetAnimation when native is down.
 */
@Mixin(MinecraftClient.class)
public abstract class ClientTickMixin {
   @Inject(method = "tick()V", at = @At("HEAD"))
   private void daamkyTickHead(CallbackInfo ci) {
      if (PanicModule.isFlag()) return;
      MinecraftClient client = (MinecraftClient) (Object) this;
      Feature.mc = client;
      if (client.player == null) return;
      ModuleDispatcher d = UnsafeAccess.getModuleDispatcher();
      if (d == null) return;
      try { d.update6(); } catch (Throwable ignored) {}
   }

   @Inject(method = "tick()V", at = @At("TAIL"))
   private void daamkyTickTail(CallbackInfo ci) {
      if (PanicModule.isFlag()) return;
      MinecraftClient client = (MinecraftClient) (Object) this;
      Feature.mc = client;
      ClientPlayerEntity player = client.player;
      if (player == null) return;
      ModuleDispatcher d = UnsafeAccess.getModuleDispatcher();
      if (d == null) return;
      try { d.update2(); } catch (Throwable ignored) {}
      try {
         d.update();
         AnimatedFloat af = d.getAnimatedFloatByFloatFloat(player.getPitch(), player.getYaw());
         // onAnimatedFloat already ran inside getAnimatedFloatByFloatFloat
      } catch (Throwable ignored) {}
      try { d.update4(); } catch (Throwable ignored) {}
      try { d.update7(); } catch (Throwable ignored) {}
   }
}
