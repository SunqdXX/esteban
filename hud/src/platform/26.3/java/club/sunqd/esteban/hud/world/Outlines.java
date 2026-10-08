package club.sunqd.esteban.hud.world;

import club.sunqd.esteban.hud.element.BlockOutline;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public final class Outlines {

    private Outlines() { }

    public static boolean supported() {
        return true;
    }

    private static RenderType type(LevelRenderContext ctx) {
        return ctx.gameRenderer().useImprovedTransparency() ? RenderTypes.linesTranslucentNoDepthWrite() : RenderTypes.linesTranslucent();
    }

    public static void register(BlockOutline outline) {
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, state) -> {
            if (!outline.enabled)
                return true;
            final Vec3 cam = ctx.levelState().cameraRenderState.pos;
            final BlockPos pos = state.pos();
            final float width = ctx.gameRenderer().gameRenderState().windowRenderState.appropriateLineWidth * outline.scale;
            final PoseStack pose = ctx.poseStack();
            pose.pushPose();
            pose.translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
            ctx.submitNodeCollector().submitShapeOutline(pose, state.shape(), type(ctx), outline.color, width, state.isTranslucent());
            pose.popPose();
            return false;
        });
    }
}
