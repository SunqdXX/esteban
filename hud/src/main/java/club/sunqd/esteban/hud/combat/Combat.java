package club.sunqd.esteban.hud.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Combat {

    public static final long COMBO_TIMEOUT = 3000;
    public static final long POP_TIMEOUT = 120_000;
    public static final int MAX_ROWS = 5;

    public record Pops(String name, int count, long last) { }

    private static Object level;
    private static int combo;
    private static long lastHit;
    private static final Map<String, Pops> pops = new LinkedHashMap<>();

    private Combat() { }

    private static void sync() {
        final Object now = Minecraft.getInstance().level;
        if (now != level) {
            level = now;
            combo = 0;
            pops.clear();
        }
    }

    public static void damage(int target, int cause) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        sync();
        final int self = mc.player.getId();
        if (target == self) {
            combo = 0;
        } else if (cause == self) {
            combo++;
            lastHit = System.currentTimeMillis();
        }
    }

    public static void event(Entity entity, byte id) {
        if (!(entity instanceof Player player))
            return;
        sync();
        final String name = player.getName().getString();
        if (id == EntityEvent.PROTECTED_FROM_DEATH) {
            final Pops was = pops.remove(name);
            pops.put(name, new Pops(name, was == null ? 1 : was.count() + 1, System.currentTimeMillis()));
        } else if (id == EntityEvent.DEATH) {
            pops.remove(name);
        }
    }

    public static int combo() {
        sync();
        if (combo > 0 && System.currentTimeMillis() - lastHit > COMBO_TIMEOUT)
            combo = 0;
        return combo;
    }

    public static List<Pops> pops() {
        sync();
        final long now = System.currentTimeMillis();
        final Iterator<Pops> it = pops.values().iterator();
        while (it.hasNext())
            if (now - it.next().last() > POP_TIMEOUT)
                it.remove();
        final List<Pops> out = new ArrayList<>(pops.values());
        Collections.reverse(out);
        return out.size() > MAX_ROWS ? out.subList(0, MAX_ROWS) : out;
    }
}
