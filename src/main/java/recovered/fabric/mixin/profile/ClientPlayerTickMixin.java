package recovered.fabric.mixin.profile;

import client.data.AnimatedFloat;
import client.module.Feature;
import client.module.client.PanicModule;
import client.util.ModuleDispatcher;
import client.util.UnsafeAccess;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Player-tick fallback for TargetAnimation rotation/attack path.
 * Yarn 1.21.4: ClientPlayerEntity.tickMovement / LivingEntity.tick mapped as tick.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerTickMixin {
   @Inject(method = "tick()V", at = @At("HEAD"))
   private void daamkyPlayerTickHead(CallbackInfo ci) {
      if (PanicModule.isFlag()) return;
      ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
      if (Feature.mc == null) {
         Feature.mc = net.minecraft.client.MinecraftClient.getInstance();
      }
      ModuleDispatcher d = UnsafeAccess.getModuleDispatcher();
      if (d == null) return;
      try {
         d.update();
         AnimatedFloat af = d.getAnimatedFloatByFloatFloat(player.getPitch(), player.getYaw());
         if (af != null && af.check() && !player.hasVehicle()) {
            // Modules (AimAssist / future AttackAura) may have written desired angles into af
            player.setYaw(af.getValue2());
            player.setPitch(af.getValue());
         }
      } catch (Throwable ignored) {}
   }

   @Inject(method = "tick()V", at = @At("TAIL"))
   private void daamkyPlayerTickTail(CallbackInfo ci) {
      if (PanicModule.isFlag()) return;
      ModuleDispatcher d = UnsafeAccess.getModuleDispatcher();
      if (d == null) return;
      try { d.update4(); } catch (Throwable ignored) {}
   }
}
