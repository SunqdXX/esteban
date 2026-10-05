package club.sunqd.esteban.modules.movement;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", "Always sprint.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) return;
        if (p.input != null && p.input.hasForwardImpulse()) {
            p.setSprinting(true);
        }
    }

    @Override
    public void onDisable() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p != null) p.setSprinting(false);
    }
}
