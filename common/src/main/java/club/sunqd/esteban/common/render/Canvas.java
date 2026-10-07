package club.sunqd.esteban.common.render;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

public interface Canvas {

    void fill(int left, int top, int right, int bottom, int color);

    void text(String s, int x, int y, int color);

    void text(String s, int x, int y, int color, boolean shadow);

    int width(String s);

    int fontHeight();

    void push(float x, float y, float scale);

    void pop();

    void item(ItemStack stack, int x, int y);

    void itemDecorations(ItemStack stack, int x, int y);

    void effectIcon(Holder<MobEffect> effect, int x, int y, int size);
}
