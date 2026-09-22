package club.sunqd.esteban.modules.render;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

public class Esp extends Module {

    private final Setting.Number range =
            register(new Setting.Number("Range", 64, 8, 256, true));
    private final Setting.Bool playersOnly =
            register(new Setting.Bool("PlayersOnly", false));
    private final Setting.Bool cornersOnly =
            register(new Setting.Bool("Corners", true));

    public Esp() {
        super("ESP", "Boxes around entities through walls.", Category.RENDER);
    }

    public double range()        { return range.get(); }
    public boolean playersOnly() { return playersOnly.get(); }
    public boolean cornersOnly() { return cornersOnly.get(); }
}
