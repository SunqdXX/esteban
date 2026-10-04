package club.sunqd.esteban.mixin;

import club.sunqd.esteban.EstebanClient;
import club.sunqd.esteban.util.KeyHooked;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyboardHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin implements KeyHooked {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void esteban$key(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == InputConstants.PRESS)
            EstebanClient.keyPressed(key);
    }
}
