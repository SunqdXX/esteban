package club.sunqd.esteban.common.render;

import club.sunqd.esteban.common.compat.EffectIcons;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

public record GfxCanvas(GuiGraphicsExtractor g, Font font) implements Canvas {

    @Override
    public void fill(int left, int top, int right, int bottom, int color) {
        g.fill(left, top, right, bottom, color);
    }

    @Override
    public void text(String s, int x, int y, int color) {
        g.text(font, s, x, y, color);
    }

    @Override
    public void text(String s, int x, int y, int color, boolean shadow) {
        g.text(font, s, x, y, color, shadow);
    }

    @Override
    public int width(String s) {
        return font.width(s);
    }

    @Override
    public int fontHeight() {
        return font.lineHeight;
    }

    @Override
    public void push(float x, float y, float scale) {
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(scale, scale);
    }

    @Override
    public void pop() {
        g.pose().popMatrix();
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
        g.item(stack, x, y);
    }

    @Override
    public void itemDecorations(ItemStack stack, int x, int y) {
        g.itemDecorations(font, stack, x, y);
    }

    @Override
    public void effectIcon(Holder<MobEffect> effect, int x, int y, int size) {
        EffectIcons.draw(g, effect, x, y, size);
    }

    @Override
    public void image(String texture, int textureWidth, int textureHeight, float x, float y, float width, float height, int color) {
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(width / textureWidth, height / textureHeight);
        g.blit(RenderPipelines.GUI_TEXTURED, Identifier.parse(texture), 0, 0, 0f, 0f,
                textureWidth, textureHeight, textureWidth, textureHeight, textureWidth, textureHeight, color);
        g.pose().popMatrix();
    }

    private static Component styled(String s, String font) {
        return Component.literal(s).withStyle(style -> style.withFont(new FontDescription.Resource(Identifier.parse(font))));
    }

    @Override
    public void text(String s, int x, int y, int color, String font) {
        g.text(this.font, styled(s, font), x, y, color, false);
    }

    @Override
    public int width(String s, String font) {
        return this.font.width(styled(s, font));
    }
}
