package club.sunqd.esteban.modules.misc;

import club.sunqd.esteban.compat.Slots;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class AntiKick extends Module {

    private static final double TURN_SCALE = 0.15;
    static final double NUDGE = 0.07;

    private final Setting.Number interval =
            register(new Setting.Number("Interval", 15, 5, 60, true));
    private final Setting.Bool step =
            register(new Setting.Bool("Step", true));

    private final Random random = new Random();

    private int idle;
    private int due;
    private int side = 1;
    private float turned;
    private float lastYaw = Float.NaN;
    private float lastPitch;

    public AntiKick() {
        super("AntiKick", "Tiny moves while you're AFK so the server never idle-kicks you.", Category.MISC);
    }

    @Override
    public void onEnable() {
        idle = 0;
        due = nextDue();
        turned = 0;
        lastYaw = Float.NaN;
    }

    @Override
    public void onTick() {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer p = mc.player;
        if (p == null || p.connection == null || p.input == null)
            return;

        if (active(mc, p)) {
            idle = 0;
            turned = 0;
        } else if (++idle >= due) {
            act(mc, p);
            idle = 0;
            due = nextDue();
        }
        lastYaw = p.getYRot();
        lastPitch = p.getXRot();
    }

    private boolean active(Minecraft mc, LocalPlayer p) {
        final Vec2 mv = p.input.getMoveVector();
        if (mv.x != 0 || mv.y != 0 || p.input.keyPresses.jump() || p.input.keyPresses.shift())
            return true;
        if (mc.options.keyAttack.isDown() || mc.options.keyUse.isDown())
            return true;
        return !Float.isNaN(lastYaw) && (p.getYRot() != lastYaw || p.getXRot() != lastPitch);
    }

    private void act(Minecraft mc, LocalPlayer p) {
        p.connection.send(new ServerboundSetCarriedItemPacket(Slots.selected(p)));

        final float turn = turned != 0 ? -turned : (random.nextBoolean() ? 1 : -1) * (0.2f + random.nextFloat() * 0.6f);
        p.turn(turn / TURN_SCALE, 0);
        turned = turned != 0 ? 0 : turn;

        if (step.get() && p.onGround() && !p.isPassenger() && !p.isInWater() && !p.isInLava()) {
            final double yaw = Math.toRadians(p.getYRot());
            final Vec3 push = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw)).scale(NUDGE * side);
            if (!mc.level.noCollision(p, p.getBoundingBox().move(push.x * 3, -0.3, push.z * 3))) {
                p.setDeltaMovement(p.getDeltaMovement().add(push));
                side = -side;
            }
        }
    }

    private int nextDue() {
        return (int) Math.round(interval.get() * 20 * (0.8 + random.nextDouble() * 0.4));
    }

    @Override
    public String getHudSuffix() {
        return interval.getInt() + "s";
    }
}
