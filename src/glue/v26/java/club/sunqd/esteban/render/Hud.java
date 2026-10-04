package club.sunqd.esteban.render;

import club.sunqd.esteban.module.ModuleManager;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class Hud {

    private Hud() { }

    public static void install(ModuleManager modules) {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("esteban", "esp"),
                (g, delta) -> EspRenderer.render(new GfxCanvas(g, Minecraft.getInstance().font),
                        delta.getGameTimeDeltaPartialTick(true)));
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("esteban", "frame"),
                (g, delta) -> modules.onFrame(delta.getGameTimeDeltaPartialTick(true)));
    }
}
