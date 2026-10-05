package club.sunqd.esteban.module;

public enum Category {
    MOVEMENT("Movement", 0xFF4FC3F7),
    RENDER  ("Render",   0xFFAB47BC),
    PLAYER  ("Player",   0xFF66BB6A),
    WORLD   ("World",    0xFFFFA726),
    COMBAT  ("Combat",   0xFFEF5350),
    MISC    ("Misc",     0xFF78909C);

    private final String display;
    private final int color;

    Category(String display, int color) {
        this.display = display;
        this.color = color;
    }

    public String display() { return display; }
    public int color()      { return color; }
}
