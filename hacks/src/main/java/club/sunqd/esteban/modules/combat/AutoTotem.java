package club.sunqd.esteban.modules.combat;

import club.sunqd.esteban.compat.Slots;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoTotem extends Module {

    private final Setting.Number delay =
            register(new Setting.Number("Delay", 0, 0, 10, true));

    private int wait;
    private int totems = -1;

    public AutoTotem() {
        super("AutoTotem", "Keeps a totem in your offhand and refills it the tick one pops.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        wait = 0;
    }

    @Override
    public void onDisable() {
        totems = -1;
    }

    @Override
    public void onTick() {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer p = mc.player;
        if (p == null || mc.gameMode == null)
            return;
        totems = count(p);
        if (wait > 0) {
            wait--;
            return;
        }
        if (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING))
            return;
        if (p.isSpectator() || p.getAbilities().instabuild || p.isDeadOrDying())
            return;
        final AbstractContainerMenu menu = p.containerMenu;
        if (menu == null || !menu.getCarried().isEmpty())
            return;
        final int slot = find(menu, p.getInventory());
        if (slot < 0)
            return;
        Slots.swapWithOffhand(mc, p, slot);
        wait = delay.getInt();
    }

    private static int find(AbstractContainerMenu menu, Inventory inv) {
        int hotbar = -1;
        for (int i = 0; i < menu.slots.size(); i++) {
            final Slot s = menu.slots.get(i);
            if (s.container != inv || s.getContainerSlot() >= Inventory.INVENTORY_SIZE)
                continue;
            if (!s.getItem().is(Items.TOTEM_OF_UNDYING))
                continue;
            if (s.getContainerSlot() >= 9)
                return i;
            if (hotbar < 0)
                hotbar = i;
        }
        return hotbar;
    }

    private static int count(LocalPlayer p) {
        final Inventory inv = p.getInventory();
        int n = p.getOffhandItem().is(Items.TOTEM_OF_UNDYING) ? p.getOffhandItem().getCount() : 0;
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            final ItemStack s = inv.getItem(i);
            if (s.is(Items.TOTEM_OF_UNDYING))
                n += s.getCount();
        }
        return n;
    }

    @Override
    public String getHudSuffix() {
        return totems < 0 ? null : Integer.toString(totems);
    }
}
