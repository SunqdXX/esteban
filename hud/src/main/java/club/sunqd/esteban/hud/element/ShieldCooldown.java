package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;

public final class ShieldCooldown extends Element {

    private static final int PAD = 2;
    private static final int ICON = 16;
    private static final int BAR = 2;
    private static final int FALLBACK_TICKS = 100;

    private boolean disabled;
    private float percent;
    private int lastTick = -1;
    private float lastPercent;
    private float perTick;
    private ItemStack icon = ItemStack.EMPTY;

    public ShieldCooldown(int x, int y) {
        super("shield", "Shield disabled", x, y);
        color = Palette.RED;
    }

    private static ItemStack shield(LocalPlayer p) {
        if (p.getOffhandItem().is(Items.SHIELD))
            return p.getOffhandItem();
        if (p.getMainHandItem().is(Items.SHIELD))
            return p.getMainHandItem();
        return new ItemStack(Items.SHIELD);
    }

    @Override
    public void update(Minecraft mc) {
        final LocalPlayer p = mc.player;
        if (p == null) {
            disabled = false;
            return;
        }
        final ItemStack stack = shield(p);
        final ItemCooldowns cooldowns = p.getCooldowns();
        disabled = cooldowns.isOnCooldown(stack);
        if (!disabled) {
            lastTick = -1;
            perTick = 0f;
            return;
        }
        icon = stack;
        final float now = cooldowns.getCooldownPercent(stack, 0f);
        final int tick = p.tickCount;
        if (tick != lastTick) {
            if (lastTick >= 0 && now < lastPercent)
                perTick = (lastPercent - now) / (tick - lastTick);
            lastTick = tick;
            lastPercent = now;
        }
        percent = now;
    }

    public float seconds() {
        final float ticks = perTick > 0f ? percent / perTick : percent * FALLBACK_TICKS;
        return ticks / 20f;
    }

    public String text() {
        return String.format(Locale.ROOT, "%.1fs", seconds());
    }

    @Override
    public boolean visible() {
        return disabled;
    }

    @Override
    public int width(Canvas c) {
        return PAD * 2 + ICON + 3 + c.width("00.0s");
    }

    @Override
    public int height(Canvas c) {
        return PAD * 2 + ICON + BAR + 1;
    }

    @Override
    protected void draw(Canvas c) {
        final int w = width(c);
        if (background)
            c.fill(0, 0, w, height(c), Palette.BACKGROUND);
        c.item(icon, PAD, PAD);
        c.text(text(), PAD + ICON + 3, PAD + 4, color, false);
        final int top = PAD + ICON + 1;
        c.fill(PAD, top, w - PAD, top + BAR, Palette.MUTED);
        c.fill(PAD, top, PAD + Math.round((w - PAD * 2) * Math.max(0f, Math.min(1f, percent))), top + BAR, color);
    }
}
