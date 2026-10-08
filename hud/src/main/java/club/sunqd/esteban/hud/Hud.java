package club.sunqd.esteban.hud;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.element.ArmorStatus;
import club.sunqd.esteban.hud.element.ArrowCounter;
import club.sunqd.esteban.hud.element.BlockOutline;
import club.sunqd.esteban.hud.element.Clock;
import club.sunqd.esteban.hud.element.Combo;
import club.sunqd.esteban.hud.element.Coords;
import club.sunqd.esteban.hud.element.Cps;
import club.sunqd.esteban.hud.element.Crosshair;
import club.sunqd.esteban.hud.element.DirectionBar;
import club.sunqd.esteban.hud.element.Element;
import club.sunqd.esteban.hud.element.Fps;
import club.sunqd.esteban.hud.element.Keystrokes;
import club.sunqd.esteban.hud.element.LowFire;
import club.sunqd.esteban.hud.element.LowShield;
import club.sunqd.esteban.hud.element.Ping;
import club.sunqd.esteban.hud.element.Potions;
import club.sunqd.esteban.hud.element.Saturation;
import club.sunqd.esteban.hud.element.ShieldCooldown;
import club.sunqd.esteban.hud.element.Speed;
import club.sunqd.esteban.hud.element.ToggleSprint;
import club.sunqd.esteban.hud.element.TotemPops;
import club.sunqd.esteban.hud.title.TitleSettings;

import net.minecraft.client.Minecraft;

import java.util.List;

public final class Hud {

    private final List<Element> elements = List.of(
            new Fps(4, 4),
            new Cps(4, 20),
            new Coords(4, 36),
            new Ping(4, 52),
            new Keystrokes(4, 68),
            new ToggleSprint(4, 156),
            new ArmorStatus(10000, 10000),
            new Potions(10000, 104),
            new DirectionBar(Element.CENTER, 2),
            new Clock(Element.CENTER, 34),
            new Speed(Element.CENTER, 50),
            new Saturation(Element.CENTER, 66),
            new ArrowCounter(10000, 200),
            new Combo(4, 172),
            new TotemPops(4, 190),
            new ShieldCooldown(Element.CENTER, 82),
            new Crosshair(),
            new BlockOutline(),
            new LowFire(),
            new LowShield()
    );

    private final TitleSettings title = new TitleSettings();

    private volatile boolean editing;

    public TitleSettings title() {
        return title;
    }

    public boolean editing() {
        return editing;
    }

    public void editing(boolean on) {
        editing = on;
    }

    public List<Element> elements() {
        return elements;
    }

    public Crosshair crosshair() {
        return (Crosshair) get("crosshair");
    }

    public BlockOutline outline() {
        return (BlockOutline) get("outline");
    }

    public boolean replaceCrosshair(Canvas canvas, int width, int height) {
        final Crosshair crosshair = crosshair();
        if (!crosshair.enabled)
            return false;
        if (!editing)
            crosshair.render(canvas, width, height);
        return true;
    }

    public Element get(String id) {
        for (Element e : elements)
            if (e.id().equals(id))
                return e;
        return null;
    }

    public void render(Canvas canvas, float partial) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || editing)
            return;
        final int width = mc.getWindow().getGuiScaledWidth();
        final int height = mc.getWindow().getGuiScaledHeight();
        for (Element e : elements) {
            if (!e.onHud())
                continue;
            if (!e.enabled) {
                e.inactive(mc);
                continue;
            }
            e.update(mc);
            if (e.visible())
                e.render(canvas, width, height);
        }
    }
}
