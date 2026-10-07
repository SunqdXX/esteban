package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.compat.Platform;

import net.minecraft.client.Minecraft;

import java.time.LocalTime;
import java.util.Locale;

public final class Clock extends TextElement {

    public Clock(int x, int y) {
        super("clock", "Clock", x, y);
        enabled = false;
    }

    @Override
    protected String text(Minecraft mc) {
        final LocalTime now = LocalTime.now();
        final String real = String.format(Locale.ROOT, "%02d:%02d", now.getHour(), now.getMinute());
        if (mc.level == null)
            return real;
        final long time = Platform.dayTime(mc.level);
        final long day = Math.floorDiv(time, 24000L) + 1;
        final long t = Math.floorMod(time + 6000L, 24000L);
        return String.format(Locale.ROOT, "%s  Day %d %02d:%02d", real, day, t / 1000, t % 1000 * 60 / 1000);
    }
}
