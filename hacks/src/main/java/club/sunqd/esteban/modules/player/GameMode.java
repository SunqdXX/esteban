package club.sunqd.esteban.modules.player;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class GameMode extends Module {

    private final Setting.Mode mode =
            register(new Setting.Mode("Mode", "creative",
                    "creative", "survival", "spectator", "adventure"));

    public GameMode() {
        super("GameMode", "Sends /gamemode (needs op).", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || p.connection == null) return;
        p.connection.sendCommand("gamemode " + mode.get());
        setEnabledSilently(false);
    }
}
