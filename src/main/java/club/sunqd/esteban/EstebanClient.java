package club.sunqd.esteban;

import club.sunqd.esteban.compat.Platform;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.ModuleManager;
import club.sunqd.esteban.modules.combat.AimLock;
import club.sunqd.esteban.modules.combat.AimLockCPS;
import club.sunqd.esteban.modules.combat.AutoTotem;
import club.sunqd.esteban.modules.combat.Hitboxes;
import club.sunqd.esteban.modules.misc.AntiKick;
import club.sunqd.esteban.modules.movement.AutoSprint;
import club.sunqd.esteban.modules.movement.Fly;
import club.sunqd.esteban.modules.movement.Jesus;
import club.sunqd.esteban.modules.movement.Jump;
import club.sunqd.esteban.modules.movement.NoFall;
import club.sunqd.esteban.modules.movement.Speed;
import club.sunqd.esteban.modules.player.GameMode;
import club.sunqd.esteban.modules.render.Esp;
import club.sunqd.esteban.modules.render.Fullbright;
import club.sunqd.esteban.modules.render.StorageEsp;
import club.sunqd.esteban.modules.render.Tracers;
import club.sunqd.esteban.modules.world.FastBreak;
import club.sunqd.esteban.modules.world.FastPlace;
import club.sunqd.esteban.render.Hud;
import club.sunqd.esteban.ui.ClickGuiModule;
import club.sunqd.esteban.util.Config;
import club.sunqd.esteban.util.KeyHooked;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EstebanClient implements ClientModInitializer {

    public static final String NAME    = "Esteban";
    public static final String VERSION = "1.3.0";

    private static EstebanClient instance;

    private final ModuleManager modules = new ModuleManager();
    private final Set<Integer> down = new HashSet<>();
    private final List<Integer> typed = new ArrayList<>();

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
                new Jesus(),
                new AutoSprint(),
                new Fullbright(),
                new Esp(),
                new Tracers(),
                new StorageEsp(),
                new AimLock(),
                new AimLockCPS(),
                new AutoTotem(),
                new Hitboxes(),
                new FastPlace(),
                new FastBreak(),
                new AntiKick(),
                new GameMode(),
                new ClickGuiModule()
        );

        Hud.install(modules);

        ClientTickEvents.START_CLIENT_TICK.register(this::onStartTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> save());
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player != null && mc.level != null)
                modules.onTickEnd();
        });

        System.out.println("[" + NAME + "] " + VERSION + " loaded with "
                + modules.getModules().size() + " module(s)");
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

    public static void keyPressed(int key) {
        final EstebanClient client = instance;
        if (client != null && Minecraft.getInstance().mouseHandler.isMouseGrabbed())
            client.typed.add(key);
    }

    private void pollBinds(Minecraft mc) {
        final List<Integer> pressed;
        if (mc.keyboardHandler instanceof KeyHooked) {
            pressed = new ArrayList<>(typed);
            typed.clear();
        } else {
            pressed = polled(mc);
        }
        for (int key : pressed)
            press(mc, key);
    }

    private List<Integer> polled(Minecraft mc) {
        final Set<Integer> keys = new HashSet<>();
        if (modules.getMasterKey() != Module.NO_KEY)
            keys.add(modules.getMasterKey());
        for (Module m : modules.getModules())
            if (m.getKey() != Module.NO_KEY)
                keys.add(m.getKey());

        final List<Integer> pressed = new ArrayList<>();
        for (int key : keys) {
            boolean isDown;
            try {
                isDown = Platform.isKeyDown(mc, key);
            } catch (Throwable t) {
                continue;
            }
            if (!isDown)
                down.remove(key);
            else if (down.add(key))
                pressed.add(key);
        }
        down.retainAll(keys);
        if (!mc.mouseHandler.isMouseGrabbed())
            pressed.clear();
        return pressed;
    }

    private void press(Minecraft mc, int key) {
        if (key == Module.NO_KEY)
            return;
        if (key == modules.getMasterKey()) {
            modules.setArmed(!modules.isArmed());
            notify(mc, modules.isArmed() ? "§aCHEATS ON" : "§cCHEATS OFF");
        }
        for (Module m : modules.getModules()) {
            if (m.getKey() != key)
                continue;
            if (m.isMenu()) {
                m.toggle();
            } else if (!modules.isArmed()) {
                notify(mc, "§cCHEATS OFF§7, arm it in the menu first");
            } else if (!m.isEnabled() && !m.canEnable()) {
                notify(mc, m.getName() + " §7cant turn on right now");
            } else {
                m.toggle();
                notify(mc, m.getName() + (m.isEnabled() ? " §aON" : " §cOFF"));
            }
        }
    }

    private void notify(Minecraft mc, String text) {
        try {
            Platform.notify(mc, Component.literal("§b" + NAME + "§r " + text));
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
