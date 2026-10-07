package club.sunqd.esteban.hud.editor;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.common.screen.Panel;
import club.sunqd.esteban.hud.Hud;
import club.sunqd.esteban.hud.HudConfig;
import club.sunqd.esteban.hud.Palette;
import club.sunqd.esteban.hud.element.Element;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HudEditor implements Panel {

    private static final int SNAP = 4;
    private static final int GRAB = 3;
    private static final int HANDLE = 4;
    private static final String[] HELP = {"Drag to move   Drag a corner to resize", "Right click on/off   Middle click color   Scroll resizes too   Esc saves"};

    private final Hud hud;
    private final HudConfig config;
    private final Map<Element, int[]> boxes = new LinkedHashMap<>();
    private final Map<Element, int[]> bases = new LinkedHashMap<>();

    private Element dragging;
    private Element hovered;
    private int grabX;
    private int grabY;
    private Element resizing;
    private int corner;
    private int anchorX;
    private int anchorY;

    public HudEditor(Hud hud, HudConfig config) {
        this.hud = hud;
        this.config = config;
    }

    public Element hovered() {
        return hovered;
    }

    public int[] boxOf(Element e) {
        final int[] b = boxes.get(e);
        return b == null ? null : b.clone();
    }

    private static boolean shown(Element e) {
        return e.enabled && e.visible();
    }

    private static final String OFF = "  off";

    private static String placeholder(Element e) {
        return e.enabled ? e.name() : e.name() + OFF;
    }

    private int[] base(Canvas c, Element e) {
        if (shown(e))
            return new int[] {Math.max(1, e.width(c)), Math.max(1, e.height(c))};
        return new int[] {c.width(placeholder(e)) + 8, c.fontHeight() + 5};
    }

    private int[] box(Canvas c, Element e, int sw, int sh) {
        final int[] base = base(c, e);
        final int w = Math.round(base[0] * e.scale);
        final int h = Math.round(base[1] * e.scale);
        return new int[] {Element.place(e.x, w, sw), Element.place(e.y, h, sh), w, h};
    }

    private static int[] cornerPoint(int[] b, int corner) {
        return new int[] {corner % 2 == 0 ? b[0] : b[0] + b[2], corner < 2 ? b[1] : b[1] + b[3]};
    }

    private Object[] cornerAt(int x, int y) {
        Object[] found = null;
        for (Map.Entry<Element, int[]> entry : boxes.entrySet()) {
            for (int k = 0; k < 4; k++) {
                final int[] p = cornerPoint(entry.getValue(), k);
                if (Math.abs(x - p[0]) <= GRAB && Math.abs(y - p[1]) <= GRAB)
                    found = new Object[] {entry.getKey(), k};
            }
        }
        return found;
    }

    private void resize(int mouseX, int mouseY, int sw, int sh) {
        final int[] base = bases.get(resizing);
        if (base == null)
            return;
        final float dx = Math.abs(mouseX - anchorX);
        final float dy = Math.abs(mouseY - anchorY);
        float s = Math.max(dx / base[0], dy / base[1]);
        s = Math.round(s * 20f) / 20f;
        s = Math.max(0.5f, Math.min(3f, s));
        resizing.scale = s;
        final int w = Math.round(base[0] * s);
        final int h = Math.round(base[1] * s);
        final int x = corner % 2 == 0 ? anchorX - w : anchorX;
        final int y = corner < 2 ? anchorY - h : anchorY;
        resizing.x = clamp(x, sw - w);
        resizing.y = clamp(y, sh - h);
    }

    private static int clamp(int v, int max) {
        return Math.max(0, Math.min(v, Math.max(0, max)));
    }

    private Element at(int x, int y) {
        Element found = null;
        for (Map.Entry<Element, int[]> entry : boxes.entrySet()) {
            final int[] b = entry.getValue();
            if (x >= b[0] && x < b[0] + b[2] && y >= b[1] && y < b[1] + b[3])
                found = entry.getKey();
        }
        return found;
    }

    @Override
    public void render(Canvas c, int mouseX, int mouseY, int sw, int sh) {
        final Minecraft mc = Minecraft.getInstance();
        final List<Element> elements = hud.elements();
        for (Element e : elements)
            if (e.enabled)
                e.update(mc);

        if (dragging != null) {
            final int[] b = box(c, dragging, sw, sh);
            int nx = clamp(mouseX - grabX, sw - b[2]);
            int ny = clamp(mouseY - grabY, sh - b[3]);
            if (nx < SNAP) nx = 0;
            if (sw - b[2] - nx < SNAP) nx = sw - b[2];
            if (ny < SNAP) ny = 0;
            if (sh - b[3] - ny < SNAP) ny = sh - b[3];
            dragging.x = nx;
            dragging.y = ny;
        }

        if (resizing != null)
            resize(mouseX, mouseY, sw, sh);

        boxes.clear();
        bases.clear();
        for (Element e : elements) {
            bases.put(e, base(c, e));
            boxes.put(e, box(c, e, sw, sh));
        }
        final Object[] grab = resizing == null && dragging == null ? cornerAt(mouseX, mouseY) : null;
        hovered = resizing != null ? resizing : dragging != null ? dragging : grab != null ? (Element) grab[0] : at(mouseX, mouseY);
        final int activeCorner = resizing != null ? corner : grab != null ? (int) grab[1] : -1;

        help(c, sw, sh);
        for (Element e : elements) {
            final int[] b = boxes.get(e);
            if (shown(e)) {
                e.render(c, sw, sh);
            } else {
                c.fill(b[0], b[1], b[0] + b[2], b[1] + b[3], Palette.BACKGROUND);
                c.push(b[0], b[1], e.scale);
                c.text(e.name(), 4, 3, Palette.DIM, false);
                if (!e.enabled)
                    c.text(OFF, 4 + c.width(e.name()), 3, Palette.RED, false);
                c.pop();
            }
            outline(c, b, e == hovered ? Palette.VENOM : e.enabled ? Palette.DIM : Palette.MUTED);
            if (e == hovered) {
                for (int k = 0; k < 4; k++) {
                    final int[] p = cornerPoint(b, k);
                    final int r = k == activeCorner ? HANDLE : HANDLE / 2;
                    c.fill(p[0] - r, p[1] - r, p[0] + r, p[1] + r, k == activeCorner ? Palette.VENOM : Palette.DIM);
                }
            }
        }

        if (hovered != null) {
            final String tip = hovered.name() + (hovered.enabled ? "" : " (off)") + "  " + Math.round(hovered.scale * 100) + "%";
            final int tw = c.width(tip) + 8;
            final int tx = Math.min(mouseX + 10, sw - tw);
            final int ty = Math.max(0, mouseY - 14);
            c.fill(tx, ty, tx + tw, ty + c.fontHeight() + 3, Palette.INK);
            c.text(tip, tx + 4, ty + 2, Palette.VENOM, false);
        }
    }

    private static void help(Canvas c, int sw, int sh) {
        int text = 0;
        for (String line : HELP)
            text = Math.max(text, c.width(line));
        final int line = c.fontHeight() + 2;
        final int w = text + 16;
        final int h = line * HELP.length + 8;
        final float scale = Math.min(1f, (sw - 8) / (float) w);
        final float x = (sw - w * scale) / 2f;
        final float y = (sh - h * scale) / 2f;
        c.push(x, y, scale);
        c.fill(0, 0, w, h, Palette.BACKGROUND);
        for (int i = 0; i < HELP.length; i++)
            c.text(HELP[i], (w - c.width(HELP[i])) / 2, 5 + i * line, Palette.DIM, false);
        c.pop();
    }

    private static void outline(Canvas c, int[] b, int color) {
        final int x0 = b[0] - 1;
        final int y0 = b[1] - 1;
        final int x1 = b[0] + b[2] + 1;
        final int y1 = b[1] + b[3] + 1;
        c.fill(x0, y0, x1, y0 + 1, color);
        c.fill(x0, y1 - 1, x1, y1, color);
        c.fill(x0, y0, x0 + 1, y1, color);
        c.fill(x1 - 1, y0, x1, y1, color);
    }

    @Override
    public boolean click(int x, int y, int button) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            final Object[] grab = cornerAt(x, y);
            if (grab != null) {
                resizing = (Element) grab[0];
                corner = (int) grab[1];
                final int[] anchor = cornerPoint(boxes.get(resizing), 3 - corner);
                anchorX = anchor[0];
                anchorY = anchor[1];
                return true;
            }
        }
        final Element e = at(x, y);
        if (e == null)
            return false;
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            final int[] b = boxes.get(e);
            dragging = e;
            grabX = x - b[0];
            grabY = y - b[1];
        } else if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
            e.enabled = !e.enabled;
            if (!e.enabled)
                e.inactive(Minecraft.getInstance());
        } else if (button == InputConstants.MOUSE_BUTTON_MIDDLE) {
            int next = 0;
            for (int i = 0; i < Palette.TEXT.length; i++)
                if (Palette.TEXT[i] == e.color)
                    next = (i + 1) % Palette.TEXT.length;
            e.color = Palette.TEXT[next];
        }
        return true;
    }

    @Override
    public void release() {
        dragging = null;
        resizing = null;
    }

    @Override
    public boolean key(int key, Runnable close) {
        if (key == InputConstants.KEY_RSHIFT) {
            close.run();
            return true;
        }
        return false;
    }

    @Override
    public boolean scroll(int x, int y, double amount) {
        final Element e = at(x, y);
        if (e == null || amount == 0)
            return false;
        final float next = Math.round((e.scale + (amount > 0 ? 0.1f : -0.1f)) * 20f) / 20f;
        e.scale = Math.max(0.5f, Math.min(3f, next));
        return true;
    }

    @Override
    public void closed() {
        dragging = null;
        resizing = null;
        config.save(hud);
        hud.editing(false);
    }
}
