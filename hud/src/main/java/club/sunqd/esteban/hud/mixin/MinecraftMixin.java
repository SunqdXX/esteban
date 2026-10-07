package club.sunqd.esteban.hud.mixin;

import club.sunqd.esteban.hud.input.Clicks;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void esteban$countUseClicks(CallbackInfo ci) {
        final int clicks = ((KeyMappingAccess) ((Minecraft) (Object) this).options.keyUse).esteban$clickCount();
        if (clicks > 0)
            Clicks.RIGHT.add(clicks);
    }
}
