package club.sunqd.esteban.common.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;

public final class HudLayers {

    private HudLayers() { }

    public static void add(String namespace, String path, HudLayer layer) {
        HudRenderCallback.EVENT.register((g, delta) -> layer.draw(new GfxCanvas(g, Minecraft.getInstance().font),
                delta.getGameTimeDeltaPartialTick(true)));
    }
}
