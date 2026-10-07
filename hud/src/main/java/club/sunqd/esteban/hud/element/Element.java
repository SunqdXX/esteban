package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;

public abstract class Element {

    public static final int CENTER = Integer.MIN_VALUE;

    private final String id;
    private final String name;

    public boolean enabled = true;
    public int x;
    public int y;
    public float scale = 1f;
    public int color = Palette.TEXT[0];
    public boolean background = true;

    protected Element(String id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void update(Minecraft mc) { }

    public void inactive(Minecraft mc) { }

    public void read(JsonObject o) { }

    public void write(JsonObject o) { }

    public boolean visible() {
        return true;
    }

    public abstract int width(Canvas c);

    public abstract int height(Canvas c);

    protected abstract void draw(Canvas c);

    public static int place(int v, int size, int screen) {
        if (v == CENTER)
            return Math.max(0, (screen - size) / 2);
        return Math.max(0, Math.min(v, Math.max(0, screen - size)));
    }

    public int left(Canvas c, int screenWidth) {
        return place(x, Math.round(width(c) * scale), screenWidth);
    }

    public int top(Canvas c, int screenHeight) {
        return place(y, Math.round(height(c) * scale), screenHeight);
    }

    public final void render(Canvas c, int screenWidth, int screenHeight) {
        c.push(left(c, screenWidth), top(c, screenHeight), scale);
        draw(c);
        c.pop();
    }
}
