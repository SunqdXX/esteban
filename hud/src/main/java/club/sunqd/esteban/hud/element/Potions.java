package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class Potions extends Element {

    private static final int PAD = 2;
    private static final int ICON = 18;
    private static final int ROW = 22;
    private static final String[] LEVELS = {"", " II", " III", " IV", " V", " VI", " VII", " VIII", " IX", " X"};

    private record Entry(Holder<MobEffect> effect, String name, String time, boolean harmful) { }

    private final List<Entry> entries = new ArrayList<>();

    public Potions(int x, int y) {
        super("potions", "Potion Effects", x, y);
    }

    @Override
    public void update(Minecraft mc) {
        entries.clear();
        if (mc.player == null)
            return;
        final List<MobEffectInstance> active = new ArrayList<>(mc.player.getActiveEffects());
        active.sort(Comparator.comparingInt((MobEffectInstance e) -> e.isInfiniteDuration() ? Integer.MAX_VALUE : e.getDuration()).reversed());
        for (MobEffectInstance e : active) {
            final int amp = e.getAmplifier();
            final String level = amp < LEVELS.length ? LEVELS[amp] : " " + (amp + 1);
            final String name = e.getEffect().value().getDisplayName().getString() + level;
            entries.add(new Entry(e.getEffect(), name, time(e), !e.getEffect().value().isBeneficial()));
        }
    }

    private static String time(MobEffectInstance e) {
        if (e.isInfiniteDuration())
            return "**:**";
        final int seconds = e.getDuration() / 20;
        if (seconds >= 3600)
            return String.format(Locale.ROOT, "%d:%02d:%02d", seconds / 3600, seconds / 60 % 60, seconds % 60);
        return String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);
    }

    public List<String> lines() {
        final List<String> out = new ArrayList<>();
        for (Entry e : entries)
            out.add(e.name() + " " + e.time());
        return out;
    }

    @Override
    public boolean visible() {
        return !entries.isEmpty();
    }

    @Override
    public int width(Canvas c) {
        int text = 0;
        for (Entry e : entries)
            text = Math.max(text, Math.max(c.width(e.name()), c.width(e.time())));
        return PAD * 2 + ICON + 3 + text;
    }

    @Override
    public int height(Canvas c) {
        return PAD * 2 + Math.max(1, entries.size()) * ROW - 2;
    }

    @Override
    protected void draw(Canvas c) {
        if (background)
            c.fill(0, 0, width(c), height(c), Palette.BACKGROUND);
        for (int i = 0; i < entries.size(); i++) {
            final Entry e = entries.get(i);
            final int y = PAD + i * ROW;
            c.effectIcon(e.effect(), PAD, y + 1, ICON);
            c.text(e.name(), PAD + ICON + 3, y + 1, e.harmful() ? Palette.RED : color, false);
            c.text(e.time(), PAD + ICON + 3, y + 11, Palette.DIM, false);
        }
    }
}
