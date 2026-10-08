package club.sunqd.esteban.hud.mixin;

import club.sunqd.esteban.common.render.GfxCanvas;
import club.sunqd.esteban.hud.EstebanHud;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.Hud")
public abstract class CrosshairMixin {

    @WrapWithCondition(method = "extractCrosshair", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private boolean esteban$crosshair(GuiGraphicsExtractor g, RenderPipeline pipeline, Identifier sprite, int x, int y, int w, int h) {
        final EstebanHud mod = EstebanHud.get();
        if (mod == null || !mod.hud().crosshair().enabled)
            return true;
        return !mod.hud().replaceCrosshair(new GfxCanvas(g, Minecraft.getInstance().font), g.guiWidth(), g.guiHeight());
    }
}
