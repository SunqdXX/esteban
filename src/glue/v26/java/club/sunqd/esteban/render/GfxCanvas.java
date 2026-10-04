package club.sunqd.esteban.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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
}
