package club.sunqd.esteban.hud;

import club.sunqd.esteban.common.compat.Platform;
import club.sunqd.esteban.common.input.Keys;
import club.sunqd.esteban.common.render.HudLayers;
import club.sunqd.esteban.common.screen.CanvasScreen;
import club.sunqd.esteban.hud.editor.HudEditor;
import club.sunqd.esteban.hud.input.Clicks;
import club.sunqd.esteban.hud.title.Backgrounds;
import club.sunqd.esteban.hud.title.Titles;
import club.sunqd.esteban.hud.world.Outlines;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.io.IOException;
import java.nio.file.Files;

public class EstebanHud implements ClientModInitializer {

    public static final String NAME    = "Esteban HUD";
    public static final String VERSION = "1.4.0";

    private static EstebanHud instance;

    private final Hud hud = new Hud();
    private HudConfig config;
    private volatile boolean openEditor;

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
        Outlines.register(hud.outline());
        ClientPreAttackCallback.EVENT.register((mc, player, clicks) -> {
            if (clicks > 0)
                Clicks.LEFT.add(clicks);
            return false;
        });
        Keys.onPress(key -> {
            final Minecraft mc = Minecraft.getInstance();
            if (key == InputConstants.KEY_RSHIFT && mc.player != null && Platform.screen(mc) == null)
                openEditor = true;
        });
        try {
            Files.createDirectories(Backgrounds.customFolder());
        } catch (IOException e) {
            System.err.println("[" + NAME + "] could not create " + Backgrounds.customFolder() + ": " + e);
        }
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            final Screen screen = Platform.screen(mc);
            if (Titles.vanilla(screen))
                Platform.setScreen(mc, Titles.swap(screen));
        });
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (!openEditor)
                return;
            openEditor = false;
            if (mc.player == null || Platform.screen(mc) != null)
                return;
            hud.editing(true);
            new CanvasScreen("HUD editor", new HudEditor(hud, config), false).show();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> config.save(hud));
        System.out.println("[" + NAME + "] " + VERSION + " loaded with " + hud.elements().size() + " element(s)");
    }
}
