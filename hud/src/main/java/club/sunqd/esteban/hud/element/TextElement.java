package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import net.minecraft.client.Minecraft;

public abstract class TextElement extends Element {

    private static final int PAD = 3;

    private String text = "";

    protected TextElement(String id, String name, int x, int y) {
        super(id, name, x, y);
    }

    protected abstract String text(Minecraft mc);

    public String current() {
        return text;
    }

    @Override
    public void update(Minecraft mc) {
        text = text(mc);
    }

    @Override
    public int width(Canvas c) {
        return c.width(text) + PAD * 2;
    }

    @Override
    public int height(Canvas c) {
        return c.fontHeight() + PAD * 2 - 1;
    }

    @Override
    protected void draw(Canvas c) {
        if (background)
            c.fill(0, 0, width(c), height(c), Palette.BACKGROUND);
        c.text(text, PAD, PAD, color, false);
    }
}
