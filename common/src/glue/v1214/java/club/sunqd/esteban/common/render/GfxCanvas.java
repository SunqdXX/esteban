package club.sunqd.esteban.common.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

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
}
