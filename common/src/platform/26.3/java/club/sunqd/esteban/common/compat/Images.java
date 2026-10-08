package club.sunqd.esteban.common.compat;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.io.IOException;
import java.io.InputStream;

public final class Images {

    private Images() { }

    private static final class Linear extends DynamicTexture {

        Linear(String name, NativeImage image) {
            super(() -> name, image);
            this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
        }
    }

    public static NativeImage read(InputStream in) throws IOException {
        return NativeImage.read(in);
    }

    public static int[] pixels(NativeImage image) {
        final int w = image.getWidth();
        final int h = image.getHeight();
        final int[] out = new int[w * h];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                out[y * w + x] = image.getPixel(x, y);
        return out;
    }

    public static NativeImage image(int width, int height, int[] argb) {
        final NativeImage image = new NativeImage(width, height, false);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                image.setPixelABGR(x, y, ARGB.toABGR(argb[y * width + x]));
        return image;
    }

    public static void register(String id, NativeImage image) {
        Minecraft.getInstance().getTextureManager().register(Identifier.parse(id), new Linear(id, image));
    }

    public static void release(String id) {
        Minecraft.getInstance().getTextureManager().release(Identifier.parse(id));
    }
}
