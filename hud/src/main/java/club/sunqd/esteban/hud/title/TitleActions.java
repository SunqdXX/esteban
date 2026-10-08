package club.sunqd.esteban.hud.title;

import java.util.List;

public interface TitleActions {

    record Entry(String key, String label, boolean active) { }

    List<Entry> entries();

    void press(int index);

    String version();

    boolean reducedMotion();
}
