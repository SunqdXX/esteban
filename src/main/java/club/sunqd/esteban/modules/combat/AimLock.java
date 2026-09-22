package club.sunqd.esteban.modules.combat;

import club.sunqd.esteban.compat.Platform;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class AimLock extends Module {

    private static final double TURN_SCALE = 0.15;

    private final Setting.Number speed =
            register(new Setting.Number("Speed", 12, 1, 30, false));
    private final Setting.Number lockRange =
            register(new Setting.Number("LockRange", 24, 4, 64, true));
    private final Setting.Bool autoHit =
            register(new Setting.Bool("AutoHit", true));

    private LivingEntity target;
    private long lastFrame;

    public AimLock() {
        super("AimLock", "Middle-click a mob or player to lock on, hits at full charge.", Category.COMBAT);
    }

    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public void onDisable() {
        target = null;
        lastFrame = 0;
    }

    @Override
    public void onTick() {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer p = mc.player;
        if (p == null || mc.level == null)
            return;

        if (target != null && !stillValid(p, target))
            target = null;

        if (!mc.mouseHandler.isMouseGrabbed())
            return;

        final LivingEntity looked = lookedAt(mc, p);
        if (looked != null || target != null) {
            while (mc.options.keyPickItem.consumeClick())
                target = (looked == null || looked == target) ? null : looked;
        }

        if (target != null && autoHit.get() && readyToHit(mc, p)) {
            mc.gameMode.attack(p, target);
            Platform.swing(p);
        }
    }

    @Override
    public void onFrame(float partial) {
        final long now = System.nanoTime();
        final double dt = lastFrame == 0 ? 0 : Math.min(0.1, (now - lastFrame) / 1e9);
        lastFrame = now;

        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer p = mc.player;
        if (target == null || p == null || dt <= 0 || !mc.mouseHandler.isMouseGrabbed())
            return;

        final Vec3 aimPoint = target.getBoundingBox().getCenter()
                .add(target.getPosition(partial).subtract(target.position()));
        final float[] want = aim(p.getEyePosition(partial), aimPoint);
        final float dYaw = Mth.wrapDegrees(want[0] - p.getYRot());
        final float dPitch = want[1] - p.getXRot();
        final double k = 1.0 - Math.exp(-speed.get() * dt);
        p.turn(dYaw * k / TURN_SCALE, dPitch * k / TURN_SCALE);
    }

    private boolean stillValid(LocalPlayer p, LivingEntity t) {
        return t.isAlive() && !t.isRemoved() && t.level() == p.level()
                && p.distanceTo(t) <= lockRange.get();
    }

    private boolean readyToHit(Minecraft mc, LocalPlayer p) {
        return mc.gameMode != null
                && mc.hitResult instanceof EntityHitResult hit
                && hit.getEntity() == target
                && !p.isUsingItem()
                && p.getAttackStrengthScale(0.5f) >= 1.0f;
    }

    private LivingEntity lookedAt(Minecraft mc, LocalPlayer p) {
        final Vec3 eye = p.getEyePosition(1.0f);
        final Vec3 end = eye.add(p.getViewVector(1.0f).scale(lockRange.get()));
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e == p || !(e instanceof LivingEntity le) || !le.isAlive())
                continue;
            final Optional<Vec3> hit = e.getBoundingBox().inflate(0.3).clip(eye, end);
            if (hit.isEmpty())
                continue;
            final double d = eye.distanceToSqr(hit.get());
            if (d < bestDist && p.hasLineOfSight(le)) {
                best = le;
                bestDist = d;
            }
        }
        return best;
    }

    static float[] aim(Vec3 from, Vec3 to) {
        final double dx = to.x - from.x;
        final double dy = to.y - from.y;
        final double dz = to.z - from.z;
        final double flat = Math.sqrt(dx * dx + dz * dz);
        final float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        final float pitch = (float) -Math.toDegrees(Math.atan2(dy, flat));
        return new float[] { yaw, pitch };
    }

    @Override
    public String getHudSuffix() {
        return target == null ? null : target.getName().getString();
    }
}
