package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;
import club.sunqd.esteban.hud.input.Clicks;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public final class Keystrokes extends Element {

    private static final int KEY = 22;
    private static final int GAP = 2;
    private static final int WIDE = KEY * 3 + GAP * 2;
    private static final int MOUSE = (WIDE - GAP) / 2;
    private static final int BAR = 10;
    private static final float SMALL = 0.75f;

    private final String[] labels = {"W", "A", "S", "D"};
    private final boolean[] down = new boolean[7];
    private int leftCps;
    private int rightCps;

    public Keystrokes(int x, int y) {
        super("keystrokes", "Keystrokes", x, y);
    }

    @Override
    public void update(Minecraft mc) {
        final Options o = mc.options;
        final KeyMapping[] keys = {o.keyUp, o.keyLeft, o.keyDown, o.keyRight, o.keyAttack, o.keyUse, o.keyJump};
        for (int i = 0; i < keys.length; i++)
            down[i] = keys[i].isDown();
        for (int i = 0; i < labels.length; i++)
            labels[i] = keys[i].getTranslatedKeyMessage().getString();
        leftCps = Clicks.LEFT.perSecond();
        rightCps = Clicks.RIGHT.perSecond();
    }

    public String pressed() {
        final String[] names = {labels[0], labels[1], labels[2], labels[3], "LMB", "RMB", "Space"};
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < down.length; i++)
            if (down[i])
                b.append(b.isEmpty() ? "" : " ").append(names[i]);
        return b.toString();
    }

    public int leftCps() {
        return leftCps;
    }

    public int rightCps() {
        return rightCps;
    }

    @Override
    public int width(Canvas c) {
        return WIDE;
    }

    @Override
    public int height(Canvas c) {
        return (KEY + GAP) * 3 + BAR;
    }

    @Override
    protected void draw(Canvas c) {
        final int row = KEY + GAP;
        key(c, row, 0, KEY, KEY, down[0], labels[0], null);
        key(c, 0, row, KEY, KEY, down[1], labels[1], null);
        key(c, row, row, KEY, KEY, down[2], labels[2], null);
        key(c, row * 2, row, KEY, KEY, down[3], labels[3], null);
        key(c, 0, row * 2, MOUSE, KEY, down[4], "LMB", leftCps + " CPS");
        key(c, MOUSE + GAP, row * 2, MOUSE, KEY, down[5], "RMB", rightCps + " CPS");
        box(c, 0, row * 3, WIDE, BAR, down[6]);
        final int line = WIDE / 3;
        c.fill((WIDE - line) / 2, row * 3 + BAR / 2, (WIDE + line) / 2, row * 3 + BAR / 2 + 1, ink(down[6]));
    }

    private void key(Canvas c, int left, int top, int w, int h, boolean on, String label, String sub) {
        box(c, left, top, w, h, on);
        final int ink = ink(on);
        final int lh = c.fontHeight() - 1;
        if (sub == null) {
            label(c, label, left, top + (h - lh) / 2, w, 1f, ink);
            return;
        }
        final int small = Math.round(lh * SMALL);
        final int y = top + (h - lh - small - 2) / 2;
        label(c, label, left, y, w, 1f, ink);
        label(c, sub, left, y + lh + 2, w, SMALL, ink);
    }

    private void label(Canvas c, String s, int left, int top, int w, float size, int ink) {
        final int tw = c.width(s);
        final float scale = Math.min(size, (w - 4) / (float) Math.max(1, tw));
        c.push(left + (w - tw * scale) / 2f, top, scale);
        c.text(s, 0, 0, ink, false);
        c.pop();
    }

    private void box(Canvas c, int left, int top, int w, int h, boolean on) {
        if (on)
            c.fill(left, top, left + w, top + h, (color & 0xFFFFFF) | 0xD0000000);
        else if (background)
            c.fill(left, top, left + w, top + h, Palette.BACKGROUND);
    }

    private int ink(boolean on) {
        return on ? Palette.INK : color;
    }
}
