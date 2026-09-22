package club.sunqd.esteban.ui;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;

public class ClickGuiModule extends Module {

    public ClickGuiModule() {
        super("ClickGUI", "Opens the Esteban menu.", Category.MISC,
              InputConstants.KEY_BACKSPACE);
        setHidden(true);
    }

    @Override
    public void onEnable() {
        Minecraft.getInstance().setScreenAndShow(new ClickGuiScreen());
        setEnabledSilently(false);
    }
}
