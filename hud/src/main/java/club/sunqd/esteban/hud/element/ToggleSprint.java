package club.sunqd.esteban.hud.element;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;

public final class ToggleSprint extends TextElement {

    private Boolean original;
    private boolean applied;

    public ToggleSprint(int x, int y) {
        super("togglesprint", "Toggle Sprint", x, y);
        enabled = false;
    }

    @Override
    public void update(Minecraft mc) {
        if (!applied) {
            if (original == null)
                original = mc.options.toggleSprint().get();
            mc.options.toggleSprint().set(true);
            applied = true;
        }
        super.update(mc);
    }

    @Override
    public void inactive(Minecraft mc) {
        if (!applied)
            return;
        mc.options.toggleSprint().set(original != null && original);
        original = null;
        applied = false;
    }

    @Override
    public void read(JsonObject o) {
        if (o.has("restore"))
            original = o.get("restore").getAsBoolean();
    }

    @Override
    public void write(JsonObject o) {
        if (original != null)
            o.addProperty("restore", original);
    }

    @Override
    protected String text(Minecraft mc) {
        final boolean key = mc.options.keySprint.isDown();
        if (key && mc.options.toggleSprint().get())
            return "Sprint toggled";
        if (key)
            return "Sprint held";
        if (mc.player != null && mc.player.isSprinting())
            return "Sprinting";
        return "";
    }

    @Override
    public boolean visible() {
        return !current().isEmpty();
    }
}
