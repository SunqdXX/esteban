package club.sunqd.esteban.hud.title;

import club.sunqd.esteban.common.compat.Images;
import club.sunqd.esteban.hud.EstebanHud;

import com.mojang.blaze3d.platform.NativeImage;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public final class Backgrounds {

    public static final int MAX_CUSTOM = 24;
    public static final String FOLDER = "custom-backgrounds";

    private static final String NS = "esteban-hud:";
    private static final String ROOT = "/assets/esteban-hud/backgrounds/";
    private static final String[] BUILT_IN = {"night", "meadow", "letters"};
    private static final int MAX_WIDTH = 3840;
    private static final int MAX_HEIGHT = 2160;
    private static final int BLUR_WIDTH = 160;
    public static final int GLOW = 64;
    private static final float FEATHER = 0.14f;

    public record Image(String id, int width, int height, String blur, int blurWidth, int blurHeight, String name) { }

    private static Backgrounds instance;

    private final List<Image> wallpapers = new CopyOnWriteArrayList<>();
    private final ConcurrentLinkedQueue<Runnable> uploads = new ConcurrentLinkedQueue<>();
    private Image logo;
    private String glow;

    private Backgrounds() { }

    public static Backgrounds get() {
        if (instance == null) {
            instance = new Backgrounds();
            instance.start();
        }
        return instance;
    }

    public Image logo() {
        return logo;
    }

    public String glow() {
        return glow;
    }

    public List<Image> wallpapers() {
        return wallpapers;
    }

    public void pump() {
        Runnable r;
        while ((r = uploads.poll()) != null)
            r.run();
    }

    private static void log(String message) {
        System.out.println("[" + EstebanHud.NAME + "] " + message);
    }

    private void start() {
        try (InputStream in = Backgrounds.class.getResourceAsStream(ROOT + "logo.png")) {
            if (in != null) {
                final NativeImage raw = Images.read(in);
                final int w = raw.getWidth();
                final int h = raw.getHeight();
                final int[] px = Images.pixels(raw);
                raw.close();
                feather(px, w, h);
                Images.register(NS + "title/logo", Images.image(w, h, px));
                logo = new Image(NS + "title/logo", w, h, null, 0, 0, "logo.png");
            }
        } catch (IOException | RuntimeException e) {
            log("could not load the logo: " + e);
        }
        Images.register(NS + "title/glow", Images.image(GLOW, GLOW, glowPixels()));
        glow = NS + "title/glow";
        final Thread loader = new Thread(this::loadWallpapers, "esteban-backgrounds");
        loader.setDaemon(true);
        loader.start();
    }

    private void loadWallpapers() {
        for (String name : BUILT_IN) {
            try (InputStream in = Backgrounds.class.getResourceAsStream(ROOT + name + ".png")) {
                if (in == null)
                    continue;
                prepare("title/" + name, name + ".png", Images.read(in));
            } catch (IOException | RuntimeException e) {
                log("could not load " + name + ".png: " + e);
            }
        }
        int custom = 0;
        for (Path file : customFiles()) {
            try (InputStream in = Files.newInputStream(file)) {
                prepare("title/custom_" + custom++, file.getFileName().toString(), Images.read(in));
            } catch (IOException | RuntimeException e) {
                log("skipped custom background " + file.getFileName() + ": " + e.getMessage());
            }
        }
    }

    public static Path customFolder() {
        return FabricLoader.getInstance().getGameDir().resolve(FOLDER);
    }

    private static List<Path> customFiles() {
        final Path dir = customFolder();
        final List<Path> out = new ArrayList<>();
        try {
            Files.createDirectories(dir);
            try (Stream<Path> files = Files.list(dir)) {
                files.filter(f -> Files.isRegularFile(f) && f.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                        .sorted()
                        .forEach(out::add);
            }
        } catch (IOException e) {
            log("could not read " + dir + ": " + e.getMessage());
        }
        if (out.size() > MAX_CUSTOM) {
            log("using the first " + MAX_CUSTOM + " of " + out.size() + " custom backgrounds");
            return out.subList(0, MAX_CUSTOM);
        }
        return out;
    }

    private void prepare(String path, String name, NativeImage raw) {
        int w = raw.getWidth();
        int h = raw.getHeight();
        int[] px = Images.pixels(raw);
        raw.close();
        if (w > MAX_WIDTH || h > MAX_HEIGHT) {
            final double s = Math.min(MAX_WIDTH / (double) w, MAX_HEIGHT / (double) h);
            final int nw = Math.max(1, (int) Math.round(w * s));
            final int nh = Math.max(1, (int) Math.round(h * s));
            log(name + " is " + w + "x" + h + ", downscaled once to " + nw + "x" + nh);
            px = downscale(px, w, h, nw, nh);
            w = nw;
            h = nh;
        }
        final int bw = Math.min(BLUR_WIDTH, w);
        final int bh = Math.max(1, Math.round(h * bw / (float) w));
        final int[] blur = downscale(px, w, h, bw, bh);
        for (int pass = 0; pass < 3; pass++)
            boxBlur(blur, bw, bh, 3);
        final NativeImage sharp = Images.image(w, h, px);
        final NativeImage soft = Images.image(bw, bh, blur);
        final int fw = w;
        final int fh = h;
        uploads.add(() -> {
            Images.register(NS + path, sharp);
            Images.register(NS + path + "_blur", soft);
            wallpapers.add(new Image(NS + path, fw, fh, NS + path + "_blur", bw, bh, name));
        });
    }

    private static void feather(int[] px, int w, int h) {
        final float edge = FEATHER * Math.min(w, h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                final float d = Math.min(Math.min(x, w - 1 - x), Math.min(y, h - 1 - y));
                final float t = Math.max(0f, Math.min(1f, d / edge));
                final float a = t * t * (3f - 2f * t);
                final int i = y * w + x;
                final int alpha = Math.round(((px[i] >>> 24) & 0xFF) * a);
                px[i] = (alpha << 24) | (px[i] & 0xFFFFFF);
            }
        }
    }

    private static int[] glowPixels() {
        final int[] px = new int[GLOW * GLOW];
        final float c = (GLOW - 1) / 2f;
        for (int y = 0; y < GLOW; y++) {
            for (int x = 0; x < GLOW; x++) {
                final float d = (float) Math.hypot(x - c, y - c) / c;
                final float a = d >= 1f ? 0f : (1f - d) * (1f - d);
                px[y * GLOW + x] = (Math.round(a * 255) << 24) | 0x39FF88;
            }
        }
        return px;
    }

    static int[] downscale(int[] src, int sw, int sh, int dw, int dh) {
        final int[] out = new int[dw * dh];
        for (int y = 0; y < dh; y++) {
            final int y0 = y * sh / dh;
            final int y1 = Math.max(y0 + 1, (y + 1) * sh / dh);
            for (int x = 0; x < dw; x++) {
                final int x0 = x * sw / dw;
                final int x1 = Math.max(x0 + 1, (x + 1) * sw / dw);
                long a = 0, r = 0, g = 0, b = 0;
                for (int j = y0; j < y1; j++) {
                    for (int i = x0; i < x1; i++) {
                        final int p = src[j * sw + i];
                        a += (p >>> 24) & 0xFF;
                        r += (p >>> 16) & 0xFF;
                        g += (p >>> 8) & 0xFF;
                        b += p & 0xFF;
                    }
                }
                final int n = (y1 - y0) * (x1 - x0);
                out[y * dw + x] = (int) (a / n) << 24 | (int) (r / n) << 16 | (int) (g / n) << 8 | (int) (b / n);
            }
        }
        return out;
    }

    private static void boxBlur(int[] px, int w, int h, int radius) {
        final int[] tmp = new int[px.length];
        pass(px, tmp, w, h, radius, true);
        pass(tmp, px, w, h, radius, false);
    }

    private static void pass(int[] src, int[] dst, int w, int h, int radius, boolean horizontal) {
        final int lines = horizontal ? h : w;
        final int length = horizontal ? w : h;
        for (int line = 0; line < lines; line++) {
            for (int i = 0; i < length; i++) {
                long a = 0, r = 0, g = 0, b = 0;
                int n = 0;
                for (int k = Math.max(0, i - radius); k <= Math.min(length - 1, i + radius); k++) {
                    final int p = horizontal ? src[line * w + k] : src[k * w + line];
                    a += (p >>> 24) & 0xFF;
                    r += (p >>> 16) & 0xFF;
                    g += (p >>> 8) & 0xFF;
                    b += p & 0xFF;
                    n++;
                }
                final int v = (int) (a / n) << 24 | (int) (r / n) << 16 | (int) (g / n) << 8 | (int) (b / n);
                if (horizontal)
                    dst[line * w + i] = v;
                else
                    dst[i * w + line] = v;
            }
        }
    }
}
