package club.sunqd.esteban.hud;

import club.sunqd.esteban.common.render.Canvas;
import club.sunqd.esteban.hud.element.Coords;
import club.sunqd.esteban.hud.element.Cps;
import club.sunqd.esteban.hud.element.Element;
import club.sunqd.esteban.hud.element.Fps;
import club.sunqd.esteban.hud.element.Ping;

import net.minecraft.client.Minecraft;

import java.util.List;

public final class Hud {

    private final List<Element> elements = List.of(
            new Fps(4, 4),
            new Cps(4, 20),
            new Coords(4, 36),
            new Ping(4, 52)
    );

    public List<Element> elements() {
        return elements;
    }

    public Element get(String id) {
        for (Element e : elements)
            if (e.id().equals(id))
                return e;
        return null;
    }

    public void render(Canvas canvas, float partial) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        final int width = mc.getWindow().getGuiScaledWidth();
        final int height = mc.getWindow().getGuiScaledHeight();
        for (Element e : elements) {
            if (!e.enabled)
                continue;
            e.update(mc);
            e.render(canvas, width, height);
        }
    }
}
