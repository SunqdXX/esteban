package club.sunqd.esteban.hud.element;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;

public final class Cps extends TextElement {

    private final ArrayDeque<Long> clicks = new ArrayDeque<>();

    public Cps(int x, int y) {
        super("cps", "CPS", x, y);
    }

    public void clicked(int count) {
        final long now = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
            clicks.addLast(now);
    }

    @Override
    protected String text(Minecraft mc) {
        final long now = System.currentTimeMillis();
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000)
            clicks.removeFirst();
        return clicks.size() + " CPS";
    }
}
