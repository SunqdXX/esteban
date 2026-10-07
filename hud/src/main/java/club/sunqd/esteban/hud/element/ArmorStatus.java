package club.sunqd.esteban.hud.element;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.Palette;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ArmorStatus extends Element {

    private static final int PAD = 2;
    private static final int ROW = 18;
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
    };

    private final List<ItemStack> stacks = new ArrayList<>();
    private final List<String> labels = new ArrayList<>();
    private final List<Boolean> low = new ArrayList<>();

    public ArmorStatus(int x, int y) {
        super("armor", "Armor", x, y);
    }

    @Override
    public void update(Minecraft mc) {
        stacks.clear();
        labels.clear();
        low.clear();
        final LocalPlayer p = mc.player;
        if (p == null)
            return;
        for (EquipmentSlot slot : SLOTS) {
            final ItemStack stack = p.getItemBySlot(slot);
            if (stack.isEmpty() || (slot == EquipmentSlot.MAINHAND && !stack.isDamageableItem()))
                continue;
            stacks.add(stack);
            if (stack.isDamageableItem()) {
                final int left = stack.getMaxDamage() - stack.getDamageValue();
                labels.add(String.valueOf(left));
                low.add(left * 4 < stack.getMaxDamage());
            } else {
                labels.add(stack.getCount() > 1 ? String.valueOf(stack.getCount()) : "");
                low.add(false);
            }
        }
    }

    public List<String> lines() {
        return List.copyOf(labels);
    }

    @Override
    public boolean visible() {
        return !stacks.isEmpty();
    }

    @Override
    public int width(Canvas c) {
        int text = 0;
        for (String s : labels)
            text = Math.max(text, c.width(s));
        return PAD * 2 + 16 + (text > 0 ? 3 + text : 0);
    }

    @Override
    public int height(Canvas c) {
        return PAD * 2 + Math.max(1, stacks.size()) * ROW - 2;
    }

    @Override
    protected void draw(Canvas c) {
        if (background)
            c.fill(0, 0, width(c), height(c), Palette.BACKGROUND);
        for (int i = 0; i < stacks.size(); i++) {
            final int y = PAD + i * ROW;
            c.item(stacks.get(i), PAD, y);
            c.itemDecorations(stacks.get(i), PAD, y);
            c.text(labels.get(i), PAD + 19, y + 4, low.get(i) ? Palette.RED : color, false);
        }
    }
}
