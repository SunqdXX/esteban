package club.sunqd.esteban.hud.world;

import club.sunqd.esteban.hud.EstebanHud;
import club.sunqd.esteban.hud.element.Element;
import club.sunqd.esteban.hud.element.LowFire;
import club.sunqd.esteban.hud.element.LowShield;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class Lowered {

    private Lowered() { }

    private static float amount(String id, float step) {
        final EstebanHud mod = EstebanHud.get();
        if (mod == null)
            return 0f;
        final Element e = mod.hud().get(id);
        return e != null && e.enabled ? step * e.scale : 0f;
    }

    public static float fire() {
        return amount("lowfire", LowFire.STEP);
    }

    public static float shield(ItemStack stack) {
        return stack != null && stack.is(Items.SHIELD) ? amount("lowshield", LowShield.STEP) : 0f;
    }
}
