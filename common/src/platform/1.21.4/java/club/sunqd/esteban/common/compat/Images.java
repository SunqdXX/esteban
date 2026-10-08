package club.sunqd.esteban.common.compat;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public final class Images {

    private Images() { }

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
                image.setPixel(x, y, argb[y * width + x]);
        return image;
    }

    public static void register(String id, NativeImage image) {
        final DynamicTexture texture = new DynamicTexture(image);
        texture.setFilter(true, false);
        texture.setClamp(true);
        Minecraft.getInstance().getTextureManager().register(ResourceLocation.parse(id), texture);
    }

    public static void release(String id) {
        Minecraft.getInstance().getTextureManager().release(ResourceLocation.parse(id));
    }
}
