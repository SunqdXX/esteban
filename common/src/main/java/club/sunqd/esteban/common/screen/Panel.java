package club.sunqd.esteban.common.screen;

import club.sunqd.esteban.common.render.Canvas;

public interface Panel {

    void render(Canvas canvas, int mouseX, int mouseY, int width, int height);

    boolean click(int x, int y, int button);

    void release();

    boolean key(int key, Runnable close);

    boolean scroll(int x, int y, double amount);

    default void closed() { }
}
