package club.sunqd.esteban;

import club.sunqd.esteban.compat.Platform;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.ModuleManager;
import club.sunqd.esteban.modules.combat.AimLock;
import club.sunqd.esteban.modules.combat.AimLockCPS;
import club.sunqd.esteban.modules.movement.AutoSprint;
import club.sunqd.esteban.modules.movement.Fly;
import club.sunqd.esteban.modules.movement.Jump;
import club.sunqd.esteban.modules.movement.NoFall;
import club.sunqd.esteban.modules.movement.Speed;
import club.sunqd.esteban.modules.player.GameMode;
import club.sunqd.esteban.modules.render.Esp;
import club.sunqd.esteban.modules.render.Fullbright;
import club.sunqd.esteban.modules.render.StorageEsp;
import club.sunqd.esteban.modules.render.Tracers;
import club.sunqd.esteban.render.EspRenderer;
import club.sunqd.esteban.ui.ClickGuiModule;
import club.sunqd.esteban.util.Config;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class EstebanClient implements ClientModInitializer {

    public static final String NAME    = "Esteban";
    public static final String VERSION = "1.2.1";

    private static EstebanClient instance;

    private final ModuleManager modules = new ModuleManager();
    private final Set<Integer> down = new HashSet<>();

    private Config config;
    private boolean loaded;

    public static EstebanClient get()          { return instance; }
    public ModuleManager getModuleManager()   { return modules; }

    @Override
    public void onInitializeClient() {
        instance = this;

        modules.register(
                new Fly(),
                new Speed(),
                new Jump(),
                new NoFall(),
                new AutoSprint(),
                new Fullbright(),
                new Esp(),
                new Tracers(),
                new StorageEsp(),
                new AimLock(),
                new AimLockCPS(),
                new GameMode(),
                new ClickGuiModule()
        );

        EspRenderer.install();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("esteban", "frame"),
                (g, delta) -> modules.onFrame(delta.getGameTimeDeltaPartialTick(true)));

        ClientTickEvents.START_CLIENT_TICK.register(this::onStartTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> save());
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player != null && mc.level != null)
                modules.onTickEnd();
        });

        System.out.println("[" + NAME + "] " + VERSION + " loaded with "
                + modules.getModules().size() + " module(s) on MC 26.x");
    }

    private void onStartTick(Minecraft mc) {
        if (mc == null) return;

        if (!loaded) {
            loaded = true;
            try {
                Path dir = mc.gameDirectory.toPath().resolve("esteban");
                java.nio.file.Files.createDirectories(dir);
                config = new Config(dir.resolve("esteban.txt"), modules);
                config.load();
            } catch (Throwable t) {
                System.err.println("[" + NAME + "] config load failed: " + t);
            }
        }

        pollBinds(mc);

        if (mc.player != null && mc.level != null) {
            modules.onTick();
        }
    }

    private void pollBinds(Minecraft mc) {
        boolean blocked = !mc.mouseHandler.isMouseGrabbed();

        for (Module m : modules.getModules()) {
            int key = m.getKey();
            if (key == Module.NO_KEY) continue;

            boolean isDown;
            try {
                isDown = Platform.isKeyDown(mc, key);
            } catch (Throwable t) {
                continue;
            }

            if (isDown) {
                if (down.add(key) && !blocked) {
                    m.toggle();
                    notify(mc, m);
                    save();
                }
            } else {
                down.remove(key);
            }
        }
    }

    private void notify(Minecraft mc, Module m) {
        try {
            String state = m.isEnabled() ? "§aON" : "§cOFF";
            Platform.notify(mc,
                    Component.literal("§b[" + NAME + "]§r " + m.getName() + " " + state));
        } catch (Throwable ignored) {
        }
    }

    public void save() {
        if (config == null) return;
        try {
            config.save();
        } catch (Throwable t) {
            System.err.println("[" + NAME + "] config save failed: " + t);
        }
    }
}
