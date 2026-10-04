package club.sunqd.esteban.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiPlayerGameMode.class)
public interface GameModeAccess {

    @Accessor("destroyDelay")
    int esteban$destroyDelay();

    @Accessor("destroyDelay")
    void esteban$destroyDelay(int value);

    @Accessor("destroyProgress")
    float esteban$destroyProgress();

    @Accessor("destroyProgress")
    void esteban$destroyProgress(float value);

    @Accessor("destroyBlockPos")
    BlockPos esteban$destroyBlockPos();
}
