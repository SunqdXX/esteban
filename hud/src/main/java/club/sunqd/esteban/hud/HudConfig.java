package club.sunqd.esteban.hud;

import club.sunqd.esteban.hud.element.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public final class HudConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;

    public HudConfig(Path file) {
        this.file = file;
    }

    public Path file() {
        return file;
    }

    public boolean load(Hud hud) {
        if (!Files.isRegularFile(file))
            return false;
        try {
            final JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            final JsonObject elements = root.has("elements") ? root.getAsJsonObject("elements") : new JsonObject();
            for (Element e : hud.elements()) {
                if (!elements.has(e.id()))
                    continue;
                final JsonObject o = elements.getAsJsonObject(e.id());
                if (o.has("enabled")) e.enabled = o.get("enabled").getAsBoolean();
                if (o.has("x")) e.x = o.get("x").getAsInt();
                if (o.has("y")) e.y = o.get("y").getAsInt();
                if (o.has("scale")) e.scale = Math.max(0.5f, Math.min(3f, o.get("scale").getAsFloat()));
                if (o.has("background")) e.background = o.get("background").getAsBoolean();
                if (o.has("color")) e.color = color(o.get("color"), e.color);
            }
            return true;
        } catch (RuntimeException | IOException ex) {
            System.err.println("[" + EstebanHud.NAME + "] could not read " + file + ": " + ex);
            return false;
        }
    }

    public void save(Hud hud) {
        final JsonObject elements = new JsonObject();
        for (Element e : hud.elements()) {
            final JsonObject o = new JsonObject();
            o.addProperty("enabled", e.enabled);
            o.addProperty("x", e.x);
            o.addProperty("y", e.y);
            o.addProperty("scale", e.scale);
            o.addProperty("background", e.background);
            o.addProperty("color", String.format(Locale.ROOT, "#%06X", e.color & 0xFFFFFF));
            elements.add(e.id(), o);
        }
        final JsonObject root = new JsonObject();
        root.add("elements", elements);
        try {
            Files.createDirectories(file.getParent());
            final Path part = file.resolveSibling(file.getFileName() + ".part");
            Files.writeString(part, GSON.toJson(root));
            Files.move(part, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            System.err.println("[" + EstebanHud.NAME + "] could not save " + file + ": " + ex);
        }
    }

    private static int color(JsonElement value, int fallback) {
        try {
            final String s = value.getAsString().trim();
            if (s.matches("#[0-9A-Fa-f]{6}"))
                return 0xFF000000 | Integer.parseInt(s.substring(1), 16);
        } catch (RuntimeException ignored) {
        }
        return fallback;
    }
}
