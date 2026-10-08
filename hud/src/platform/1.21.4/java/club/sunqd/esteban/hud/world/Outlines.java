package club.sunqd.esteban.hud.world;

import club.sunqd.esteban.hud.element.BlockOutline;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

public final class Outlines {

    private static final Map<Integer, RenderType> TYPES = new HashMap<>();

    private Outlines() { }

    public static boolean supported() {
        return true;
    }

    public static void register(BlockOutline outline) {
        WorldRenderEvents.BLOCK_OUTLINE.register((ctx, block) -> {
            if (!outline.enabled || ctx.matrixStack() == null || ctx.consumers() == null || ctx.world() == null)
                return true;
            final BlockPos pos = block.blockPos();
            final float vanilla = Math.max(2.5f, Minecraft.getInstance().getWindow().getWidth() / 1920f * 2.5f);
            ShapeRenderer.renderShape(ctx.matrixStack(), ctx.consumers().getBuffer(lines(vanilla * outline.scale)),
                    block.blockState().getShape(ctx.world(), pos, CollisionContext.of(block.entity())),
                    pos.getX() - block.cameraX(), pos.getY() - block.cameraY(), pos.getZ() - block.cameraZ(), outline.color);
            return false;
        });
    }

    private static RenderType lines(float width) {
        return TYPES.computeIfAbsent(Math.round(width * 4), k -> RenderType.create("esteban_outline_" + k,
                DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 1536,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                        .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(k / 4.0)))
                        .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false)));
    }
}
