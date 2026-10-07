package club.sunqd.esteban.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Locale;

public final class Speed extends TextElement {

    public Speed(int x, int y) {
        super("speed", "Speed", x, y);
        enabled = false;
    }

    @Override
    protected String text(Minecraft mc) {
        final LocalPlayer p = mc.player;
        if (p == null)
            return "- b/s";
        final double dx = p.getX() - p.xo;
        final double dz = p.getZ() - p.zo;
        return String.format(Locale.ROOT, "%.2f b/s", Math.sqrt(dx * dx + dz * dz) * 20);
    }
}
