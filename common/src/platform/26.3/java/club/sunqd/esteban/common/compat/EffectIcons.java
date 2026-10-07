package club.sunqd.esteban.common.compat;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public final class EffectIcons {

    private EffectIcons() { }

    public static void draw(GuiGraphicsExtractor g, Holder<MobEffect> effect, int x, int y, int size) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.getMobEffectSprite(effect), x, y, size, size);
    }
}
