package club.sunqd.esteban.hud.title;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.common.screen.Panel;
import club.sunqd.esteban.hud.Palette;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public final class TitlePanel implements Panel {

    public static final String FONT = "esteban-hud:departure";

    private static final int ROW = 22;
    private static final int PAD = 14;
    private static final int ICON = 20;
    private static final int GAP = 6;
    private static final int TEXT = 11;
    private static final int RAIL = 0xE41A1B26;
    private static final int BORDER = 0xFF3B4261;
    private static final int BACKING = 0xB01A1B26;
    private static final String OPTIONS = "menu.options";
    private static final String QUIT = "menu.quit";
    private static final String LANGUAGE = "options.language";
    private static final String ACCESSIBILITY = "options.accessibility";
    private static final String CREDITS = "title.credits";
    private static final List<String> PRIMARY = List.of("menu.singleplayer", "menu.multiplayer", "menu.online", "menu.playdemo", "menu.resetdemo");
    private static final List<String> PLACED = List.of(OPTIONS, QUIT, LANGUAGE, ACCESSIBILITY, CREDITS);

    private record Hit(int x, int y, int w, int h, Runnable action, String tip, int order, String label) {

        boolean contains(int mx, int my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private final TitleActions actions;
    private final TitleSettings settings;
    private final Runnable save;
    private final List<Hit> hits = new ArrayList<>();
    private int focus = -1;
    private int menuSize;

    public TitlePanel(TitleActions actions, TitleSettings settings, Runnable save) {
        this.actions = actions;
        this.settings = settings;
        this.save = save;
    }

    public TitleActions actions() {
        return actions;
    }

    public int[] box(String label) {
        for (Hit hit : hits)
            if (label.equals(hit.label()))
                return new int[] {hit.x(), hit.y(), hit.w(), hit.h()};
        return null;
    }

    public List<String> menuLabels() {
        final List<String> out = new ArrayList<>();
        for (int i : menu(actions.entries()))
            out.add(clean(actions.entries().get(i).label()));
        return out;
    }

    private static String clean(String label) {
        String s = label.strip();
        if (s.endsWith("..."))
            s = s.substring(0, s.length() - 3);
        if (s.endsWith("…"))
            s = s.substring(0, s.length() - 1);
        return s.strip();
    }

    private static int find(List<TitleActions.Entry> entries, String key) {
        for (int i = 0; i < entries.size(); i++)
            if (entries.get(i).key().equals(key))
                return i;
        return -1;
    }

    private static List<Integer> menu(List<TitleActions.Entry> entries) {
        final List<Integer> out = new ArrayList<>();
        for (String key : PRIMARY) {
            final int i = find(entries, key);
            if (i >= 0)
                out.add(i);
        }
        for (int i = 0; i < entries.size(); i++) {
            final String key = entries.get(i).key();
            if (!PRIMARY.contains(key) && !PLACED.contains(key) && !entries.get(i).label().isBlank())
                out.add(i);
        }
        final int options = find(entries, OPTIONS);
        if (options >= 0)
            out.add(options);
        return out;
    }

    private boolean motion() {
        return settings.motion && !actions.reducedMotion();
    }

    @Override
    public void render(Canvas c, int mouseX, int mouseY, int w, int h) {
        final Minecraft mc = Minecraft.getInstance();
        final Backgrounds b = Backgrounds.get();
        b.pump();
        final long now = System.currentTimeMillis();
        final List<TitleActions.Entry> entries = actions.entries();
        final List<Integer> menu = menu(entries);
        final int quit = find(entries, QUIT);

        int labels = 0;
        for (int i : menu)
            labels = Math.max(labels, c.width(clean(entries.get(i).label()), FONT));
        if (quit >= 0)
            labels = Math.max(labels, c.width(clean(entries.get(quit).label()), FONT));
        final int railW = Math.max(140, Math.min(labels + PAD * 2 + 8, Math.max(140, w * 2 / 5)));
        final int railX = w - railW;

        Slideshow.get().draw(c, b, w, h, railX, mc.getWindow().getGuiScale(), mc.getWindow().getWidth(), mc.getWindow().getHeight(), motion(), now);

        hits.clear();
        c.fill(railX, 0, w, h, RAIL);
        c.fill(railX, 0, railX + 1, h, BORDER);

        int y = 14;
        final Backgrounds.Image logo = b.logo();
        if (logo != null) {
            final int lw = railW - PAD * 2;
            final int lh = Math.round(lw * logo.height() / (float) logo.width());
            c.image(logo.id(), logo.width(), logo.height(), railX + PAD, y, lw, lh, 0xFFFFFFFF);
            y += lh + 8;
        } else {
            c.text("esteban", railX + PAD, y, Palette.VENOM, FONT);
            y += 22;
        }

        final int quitY = h - 10 - ROW;
        final int iconY = quitY - GAP - ICON;
        final int menuBottom = iconY - GAP * 2;
        final int row = menu.isEmpty() ? ROW : Math.max(16, Math.min(ROW, (menuBottom - y) / menu.size()));
        int order = 0;
        for (int i : menu) {
            final TitleActions.Entry e = entries.get(i);
            final int index = i;
            row(c, railX, y, railW, row, clean(e.label()), e.active(), mouseX, mouseY, order, Palette.VENOM, () -> actions.press(index));
            y += row;
            order++;
        }

        c.fill(railX + PAD, iconY - GAP, w - PAD, iconY - GAP + 1, BORDER);
        int ix = railX + PAD;
        final int language = find(entries, LANGUAGE);
        if (language >= 0) {
            icon(c, ix, iconY, 0, true, mouseX, mouseY, clean(entries.get(language).label()), () -> actions.press(language));
            ix += ICON + GAP;
        }
        final int accessibility = find(entries, ACCESSIBILITY);
        if (accessibility >= 0) {
            icon(c, ix, iconY, 1, true, mouseX, mouseY, clean(entries.get(accessibility).label()), () -> actions.press(accessibility));
            ix += ICON + GAP;
        }
        icon(c, ix, iconY, 2, settings.motion, mouseX, mouseY, "Motion: " + (settings.motion ? "on" : "off"), () -> {
            settings.motion = !settings.motion;
            save.run();
        });

        if (quit >= 0)
            row(c, railX, quitY, railW, ROW, clean(entries.get(quit).label()), entries.get(quit).active(), mouseX, mouseY, order, Palette.RED, () -> actions.press(quit));
        menuSize = order + (quit >= 0 ? 1 : 0);

        footer(c, b, railX, h, mouseX, mouseY, entries, now);

        for (Hit hit : hits)
            if (hit.tip() != null && hit.contains(mouseX, mouseY))
                tooltip(c, hit.tip(), mouseX, mouseY, railX);
    }

    private void row(Canvas c, int x, int y, int w, int h, String label, boolean active, int mx, int my, int order, int hot, Runnable action) {
        final boolean over = active && (mx >= x && mx < x + w && my >= y && my < y + h || focus == order);
        if (over) {
            c.fill(x + 1, y, x + w, y + h, Palette.SURFACE);
            c.fill(x + 1, y, x + 3, y + h, hot);
        }
        final int color = !active ? Palette.MUTED : over ? hot : Palette.TEXT[0];
        c.text(label, x + PAD, y + (h - TEXT) / 2 + 1, color, FONT);
        if (active)
            hits.add(new Hit(x, y, w, h, action, null, order, label));
    }

    private void icon(Canvas c, int x, int y, int kind, boolean on, int mx, int my, String tip, Runnable action) {
        final boolean over = mx >= x && mx < x + ICON && my >= y && my < y + ICON;
        c.fill(x, y, x + ICON, y + ICON, over ? Palette.SURFACE : 0x00000000);
        outline(c, x, y, ICON, ICON, over ? Palette.VENOM : BORDER);
        final int ink = over ? Palette.VENOM : on ? Palette.DIM : Palette.MUTED;
        final int ox = x + (ICON - 9) / 2;
        final int oy = y + (ICON - 9) / 2;
        switch (kind) {
            case 0 -> globe(c, ox, oy, ink);
            case 1 -> person(c, ox, oy, ink);
            default -> wave(c, ox, oy, ink, on);
        }
        hits.add(new Hit(x, y, ICON, ICON, action, tip, -1, tip));
    }

    private static void globe(Canvas c, int x, int y, int ink) {
        c.fill(x + 3, y, x + 6, y + 1, ink);
        c.fill(x + 3, y + 8, x + 6, y + 9, ink);
        c.fill(x, y + 3, x + 1, y + 6, ink);
        c.fill(x + 8, y + 3, x + 9, y + 6, ink);
        c.fill(x + 1, y + 1, x + 3, y + 2, ink);
        c.fill(x + 6, y + 1, x + 8, y + 2, ink);
        c.fill(x + 1, y + 7, x + 3, y + 8, ink);
        c.fill(x + 6, y + 7, x + 8, y + 8, ink);
        c.fill(x + 1, y + 2, x + 2, y + 3, ink);
        c.fill(x + 7, y + 2, x + 8, y + 3, ink);
        c.fill(x + 1, y + 6, x + 2, y + 7, ink);
        c.fill(x + 7, y + 6, x + 8, y + 7, ink);
        c.fill(x + 4, y + 1, x + 5, y + 8, ink);
        c.fill(x + 1, y + 4, x + 8, y + 5, ink);
    }

    private static void person(Canvas c, int x, int y, int ink) {
        c.fill(x + 3, y, x + 6, y + 2, ink);
        c.fill(x, y + 3, x + 9, y + 4, ink);
        c.fill(x + 3, y + 4, x + 6, y + 6, ink);
        c.fill(x + 2, y + 6, x + 4, y + 9, ink);
        c.fill(x + 5, y + 6, x + 7, y + 9, ink);
    }

    private static void wave(Canvas c, int x, int y, int ink, boolean on) {
        for (int row = 0; row < 3; row++) {
            final int top = y + 1 + row * 3;
            c.fill(x, top + 1, x + 2, top + 2, ink);
            c.fill(x + 2, top, x + 4, top + 1, ink);
            c.fill(x + 4, top + 1, x + 6, top + 2, ink);
            c.fill(x + 6, top, x + 9, top + 1, ink);
        }
        if (!on)
            for (int i = 0; i < 9; i++)
                c.fill(x + i, y + 8 - i, x + i + 1, y + 9 - i, Palette.RED);
    }

    private void footer(Canvas c, Backgrounds b, int free, int h, int mx, int my, List<TitleActions.Entry> entries, long now) {
        final String version = actions.version();
        final int fh = c.fontHeight();
        final int line = fh + 4;
        final int versionW = c.width(version) + 8;
        final int credits = find(entries, CREDITS);
        final String creditsLabel = credits >= 0 ? entries.get(credits).label() : "";
        final int creditsW = credits >= 0 ? c.width(creditsLabel) + 8 : 0;
        final Slideshow show = Slideshow.get();
        final int n = show.count(b);
        final int bar = 12;
        final int barsW = n < 2 ? 0 : n * bar + (n - 1) * 4;
        final boolean oneRow = versionW + creditsW + barsW + 24 <= free;

        final int versionY = oneRow ? h - line : h - line * 2;
        c.fill(0, versionY, versionW, versionY + line, BACKING);
        c.text(version, 4, versionY + 3, Palette.DIM, false);

        if (credits >= 0) {
            final int cx = free - creditsW;
            final boolean over = mx >= cx && mx < free && my >= h - line && my < h;
            c.fill(cx, h - line, free, h, BACKING);
            c.text(creditsLabel, cx + 4, h - line + 3, over ? Palette.VENOM : Palette.DIM, false);
            hits.add(new Hit(cx, h - line, creditsW, line, () -> actions.press(credits), null, -1, creditsLabel));
        }

        if (n < 2)
            return;
        int x = oneRow ? (free - barsW) / 2 : 6;
        final int y = oneRow ? h - 8 : 6;
        for (int i = 0; i < n; i++) {
            final int bx = x;
            final boolean over = mx >= bx && mx < bx + bar && my >= y - 4 && my < y + 6;
            c.fill(bx, over ? y - 1 : y, bx + bar, y + 2, i == show.current() ? Palette.VENOM : over ? Palette.DIM : BORDER);
            final int index = i;
            hits.add(new Hit(bx, y - 4, bar, 10, () -> Slideshow.get().jump(index, System.currentTimeMillis()), null, -1, "slide " + index));
            x += bar + 4;
        }
    }

    private static void outline(Canvas c, int x, int y, int w, int h, int color) {
        c.fill(x, y, x + w, y + 1, color);
        c.fill(x, y + h - 1, x + w, y + h, color);
        c.fill(x, y, x + 1, y + h, color);
        c.fill(x + w - 1, y, x + w, y + h, color);
    }

    private static void tooltip(Canvas c, String tip, int mx, int my, int limit) {
        final int tw = c.width(tip, FONT) + 10;
        final int tx = Math.max(0, Math.min(mx - tw / 2, limit + 4));
        final int ty = Math.max(0, my - 22);
        c.fill(tx, ty, tx + tw, ty + TEXT + 6, Palette.INK);
        outline(c, tx, ty, tw, TEXT + 6, BORDER);
        c.text(tip, tx + 5, ty + 4, Palette.VENOM, FONT);
    }

    @Override
    public boolean click(int x, int y, int button) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT)
            return false;
        for (Hit hit : hits) {
            if (hit.contains(x, y)) {
                hit.action().run();
                return true;
            }
        }
        return false;
    }

    @Override
    public void release() { }

    @Override
    public boolean key(int key, Runnable close) {
        if (key == InputConstants.KEY_DOWN || key == InputConstants.KEY_UP) {
            if (menuSize > 0)
                focus = Math.floorMod(focus + (key == InputConstants.KEY_DOWN ? 1 : focus < 0 ? 0 : -1), menuSize);
            return true;
        }
        if ((key == InputConstants.KEY_RETURN || key == InputConstants.KEY_SPACE || key == InputConstants.KEY_NUMPADENTER) && focus >= 0) {
            for (Hit hit : hits) {
                if (hit.order() == focus) {
                    hit.action().run();
                    return true;
                }
            }
        }
        return key == InputConstants.KEY_ESCAPE;
    }

    @Override
    public boolean scroll(int x, int y, double amount) {
        return false;
    }
}
