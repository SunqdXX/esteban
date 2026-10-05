package club.sunqd.esteban.modules.world;

import club.sunqd.esteban.mixin.GameModeAccess;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FastBreak extends Module {

    static final float BREAK_AT = 0.7f;

    private final Setting.Bool boost =
            register(new Setting.Bool("Boost", false));

    public FastBreak() {
        super("FastBreak", "No cooldown between breaking blocks. Boost ends each block at 70%.", Category.WORLD);
    }

    @Override
    public void onTick() {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer p = mc.player;
        if (p == null || mc.level == null || !(mc.gameMode instanceof GameModeAccess gm))
            return;
        if (gm.esteban$destroyDelay() > 0)
            gm.esteban$destroyDelay(0);

        if (!boost.get() || !mc.gameMode.isDestroying() || p.getAbilities().instabuild)
            return;
        final BlockPos pos = gm.esteban$destroyBlockPos();
        if (pos == null || !(mc.hitResult instanceof BlockHitResult hit) || !hit.getBlockPos().equals(pos))
            return;
        final BlockState state = mc.level.getBlockState(pos);
        if (state.isAir())
            return;
        if (finishNow(gm.esteban$destroyProgress(), state.getDestroyProgress(p, mc.level, pos)))
            gm.esteban$destroyProgress(1.0f);
    }

    static boolean finishNow(float done, float step) {
        return step > 0 && done + step >= BREAK_AT && done + step < 1.0f;
    }

    @Override
    public String getHudSuffix() {
        return boost.get() ? "Boost" : null;
    }
}
