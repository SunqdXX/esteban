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
    private static final int BELOW_CENTER = 30;
    private static final int CHIP_GAP = 6;
    private static final String OFF = "  off";
    private static final String UNAVAILABLE = "  not available on this version";
    private static final String[] HELP = {
            "Drag to move   Drag a corner to resize",
            "Right click on/off   Middle click color   Scroll resizes too",
            "Click the crosshair to change its shape   Esc saves"
    };

    private final Hud hud;
    private final HudConfig config;
    private final Map<Element, int[]> boxes = new LinkedHashMap<>();
    private final Map<Element, int[]> bases = new LinkedHashMap<>();
    private final Map<Element, int[]> chips = new LinkedHashMap<>();

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
        final int[] b = e.world() ? chips.get(e) : boxes.get(e);
        return b == null ? null : b.clone();
    }

    private static boolean shown(Element e) {
        return e.enabled && e.visible();
    }

    private static String placeholder(Element e) {
        return e.enabled ? e.name() : e.name() + OFF;
    }

    private int[] base(Canvas c, Element e) {
        if (shown(e))
            return new int[] {Math.max(1, e.width(c)), Math.max(1, e.height(c))};
        return new int[] {c.width(placeholder(e)) + 8, c.fontHeight() + 5};
    }

    private static float size(Element e) {
        return shown(e) || e.movable() ? e.scale : 1f;
    }

    private int[] box(Canvas c, Element e, int sw, int sh) {
        final int[] base = base(c, e);
        final int w = Math.round(base[0] * size(e));
        final int h = Math.round(base[1] * size(e));
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
        final boolean centered = !resizing.movable();
        final float spread = centered ? 2f : 1f;
        final float dx = Math.abs(mouseX - anchorX) * spread;
        final float dy = Math.abs(mouseY - anchorY) * spread;
        float s = Math.max(dx / base[0], dy / base[1]);
        s = Math.round(s * 20f) / 20f;
        s = Math.max(0.5f, Math.min(3f, s));
        resizing.scale = s;
        if (centered)
            return;
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

    private static Element hit(Map<Element, int[]> map, int x, int y) {
        Element found = null;
        for (Map.Entry<Element, int[]> entry : map.entrySet()) {
            final int[] b = entry.getValue();
            if (x >= b[0] && x < b[0] + b[2] && y >= b[1] && y < b[1] + b[3])
                found = entry.getKey();
        }
        return found;
    }

    private Element at(int x, int y) {
        final Element chip = hit(chips, x, y);
        return chip != null ? chip : hit(boxes, x, y);
    }

    @Override
    public void render(Canvas c, int mouseX, int mouseY, int sw, int sh) {
        final Minecraft mc = Minecraft.getInstance();
        final List<Element> elements = hud.elements();
        for (Element e : elements)
            if (e.enabled && e.onHud())
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
            if (e.world())
                continue;
            bases.put(e, base(c, e));
            boxes.put(e, box(c, e, sw, sh));
        }
        final Object[] grab = resizing == null && dragging == null ? cornerAt(mouseX, mouseY) : null;
        final Element chipHover = resizing == null && dragging == null ? hit(chips, mouseX, mouseY) : null;
        hovered = resizing != null ? resizing : dragging != null ? dragging : chipHover != null ? chipHover
                : grab != null ? (Element) grab[0] : hit(boxes, mouseX, mouseY);
        final int activeCorner = resizing != null ? corner : grab != null && hovered == grab[0] ? (int) grab[1] : -1;

        panel(c, sw, sh);
        for (Element e : elements) {
            final int[] b = boxes.get(e);
            if (b == null)
                continue;
            if (shown(e)) {
                e.render(c, sw, sh);
            } else {
                c.fill(b[0], b[1], b[0] + b[2], b[1] + b[3], Palette.BACKGROUND);
                c.push(b[0], b[1], size(e));
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

        if (hovered != null)
            tooltip(c, hovered, mouseX, mouseY, sw);
    }

    private static String tip(Element e) {
        if (!e.available())
            return e.name() + UNAVAILABLE;
        final StringBuilder b = new StringBuilder(e.name());
        if (!e.enabled)
            b.append(" (off)");
        b.append("  ");
        b.append(e.world() && e.option() != null ? e.option() : Math.round(e.scale * 100) + "%");
        if (!e.world() && e.option() != null)
            b.append("  ").append(e.option());
        return b.toString();
    }

    private static void tooltip(Canvas c, Element e, int mouseX, int mouseY, int sw) {
        final String tip = tip(e);
        final int tw = c.width(tip) + 8;
        final int tx = Math.max(0, Math.min(mouseX + 10, sw - tw));
        final int ty = Math.max(0, mouseY - 14);
        c.fill(tx, ty, tx + tw, ty + c.fontHeight() + 3, Palette.INK);
        c.text(tip, tx + 4, ty + 2, Palette.VENOM, false);
    }

    private static String status(Element e) {
        if (!e.available())
            return UNAVAILABLE;
        if (!e.enabled)
            return OFF;
        return e.option() == null ? "" : "  " + e.option();
    }

    private int chipWidth(Canvas c, Element e) {
        return 4 + e.width(c) + 4 + c.width(e.name() + status(e)) + 4;
    }

    private void panel(Canvas c, int sw, int sh) {
        final List<Element> world = hud.elements().stream().filter(Element::world).toList();
        final int line = c.fontHeight() + 2;
        final int chipH = c.fontHeight() + 7;
        int text = 0;
        for (String l : HELP)
            text = Math.max(text, c.width(l));
        int row = 0;
        for (Element e : world)
            row += (row == 0 ? 0 : CHIP_GAP) + chipWidth(c, e);
        final int w = Math.max(text, row) + 16;
        final int h = line * HELP.length + 8 + (world.isEmpty() ? 0 : chipH + 6);
        final float scale = Math.min(1f, (sw - 8) / (float) w);
        final float x = (sw - w * scale) / 2f;
        final float y = Math.max(0f, Math.min(sh / 2f + BELOW_CENTER, sh - h * scale - 2));

        chips.clear();
        c.push(x, y, scale);
        c.fill(0, 0, w, h, Palette.BACKGROUND);
        for (int i = 0; i < HELP.length; i++)
            c.text(HELP[i], (w - c.width(HELP[i])) / 2, 5 + i * line, Palette.DIM, false);
        int cx = (w - row) / 2;
        final int cy = 5 + HELP.length * line + 2;
        for (Element e : world) {
            final int cw = chipWidth(c, e);
            c.fill(cx, cy, cx + cw, cy + chipH, Palette.SURFACE);
            outline(c, new int[] {cx, cy, cw, chipH}, e == hovered ? Palette.VENOM : e.enabled && e.available() ? Palette.DIM : Palette.MUTED);
            final int icon = e.height(c);
            e.renderAt(c, cx + 4, cy + (chipH - icon) / 2f, 1f);
            final int tx = cx + 4 + e.width(c) + 4;
            final int ty = cy + (chipH - c.fontHeight()) / 2 + 1;
            c.text(e.name(), tx, ty, e.available() ? Palette.DIM : Palette.MUTED, false);
            c.text(status(e), tx + c.width(e.name()), ty, !e.available() ? Palette.MUTED : !e.enabled ? Palette.RED : e.colored() ? e.color : Palette.VENOM, false);
            chips.put(e, new int[] {Math.round(x + cx * scale), Math.round(y + cy * scale), Math.round(cw * scale), Math.round(chipH * scale)});
            cx += cw + CHIP_GAP;
        }
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
        if (button == InputConstants.MOUSE_BUTTON_LEFT && hit(chips, x, y) == null) {
            final Object[] grab = cornerAt(x, y);
            if (grab != null) {
                resizing = (Element) grab[0];
                corner = (int) grab[1];
                final int[] b = boxes.get(resizing);
                final int[] anchor = resizing.movable() ? cornerPoint(b, 3 - corner) : new int[] {b[0] + b[2] / 2, b[1] + b[3] / 2};
                anchorX = anchor[0];
                anchorY = anchor[1];
                return true;
            }
        }
        final Element e = at(x, y);
        if (e == null)
            return false;
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            if (!e.movable()) {
                if (e.available())
                    e.cycle();
                return true;
            }
            final int[] b = boxes.get(e);
            dragging = e;
            grabX = x - b[0];
            grabY = y - b[1];
        } else if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
            if (!e.available())
                return true;
            e.enabled = !e.enabled;
            if (!e.enabled)
                e.inactive(Minecraft.getInstance());
        } else if (button == InputConstants.MOUSE_BUTTON_MIDDLE && e.colored()) {
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
