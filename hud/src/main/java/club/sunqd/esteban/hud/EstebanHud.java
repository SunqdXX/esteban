package club.sunqd.esteban.hud;

import club.sunqd.esteban.common.render.HudLayers;
import club.sunqd.esteban.hud.element.Cps;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.loader.api.FabricLoader;

public class EstebanHud implements ClientModInitializer {

    public static final String NAME    = "Esteban HUD";
    public static final String VERSION = "1.4.0";

    private static EstebanHud instance;

    private final Hud hud = new Hud();
    private HudConfig config;

    public static EstebanHud get() {
        return instance;
    }

    public Hud hud() {
        return hud;
    }

    public HudConfig config() {
        return config;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        config = new HudConfig(FabricLoader.getInstance().getConfigDir().resolve("esteban-hud.json"));
        if (!config.load(hud))
            config.save(hud);
        HudLayers.add("esteban-hud", "elements", hud::render);
        final Cps cps = (Cps) hud.get("cps");
        ClientPreAttackCallback.EVENT.register((mc, player, clicks) -> {
            if (clicks > 0)
                cps.clicked(clicks);
            return false;
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> config.save(hud));
        System.out.println("[" + NAME + "] " + VERSION + " loaded with " + hud.elements().size() + " element(s)");
    }
}
