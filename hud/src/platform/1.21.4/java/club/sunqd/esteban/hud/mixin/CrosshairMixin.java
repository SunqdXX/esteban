package club.sunqd.esteban.hud.mixin;

import club.sunqd.esteban.common.render.GfxCanvas;
import club.sunqd.esteban.hud.EstebanHud;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(Gui.class)
public abstract class CrosshairMixin {

    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private boolean esteban$crosshair(GuiGraphics g, Function<ResourceLocation, RenderType> type, ResourceLocation sprite, int x, int y, int w, int h) {
        final EstebanHud mod = EstebanHud.get();
        if (mod == null || !mod.hud().crosshair().enabled)
            return true;
        return !mod.hud().replaceCrosshair(new GfxCanvas(g, Minecraft.getInstance().font), g.guiWidth(), g.guiHeight());
    }
}
