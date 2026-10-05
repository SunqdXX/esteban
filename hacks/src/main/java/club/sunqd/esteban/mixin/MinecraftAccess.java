package club.sunqd.esteban.mixin;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccess {

    @Accessor("rightClickDelay")
    int esteban$rightClickDelay();

    @Accessor("rightClickDelay")
    void esteban$rightClickDelay(int value);
}
