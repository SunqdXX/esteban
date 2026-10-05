package club.sunqd.esteban.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;

public final class Slots {

    private Slots() { }

    public static void swapWithOffhand(Minecraft mc, LocalPlayer p, int menuSlot) {
        mc.gameMode.handleContainerInput(p.containerMenu.containerId, menuSlot, Inventory.SLOT_OFFHAND, ContainerInput.SWAP, p);
    }

    public static int selected(LocalPlayer p) {
        return p.getInventory().getSelectedSlot();
    }
}
