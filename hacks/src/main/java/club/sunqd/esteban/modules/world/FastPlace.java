package club.sunqd.esteban.modules.world;

import club.sunqd.esteban.mixin.MinecraftAccess;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;

public class FastPlace extends Module {

    private final Setting.Number delay =
            register(new Setting.Number("Delay", 0, 0, 3, true));

    public FastPlace() {
        super("FastPlace", "No cooldown between placing blocks or using items.", Category.WORLD);
    }

    @Override
    public void onTick() {
        final MinecraftAccess mc = (MinecraftAccess) Minecraft.getInstance();
        final int cap = cap(delay.getInt());
        if (mc.esteban$rightClickDelay() > cap)
            mc.esteban$rightClickDelay(cap);
    }

    static int cap(int delay) {
        return delay + 1;
    }

    @Override
    public String getHudSuffix() {
        return Integer.toString(delay.getInt());
    }
}
