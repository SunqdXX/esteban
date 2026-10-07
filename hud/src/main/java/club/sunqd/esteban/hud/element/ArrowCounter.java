package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ArrowCounter extends Element {

    private static final int PAD = 2;

    private boolean holding;
    private int count;
    private ItemStack icon = ItemStack.EMPTY;

    public ArrowCounter(int x, int y) {
        super("arrows", "Arrows", x, y);
    }

    private static boolean launcher(ItemStack s) {
        return s.getItem() instanceof BowItem || s.getItem() instanceof CrossbowItem;
    }

    @Override
    public void update(Minecraft mc) {
        final LocalPlayer p = mc.player;
        holding = p != null && (launcher(p.getMainHandItem()) || launcher(p.getOffhandItem()));
        count = 0;
        icon = ItemStack.EMPTY;
        if (!holding)
            return;
        final Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            final ItemStack s = inv.getItem(i);
            if (s.getItem() instanceof ArrowItem) {
                count += s.getCount();
                if (icon.isEmpty())
                    icon = s;
            }
        }
        if (icon.isEmpty())
            icon = new ItemStack(Items.ARROW);
    }

    public int count() {
        return count;
    }

    @Override
    public boolean visible() {
        return holding;
    }

    @Override
    public int width(Canvas c) {
        return PAD * 2 + 16 + 3 + c.width(String.valueOf(count));
    }

    @Override
    public int height(Canvas c) {
        return PAD * 2 + 16;
    }

    @Override
    protected void draw(Canvas c) {
        if (background)
            c.fill(0, 0, width(c), height(c), Palette.BACKGROUND);
        c.item(icon, PAD, PAD);
        c.text(String.valueOf(count), PAD + 19, PAD + 4, count == 0 ? Palette.RED : color, false);
    }
}
