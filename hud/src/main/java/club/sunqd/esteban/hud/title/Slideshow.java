package club.sunqd.esteban.hud.title;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.EstebanHud;

import java.util.ArrayList;
import java.util.List;

public final class Slideshow {

    public static final long SLIDE_MS = 25_000;
    public static final long FADE_MS = 1_600;
    public static final double PAN_MS = 40_000;
    public static final double MARGIN = 1.06;
    public static final double MAX_UPSCALE = 1.2;
    public static final double LOGO_WIDTH = 0.4;

    private static final int BASE = 0x1A1B26;
    private static final int DARKEN = 0x5A5A5A;
    private static final Slideshow INSTANCE = new Slideshow();

    private int current;
    private int previous = -1;
    private long shownAt = -1;
    private long previousShownAt;
    private long fadeAt;
    private String size = "";

    private Slideshow() { }

    public static Slideshow get() {
        return INSTANCE;
    }

    public int current() {
        return current;
    }

    public int count(Backgrounds b) {
        return 1 + b.wallpapers().size();
    }

    public void jump(int index, long now) {
        if (index == current)
            return;
        previous = current;
        previousShownAt = shownAt;
        current = index;
        shownAt = now;
        fadeAt = now;
    }

    public static boolean tooSmall(Backgrounds.Image img, double windowWidth, double windowHeight) {
        return Math.max(windowWidth / img.width(), windowHeight / img.height()) * MARGIN > MAX_UPSCALE;
    }

    public void draw(Canvas c, Backgrounds b, int w, int h, int free, double gs, int winW, int winH, boolean motion, long now) {
        if (shownAt < 0) {
            shownAt = now;
            fadeAt = now - FADE_MS;
        }
        final int n = count(b);
        if (current >= n)
            current = 0;
        if (n > 1 && now - shownAt > SLIDE_MS)
            jump((current + 1) % n, now);
        final float fade = Math.min(1f, (now - fadeAt) / (float) FADE_MS);
        c.fill(0, 0, w, h, 0xFF000000 | BASE);
        if (fade < 1f && previous >= 0 && previous < n)
            slide(c, b, previous, 1f, now - previousShownAt, w, h, free, gs, winW, winH, motion);
        slide(c, b, current, fade, now - shownAt, w, h, free, gs, winW, winH, motion);
        report(b, winW, winH);
    }

    private static int tint(float alpha, int rgb) {
        return (Math.round(Math.max(0f, Math.min(1f, alpha)) * 255) << 24) | rgb;
    }

    private void slide(Canvas c, Backgrounds b, int index, float alpha, long t, int w, int h, int free, double gs, int winW, int winH, boolean motion) {
        if (index == 0) {
            logo(c, b, alpha, t, w, h, free, gs, winW, motion);
            return;
        }
        final Backgrounds.Image img = b.wallpapers().get(index - 1);
        double p = motion ? 0.5 - 0.5 * Math.cos(2 * Math.PI * t / PAN_MS) : 0.5;
        if (index % 2 == 0)
            p = 1 - p;
        if (!tooSmall(img, winW, winH)) {
            final double cover = Math.max(winW / (double) img.width(), winH / (double) img.height()) * MARGIN;
            final double dw = img.width() * cover;
            final double dh = img.height() * cover;
            c.image(img.id(), img.width(), img.height(), (float) (-(dw - winW) * p / gs), (float) (-(dh - winH) / 2 / gs),
                    (float) (dw / gs), (float) (dh / gs), tint(alpha, 0xFFFFFF));
            return;
        }
        final double cover = Math.max(winW / (double) img.blurWidth(), winH / (double) img.blurHeight()) * MARGIN;
        final double bw = img.blurWidth() * cover;
        final double bh = img.blurHeight() * cover;
        c.image(img.blur(), img.blurWidth(), img.blurHeight(), (float) (-(bw - winW) * p / gs), (float) (-(bh - winH) / 2 / gs),
                (float) (bw / gs), (float) (bh / gs), tint(alpha, DARKEN));
        final double sw = img.width() * MAX_UPSCALE;
        final double sh = img.height() * MAX_UPSCALE;
        c.image(img.id(), img.width(), img.height(), (float) ((winW - sw) / 2 / gs), (float) ((winH - sh) / 2 / gs),
                (float) (sw / gs), (float) (sh / gs), tint(alpha, 0xFFFFFF));
    }

    private void logo(Canvas c, Backgrounds b, float alpha, long t, int w, int h, int free, double gs, int winW, boolean motion) {
        c.fill(0, 0, w, h, tint(alpha, BASE));
        final Backgrounds.Image logo = b.logo();
        if (logo == null)
            return;
        final double real = Math.min(winW * LOGO_WIDTH, logo.width() * MAX_UPSCALE);
        final float lw = (float) (real / gs);
        final float lh = lw * logo.height() / logo.width();
        final float dx = motion ? (float) Math.sin(2 * Math.PI * t / 22_000.0) * 3f : 0f;
        final float dy = motion ? (float) Math.sin(2 * Math.PI * t / 14_000.0) * 2f : 0f;
        final float cx = free / 2f + dx;
        final float cy = h / 2f + dy;
        if (b.glow() != null)
            c.image(b.glow(), Backgrounds.GLOW, Backgrounds.GLOW, cx - lw * 0.8f, cy - lh * 0.8f, lw * 1.6f, lh * 1.6f, tint(alpha * 0.22f, 0xFFFFFF));
        c.image(logo.id(), logo.width(), logo.height(), cx - lw / 2, cy - lh / 2, lw, lh, tint(alpha, 0xFFFFFF));
    }

    private void report(Backgrounds b, int winW, int winH) {
        final String now = winW + "x" + winH + ":" + b.wallpapers().size();
        if (now.equals(size))
            return;
        size = now;
        final List<String> small = new ArrayList<>();
        for (Backgrounds.Image img : b.wallpapers())
            if (tooSmall(img, winW, winH))
                small.add(img.name());
        if (!small.isEmpty())
            System.out.println("[" + EstebanHud.NAME + "] too small for " + winW + "x" + winH + ", shown sharp over a blurred copy: " + String.join(", ", small));
    }
}
