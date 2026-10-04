package club.sunqd.esteban.mixin;

import club.sunqd.esteban.render.Hud;

import net.minecraft.client.MouseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"))
    private void esteban$beforeCamera(CallbackInfo ci) {
        Hud.beforeCamera();
    }
}
