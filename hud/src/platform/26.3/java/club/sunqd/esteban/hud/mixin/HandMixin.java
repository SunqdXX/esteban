package club.sunqd.esteban.hud.mixin;

import club.sunqd.esteban.hud.world.Lowered;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class HandMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void esteban$lowerShield(PlayerRenderState player, FirstPersonHandsAndItemsRenderState state, float partial, float pitch, InteractionHand hand, float swing, ItemStack stack, float equip, PoseStack pose, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        pose.pushPose();
        pose.translate(0f, -Lowered.shield(stack), 0f);
    }

    @Inject(method = "submitArmWithItem", at = @At("RETURN"))
    private void esteban$restoreShield(PlayerRenderState player, FirstPersonHandsAndItemsRenderState state, float partial, float pitch, InteractionHand hand, float swing, ItemStack stack, float equip, PoseStack pose, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        pose.popPose();
    }
}
