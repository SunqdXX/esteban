package club.sunqd.esteban.render;

import club.sunqd.esteban.module.ModuleManager;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;

public final class Hud {

    private static ModuleManager modules;
    private static boolean aimed;

    private Hud() { }

    public static void install(ModuleManager mm) {
        modules = mm;
        HudRenderCallback.EVENT.register((g, delta) -> {
            final float partial = delta.getGameTimeDeltaPartialTick(true);
            EspRenderer.render(new GfxCanvas(g, Minecraft.getInstance().font), partial);
            if (!aimed)
                modules.onFrame(partial);
            aimed = false;
        });
    }

    public static void beforeCamera() {
        final Minecraft mc = Minecraft.getInstance();
        if (modules == null || mc.player == null || mc.level == null)
            return;
        aimed = true;
        modules.onFrame(mc.getDeltaTracker().getGameTimeDeltaPartialTick(true));
    }
}
