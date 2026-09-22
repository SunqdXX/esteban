package club.sunqd.esteban.compat;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

public final class Platform {

    private Platform() { }

    public static Camera camera(Minecraft mc) {
        return mc.gameRenderer.getMainCamera();
    }

    public static void notify(Minecraft mc, Component message) {
        mc.gui.getChat().addClientSystemMessage(message);
    }

    public static boolean isKeyDown(Minecraft mc, int key) {
        return InputConstants.isKeyDown(mc.getWindow(), key);
    }

    public static void swing(LocalPlayer p) {
        p.swing(InteractionHand.MAIN_HAND);
    }

    public static String keyName(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
    }
}
