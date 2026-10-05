package club.sunqd.esteban.module;

public abstract class Setting {
    private final String name;

    protected Setting(String name) { this.name = name; }

    public String getName() { return name; }

    public abstract String serialize();

    public abstract void deserialize(String raw);

    public static final class Bool extends Setting {
        private boolean value;

        public Bool(String name, boolean def) { super(name); this.value = def; }

        public boolean get()            { return value; }
        public void set(boolean v)      { this.value = v; }
        public void toggle()            { this.value = !this.value; }

        @Override public String serialize() { return Boolean.toString(value); }

        @Override public void deserialize(String raw) {
            this.value = Boolean.parseBoolean(raw);
        }
    }

    public static final class Number extends Setting {
        private final double min, max;
        private final boolean integer;
        private double value;

        public Number(String name, double def, double min, double max, boolean integer) {
            super(name);
            this.min = min;
            this.max = max;
            this.integer = integer;
            set(def);
        }

        public double get()      { return value; }
        public int getInt()      { return (int) Math.round(value); }
        public float getFloat()  { return (float) value; }
        public double min()      { return min; }
        public double max()      { return max; }
        public boolean integer() { return integer; }

        public void set(double v) {
            double c = Math.max(min, Math.min(max, v));
            this.value = integer ? Math.round(c) : c;
        }

        public double ratio()            { return (value - min) / (max - min); }
        public void setRatio(double r)   { set(min + (max - min) * Math.max(0, Math.min(1, r))); }

        @Override public String serialize() { return Double.toString(value); }

        @Override public void deserialize(String raw) {
            try { set(Double.parseDouble(raw)); } catch (NumberFormatException ignored) { }
        }
    }

    public static final class Mode extends Setting {
        private final String[] options;
        private int index;

        public Mode(String name, String... options) {
            super(name);
            if (options.length == 0) throw new IllegalArgumentException("Mode needs options");
            this.options = options;
            this.index = 0;
        }

        public String get()          { return options[index]; }
        public String[] options()    { return options; }
        public int index()           { return index; }
        public boolean is(String s)  { return options[index].equalsIgnoreCase(s); }

        public void cycle()          { index = (index + 1) % options.length; }

        public void set(String s) {
            for (int i = 0; i < options.length; i++) {
                if (options[i].equalsIgnoreCase(s)) { index = i; return; }
            }
        }

        @Override public String serialize() { return get(); }

        @Override public void deserialize(String raw) { set(raw); }
    }
}
