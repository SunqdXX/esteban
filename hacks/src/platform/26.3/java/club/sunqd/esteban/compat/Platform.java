package club.sunqd.esteban.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

public final class Platform {

    private Platform() { }

    public static double fov(Minecraft mc, float partial) {
        return mc.gameRenderer.mainCamera().getFov();
    }

    public static void swing(LocalPlayer p) {
        p.swing(InteractionHand.MAIN_HAND, p.getMainHandItem().getAttackAnimation(), false);
    }
}
