package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;

import com.google.gson.JsonObject;

public final class Crosshair extends Element {

    public static final int SIZE = 15;
    private static final int MID = SIZE / 2;
    private static final int SHADE = 0x90000000;

    public enum Shape {
        CROSS("cross"),
        GAP("gap"),
        DOT("dot"),
        CIRCLE("circle"),
        T("t"),
        X("x");

        private final String label;
        private boolean[][] mask;
        private boolean[][] shade;

        Shape(String label) {
            this.label = label;
        }

        boolean[][] mask() {
            if (mask == null) {
                final boolean[][] m = new boolean[SIZE + 2][SIZE + 2];
                for (int y = 0; y < SIZE; y++)
                    for (int x = 0; x < SIZE; x++)
                        m[y + 1][x + 1] = on(x - MID, y - MID);
                final boolean[][] r = new boolean[SIZE + 2][SIZE + 2];
                for (int y = 0; y < SIZE + 2; y++)
                    for (int x = 0; x < SIZE + 2; x++)
                        r[y][x] = !m[y][x] && near(m, x, y);
                shade = r;
                mask = m;
            }
            return mask;
        }

        boolean[][] shade() {
            mask();
            return shade;
        }

        public String label() {
            return label;
        }

        private boolean on(int dx, int dy) {
            final int ax = Math.abs(dx);
            final int ay = Math.abs(dy);
            final boolean center = dx == 0 && dy == 0;
            final boolean arm = (dy == 0 && ax >= 3 && ax <= 6) || (dx == 0 && ay >= 3 && ay <= 6);
            return switch (this) {
                case CROSS -> dx == 0 || dy == 0;
                case GAP -> center || arm;
                case DOT -> ax <= 1 && ay <= 1;
                case CIRCLE -> center || Math.abs(Math.hypot(dx, dy) - 5) < 0.5;
                case T -> center || (arm && !(dx == 0 && dy < 0));
                case X -> center || (ax == ay && ax >= 2 && ax <= 5);
            };
        }

        static Shape of(String label) {
            for (Shape s : values())
                if (s.label.equals(label))
                    return s;
            return CROSS;
        }
    }

    public Shape shape = Shape.CROSS;

    public Crosshair() {
        super("crosshair", "Crosshair", CENTER, CENTER);
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
    public String option() {
        return "shape " + shape.label;
    }

    @Override
    public void cycle() {
        final Shape[] all = Shape.values();
        shape = all[(shape.ordinal() + 1) % all.length];
    }

    @Override
    public void read(JsonObject o) {
        if (o.has("shape"))
            shape = Shape.of(o.get("shape").getAsString());
    }

    @Override
    public void write(JsonObject o) {
        o.addProperty("shape", shape.label);
    }

    @Override
    public int width(Canvas c) {
        return SIZE;
    }

    @Override
    public int height(Canvas c) {
        return SIZE;
    }

    @Override
    protected void draw(Canvas c) {
        final boolean[][] shade = shape.shade();
        final boolean[][] mask = shape.mask();
        for (int y = 0; y < SIZE + 2; y++)
            runs(c, y, shade[y], SHADE);
        for (int y = 0; y < SIZE + 2; y++)
            runs(c, y, mask[y], color);
    }

    private static boolean near(boolean[][] m, int x, int y) {
        for (int j = Math.max(0, y - 1); j <= Math.min(SIZE + 1, y + 1); j++)
            for (int i = Math.max(0, x - 1); i <= Math.min(SIZE + 1, x + 1); i++)
                if (m[j][i])
                    return true;
        return false;
    }

    private static void runs(Canvas c, int y, boolean[] filled, int color) {
        int x = 0;
        while (x < SIZE + 2) {
            if (!filled[x]) {
                x++;
                continue;
            }
            final int start = x;
            while (x < SIZE + 2 && filled[x])
                x++;
            c.fill(start - 1, y - 1, x - 1, y, color);
        }
    }
}
