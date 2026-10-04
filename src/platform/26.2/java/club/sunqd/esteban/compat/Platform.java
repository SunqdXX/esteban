package club.sunqd.esteban.compat;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

public final class Platform {

    private Platform() { }

    public static double fov(Minecraft mc, float partial) {
        return mc.gameRenderer.mainCamera().getFov();
    }

    public static void notify(Minecraft mc, Component message) {
        if (mc.player != null)
            mc.player.sendOverlayMessage(message);
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

    public static String keyId(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key).getName();
    }

    public static int keyCode(String id) {
        try {
            final InputConstants.Key k = InputConstants.getKey(id);
            return k.getType() == InputConstants.Type.KEYSYM ? k.getValue() : -1;
        } catch (RuntimeException e) {
            return -1;
        }
    }
}
