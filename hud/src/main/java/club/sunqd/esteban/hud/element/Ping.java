package club.sunqd.esteban.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

public final class Ping extends TextElement {

    public Ping(int x, int y) {
        super("ping", "Ping", x, y);
    }

    @Override
    protected String text(Minecraft mc) {
        final ClientPacketListener connection = mc.getConnection();
        if (connection == null || mc.player == null)
            return "- ms";
        final PlayerInfo info = connection.getPlayerInfo(mc.player.getUUID());
        return info == null ? "- ms" : info.getLatency() + " ms";
    }
}
