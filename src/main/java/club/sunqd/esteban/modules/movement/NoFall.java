package club.sunqd.esteban.modules.movement;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class NoFall extends Module {

    private static final double TRIGGER = -0.45;

    public NoFall() {
        super("NoFall", "Cancels fall damage.", Category.MOVEMENT);
    }

    @Override
    public void onTickEnd() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null)
            return;

        if (!p.onGround() && p.getDeltaMovement().y < TRIGGER) {
            p.setOnGround(true);
            p.fallDistance = 0.0;
        }
    }
}
