package club.sunqd.esteban.hud.title;

import club.sunqd.esteban.hud.EstebanHud;
import club.sunqd.esteban.hud.Hud;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

public final class Titles {

    private Titles() { }

    public static boolean vanilla(Screen screen) {
        return screen != null && screen.getClass() == TitleScreen.class;
    }

    public static Screen swap(Screen screen) {
        if (!vanilla(screen))
            return screen;
        final EstebanHud mod = EstebanHud.get();
        if (mod == null || !mod.hud().title().enabled)
            return screen;
        final Hud hud = mod.hud();
        return new EstebanTitleScreen(hud.title(), () -> mod.config().save(hud));
    }
}
