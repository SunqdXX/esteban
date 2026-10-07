package club.sunqd.esteban.hud.mixin;

import net.minecraft.client.KeyMapping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccess {

    @Accessor("clickCount")
    int esteban$clickCount();
}
