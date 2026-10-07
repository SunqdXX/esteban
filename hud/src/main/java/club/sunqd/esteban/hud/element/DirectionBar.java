package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import net.minecraft.client.Minecraft;

import java.util.Locale;

public final class DirectionBar extends Element {

    private static final int W = 161;
    private static final int H = 30;
    private static final int SPAN = 80;
    private static final String[] NAMES = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    private int heading;

    public DirectionBar(int x, int y) {
        super("direction", "Direction", x, y);
        enabled = false;
    }

    @Override
    public void update(Minecraft mc) {
        if (mc.player != null)
            heading = Math.floorMod(Math.round(mc.player.getYRot()) + 180, 360);
    }

    public int heading() {
        return heading;
    }

    public String text() {
        return String.format(Locale.ROOT, "%d %s", heading, NAMES[Math.floorMod(Math.round(heading / 45f), 8)]);
    }

    @Override
    public int width(Canvas c) {
        return W;
    }

    @Override
    public int height(Canvas c) {
        return H;
    }

    @Override
    protected void draw(Canvas c) {
        if (background)
            c.fill(0, 0, W, H, Palette.BACKGROUND);
        final int center = W / 2;
        for (int deg = heading - SPAN; deg <= heading + SPAN; deg++) {
            final int d = Math.floorMod(deg, 360);
            if (d % 15 != 0)
                continue;
            final int x = center + deg - heading;
            final boolean major = d % 45 == 0;
            c.fill(x, major ? 12 : 14, x + 1, 17, major ? color : Palette.DIM);
            if (!major)
                continue;
            final String name = NAMES[d / 45];
            final int tw = c.width(name);
            final int lx = x - tw / 2;
            if (lx >= 2 && lx + tw <= W - 2)
                c.text(name, lx, 2, d % 90 == 0 ? color : Palette.DIM, false);
        }
        c.fill(center, 11, center + 1, 18, Palette.VENOM);
        final String deg = heading + "\u00b0";
        c.text(deg, center - c.width(deg) / 2, 20, color, false);
    }
}
