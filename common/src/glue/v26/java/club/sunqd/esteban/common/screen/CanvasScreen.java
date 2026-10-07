package club.sunqd.esteban.common.screen;

import club.sunqd.esteban.common.render.GfxCanvas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        if (dim)
            super.extractBackground(g, mouseX, mouseY, delta);
    }

    public Panel panel() {
        return panel;
    }

    public void show() {
        Minecraft.getInstance().setScreenAndShow(this);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        panel.render(new GfxCanvas(g, this.font), mouseX, mouseY, this.width, this.height);
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (panel.click((int) event.x(), (int) event.y(), event.button()))
            return true;
        return super.mouseClicked(event, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        panel.release();
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (panel.key(event.key(), this::onClose))
            return true;
        return super.keyPressed(event);
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
