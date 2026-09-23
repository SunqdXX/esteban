package club.sunqd.esteban.modules.combat;

import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class AimLock extends LockOn {

    static final float SYNC = -0.5f;

    private final Setting.Bool crits =
            register(new Setting.Bool("Crits", true));

    public AimLock() {
        super("AimLock", "Middle-click to lock on. Max damage hit every time the bar is full.");
    }

    @Override
    protected void hit(Minecraft mc, LocalPlayer p) {
        if (!onTarget(mc) || p.getAttackStrengthScale(SYNC) < 1.0f)
            return;

        if (crits.get() && rising(p))
            return;

        final boolean spoof = crits.get() && p.isSprinting() && canCrit(p);
        if (spoof) {
            p.connection.send(new ServerboundPlayerCommandPacket(p, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
            p.setSprinting(false);
        }
        attackOnce(mc, p);
        if (spoof) {
            p.setSprinting(true);
            p.connection.send(new ServerboundPlayerCommandPacket(p, ServerboundPlayerCommandPacket.Action.START_SPRINTING));
        }
    }

    private static boolean rising(LocalPlayer p) {
        return !p.onGround() && p.getDeltaMovement().y > 0
                && !p.onClimbable() && !p.isInWater() && !p.isInLava() && !p.isPassenger()
                && !p.isFallFlying() && !p.getAbilities().flying;
    }

    private static boolean canCrit(LocalPlayer p) {
        return p.fallDistance > 0 && !p.onGround() && !p.onClimbable()
                && !p.isInWater() && !p.isPassenger();
    }

    @Override
    public String getHudSuffix() {
        return target == null ? null : target.getName().getString();
    }
}
