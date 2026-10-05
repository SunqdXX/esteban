package club.sunqd.esteban.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Locale;

public final class Coords extends TextElement {

    public Coords(int x, int y) {
        super("coords", "Coordinates", x, y);
    }

    @Override
    protected String text(Minecraft mc) {
        final LocalPlayer p = mc.player;
        if (p == null)
            return "XYZ -";
        final String facing = p.getDirection().getName().substring(0, 1).toUpperCase(Locale.ROOT);
        return "XYZ " + p.getBlockX() + " " + p.getBlockY() + " " + p.getBlockZ() + "  " + facing;
    }
}
