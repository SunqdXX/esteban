package club.sunqd.esteban.common.compat;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class Platform {

    private Platform() { }

    public static void notify(Minecraft mc, Component message) {
        if (mc.player != null)
            mc.player.sendOverlayMessage(message);
    }

    public static boolean isKeyDown(Minecraft mc, int key) {
        return InputConstants.isKeyDown(key);
    }

    public static String keyName(int key) {
        return InputConstants.Type.KEYBOARD.getOrCreate(key).getDisplayName().getString();
    }

    public static String keyId(int key) {
        return InputConstants.Type.KEYBOARD.getOrCreate(key).getName();
    }

    public static int keyCode(String id) {
        try {
            final InputConstants.Key k = InputConstants.getKey(id);
            return k.getType() == InputConstants.Type.KEYBOARD ? k.getValue() : -1;
        } catch (RuntimeException e) {
            return -1;
        }
    }
}
