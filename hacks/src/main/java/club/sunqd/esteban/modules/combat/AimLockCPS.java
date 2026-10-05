package club.sunqd.esteban.modules.combat;

import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.concurrent.ThreadLocalRandom;

public class AimLockCPS extends LockOn {

    static final int TICKS_PER_SECOND = 20;

    private final Setting.Number cps =
            register(new Setting.Number("CPS", 20, 1, 100, true));
    private final Setting.Bool random =
            register(new Setting.Bool("Random", true));

    private double pending;

    public AimLockCPS() {
        super("AimLockCPS", "Middle-click to lock on. Spams hits at your CPS when they're in reach.");
    }

    @Override
    protected void reset() {
        pending = 0;
    }

    @Override
    protected void hit(Minecraft mc, LocalPlayer p) {
        if (!onTarget(mc)) {
            pending = 0;
            return;
        }
        final double jitter = random.get() ? ThreadLocalRandom.current().nextDouble(-0.2, 0.2) : 0;
        final int clicks = clicksDue(cps.get(), jitter);
        for (int i = 0; i < clicks && target != null && target.isAlive(); i++)
            attackOnce(mc, p);
    }

    int clicksDue(double perSecond, double jitter) {
        pending += perSecond * (1.0 + jitter) / TICKS_PER_SECOND;
        final int n = (int) (pending + 1e-9);
        pending -= n;
        return n;
    }

    @Override
    public String getHudSuffix() {
        return cps.getInt() + " cps";
    }
}
