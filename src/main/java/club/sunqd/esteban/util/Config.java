package club.sunqd.esteban.util;

import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.ModuleManager;
import club.sunqd.esteban.module.Setting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Config {

    private final Path file;
    private final ModuleManager manager;

    public Config(Path file, ModuleManager manager) {
        this.file = file;
        this.manager = manager;
    }

    public void save() {
        List<String> lines = new ArrayList<>();
        for (Module m : manager.getModules()) {
            String n = m.getName();
            for (Setting s : m.getSettings()) {
                lines.add(n + ".s." + s.getName() + "=" + s.serialize());
            }
        }
        try {
            Path parent = file.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[Esteban] could not save config: " + e);
        }
    }

    public void load() {
        if (!Files.exists(file)) return;
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[Esteban] could not read config: " + e);
            return;
        }

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

            int eq = trimmed.indexOf('=');
            if (eq <= 0) continue;
            String key = trimmed.substring(0, eq);
            String val = trimmed.substring(eq + 1);

            int firstDot = key.indexOf('.');
            if (firstDot <= 0) continue;
            String moduleName = key.substring(0, firstDot);
            String rest = key.substring(firstDot + 1);

            Module m = manager.get(moduleName);
            if (m == null) continue;

            if (rest.startsWith("s.")) {
                Setting s = m.getSetting(rest.substring(2));
                if (s != null) s.deserialize(val);
            }
        }
    }
}
