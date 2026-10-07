package club.sunqd.esteban.common.screen;

import club.sunqd.esteban.common.render.GfxCanvas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CanvasScreen extends Screen {

    private final Panel panel;
    private final boolean dim;

    public CanvasScreen(String title, Panel panel) {
        this(title, panel, true);
    }

    public CanvasScreen(String title, Panel panel, boolean dim) {
        super(Component.literal(title));
        this.panel = panel;
        this.dim = dim;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (dim)
            super.renderBackground(g, mouseX, mouseY, delta);
    }

    public Panel panel() {
        return panel;
    }

    public void show() {
        Minecraft.getInstance().setScreen(this);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        panel.render(new GfxCanvas(g, this.font), mouseX, mouseY, this.width, this.height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (panel.click((int) mouseX, (int) mouseY, button))
            return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        panel.release();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (panel.key(key, this::onClose))
            return true;
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (panel.scroll((int) mouseX, (int) mouseY, vertical))
            return true;
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public void removed() {
        panel.closed();
    }
}
