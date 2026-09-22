package club.sunqd.esteban.modules.movement;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Speed extends Module {

    private static final double MAX_SAFE = 9.0;

    private final Setting.Number speed =
            register(new Setting.Number("Speed", 0.5, 0.1, MAX_SAFE, false));
    private final Setting.Bool groundOnly =
            register(new Setting.Bool("GroundOnly", false));

    public Speed() {
        super("Speed", "Move faster.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || p.input == null)
            return;
        if (groundOnly.get() && !p.onGround())
            return;

        final Vec2 mv = p.input.getMoveVector();
        double strafe = mv.x;
        double forward = mv.y;
        final double len = Math.sqrt(strafe * strafe + forward * forward);
        if (len < 1.0E-4)
            return;
        strafe /= len;
        forward /= len;

        final double yaw = Math.toRadians(p.getYRot());
        final double sin = Math.sin(yaw);
        final double cos = Math.cos(yaw);

        final double s = Math.min(speed.get(), MAX_SAFE);
        final double vx = (forward * -sin + strafe * cos) * s;
        final double vz = (forward * cos + strafe * sin) * s;

        final Vec3 v = p.getDeltaMovement();
        p.setDeltaMovement(vx, v.y, vz);
    }

    @Override
    public String getHudSuffix() {
        return String.format("%.1f", speed.get());
    }
}
