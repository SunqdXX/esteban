package club.sunqd.esteban.hud.mixin;

import club.sunqd.esteban.hud.world.Lowered;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class FireMixin {

    @Inject(method = "renderFire", at = @At("HEAD"))
    private static void esteban$lowerFire(PoseStack pose, MultiBufferSource buffers, CallbackInfo ci) {
        pose.pushPose();
        pose.translate(0f, -Lowered.fire(), 0f);
    }

    @Inject(method = "renderFire", at = @At("RETURN"))
    private static void esteban$restoreFire(PoseStack pose, MultiBufferSource buffers, CallbackInfo ci) {
        pose.popPose();
    }
}
