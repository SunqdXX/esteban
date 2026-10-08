package club.sunqd.esteban.hud.mixin;

import club.sunqd.esteban.hud.combat.Combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(method = "handleDamageEvent", at = @At("TAIL"))
    private void esteban$damage(ClientboundDamageEventPacket packet, CallbackInfo ci) {
        Combat.damage(packet.entityId(), packet.sourceCauseId());
    }

    @Inject(method = "handleEntityEvent", at = @At("TAIL"))
    private void esteban$entityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level != null)
            Combat.event(packet.getEntity(mc.level), packet.getEventId());
    }
}
