package club.sunqd.esteban.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

public final class Platform {

    private Platform() { }

    public static double fov(Minecraft mc, float partial) {
        final double base = mc.options.fov().get();
        final LocalPlayer p = mc.player;
        if (p == null)
            return base;
        return base * p.getFieldOfViewModifier(mc.options.getCameraType().isFirstPerson(),
                mc.options.fovEffectScale().get().floatValue());
    }

    public static void swing(LocalPlayer p) {
        p.swing(InteractionHand.MAIN_HAND);
    }
}
