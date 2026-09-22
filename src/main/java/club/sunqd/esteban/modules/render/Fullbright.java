package club.sunqd.esteban.modules.render;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

import java.lang.reflect.Field;

public class Fullbright extends Module {

    private static final double BRIGHT = 16.0;

    private static Field valueField;

    private Double previous;

    public Fullbright() {
        super("Fullbright", "See in the dark.", Category.RENDER);
    }

    private static Field field() throws NoSuchFieldException {
        if (valueField == null) {
            valueField = OptionInstance.class.getDeclaredField("value");
            valueField.setAccessible(true);
        }
        return valueField;
    }

    private static void write(OptionInstance<Double> opt, double v) {
        try {
            field().set(opt, v);
        } catch (Throwable t) {
            opt.set(v);
        }
    }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;
        if (previous == null) previous = mc.options.gamma().get();
        write(mc.options.gamma(), BRIGHT);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null && mc.options.gamma().get() < BRIGHT)
            write(mc.options.gamma(), BRIGHT);
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null || previous == null) return;
        write(mc.options.gamma(), previous);
        previous = null;
    }
}
