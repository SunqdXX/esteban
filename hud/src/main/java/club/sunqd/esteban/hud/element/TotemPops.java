package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;
import club.sunqd.esteban.hud.combat.Combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class TotemPops extends Element {

    private static final int PAD = 2;
    private static final int ICON = 16;

    private final List<Combat.Pops> rows = new ArrayList<>();
    private ItemStack icon = ItemStack.EMPTY;

    public TotemPops(int x, int y) {
        super("totems", "Totem pops", x, y);
        enabled = false;
    }

    @Override
    public void update(Minecraft mc) {
        rows.clear();
        rows.addAll(Combat.pops());
        if (icon.isEmpty())
            icon = new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    public List<String> lines() {
        final List<String> out = new ArrayList<>();
        for (Combat.Pops p : rows)
            out.add(p.name() + " " + p.count());
        return out;
    }

    @Override
    public boolean visible() {
        return !rows.isEmpty();
    }

    private int line(Canvas c) {
        return c.fontHeight() + 1;
    }

    @Override
    public int width(Canvas c) {
        int names = 0;
        int counts = 0;
        for (Combat.Pops p : rows) {
            names = Math.max(names, c.width(p.name()));
            counts = Math.max(counts, c.width(String.valueOf(p.count())));
        }
        return PAD * 2 + ICON + 3 + names + 6 + counts;
    }

    @Override
    public int height(Canvas c) {
        return PAD * 2 + Math.max(ICON, rows.size() * line(c) - 1);
    }

    @Override
    protected void draw(Canvas c) {
        final int w = width(c);
        if (background)
            c.fill(0, 0, w, height(c), Palette.BACKGROUND);
        c.item(icon, PAD, PAD);
        final int top = PAD + Math.max(0, (ICON - (rows.size() * line(c) - 1)) / 2);
        for (int i = 0; i < rows.size(); i++) {
            final Combat.Pops p = rows.get(i);
            final int y = top + i * line(c);
            final String count = String.valueOf(p.count());
            c.text(p.name(), PAD + ICON + 3, y, color, false);
            c.text(count, w - PAD - c.width(count), y, Palette.RED, false);
        }
    }
}
