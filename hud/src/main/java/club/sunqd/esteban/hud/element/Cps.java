package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.hud.input.Clicks;

import net.minecraft.client.Minecraft;

public final class Cps extends TextElement {

    public Cps(int x, int y) {
        super("cps", "CPS", x, y);
    }

    @Override
    protected String text(Minecraft mc) {
        return Clicks.LEFT.perSecond() + " CPS";
    }
}
