package club.sunqd.esteban.ui;

import club.sunqd.esteban.common.screen.CanvasScreen;

public class ClickGuiScreen extends CanvasScreen {

    private final ClickGui gui;

    public ClickGuiScreen() {
        this(new ClickGui());
    }

    private ClickGuiScreen(ClickGui gui) {
        super("Esteban", gui);
        this.gui = gui;
    }

    public static void open() {
        new ClickGuiScreen().show();
    }
}
