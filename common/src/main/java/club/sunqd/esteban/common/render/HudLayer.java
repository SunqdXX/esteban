package club.sunqd.esteban.common.render;

@FunctionalInterface
public interface HudLayer {

    void draw(Canvas canvas, float partial);
}
