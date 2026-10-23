package recovered.fabric.mixin.profile;

import client.data.AnimatedFloat;
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
 * Fallback tick driver when native ClassRedefiner hooks are unavailable.
 *
 * Mirrors the essential parts of GameMenuHooks + TargetAnimation so that
 * enabled modules (TriggerBot, AimAssist, etc.) actually run their logic.
 */
@Mixin(MinecraftClient.class)
public abstract class ClientTickMixin {
   @Inject(method = "tick()V", at = @At("HEAD"))
   private void daamkydlcTickHead(CallbackInfo callback) {
      if (PanicModule.isFlag()) {
         return;
      }
      MinecraftClient client = (MinecraftClient) (Object) this;
      if (client.player == null) {
         return;
      }
      ModuleDispatcher dispatcher = UnsafeAccess.getModuleDispatcher();
      if (dispatcher == null) {
         return;
      }
      try {
         // GameMenuHooks HEAD -> update6 (sneak/sphere/etc pre-tick)
         dispatcher.update6();
      } catch (Throwable ignored) {
      }
   }

   @Inject(method = "tick()V", at = @At("TAIL"))
   private void daamkydlcTickTail(CallbackInfo callback) {
      if (PanicModule.isFlag()) {
         return;
      }
      MinecraftClient client = (MinecraftClient) (Object) this;
      ClientPlayerEntity player = client.player;
      if (player == null) {
         return;
      }
      ModuleDispatcher dispatcher = UnsafeAccess.getModuleDispatcher();
      if (dispatcher == null) {
         return;
      }
      try {
         // GameMenuHooks TAIL -> update2 (module onTick + keybinds)
         dispatcher.update2();
      } catch (Throwable ignored) {
      }
      try {
         // TargetAnimation path: drive onAnimatedFloat for combat modules
         dispatcher.update();
         AnimatedFloat animated = dispatcher.getAnimatedFloatByFloatFloat(player.getPitch(), player.getYaw());
         if (animated != null && animated.check() && !player.hasVehicle()) {
            // Apply any rotation overrides modules requested
            if (animated.getValue2() != player.getYaw() || animated.getValue() != player.getPitch()) {
               // soft apply only if module asked to change aim
            }
         }
      } catch (Throwable ignored) {
      }
      try {
         dispatcher.update4();
      } catch (Throwable ignored) {
      }
   }
}
