package club.sunqd.esteban.hud.title;

import club.sunqd.esteban.common.render.GfxCanvas;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
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
        Backgrounds.get().refresh();
        widgets.clear();
        for (GuiEventListener child : children())
            if (child instanceof AbstractWidget w && w.visible)
                widgets.add(w);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        panel.render(new GfxCanvas(g, this.font), mouseX, mouseY, this.width, this.height);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) { }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        return panel.click((int) event.x(), (int) event.y(), event.button());
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return panel.key(event.key(), () -> { });
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
        final MouseButtonEvent event = new MouseButtonEvent(w.getX() + w.getWidth() / 2.0, w.getY() + w.getHeight() / 2.0,
                new MouseButtonInfo(InputConstants.MOUSE_BUTTON_LEFT, 0));
        if (super.mouseClicked(event, false))
            super.mouseReleased(event);
    }

    @Override
    public String version() {
        final Minecraft mc = Minecraft.getInstance();
        String s = "Minecraft " + SharedConstants.getCurrentVersion().name();
        if (mc.isDemo())
            s += " Demo";
        if (Minecraft.checkModStatus().shouldReportAsModified())
            s += I18n.get("menu.modded");
        return s;
    }

    @Override
    public boolean reducedMotion() {
        return Minecraft.getInstance().options.panoramaSpeed().get() <= 0.0;
    }
}
