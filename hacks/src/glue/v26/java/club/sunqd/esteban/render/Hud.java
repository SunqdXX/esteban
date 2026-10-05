package club.sunqd.esteban.render;

import club.sunqd.esteban.common.render.HudLayers;
import club.sunqd.esteban.module.ModuleManager;

public final class Hud {

    private Hud() { }

    public static void install(ModuleManager modules) {
        HudLayers.add("esteban", "esp", EspRenderer::render);
        HudLayers.add("esteban", "frame", (canvas, partial) -> modules.onFrame(partial));
    }
}
