package club.sunqd.esteban.modules.render;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

public class Tracers extends Module {

    private final Setting.Number range =
            register(new Setting.Number("Range", 64, 8, 256, true));

    public Tracers() {
        super("Tracers", "Draws a line from you to each entity.", Category.RENDER);
    }

    public double range() { return range.get(); }
}
