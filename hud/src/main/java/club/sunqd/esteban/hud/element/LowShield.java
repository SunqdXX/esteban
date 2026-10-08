package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import java.util.Locale;

public final class LowShield extends Element {

    public static final float STEP = 0.1f;
    private static final int ICON = 11;
    private static final int[][] SHIELD = {
            {1, 0, 10}, {1, 1, 10}, {1, 2, 10}, {1, 3, 10}, {1, 4, 10}, {1, 5, 10},
            {2, 6, 9}, {2, 7, 9}, {3, 8, 8}, {4, 9, 7}, {5, 10, 6}
    };

    public LowShield() {
        super("lowshield", "Low shield", 0, 0);
        enabled = false;
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
    public boolean colored() {
        return false;
    }

    @Override
    public String option() {
        return String.format(Locale.ROOT, "%.1fx lower", scale);
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
        for (int[] row : SHIELD)
            c.fill(row[0], row[1], row[2], row[1] + 1, Palette.TEXT[2]);
        c.fill(5, 1, 6, 9, Palette.SURFACE);
        c.fill(2, 4, 9, 5, Palette.SURFACE);
    }
}
