package club.sunqd.esteban.common.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public final class EffectIcons {

    private EffectIcons() { }

    public static void draw(GuiGraphics g, Holder<MobEffect> effect, int x, int y, int size) {
        g.blitSprite(RenderType::guiTextured, Minecraft.getInstance().getMobEffectTextures().get(effect), x, y, size, size);
    }
}
