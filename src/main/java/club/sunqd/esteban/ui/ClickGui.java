package club.sunqd.esteban.ui;

import club.sunqd.esteban.EstebanClient;
import club.sunqd.esteban.compat.Platform;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.ModuleManager;
import club.sunqd.esteban.module.Setting;
import club.sunqd.esteban.render.Canvas;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ClickGui {

    private static final int PANEL_W  = 104;
    private static final int HEADER_H = 15;
    private static final int ROW_H    = 14;
    private static final int BG       = 0xD0121216;
    private static final int ROW_BG   = 0xB01B1B22;
    private static final int ROW_ON   = 0xC02A2A38;
    private static final int TEXT     = 0xFFE6E6E6;
    private static final int TEXT_DIM = 0xFF8A8A95;
    private static final int TEXT_BLOCKED = 0xFF4A4A54;
    private static final int ON_BG    = 0xFF2E7D32;
    private static final int OFF_BG   = 0xFF7A1F1F;
    private static final int SET_BG    = 0xB0161620;
    private static final int SLIDER_BG = 0xFF3A3A46;
    private static final int LISTEN    = 0xFFFFC857;
    private static final int MASTER_H = 20;
    private static final int MASTER_W = 150;
    private static final int TOP      = 12 + MASTER_H + 8;

    private static final List<Panel> PANELS = new ArrayList<>();
    private static final Set<Module> EXPANDED = new HashSet<>();

    private Panel dragging;
    private int dragOffX, dragOffY;

    private Module binding;
    private boolean bindingMaster;
    private String flash;
    private long flashUntil;

    private Setting.Number draggingSlider;
    private int sliderX0, sliderX1;

    public void render(Canvas g, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (PANELS.isEmpty())
            place(screenWidth);

        if (dragging != null) {
            dragging.x = mouseX - dragOffX;
            dragging.y = mouseY - dragOffY;
        }

        if (draggingSlider != null && sliderX1 > sliderX0)
            draggingSlider.setRatio((double) (mouseX - sliderX0) / (sliderX1 - sliderX0));

        final ModuleManager mm = EstebanClient.get().getModuleManager();
        final boolean armed = mm.isArmed();
        g.fill(12, 12, 12 + MASTER_W, 12 + MASTER_H, armed ? ON_BG : OFF_BG);
        g.text(armed ? "CHEATS: ON" : "CHEATS: OFF", 20, 18, TEXT, false);
        final String masterKey = keyName(mm.getMasterKey());
        if (bindingMaster)
            g.text("press a key", 20 + 92, 19, LISTEN, false);
        else
            g.text(masterKey == null ? "click to toggle" : "click or " + masterKey, 20 + 92, 19, TEXT_DIM, false);

        String status = null;

        for (Panel p : PANELS) {
            List<Module> mods = mm.byCategory(p.category);
            int h = height(p, mods);

            g.fill(p.x, p.y, p.x + PANEL_W, p.y + h, BG);
            g.fill(p.x, p.y, p.x + PANEL_W, p.y + 2, p.category.color());
            g.text(p.category.display(), p.x + 5, p.y + 5, TEXT, false);
            g.text(p.open ? "-" : "+", p.x + PANEL_W - 10, p.y + 5, TEXT_DIM, false);

            if (!p.open)
                continue;

            int rowY = p.y + HEADER_H;
            for (Module m : mods) {
                boolean hovered = mouseX >= p.x && mouseX <= p.x + PANEL_W
                        && mouseY >= rowY && mouseY <= rowY + ROW_H;

                g.fill(p.x, rowY, p.x + PANEL_W, rowY + ROW_H,
                        m.isEnabled() ? ROW_ON : ROW_BG);
                if (m.isEnabled())
                    g.fill(p.x, rowY, p.x + 2, rowY + ROW_H, p.category.color());

                g.text(m.getName(), p.x + 6, rowY + 3,
                        !armed ? TEXT_DIM : m.isEnabled() ? TEXT : m.canEnable() ? TEXT_DIM : TEXT_BLOCKED, false);

                String right = (binding == m) ? "..." : keyName(m.getKey());
                if (right != null) {
                    int w = g.width(right);
                    g.text(right, p.x + PANEL_W - w - 5, rowY + 3, binding == m ? LISTEN : TEXT_DIM, false);
                }

                if (hovered && m.getDescription() != null)
                    status = m.getDescription();

                rowY += ROW_H;

                if (EXPANDED.contains(m)) {
                    for (Setting st : m.getSettings()) {
                        g.fill(p.x, rowY, p.x + PANEL_W, rowY + ROW_H, SET_BG);

                        if (st instanceof Setting.Number num) {
                            final int bx0 = p.x + 6;
                            final int bx1 = p.x + PANEL_W - 34;
                            final int by = rowY + ROW_H / 2 - 1;
                            g.fill(bx0, by, bx1, by + 2, SLIDER_BG);
                            final int fill = bx0 + (int) ((bx1 - bx0) * num.ratio());
                            g.fill(bx0, by, fill, by + 2, p.category.color());
                            g.fill(fill - 1, by - 3, fill + 2, by + 5, TEXT);

                            String v = num.integer()
                                    ? String.valueOf(num.getInt())
                                    : String.format(Locale.ROOT, "%.2f", num.get());
                            g.text(v, p.x + PANEL_W - 30, rowY + 3, TEXT_DIM, false);
                        } else if (st instanceof Setting.Bool b) {
                            g.text(st.getName(), p.x + 10, rowY + 3, TEXT_DIM, false);
                            g.text(b.get() ? "ON" : "OFF",
                                    p.x + PANEL_W - 24, rowY + 3,
                                    b.get() ? TEXT : TEXT_DIM, false);
                        } else if (st instanceof Setting.Mode mo) {
                            g.text(st.getName(), p.x + 10, rowY + 3, TEXT_DIM, false);
                            int w = g.width(mo.get());
                            g.text(mo.get(), p.x + PANEL_W - w - 6, rowY + 3, TEXT, false);
                        }
                        rowY += ROW_H;
                    }
                }
            }
        }

        if (binding != null || bindingMaster) {
            g.text("press any key for " + (bindingMaster ? "CHEATS" : binding.getName())
                    + ", esc cancels, delete removes it", 8, screenHeight - 12, LISTEN, false);
        } else if (flash != null && System.currentTimeMillis() < flashUntil) {
            g.text(flash, 8, screenHeight - 12, LISTEN, false);
        } else if (status != null) {
            g.text(status, 8, screenHeight - 12, TEXT_DIM, false);
        }
    }

    private static void place(int screenWidth) {
        int x = 12, y = TOP;
        for (Category c : Category.values()) {
            if (x > 12 && x + PANEL_W > screenWidth - 4) {
                x = 12;
                y = TOP + 8 + HEADER_H + 6 * ROW_H;
            }
            PANELS.add(new Panel(c, x, y));
            x += PANEL_W + 8;
        }
    }

    private static int height(Panel p, List<Module> mods) {
        int rows = mods.size();
        if (p.open)
            for (Module m : mods)
                if (EXPANDED.contains(m))
                    rows += m.getSettings().size();
        return HEADER_H + (p.open ? rows * ROW_H : 0);
    }

    public boolean click(int mx, int my, int button) {
        final ModuleManager mm = EstebanClient.get().getModuleManager();

        if (binding != null || bindingMaster) {
            binding = null;
            bindingMaster = false;
            return true;
        }

        if (mx >= 12 && mx <= 12 + MASTER_W && my >= 12 && my <= 12 + MASTER_H) {
            if (button == InputConstants.MOUSE_BUTTON_MIDDLE) {
                bindingMaster = true;
            } else {
                mm.setArmed(!mm.isArmed());
                flash = null;
            }
            return true;
        }

        for (int i = PANELS.size() - 1; i >= 0; i--) {
            final Panel p = PANELS.get(i);
            List<Module> mods = mm.byCategory(p.category);

            if (mx >= p.x && mx <= p.x + PANEL_W && my >= p.y && my <= p.y + HEADER_H) {
                if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
                    p.open = !p.open;
                } else {
                    dragging = p;
                    dragOffX = mx - p.x;
                    dragOffY = my - p.y;
                    PANELS.remove(p);
                    PANELS.add(p);
                }
                return true;
            }

            if (!p.open)
                continue;

            int rowY = p.y + HEADER_H;
            for (Module m : mods) {
                if (mx >= p.x && mx <= p.x + PANEL_W && my >= rowY && my <= rowY + ROW_H) {
                    if (button == InputConstants.MOUSE_BUTTON_MIDDLE) {
                        binding = m;
                    } else if (button == InputConstants.MOUSE_BUTTON_RIGHT || m.isMenu()) {
                        if (EXPANDED.contains(m))
                            EXPANDED.remove(m);
                        else
                            EXPANDED.add(m);
                    } else if (mm.isArmed()) {
                        m.toggle();
                    } else {
                        flash = "cheats are off, click the red CHEATS box first";
                        flashUntil = System.currentTimeMillis() + 2500;
                    }
                    return true;
                }
                rowY += ROW_H;

                if (!EXPANDED.contains(m))
                    continue;

                for (Setting st : m.getSettings()) {
                    if (my >= rowY && my <= rowY + ROW_H && mx >= p.x && mx <= p.x + PANEL_W) {
                        if (st instanceof Setting.Number num) {
                            sliderX0 = p.x + 6;
                            sliderX1 = p.x + PANEL_W - 34;
                            draggingSlider = num;
                            num.setRatio((double) (mx - sliderX0) / (sliderX1 - sliderX0));
                        } else if (st instanceof Setting.Bool b) {
                            b.toggle();
                        } else if (st instanceof Setting.Mode mo) {
                            mo.cycle();
                        }
                        return true;
                    }
                    rowY += ROW_H;
                }
            }
        }
        return false;
    }

    public void release() {
        dragging = null;
        draggingSlider = null;
    }

    public boolean scroll(int mx, int my, double amount) {
        final ModuleManager mm = EstebanClient.get().getModuleManager();
        for (int i = PANELS.size() - 1; i >= 0; i--) {
            final Panel p = PANELS.get(i);
            final int h = height(p, mm.byCategory(p.category));
            if (mx >= p.x && mx <= p.x + PANEL_W && my >= p.y && my <= p.y + h) {
                p.y += (int) Math.round(amount * 14);
                return true;
            }
        }
        return false;
    }

    public boolean key(int key, Runnable close) {
        final ModuleManager mm = EstebanClient.get().getModuleManager();
        final Module menu = menu(mm);

        if (binding != null || bindingMaster) {
            if (key != InputConstants.KEY_ESCAPE)
                assign(mm, menu, key);
            binding = null;
            bindingMaster = false;
            EstebanClient.get().save();
            return true;
        }

        if (menu != null && key == menu.getKey()) {
            close.run();
            return true;
        }
        return false;
    }

    private void assign(ModuleManager mm, Module menu, int key) {
        final boolean clear = key == InputConstants.KEY_BACKSPACE || key == InputConstants.KEY_DELETE;

        if (binding != null && binding.isMenu()) {
            final String user = clear ? null : userOf(mm, key);
            if (user != null)
                warn(keyName(key) + " is already on " + user + ", pick another key");
            else
                binding.setKey(clear ? ClickGuiModule.DEFAULT_KEY : key);
            return;
        }

        if (!clear && menu != null && key == menu.getKey()) {
            warn(keyName(key) + " opens this menu, pick another key");
            return;
        }

        if (bindingMaster)
            mm.setMasterKey(clear ? Module.NO_KEY : key);
        else if (binding != null)
            binding.setKey(clear ? Module.NO_KEY : key);
    }

    private void warn(String text) {
        flash = text;
        flashUntil = System.currentTimeMillis() + 2500;
    }

    private static String userOf(ModuleManager mm, int key) {
        if (mm.getMasterKey() == key)
            return "CHEATS";
        for (Module m : mm.getModules())
            if (!m.isMenu() && m.getKey() == key)
                return m.getName();
        return null;
    }

    private static Module menu(ModuleManager mm) {
        for (Module m : mm.getModules())
            if (m.isMenu())
                return m;
        return null;
    }

    private static String keyName(int key) {
        if (key == Module.NO_KEY)
            return null;
        return switch (key) {
            case InputConstants.KEY_RSHIFT    -> "RSHIFT";
            case InputConstants.KEY_LSHIFT    -> "LSHIFT";
            case InputConstants.KEY_LCONTROL  -> "LCTRL";
            case InputConstants.KEY_RCONTROL  -> "RCTRL";
            case InputConstants.KEY_LALT      -> "LALT";
            case InputConstants.KEY_RALT      -> "RALT";
            case InputConstants.KEY_BACKSPACE -> "BACK";
            default -> Platform.keyName(key).toUpperCase(Locale.ROOT);
        };
    }

    private static final class Panel {
        final Category category;
        int x, y;
        boolean open = true;

        Panel(Category c, int x, int y) {
            this.category = c;
            this.x = x;
            this.y = y;
        }
    }
}
