package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;
import club.sunqd.esteban.hud.world.Outlines;

import java.util.Locale;

public final class BlockOutline extends Element {

    private static final int ICON = 11;

    public BlockOutline() {
        super("outline", "Block outline", 0, 0);
        enabled = false;
        color = Palette.VENOM;
    }

    @Override
    public boolean movable() {
        return false;
    }

    @Override
    public boolean onHud() {
        return false;
    }

    @Override
    public boolean world() {
        return true;
    }

    @Override
    public boolean available() {
        return Outlines.supported();
    }

    @Override
    public String option() {
        return String.format(Locale.ROOT, "%.1fx thick", scale);
    }

    @Override
    public int width(Canvas c) {
        return ICON;
    }

    @Override
    public int height(Canvas c) {
        return ICON;
    }

    @Override
    protected void draw(Canvas c) {
        final int s = 7;
        final int d = ICON - 1 - s;
        square(c, d, 0, s);
        square(c, 0, d, s);
        for (int i = 1; i < d; i++) {
            c.fill(i, d - i, i + 1, d - i + 1, color);
            c.fill(s + i, d - i, s + i + 1, d - i + 1, color);
            c.fill(i, ICON - 1 - i, i + 1, ICON - i, color);
            c.fill(s + i, ICON - 1 - i, s + i + 1, ICON - i, color);
        }
    }

    private void square(Canvas c, int x, int y, int s) {
        c.fill(x, y, x + s + 1, y + 1, color);
        c.fill(x, y + s, x + s + 1, y + s + 1, color);
        c.fill(x, y, x + 1, y + s + 1, color);
        c.fill(x + s, y, x + s + 1, y + s + 1, color);
    }
}
