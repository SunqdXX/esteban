package club.sunqd.esteban.render;

import club.sunqd.esteban.EstebanClient;
import club.sunqd.esteban.compat.Platform;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.modules.render.Esp;
import club.sunqd.esteban.modules.render.StorageEsp;
import club.sunqd.esteban.modules.render.Tracers;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class EspRenderer implements HudElement {

    private static final double NEAR = 0.05;

    private EspRenderer() { }

    public static void install() {
        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath("esteban", "esp"), new EspRenderer());
    }

    private static double[] toCamera(Vec3 eye, double yawRad, double pitchRad, Vec3 world) {
        final double dx = world.x - eye.x;
        final double dy = world.y - eye.y;
        final double dz = world.z - eye.z;

        final double sy = Math.sin(yawRad), cy = Math.cos(yawRad);
        final double sp = Math.sin(pitchRad), cp = Math.cos(pitchRad);

        final double fx = -sy * cp, fy = -sp, fz = cy * cp;
        final double rx = -cy,      ry = 0.0, rz = -sy;
        final double ux = ry * fz - rz * fy;
        final double uy = rz * fx - rx * fz;
        final double uz = rx * fy - ry * fx;

        return new double[] {
            dx * rx + dy * ry + dz * rz,
            dx * ux + dy * uy + dz * uz,
            dx * fx + dy * fy + dz * fz
        };
    }

    private static int[] projectBounds(AABB box, Vec3 eye, double yaw, double pitch,
                                       double focal, int sw, int sh) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        boolean any = false;

        for (int i = 0; i < 8; i++) {
            final Vec3 corner = new Vec3(
                    (i & 1) == 0 ? box.minX : box.maxX,
                    (i & 2) == 0 ? box.minY : box.maxY,
                    (i & 4) == 0 ? box.minZ : box.maxZ);

            final double[] cam = toCamera(eye, yaw, pitch, corner);
            if (cam[2] < NEAR)
                continue;

            final double sx = sw / 2.0 + (cam[0] / cam[2]) * focal;
            final double sy = sh / 2.0 - (cam[1] / cam[2]) * focal;
            if (sx < minX) minX = sx;
            if (sx > maxX) maxX = sx;
            if (sy < minY) minY = sy;
            if (sy > maxY) maxY = sy;
            any = true;
        }

        if (!any || minX > maxX)
            return null;
        if (maxX < 0 || minX > sw || maxY < 0 || minY > sh)
            return null;

        final int l = (int) Math.max(0, minX);
        final int t = (int) Math.max(0, minY);
        final int r = (int) Math.min(sw, maxX);
        final int b = (int) Math.min(sh, maxY);
        if (r - l < 1 || b - t < 1)
            return null;

        return new int[] { l, t, r, b };
    }

    private static void bresenham(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1,
                                  int colour, int sw, int sh) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        int guard = 0;
        while (guard++ < 4000) {
            if (x0 >= 0 && x0 < sw && y0 >= 0 && y0 < sh)
                g.fill(x0, y0, x0 + 1, y0 + 1, colour);
            if (x0 == x1 && y0 == y1)
                return;
            final int e2 = err << 1;
            if (e2 >= dy) { err += dy; x0 += sx; }
            if (e2 <= dx) { err += dx; y0 += sy; }
        }
    }

    private static void rect(GuiGraphicsExtractor g, int l, int t, int r, int b, int c) {
        g.fill(l, t, r, t + 1, c);
        g.fill(l, b - 1, r, b, c);
        g.fill(l, t, l + 1, b, c);
        g.fill(r - 1, t, r, b, c);
    }

    private static void corners(GuiGraphicsExtractor g, int l, int t, int r, int b, int c) {
        final int len = Math.max(2, Math.min((r - l), (b - t)) / 4);
        g.fill(l, t, l + len, t + 1, c);    g.fill(l, t, l + 1, t + len, c);
        g.fill(r - len, t, r, t + 1, c);    g.fill(r - 1, t, r, t + len, c);
        g.fill(l, b - 1, l + len, b, c);    g.fill(l, b - len, l + 1, b, c);
        g.fill(r - len, b - 1, r, b, c);    g.fill(r - 1, b - len, r, b, c);
    }

    private static void nametag(GuiGraphicsExtractor g, Minecraft mc, String s,
                                int centreX, int y, int colour) {
        final int w = mc.font.width(s);
        final int x = centreX - w / 2;
        g.fill(x - 1, y - 1, x + w + 1, y + 9, 0xB0000000);
        g.text(mc.font, s, x, y, colour);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        final var mm = EstebanClient.get().getModuleManager();
        if (!mm.isArmed())
            return;

        final Module espM = mm.get("ESP");
        final Module traM = mm.get("Tracers");
        final Module stoM = mm.get("StorageESP");
        final boolean esp = espM instanceof Esp && espM.isEnabled();
        final boolean tra = traM instanceof Tracers && traM.isEnabled();
        final boolean sto = stoM instanceof StorageEsp && stoM.isEnabled();
        if (!esp && !tra && !sto)
            return;

        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer p = mc.player;
        if (p == null || mc.level == null)
            return;

        final int sw = mc.getWindow().getGuiScaledWidth();
        final int sh = mc.getWindow().getGuiScaledHeight();
        final Camera cam = Platform.camera(mc);
        if (cam == null)
            return;
        final double fov = cam.getFov();
        final float partial = delta.getGameTimeDeltaPartialTick(true);

        final Vec3 eye = p.getEyePosition(partial);
        final double yaw = Math.toRadians(p.getYRot());
        final double pitch = Math.toRadians(p.getXRot());
        final double focal = (sh / 2.0) / Math.tan(Math.toRadians(fov) / 2.0);

        if (esp || tra)
            drawEntities(g, mc, p, esp, tra, espM, traM, eye, yaw, pitch, focal, sw, sh, partial);

        if (sto)
            drawStorage(g, mc, (StorageEsp) stoM, p, eye, yaw, pitch, focal, sw, sh);
    }

    private void drawEntities(GuiGraphicsExtractor g, Minecraft mc, LocalPlayer p,
                              boolean esp, boolean tra, Module espM, Module traM,
                              Vec3 eye, double yaw, double pitch, double focal,
                              int sw, int sh, float partial) {
        final double range = esp ? ((Esp) espM).range() : ((Tracers) traM).range();
        final boolean playersOnly = esp && ((Esp) espM).playersOnly();
        final boolean cornersOnly = esp && ((Esp) espM).cornersOnly();

        for (Entity e : mc.level.entitiesForRendering()) {
            if (e == p || !(e instanceof LivingEntity))
                continue;
            if (playersOnly && !(e instanceof Player))
                continue;
            if (e.distanceTo(p) > range)
                continue;

            final Vec3 lerped = e.getPosition(partial);
            final AABB box = e.getBoundingBox().move(lerped.subtract(e.position()));

            final int[] bb = projectBounds(box, eye, yaw, pitch, focal, sw, sh);
            if (bb == null)
                continue;
            final int l = bb[0], t = bb[1], r = bb[2], b = bb[3];

            final int colour = (e instanceof Player) ? 0xFFFF4040 : 0xFF40FF80;

            if (esp) {
                if (cornersOnly)
                    corners(g, l, t, r, b, colour);
                else
                    rect(g, l, t, r, b, colour);
            }
            if (tra)
                bresenham(g, sw / 2, sh, (l + r) / 2, b, colour, sw, sh);
        }
    }

    private static int colourFor(BlockEntity be, StorageEsp s) {
        if (be instanceof SpawnerBlockEntity)     return s.spawners() ? 0xFFFF3030 : 0;
        if (be instanceof ShulkerBoxBlockEntity)  return s.shulkers() ? 0xFFC060FF : 0;
        if (be instanceof BarrelBlockEntity)      return s.barrels()  ? 0xFFD8A040 : 0;
        if (be instanceof EnderChestBlockEntity)  return s.chests()   ? 0xFF30E0C0 : 0;
        if (be instanceof ChestBlockEntity)       return s.chests()   ? 0xFFFFC020 : 0;
        if (be instanceof HopperBlockEntity
                || be instanceof DispenserBlockEntity)
                                                  return s.droppers() ? 0xFFB0B0B0 : 0;
        return 0;
    }

    private void drawStorage(GuiGraphicsExtractor g, Minecraft mc, StorageEsp s,
                             LocalPlayer p, Vec3 eye, double yaw, double pitch,
                             double focal, int sw, int sh) {
        final ClientLevel level = mc.level;
        if (level == null)
            return;

        final double range = s.range();
        final double range2 = range * range;
        final boolean names = s.names();
        final boolean tracers = s.tracers();

        final int cr = (int) Math.ceil(range / 16.0) + 1;
        final int pcx = p.blockPosition().getX() >> 4;
        final int pcz = p.blockPosition().getZ() >> 4;

        for (int cx = pcx - cr; cx <= pcx + cr; cx++) {
            for (int cz = pcz - cr; cz <= pcz + cr; cz++) {
                final ChunkAccess ca = level.getChunk(cx, cz, ChunkStatus.FULL, false);
                if (!(ca instanceof LevelChunk chunk))
                    continue;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    final int colour = colourFor(be, s);
                    if (colour == 0)
                        continue;

                    final BlockPos pos = be.getBlockPos();
                    final Vec3 centre = Vec3.atCenterOf(pos);
                    if (centre.distanceToSqr(eye) > range2)
                        continue;

                    final int[] bb = projectBounds(new AABB(pos), eye, yaw, pitch, focal, sw, sh);
                    if (bb == null)
                        continue;
                    final int l = bb[0], t = bb[1], r = bb[2], b = bb[3];

                    rect(g, l, t, r, b, colour);
                    if (tracers)
                        bresenham(g, sw / 2, sh, (l + r) / 2, (t + b) / 2, colour, sw, sh);
                    if (names) {
                        final String label = be.getBlockState().getBlock().getName().getString();
                        nametag(g, mc, label, (l + r) / 2, t - 11, colour);
                    }
                }
            }
        }
    }
}
