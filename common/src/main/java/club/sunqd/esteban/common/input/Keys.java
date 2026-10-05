package club.sunqd.esteban.common.input;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

public final class Keys {

    private static final List<IntConsumer> LISTENERS = new CopyOnWriteArrayList<>();

    private Keys() { }

    public static void onPress(IntConsumer listener) {
        LISTENERS.add(listener);
    }

    public static void pressed(int key) {
        for (IntConsumer listener : LISTENERS)
            listener.accept(key);
    }
}
