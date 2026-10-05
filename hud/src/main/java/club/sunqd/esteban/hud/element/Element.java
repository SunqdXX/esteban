package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import net.minecraft.client.Minecraft;

public abstract class Element {

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

    public abstract int width(Canvas c);

    public abstract int height(Canvas c);

    protected abstract void draw(Canvas c);

    public int left(Canvas c, int screenWidth) {
        return Math.max(0, Math.min(x, screenWidth - Math.round(width(c) * scale)));
    }

    public int top(Canvas c, int screenHeight) {
        return Math.max(0, Math.min(y, screenHeight - Math.round(height(c) * scale)));
    }

    public final void render(Canvas c, int screenWidth, int screenHeight) {
        c.push(left(c, screenWidth), top(c, screenHeight), scale);
        draw(c);
        c.pop();
    }
}
