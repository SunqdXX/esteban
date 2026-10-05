package club.sunqd.esteban.common.mixin;

import club.sunqd.esteban.common.input.KeyHooked;
import club.sunqd.esteban.common.input.Keys;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin implements KeyHooked {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void estebancommon$key(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (action == InputConstants.PRESS)
            Keys.pressed(event.key());
    }
}
