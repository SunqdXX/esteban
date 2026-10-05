package club.sunqd.esteban.ui;

import club.sunqd.esteban.common.screen.CanvasScreen;

public class ClickGuiScreen extends CanvasScreen {

    public ClickGuiScreen() {
        super("Esteban", new ClickGui());
    }

    public static void open() {
        new ClickGuiScreen().show();
    }
}
