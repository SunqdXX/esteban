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
    static final double TELEPORT = 4.0;

    private double lastY = Double.NaN;
    private double fallen;

    public NoFall() {
        super("NoFall", "Cancels fall damage.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        reset();
    }

    @Override
    public void onDisable() {
        reset();
    }

    private void reset() {
        lastY = Double.NaN;
        fallen = 0;
    }

    @Override
    public void onTick() {
        final LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || p.connection == null) {
            reset();
            return;
        }
        final double y = p.getY();
        final double dy = Double.isNaN(lastY) ? 0 : y - lastY;
        lastY = y;

        if (p.onGround() || exempt(p) || Math.abs(dy) > TELEPORT) {
            fallen = 0;
            return;
        }
        fallen = track(fallen, dy);

        Vec3 v = p.getDeltaMovement();
        if (v.y < -CAP) {
            v = new Vec3(v.x, -CAP, v.z);
            p.setDeltaMovement(v);
        }
        if (v.y < 0 && shouldSpoof(fallen, v.y)) {
            p.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, p.horizontalCollision));
            fallen = 0;
        }
    }

    static double track(double fallen, double dy) {
        return dy < 0 ? fallen - dy : fallen;
    }

    static boolean shouldSpoof(double fallen, double vy) {
        return fallen + Math.min(CAP, -vy) > SPOOF_AT;
    }

    private static boolean exempt(LocalPlayer p) {
        return p.isFallFlying() || p.isPassenger() || p.getAbilities().flying
                || p.isInWater() || p.isInLava() || p.onClimbable();
    }
}
