package club.sunqd.esteban.module;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    public static final int NO_KEY = -1;

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting> settings = new ArrayList<>();

    private int key;
    private boolean enabled;
    private boolean hidden;

    protected Module(String name, String description, Category category) {
        this(name, description, category, NO_KEY);
    }

    protected Module(String name, String description, Category category, int key) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.key = key;
    }

    protected <T extends Setting> T register(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting> getSettings() { return settings; }

    public Setting getSetting(String n) {
        for (Setting s : settings) {
            if (s.getName().equalsIgnoreCase(n)) return s;
        }
        return null;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public Category getCategory()  { return category; }
    public int getKey()            { return key; }
    public void setKey(int k)      { this.key = k; }
    public boolean isEnabled()     { return enabled; }
    public boolean isHidden()      { return hidden; }
    protected void setHidden(boolean h) { this.hidden = h; }

    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (this.enabled == value) return;
        this.enabled = value;
        try {
            if (value) onEnable(); else onDisable();
        } catch (Throwable t) {
            this.enabled = false;
            System.err.println("[Esteban] " + name + " threw on toggle: " + t);
            t.printStackTrace();
        }
    }

    public void setEnabledSilently(boolean value) { this.enabled = value; }

    public String getHudSuffix() { return null; }

    public void onEnable()  { }
    public void onDisable() { }
    public void onTick()    { }
    public void onTickAlways() { }

    public void onTickEnd() { }

    public void onFrame(float partial) { }

    @Override public String toString() { return name; }
}
