package club.sunqd.esteban.hud.title;

import club.sunqd.esteban.common.render.GfxCanvas;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;

public final class EstebanTitleScreen extends TitleScreen implements TitleActions {

    private final TitlePanel panel;
    private final List<AbstractWidget> widgets = new ArrayList<>();

    public EstebanTitleScreen(TitleSettings settings, Runnable save) {
        this.panel = new TitlePanel(this, settings, save);
    }

    public TitlePanel panel() {
        return panel;
    }

    @Override
    protected void init() {
        super.init();
        widgets.clear();
        for (GuiEventListener child : children())
            if (child instanceof AbstractWidget w && w.visible)
                widgets.add(w);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        panel.render(new GfxCanvas(g, this.font), mouseX, mouseY, this.width, this.height);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) { }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return panel.click((int) mouseX, (int) mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return panel.key(key, () -> { });
    }

    @Override
    public List<Entry> entries() {
        final List<Entry> out = new ArrayList<>(widgets.size());
        for (AbstractWidget w : widgets)
            out.add(new Entry(w.getMessage().getContents() instanceof TranslatableContents t ? t.getKey() : "", w.getMessage().getString(), w.active));
        return out;
    }

    @Override
    public void press(int index) {
        if (index < 0 || index >= widgets.size())
            return;
        final AbstractWidget w = widgets.get(index);
        final double x = w.getX() + w.getWidth() / 2.0;
        final double y = w.getY() + w.getHeight() / 2.0;
        if (super.mouseClicked(x, y, InputConstants.MOUSE_BUTTON_LEFT))
            super.mouseReleased(x, y, InputConstants.MOUSE_BUTTON_LEFT);
    }

    @Override
    public String version() {
        final Minecraft mc = Minecraft.getInstance();
        String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
        if (mc.isDemo())
            s += " Demo";
        else
            s += "release".equalsIgnoreCase(mc.getVersionType()) ? "" : "/" + mc.getVersionType();
        if (Minecraft.checkModStatus().shouldReportAsModified())
            s += I18n.get("menu.modded");
        return s;
    }

    @Override
    public boolean reducedMotion() {
        return Minecraft.getInstance().options.panoramaSpeed().get() <= 0.0;
    }
}
