package club.sunqd.esteban.common.render;

import club.sunqd.esteban.common.compat.EffectIcons;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

public record GfxCanvas(GuiGraphics g, Font font) implements Canvas {

    @Override
    public void fill(int left, int top, int right, int bottom, int color) {
        g.fill(left, top, right, bottom, color);
    }

    @Override
    public void text(String s, int x, int y, int color) {
        g.drawString(font, s, x, y, color);
    }

    @Override
    public void text(String s, int x, int y, int color, boolean shadow) {
        g.drawString(font, s, x, y, color, shadow);
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
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1);
    }

    @Override
    public void pop() {
        g.pose().popPose();
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
        g.renderItem(stack, x, y);
    }

    @Override
    public void itemDecorations(ItemStack stack, int x, int y) {
        g.renderItemDecorations(font, stack, x, y);
    }

    @Override
    public void effectIcon(Holder<MobEffect> effect, int x, int y, int size) {
        EffectIcons.draw(g, effect, x, y, size);
    }

    @Override
    public void image(String texture, int textureWidth, int textureHeight, float x, float y, float width, float height, int color) {
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(width / textureWidth, height / textureHeight, 1);
        g.blit(RenderType::guiTextured, ResourceLocation.parse(texture), 0, 0, 0f, 0f,
                textureWidth, textureHeight, textureWidth, textureHeight, textureWidth, textureHeight, color);
        g.pose().popPose();
    }

    private static Component styled(String s, String font) {
        return Component.literal(s).withStyle(style -> style.withFont(ResourceLocation.parse(font)));
    }

    @Override
    public void text(String s, int x, int y, int color, String font) {
        g.drawString(this.font, styled(s, font), x, y, color, false);
    }

    @Override
    public int width(String s, String font) {
        return this.font.width(styled(s, font));
    }
}
