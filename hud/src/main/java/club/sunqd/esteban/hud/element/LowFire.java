package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import java.util.Locale;

public final class LowFire extends Element {

    public static final float STEP = 0.1f;
    private static final int ICON = 11;
    private static final int[][] FLAME = {
            {5, 0, 6}, {4, 1, 7}, {4, 2, 8}, {3, 3, 8}, {2, 4, 9}, {2, 5, 9},
            {1, 6, 10}, {1, 7, 10}, {1, 8, 10}, {2, 9, 9}, {3, 10, 8}
    };

    public LowFire() {
        super("lowfire", "Low fire", 0, 0);
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
        for (int[] row : FLAME)
            c.fill(row[0], row[1], row[2], row[1] + 1, Palette.RED);
        c.fill(4, 7, 7, 10, Palette.SURFACE);
    }
}
