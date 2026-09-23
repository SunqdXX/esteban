package club.sunqd.esteban.ui;

import club.sunqd.esteban.EstebanClient;
import club.sunqd.esteban.compat.Platform;
import club.sunqd.esteban.module.Category;
import club.sunqd.esteban.module.Module;
import club.sunqd.esteban.module.Setting;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {

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
    private static final int MASTER_H = 20;
    private static final int MASTER_W = 150;

    private static final List<Panel> PANELS = new ArrayList<>();

    private Panel dragging;
    private int dragOffX, dragOffY;
    private Module binding;

    private static final java.util.Set<Module> EXPANDED = new java.util.HashSet<>();
    private Setting.Number draggingSlider;
    private int sliderX0, sliderX1;

    public ClickGuiScreen() {
        super(Component.literal("Esteban"));
        if (PANELS.isEmpty()) {
            int x = 12;
            for (Category c : Category.values()) {
                PANELS.add(new Panel(c, x, 12 + MASTER_H + 8));
                x += PANEL_W + 8;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {

        if (dragging != null) {
            dragging.x = mouseX - dragOffX;
            dragging.y = mouseY - dragOffY;
        }

        if (draggingSlider != null && sliderX1 > sliderX0)
            draggingSlider.setRatio((double) (mouseX - sliderX0) / (sliderX1 - sliderX0));

        final boolean armed = EstebanClient.get().getModuleManager().isArmed();
        g.fill(12, 12, 12 + MASTER_W, 12 + MASTER_H, armed ? ON_BG : OFF_BG);
        g.text(this.font, armed ? "CHEATS: ON" : "CHEATS: OFF", 20, 18, TEXT, false);
        g.text(this.font, "click to toggle", 20 + 92, 19, TEXT_DIM, false);

        for (Panel p : PANELS) {
            List<Module> mods = EstebanClient.get().getModuleManager().byCategory(p.category);
            int rows = mods.size();
            if (p.open)
                for (Module m : mods)
                    if (EXPANDED.contains(m))
                        rows += m.getSettings().size();
            int h = HEADER_H + (p.open ? rows * ROW_H : 0);

            g.fill(p.x, p.y, p.x + PANEL_W, p.y + h, BG);
            g.fill(p.x, p.y, p.x + PANEL_W, p.y + 2, p.category.color());
            g.text(this.font, p.category.display(), p.x + 5, p.y + 5, TEXT, false);
            g.text(this.font, p.open ? "-" : "+", p.x + PANEL_W - 10, p.y + 5, TEXT_DIM, false);

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

                g.text(this.font, m.getName(), p.x + 6, rowY + 3,
                        !armed ? TEXT_DIM : m.isEnabled() ? TEXT : m.canEnable() ? TEXT_DIM : TEXT_BLOCKED, false);

                String right = (binding == m) ? "..." : keyName(m.getKey());
                if (right != null) {
                    int w = this.font.width(right);
                    g.text(this.font, right, p.x + PANEL_W - w - 5, rowY + 3, TEXT_DIM, false);
                }

                if (hovered && m.getDescription() != null)
                    g.text(this.font, m.getDescription(), 8, this.height - 12, TEXT_DIM, false);

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
                                    : String.format("%.2f", num.get());
                            g.text(this.font, v, p.x + PANEL_W - 30, rowY + 3, TEXT_DIM, false);
                        } else if (st instanceof Setting.Bool b) {
                            g.text(this.font, st.getName(), p.x + 10, rowY + 3, TEXT_DIM, false);
                            g.text(this.font, b.get() ? "ON" : "OFF",
                                    p.x + PANEL_W - 24, rowY + 3,
                                    b.get() ? TEXT : TEXT_DIM, false);
                        } else if (st instanceof Setting.Mode mo) {
                            g.text(this.font, st.getName(), p.x + 10, rowY + 3, TEXT_DIM, false);
                            int w = this.font.width(mo.get());
                            g.text(this.font, mo.get(), p.x + PANEL_W - w - 6, rowY + 3, TEXT, false);
                        }
                        rowY += ROW_H;
                    }
                }
            }
        }

        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        int mx = (int) event.x(), my = (int) event.y();

        if (mx >= 12 && mx <= 12 + MASTER_W && my >= 12 && my <= 12 + MASTER_H) {
            var mm = EstebanClient.get().getModuleManager();
            mm.setArmed(!mm.isArmed());
            return true;
        }

        for (Panel p : PANELS) {
            List<Module> mods = EstebanClient.get().getModuleManager().byCategory(p.category);

            if (mx >= p.x && mx <= p.x + PANEL_W && my >= p.y && my <= p.y + HEADER_H) {
                if (event.button() == 1) {
                    p.open = !p.open;
                } else {
                    dragging = p;
                    dragOffX = mx - p.x;
                    dragOffY = my - p.y;
                }
                return true;
            }

            if (!p.open)
                continue;

            int rowY = p.y + HEADER_H;
            for (Module m : mods) {
                if (mx >= p.x && mx <= p.x + PANEL_W && my >= rowY && my <= rowY + ROW_H) {
                    if (event.button() == 1) {
                        if (EXPANDED.contains(m))
                            EXPANDED.remove(m);
                        else
                            EXPANDED.add(m);
                    } else if (EstebanClient.get().getModuleManager().isArmed()) {
                        m.toggle();
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
        return super.mouseClicked(event, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = null;
        draggingSlider = null;
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (binding == null && event.key() == InputConstants.KEY_BACKSPACE) {
            onClose();
            return true;
        }
        if (binding != null) {
            binding.setKey(event.key() == InputConstants.KEY_DELETE ? Module.NO_KEY : event.key());
            binding = null;
            return true;
        }
        return super.keyPressed(event);
    }

    private static String keyName(int key) {
        if (key == Module.NO_KEY)
            return null;
        return switch (key) {
            case InputConstants.KEY_RSHIFT    -> "RSHIFT";
            case InputConstants.KEY_LSHIFT    -> "LSHIFT";
            case InputConstants.KEY_LCONTROL  -> "LCTRL";
            case InputConstants.KEY_BACKSPACE -> "BACK";
            default -> Platform.keyName(key).toUpperCase();
        };
    }

    @Override
    public void removed() {
        EstebanClient.get().save();
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
