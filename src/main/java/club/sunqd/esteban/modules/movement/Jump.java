package club.sunqd.esteban.modules.movement;

import club.sunqd.esteban.EstebanClient;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class Jump extends Module {

    private static final double GRAVITY = 0.08;
    private static final double DRAG    = 0.98;

    private static final double MAX_TAKEOFF_CLIMB = 1.5;

    private final Setting.Number height =
            register(new Setting.Number("Height", 3.0, 1.5, 30.0, false));

    private boolean lastOnGround;
    private double lastY;
    private boolean primed;

    public Jump() {
        super("Jump", "Jump to an exact height, in blocks.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        primed = false;
    }

    @Override
    public void onTick() {
        final LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || p.input == null) {
            primed = false;
            return;
        }

        if (primed && justJumped(p)) {
            final double stillToClimb = height.get() - (p.getY() - lastY);
            if (stillToClimb > 0) {
                final Vec3 v = p.getDeltaMovement();
                final double vy = velocityForClimb(stillToClimb);
                if (vy > v.y)
                    p.setDeltaMovement(v.x, vy, v.z);
            }
        }

        lastOnGround = p.onGround();
        lastY = p.getY();
        primed = true;
    }

    private boolean justJumped(LocalPlayer p) {
        if (!lastOnGround || p.onGround())
            return false;
        final double climbed = p.getY() - lastY;
        if (p.getDeltaMovement().y <= 0 || climbed <= 0.05 || climbed > MAX_TAKEOFF_CLIMB)
            return false;
        if (!p.input.keyPresses.jump())
            return false;
        if (p.hurtTime > 0)
            return false;
        if (p.isInWater() || p.isInLava() || p.onClimbable())
            return false;
        if (p.isFallFlying() || p.isPassenger() || p.getAbilities().flying)
            return false;
        final Module fly = EstebanClient.get().getModuleManager().get("Fly");
        return fly == null || !fly.isEnabled();
    }

    private static double climb(double v) {
        double total = 0.0;
        for (int i = 0; i < 400 && v > 0.0; i++) {
            total += v;
            v = (v - GRAVITY) * DRAG;
        }
        return total;
    }

    private static double velocityForClimb(double blocks) {
        double lo = 0.0, hi = 10.0;
        for (int i = 0; i < 48; i++) {
            final double mid = (lo + hi) / 2.0;
            if (climb(mid) < blocks) lo = mid;
            else hi = mid;
        }
        return hi;
    }

    @Override
    public String getHudSuffix() {
        return String.format("%.1fb", height.get());
    }
}
