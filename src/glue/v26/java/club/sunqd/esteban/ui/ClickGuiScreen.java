package club.sunqd.esteban.ui;

import club.sunqd.esteban.EstebanClient;
import club.sunqd.esteban.render.GfxCanvas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ClickGuiScreen extends Screen {

    private final ClickGui gui = new ClickGui();

    public ClickGuiScreen() {
        super(Component.literal("Esteban"));
    }

    public static void open() {
        Minecraft.getInstance().setScreenAndShow(new ClickGuiScreen());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        gui.render(new GfxCanvas(g, this.font), mouseX, mouseY, this.width, this.height);
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (gui.click((int) event.x(), (int) event.y(), event.button()))
            return true;
        return super.mouseClicked(event, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        gui.release();
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (gui.key(event.key(), this::onClose))
            return true;
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (gui.scroll((int) mouseX, (int) mouseY, vertical))
            return true;
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public void removed() {
        EstebanClient.get().save();
    }
}
