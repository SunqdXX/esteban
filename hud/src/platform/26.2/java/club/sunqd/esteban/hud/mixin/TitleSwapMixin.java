package club.sunqd.esteban.hud.mixin;

import club.sunqd.esteban.hud.title.Titles;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class TitleSwapMixin {

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen esteban$title(Screen screen) {
        return Titles.swap(screen);
    }
}
