package club.sunqd.esteban.render;

public interface Canvas {

    void fill(int left, int top, int right, int bottom, int color);

    void text(String s, int x, int y, int color);

    void text(String s, int x, int y, int color, boolean shadow);

    int width(String s);
}
