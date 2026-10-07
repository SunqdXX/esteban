package club.sunqd.esteban.hud.input;

import java.util.ArrayDeque;

public final class Clicks {

    public static final Clicks LEFT = new Clicks();
    public static final Clicks RIGHT = new Clicks();

    private final ArrayDeque<Long> times = new ArrayDeque<>();

    private Clicks() { }

    public void add(int count) {
        final long now = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
            times.addLast(now);
    }

    public int perSecond() {
        final long now = System.currentTimeMillis();
        while (!times.isEmpty() && now - times.peekFirst() > 1000)
            times.removeFirst();
        return times.size();
    }
}
