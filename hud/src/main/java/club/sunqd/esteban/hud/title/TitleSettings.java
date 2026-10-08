package club.sunqd.esteban.hud.title;

import com.google.gson.JsonObject;

public final class TitleSettings {

    public boolean enabled = true;
    public boolean motion = true;

    public void read(JsonObject o) {
        if (o.has("enabled")) enabled = o.get("enabled").getAsBoolean();
        if (o.has("motion")) motion = o.get("motion").getAsBoolean();
    }

    public JsonObject write() {
        final JsonObject o = new JsonObject();
        o.addProperty("enabled", enabled);
        o.addProperty("motion", motion);
        return o;
    }
}
