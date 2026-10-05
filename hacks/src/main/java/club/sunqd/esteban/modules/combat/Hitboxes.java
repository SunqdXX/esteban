package club.sunqd.esteban.modules.combat;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class Hitboxes extends Module {

    private static Hitboxes instance;

    private final Setting.Number expand =
            register(new Setting.Number("Expand", 0.4, 0.05, 1.0, false));
    private final Setting.Bool playersOnly =
            register(new Setting.Bool("PlayersOnly", false));

    public Hitboxes() {
        super("Hitboxes", "Bigger hitboxes on players and mobs so your hits land.", Category.COMBAT);
        instance = this;
    }

    public static void afterPick(float partial) {
        final Hitboxes h = instance;
        if (h == null || !h.isActive())
            return;
        final Minecraft mc = Minecraft.getInstance();
        final Entity cam = mc.getCameraEntity();
        final LocalPlayer p = mc.player;
        if (cam == null || p == null || mc.level == null || mc.hitResult instanceof EntityHitResult)
            return;

        final Vec3 eye = cam.getEyePosition(partial);
        double limit = p.entityInteractionRange();
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK)
            limit = Math.min(limit, eye.distanceTo(mc.hitResult.getLocation()));
        final Vec3 ray = cam.getViewVector(partial).scale(limit);
        final Vec3 end = eye.add(ray);
        final double grow = h.expand.get();
        final AABB area = cam.getBoundingBox().expandTowards(ray).inflate(1.0 + grow);

        Entity best = null;
        Vec3 bestAt = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : mc.level.getEntities(cam, area, e -> h.valid(cam, e))) {
            final AABB box = e.getBoundingBox().inflate(e.getPickRadius() + grow);
            if (box.contains(eye)) {
                best = e;
                bestAt = eye;
                break;
            }
            final Optional<Vec3> hit = box.clip(eye, end);
            if (hit.isEmpty())
                continue;
            final double d = eye.distanceToSqr(hit.get());
            if (d < bestDist) {
                best = e;
                bestAt = hit.get();
                bestDist = d;
            }
        }
        if (best != null) {
            mc.hitResult = new EntityHitResult(best, bestAt);
            mc.crosshairPickEntity = best;
        }
    }

    private boolean valid(Entity cam, Entity e) {
        return e instanceof LivingEntity le && le.isAlive() && !e.isSpectator() && e.isPickable()
                && e.getRootVehicle() != cam.getRootVehicle()
                && (!playersOnly.get() || e instanceof Player);
    }

    @Override
    public String getHudSuffix() {
        return String.format("%.2f", expand.get());
    }
}
