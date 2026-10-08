package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.hud.combat.Combat;

import net.minecraft.client.Minecraft;

public final class Combo extends TextElement {

    public Combo(int x, int y) {
        super("combo", "Combo", x, y);
        enabled = false;
    }

    @Override
    protected String text(Minecraft mc) {
        return Combat.combo() + " combo";
    }
}
