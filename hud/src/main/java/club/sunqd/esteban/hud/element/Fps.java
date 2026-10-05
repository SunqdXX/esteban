package club.sunqd.esteban.hud.element;

import net.minecraft.client.Minecraft;

public final class Fps extends TextElement {

    public Fps(int x, int y) {
        super("fps", "FPS", x, y);
    }

    @Override
    protected String text(Minecraft mc) {
        return mc.getFps() + " FPS";
    }
}
