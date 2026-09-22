package club.sunqd.esteban.modules.movement;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Fly extends Module {

    private final Setting.Mode mode =
            register(new Setting.Mode("Mode", "Motion", "Vanilla", "TP"));
    private final Setting.Number speed =
            register(new Setting.Number("Speed", 2.0, 0.1, 25.0, false));
    private final Setting.Bool hover =
            register(new Setting.Bool("Hover", true));

    private boolean appliedVanilla;
    private boolean appliedNoGravity;
    private boolean lastSentFlying;
    private boolean landingGrace;

    public Fly() {
        super("Fly", "Lets you fly.", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) {
            appliedVanilla = appliedNoGravity = false;
            return;
        }

        if (appliedNoGravity) {
            p.setNoGravity(false);
            appliedNoGravity = false;
        }

        if (appliedVanilla) {
            Abilities a = p.getAbilities();
            if (!a.instabuild) {
                a.mayfly = false;
                a.flying = false;
                a.setFlyingSpeed(0.05F);
                p.onUpdateAbilities();
            }
            appliedVanilla = false;
            lastSentFlying = false;
        }

        if (!p.onGround()) {
            landingGrace = true;
            p.fallDistance = 0.0;
        }
    }

    @Override
    public void onTickAlways() {
        if (!landingGrace) return;
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) { landingGrace = false; return; }
        p.fallDistance = 0.0;
        if (p.onGround()) landingGrace = false;
    }

    @Override
    public void onTick() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) return;

        p.fallDistance = 0.0;

        if (mode.is("Vanilla"))
            vanilla(p);
        else
            motion(p, mode.is("TP"));
    }

    private void vanilla(LocalPlayer p) {
        releaseNoGravity(p);

        Abilities a = p.getAbilities();
        a.mayfly = true;
        a.flying = true;
        a.setFlyingSpeed(0.05F * speed.getFloat());
        appliedVanilla = true;

        if (!lastSentFlying) {
            p.onUpdateAbilities();
            lastSentFlying = true;
        }
    }

    private void motion(LocalPlayer p, boolean teleport) {
        if (!p.isNoGravity()) {
            p.setNoGravity(true);
        }
        appliedNoGravity = true;

        final double s = (teleport ? 0.15 : 0.35) * speed.get();

        final var keys = p.input.keyPresses;

        double vy = 0.0;
        if (keys.jump())  vy += s;
        if (keys.shift()) vy -= s;

        final Vec2 mv = p.input.getMoveVector();
        double forward = mv.y;
        double strafe = mv.x;

        double vx = 0.0, vz = 0.0;
        if (forward != 0 || strafe != 0) {
            final double len = Math.sqrt(forward * forward + strafe * strafe);
            forward /= len;
            strafe /= len;
            final double yaw = Math.toRadians(p.getYRot());
            final double sin = Math.sin(yaw);
            final double cos = Math.cos(yaw);
            vx = (forward * -sin + strafe * cos) * s;
            vz = (forward * cos + strafe * sin) * s;
        }

        if (teleport) {
            p.setDeltaMovement(Vec3.ZERO);
            p.setPos(p.getX() + vx, p.getY() + vy, p.getZ() + vz);
            p.setOnGround(false);
        } else {
            if (hover.get() && vx == 0 && vz == 0 && vy == 0)
                p.setDeltaMovement(Vec3.ZERO);
            else
                p.setDeltaMovement(vx, vy, vz);
        }

        p.fallDistance = 0.0;
    }

    private void releaseNoGravity(LocalPlayer p) {
        if (appliedNoGravity) {
            p.setNoGravity(false);
            appliedNoGravity = false;
        }
    }

    @Override
    public String getHudSuffix() { return mode.get(); }
}
