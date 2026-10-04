package club.sunqd.esteban.mixin;

import club.sunqd.esteban.modules.combat.Hitboxes;

import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererPickMixin {

    @Inject(method = "pick(F)V", at = @At("RETURN"))
    private void esteban$pick(float partial, CallbackInfo ci) {
        Hitboxes.afterPick(partial);
    }
}
