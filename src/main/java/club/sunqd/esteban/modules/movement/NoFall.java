package club.sunqd.esteban.modules.movement;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;

public class NoFall extends Module {

    static final double CAP = 2.9;
    static final double SPOOF_AT = 2.5;

    public NoFall() {
        super("NoFall", "Cancels fall damage.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        final LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || exempt(p))
            return;
        final Vec3 v = p.getDeltaMovement();
        if (v.y < -CAP)
            p.setDeltaMovement(v.x, -CAP, v.z);
    }

    @Override
    public void onTickEnd() {
        final LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || p.connection == null || p.onGround() || exempt(p))
            return;
        final double vy = p.getDeltaMovement().y;
        if (vy < 0 && shouldSpoof(p.fallDistance, vy))
            p.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, p.horizontalCollision));
    }

    static boolean shouldSpoof(double fallDistance, double vy) {
        return fallDistance + Math.min(CAP, -vy) > SPOOF_AT;
    }

    private static boolean exempt(LocalPlayer p) {
        return p.isFallFlying() || p.isPassenger() || p.getAbilities().flying
                || p.isInWater() || p.isInLava() || p.onClimbable();
    }
}
