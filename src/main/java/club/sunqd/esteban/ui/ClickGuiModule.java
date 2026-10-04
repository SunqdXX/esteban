package club.sunqd.esteban.ui;

import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;

import com.mojang.blaze3d.platform.InputConstants;

public class ClickGuiModule extends Module {

    public ClickGuiModule() {
        super("ClickGUI", "Opens the Esteban menu.", Category.MISC,
              DEFAULT_KEY);
        setHidden(true);
    }

    public static final int DEFAULT_KEY = InputConstants.KEY_BACKSPACE;

    @Override
    public boolean isMenu() {
        return true;
    }

    @Override
    public void onEnable() {
        ClickGuiScreen.open();
        setEnabledSilently(false);
    }
}
