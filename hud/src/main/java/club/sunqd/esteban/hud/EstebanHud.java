package club.sunqd.esteban.hud;

import net.fabricmc.api.ClientModInitializer;

public class EstebanHud implements ClientModInitializer {

    public static final String NAME    = "Esteban HUD";
    public static final String VERSION = "1.4.0";

    @Override
    public void onInitializeClient() {
        System.out.println("[" + NAME + "] " + VERSION + " loaded");
    }
}
