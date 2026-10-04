package club.sunqd.esteban.ui;

import club.sunqd.esteban.EstebanClient;
import club.sunqd.esteban.render.GfxCanvas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClickGuiScreen extends Screen {

    private final ClickGui gui = new ClickGui();

    public ClickGuiScreen() {
        super(Component.literal("Esteban"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new ClickGuiScreen());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        gui.render(new GfxCanvas(g, this.font), mouseX, mouseY, this.width, this.height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (gui.click((int) mouseX, (int) mouseY, button))
            return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        gui.release();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (gui.key(key, this::onClose))
            return true;
        return super.keyPressed(key, scanCode, modifiers);
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
