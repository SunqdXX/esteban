package club.sunqd.esteban.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final Map<String, Module> byName = new LinkedHashMap<>();

    private boolean armed;

    public boolean isArmed() { return armed; }

    public void setArmed(boolean value) {
        if (this.armed == value) return;
        this.armed = value;
        if (!value) {
            for (Module m : modules) {
                if (m.isEnabled()) {
                    try {
                        m.setEnabled(false);
                    } catch (Throwable t) {
                        System.err.println("[Esteban] " + m.getName() + " threw while disarming: " + t);
                    }
                }
            }
        }
    }

    public void register(Module... mods) {
        for (Module m : mods) {
            if (byName.containsKey(m.getName().toLowerCase())) {
                throw new IllegalStateException("Duplicate module: " + m.getName());
            }
            modules.add(m);
            byName.put(m.getName().toLowerCase(), m);
        }
    }

    public List<Module> getModules() { return modules; }

    public Module get(String name) { return byName.get(name.toLowerCase()); }

    public List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : modules) if (m.getCategory() == c) out.add(m);
        out.sort(Comparator.comparing(Module::getName));
        return out;
    }

    public List<Module> getActiveForHud() {
        List<Module> out = new ArrayList<>();
        for (Module m : modules) if (m.isEnabled() && !m.isHidden()) out.add(m);
        return out;
    }

    public void onTick() {
        if (!armed) return;

        for (Module m : modules) {
            try {
                m.onTickAlways();
            } catch (Throwable t) {
                System.err.println("[Esteban] " + m.getName() + " threw in onTickAlways: " + t);
            }
        }
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            try {
                m.onTick();
            } catch (Throwable t) {
                System.err.println("[Esteban] " + m.getName() + " threw in onTick, disabling: " + t);
                t.printStackTrace();
                m.setEnabled(false);
            }
        }
    }

    public void onTickEnd() {
        if (!armed) return;
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            try {
                m.onTickEnd();
            } catch (Throwable t) {
                System.err.println("[Esteban] " + m.getName() + " threw in onTickEnd: " + t);
            }
        }
    }

    public void onFrame(float partial) {
        if (!armed) return;
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            try {
                m.onFrame(partial);
            } catch (Throwable t) {
                System.err.println("[Esteban] " + m.getName() + " threw in onFrame: " + t);
            }
        }
    }

    public boolean onKey(int key) {
        if (key == Module.NO_KEY) return false;
        boolean hit = false;
        for (Module m : modules) {
            if (m.getKey() == key) { m.toggle(); hit = true; }
        }
        return hit;
    }
}
