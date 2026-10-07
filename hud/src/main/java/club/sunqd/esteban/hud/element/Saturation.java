package club.sunqd.esteban.hud.element;

import net.minecraft.client.Minecraft;

import java.util.Locale;

public final class Saturation extends TextElement {

    public Saturation(int x, int y) {
        super("saturation", "Saturation", x, y);
        enabled = false;
    }

    @Override
    protected String text(Minecraft mc) {
        if (mc.player == null)
            return "- saturation";
        return String.format(Locale.ROOT, "%.1f saturation", mc.player.getFoodData().getSaturationLevel());
    }
}
