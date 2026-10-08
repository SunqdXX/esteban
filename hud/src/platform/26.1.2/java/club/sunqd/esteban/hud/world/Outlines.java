package club.sunqd.esteban.hud.world;

import club.sunqd.esteban.hud.element.BlockOutline;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public final class Outlines {

    private Outlines() { }

    public static boolean supported() {
        return true;
    }

    public static void register(BlockOutline outline) {
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, state) -> {
            if (!outline.enabled)
                return true;
            final Vec3 cam = ctx.levelState().cameraRenderState.pos;
            final BlockPos pos = state.pos();
            final float width = ctx.gameRenderer().getGameRenderState().windowRenderState.appropriateLineWidth * outline.scale;
            ShapeRenderer.renderShape(ctx.poseStack(), ctx.bufferSource().getBuffer(RenderTypes.lines()), state.shape(),
                    pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z, outline.color, width);
            ctx.bufferSource().endLastBatch();
            return false;
        });
    }
}
