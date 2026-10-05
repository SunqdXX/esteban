package club.sunqd.esteban.modules.render;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

public class StorageEsp extends Module {

    private final Setting.Number range =
            register(new Setting.Number("Range", 64, 8, 256, true));
    private final Setting.Bool chests =
            register(new Setting.Bool("Chests", true));
    private final Setting.Bool shulkers =
            register(new Setting.Bool("Shulkers", true));
    private final Setting.Bool barrels =
            register(new Setting.Bool("Barrels", true));
    private final Setting.Bool droppers =
            register(new Setting.Bool("Droppers", true));
    private final Setting.Bool spawners =
            register(new Setting.Bool("Spawners", true));
    private final Setting.Bool names =
            register(new Setting.Bool("Names", true));
    private final Setting.Bool tracers =
            register(new Setting.Bool("Tracers", false));

    public StorageEsp() {
        super("StorageESP", "Boxes + nametags on every stash, through walls.", Category.RENDER);
    }

    public double range()     { return range.get(); }
    public boolean chests()   { return chests.get(); }
    public boolean shulkers() { return shulkers.get(); }
    public boolean barrels()  { return barrels.get(); }
    public boolean droppers() { return droppers.get(); }
    public boolean spawners() { return spawners.get(); }
    public boolean names()    { return names.get(); }
    public boolean tracers()  { return tracers.get(); }
}
