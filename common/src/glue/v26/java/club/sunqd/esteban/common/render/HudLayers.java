package club.sunqd.esteban.common.render;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class HudLayers {

    private HudLayers() { }

    public static void add(String namespace, String path, HudLayer layer) {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(namespace, path),
                (g, delta) -> layer.draw(new GfxCanvas(g, Minecraft.getInstance().font),
                        delta.getGameTimeDeltaPartialTick(true)));
    }
}
