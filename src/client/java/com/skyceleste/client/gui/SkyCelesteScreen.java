package com.skyceleste.client.gui;

import com.skyceleste.client.features.FeatureRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.List;

public class SkyCelesteScreen extends Screen {

    private record Layout(int sideW, int panelX, int headerH, int startX, int startY, int cols, int cardW, int cardH,
            int gapX, int gapY) {
    }

    private static final int BG = 0xFF2B3A52;
    private static final int SIDEBAR_BG = 0xFF1E2B3E;
    private static final int CARD_BG = 0xFF243044;
    private static final int CARD_HOVER = 0xFF2E3D56;
    private static final int ACCENT = 0xFF4FC3F7;
    private static final int TEXT_WHITE = 0xFFFFFFFF;
    private static final int TEXT_GRAY = 0xFF8899AA;
    private static final int GREEN = 0xFF4CAF50;
    private static final int RED = 0xFFE53935;
    private static final int HEADER_H = 42;
    private static final int SIDEBAR_MIN_W = 96;
    private static final int SIDEBAR_MAX_W = 168;
    private static final int CARD_MIN_W = 170;
    private static final int CARD_MAX_W = 260;
    private static final int CARD_H = 75;
    private static final int GRID_GAP = 8;
    private static final int CONTENT_PAD = 10;
    private static final int SCROLL_STEP = 24;

    private static final List<String> SIDEBAR_ITEMS = List.of(
            "All", "Quality Of Life", "HUD");

    private String selectedCategory = "All";
    private EditBox searchBox;
    private int contentScrollY;
    private boolean awaitingZoomKey = false;

    public SkyCelesteScreen() {
        super(Component.literal("SkyCeleste"));
    }

    private Layout layout() {
        int sideW = Math.max(SIDEBAR_MIN_W, Math.min(SIDEBAR_MAX_W, this.width / 5));
        int panelX = sideW + GRID_GAP;
        int panelW = Math.max(1, this.width - panelX - GRID_GAP);

        int cols = Math.max(1, (panelW + GRID_GAP) / (CARD_MIN_W + GRID_GAP));
        int cardW = (panelW - (cols + 1) * GRID_GAP) / cols;
        while (cols > 1 && cardW > CARD_MAX_W) {
            cols++;
            cardW = (panelW - (cols + 1) * GRID_GAP) / cols;
        }

        int startX = panelX + GRID_GAP;
        int startY = HEADER_H + 10;
        return new Layout(sideW, panelX, HEADER_H, startX, startY, cols, cardW, CARD_H, GRID_GAP, GRID_GAP);
    }

    private List<FeatureRegistry.Feature> getVisibleFeatures() {
        String query = searchBox == null ? "" : searchBox.getValue().toLowerCase();
        return FeatureRegistry.FEATURES.stream()
                .filter(f -> selectedCategory.equals("All") || f.category().equals(selectedCategory))
                .filter(f -> query.isEmpty() || f.name().toLowerCase().contains(query))
                .toList();
    }

    private int contentTop() {
        return HEADER_H + 10;
    }

    private int contentBottom() {
        return this.height - CONTENT_PAD;
    }

    private int contentHeight(List<FeatureRegistry.Feature> visible, Layout l) {
        if (visible.isEmpty()) {
            return 0;
        }

        int rows = (visible.size() + l.cols() - 1) / l.cols();
        return rows * l.cardH() + Math.max(0, rows - 1) * l.gapY();
    }

    private int maxScrollY(List<FeatureRegistry.Feature> visible, Layout l) {
        int visibleHeight = Math.max(1, contentBottom() - contentTop());
        return Math.max(0, contentHeight(visible, l) - visibleHeight);
    }

    private int cardY(int baseY) {
        return baseY - contentScrollY;
    }

    @Override
    protected void init() {
        Layout l = layout();
        int searchW = Math.min(220, this.width - l.panelX() - 56);
        int searchX = l.panelX() + Math.max(8, (this.width - l.panelX() - searchW) / 2);

        searchBox = new EditBox(this.font, searchX, 14, searchW, 16, Component.literal("Search features"));
        searchBox.setHint(Component.literal("Search features"));
        searchBox.setBordered(true);
        this.addRenderableWidget(searchBox);

        this.addRenderableWidget(
                Button.builder(Component.literal("X"), btn -> this.onClose())
                        .bounds(this.width - 28, 8, 20, 20)
                        .build());

        for (int i = 0; i < SIDEBAR_ITEMS.size(); i++) {
            final String category = SIDEBAR_ITEMS.get(i);
            int iy = 45 + i * 22;
            String label = category.equals(selectedCategory) ? "> " + category + " <" : category;
            this.addRenderableWidget(
                    Button.builder(Component.literal(label), btn -> {
                        selectedCategory = category;
                        rebuildWidgets();
                    })
                            .bounds(0, iy, l.sideW(), 20)
                            .build());
        }
    }

    private int activeDragSlider = -1;
    private FeatureRegistry.Feature activeDragFeature = null;

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            double mouseX = event.x();
            double mouseY = event.y();
            Layout l = layout();

            List<FeatureRegistry.Feature> visible = getVisibleFeatures();
            for (int i = 0; i < visible.size(); i++) {
                int col = i % l.cols();
                int row = i / l.cols();
                int cx = l.startX() + col * (l.cardW() + l.gapX());
                int cy = cardY(l.startY() + row * (l.cardH() + l.gapY()));

                if (visible.get(i) == FeatureRegistry.ZOOM
                        && isInsideZoomKeybind(mouseX, mouseY, cx, cy, l.cardW())) {
                    awaitingZoomKey = true;
                    return true;
                }

                if (visible.get(i) == FeatureRegistry.HIT_COLOR) {
                    com.skyceleste.client.features.HitColor hc = (com.skyceleste.client.features.HitColor) visible.get(i);
                    int previewW = 30;
                    int previewX = cx + 8;
                    int previewY = cy + 34; // color box
                    int previewH = 18;
                    
                    int resetY = cy + 54;
                    int resetH = 12;

                    // Block toggle if clicking color box
                    if (mouseX >= previewX && mouseX < previewX + previewW && mouseY >= previewY && mouseY < previewY + previewH) {
                        return true;
                    }
                    
                    // Reset click
                    if (mouseX >= previewX && mouseX < previewX + previewW && mouseY >= resetY && mouseY < resetY + resetH) {
                        hc.r = 255;
                        hc.g = 0;
                        hc.b = 0;
                        com.skyceleste.client.utils.HitColorHelper.updateColorConfig();
                        FeatureRegistry.saveStates();
                        return true;
                    }
                    
                    int sliderY = cy + 34;
                    int sliderX = cx + 64;
                    int sliderWidth = Math.max(l.cardW() - 75, 40);
                    
                    for (int s = 0; s < 3; s++) {
                        int y = sliderY + s * 12;
                        if (mouseX >= sliderX && mouseX < sliderX + sliderWidth && mouseY >= y && mouseY < y + 8) {
                            activeDragSlider = s;
                            activeDragFeature = visible.get(i);
                            updateSliderValue((com.skyceleste.client.features.HitColor) visible.get(i), s, mouseX, sliderX, sliderWidth);
                            return true;
                        }
                    }
                }

                if (mouseX >= cx && mouseX < cx + l.cardW() && mouseY >= cy && mouseY < cy + l.cardH()) {
                    awaitingZoomKey = false;
                    visible.get(i).toggle();
                    FeatureRegistry.saveStates();
                    return true;
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (event.button() == 0 && activeDragSlider >= 0 && activeDragFeature instanceof com.skyceleste.client.features.HitColor hc) {
            Layout l = layout();
            List<FeatureRegistry.Feature> visible = getVisibleFeatures();
            int idx = visible.indexOf(hc);
            if (idx >= 0) {
                int col = idx % l.cols();
                int cx = l.startX() + col * (l.cardW() + l.gapX());
                int sliderX = cx + 64;
                int sliderWidth = Math.max(l.cardW() - 75, 40);
                updateSliderValue(hc, activeDragSlider, event.x(), sliderX, sliderWidth);
                return true;
            }
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && activeDragSlider >= 0) {
            activeDragSlider = -1;
            activeDragFeature = null;
            FeatureRegistry.saveStates();
            return true;
        }
        return super.mouseReleased(event);
    }

    private void updateSliderValue(com.skyceleste.client.features.HitColor feat, int component, double mouseX, int sliderX, int sliderWidth) {
        int newVal = (int) Math.max(0, Math.min(255, ((mouseX - sliderX) / sliderWidth) * 255));
        if (component == 0) feat.r = newVal;
        else if (component == 1) feat.g = newVal;
        else if (component == 2) feat.b = newVal;
        com.skyceleste.client.utils.HitColorHelper.updateColorConfig();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!awaitingZoomKey) {
            return super.keyPressed(event);
        }

        if (event.key() == InputConstants.KEY_ESCAPE) {
            awaitingZoomKey = false;
            return true;
        }

        applyZoomKeybind(event.key());
        awaitingZoomKey = false;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Layout l = layout();
        int panelX = l.startX();
        int panelRight = this.width - CONTENT_PAD;
        int top = contentTop();
        int bottom = contentBottom();

        if (mouseX >= panelX && mouseX < panelRight && mouseY >= top && mouseY < bottom) {
            List<FeatureRegistry.Feature> visible = getVisibleFeatures();
            int maxScroll = maxScrollY(visible, l);
            if (maxScroll > 0) {
                contentScrollY = Math.max(0,
                        Math.min(maxScroll, contentScrollY - (int) Math.signum(verticalAmount) * SCROLL_STEP));
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        Layout l = layout();
        int w = this.width;
        int h = this.height;

        g.fill(0, 0, w, h, BG);
        g.fill(0, 0, l.sideW(), h, SIDEBAR_BG);

        g.drawString(this.font, "SkyCeleste", 8, 10, ACCENT, false);
        g.drawString(this.font, "v0.0.2", 8, 20, TEXT_GRAY, false);

        for (int i = 0; i < SIDEBAR_ITEMS.size(); i++) {
            String item = SIDEBAR_ITEMS.get(i);
            int iy = 45 + i * 22;
            boolean selected = item.equals(selectedCategory);
            if (selected) {
                g.fill(0, iy, l.sideW(), iy + 20, 0xFF2E3D56);
                g.fill(0, iy, 5, iy + 20, ACCENT);
                g.fill(0, iy, l.sideW(), iy + 1, 0xFF5ED0FF);
                g.fill(0, iy + 19, l.sideW(), iy + 20, 0xFF5ED0FF);
            }
        }

        g.fill(l.sideW(), 0, w, l.headerH(), SIDEBAR_BG);
        g.fill(l.sideW(), l.headerH(), w, l.headerH() + 1, 0xFF3A4F6A);

        List<FeatureRegistry.Feature> visible = getVisibleFeatures();
        int maxScroll = maxScrollY(visible, l);
        contentScrollY = Math.max(0, Math.min(maxScroll, contentScrollY));

        int clipLeft = l.startX() - 2;
        int clipTop = contentTop();
        int clipRight = w - CONTENT_PAD;
        int clipBottom = contentBottom();

        g.enableScissor(clipLeft, clipTop, clipRight, clipBottom);

        for (int i = 0; i < visible.size(); i++) {
            int col = i % l.cols();
            int row = i / l.cols();
            int cx = l.startX() + col * (l.cardW() + l.gapX());
            int cy = cardY(l.startY() + row * (l.cardH() + l.gapY()));

            if (cy + l.cardH() < clipTop || cy > clipBottom) {
                continue;
            }

            FeatureRegistry.Feature feat = visible.get(i);
            boolean on = feat.isEnabled();
            boolean hovered = mouseX >= cx && mouseX < cx + l.cardW() && mouseY >= cy && mouseY < cy + l.cardH();

            g.fill(cx, cy, cx + l.cardW(), cy + l.cardH(), hovered ? CARD_HOVER : CARD_BG);
            g.fill(cx, cy, cx + 3, cy + l.cardH(), ACCENT);

            g.drawString(this.font, feat.name(), cx + 8, cy + 10, TEXT_WHITE, false);
            g.drawString(this.font, feat.category(), cx + 8, cy + 22, TEXT_GRAY, false);
            if (feat == FeatureRegistry.ZOOM) {
                String keybindText = awaitingZoomKey ? "Keybind: Press key..."
                        : "Keybind: " + getCurrentZoomKeyName();
                int kbX1 = cx + 6;
                int kbY1 = cy + 32;
                int kbX2 = Math.min(cx + l.cardW() - 46, cx + 130);
                int kbY2 = cy + 46;
                boolean kbHover = isInsideZoomKeybind(mouseX, mouseY, cx, cy, l.cardW());
                int kbBg = awaitingZoomKey ? 0xFF3F5675 : (kbHover ? 0xFF384A66 : 0xFF2D3E58);
                g.fill(kbX1, kbY1, kbX2, kbY2, kbBg);
                g.drawString(this.font, keybindText, kbX1 + 4, kbY1 + 3, TEXT_WHITE, false);
            } else if (feat == FeatureRegistry.HIT_COLOR) {
                com.skyceleste.client.features.HitColor hc = (com.skyceleste.client.features.HitColor) feat;
                int previewW = 30;
                int previewX = cx + 8;
                int previewY = cy + 34;
                int previewH = 18;

                int previewColor = (0xFF000000 | (hc.r << 16) | (hc.g << 8) | hc.b);
                g.fill(previewX, previewY, previewX + previewW, previewY + previewH, previewColor);
                g.fill(previewX - 1, previewY - 1, previewX + previewW + 1, previewY, TEXT_GRAY);
                g.fill(previewX - 1, previewY + previewH, previewX + previewW + 1, previewY + previewH + 1, TEXT_GRAY);
                g.fill(previewX - 1, previewY - 1, previewX, previewY + previewH + 1, TEXT_GRAY);
                g.fill(previewX + previewW, previewY - 1, previewX + previewW + 1, previewY + previewH + 1, TEXT_GRAY);

                int resetY = cy + 54;
                int resetH = 12;
                boolean resetHover = mouseX >= previewX && mouseX < previewX + previewW && mouseY >= resetY && mouseY < resetY + resetH;
                g.fill(previewX, resetY, previewX + previewW, resetY + resetH, resetHover ? 0xFF384A66 : 0xFF2D3E58);
                // "Reset" text (width ~26px), so center it manually inside the 30px width box
                g.drawString(this.font, "Reset", previewX + 2, resetY + 2, TEXT_WHITE, false);

                int sliderY = cy + 34;
                int sliderX = cx + 64;
                int sliderWidth = Math.max(l.cardW() - 75, 40);
                
                for (int s = 0; s < 3; s++) {
                    int y = sliderY + s * 12;
                    int color = s == 0 ? 0xFFFF4444 : (s == 1 ? 0xFF44FF44 : 0xFF4444FF);
                    String label = s == 0 ? "R" : (s == 1 ? "G" : "B");
                    int val = s == 0 ? hc.r : (s == 1 ? hc.g : hc.b);
                    
                    g.fill(sliderX - 1, y - 1, sliderX + sliderWidth + 1, y + 9, TEXT_GRAY); // Border background
                    g.fill(sliderX, y, sliderX + sliderWidth, y + 8, 0xFF1A1A1A); // Dark track background
                    int fillWidth = (int) (sliderWidth * (val / 255.0f));
                    g.fill(sliderX, y, sliderX + fillWidth, y + 8, color); // Filled track
                    g.drawString(this.font, label + ":", sliderX - 14, y + 1, TEXT_WHITE, false);
                    
                    if (mouseX >= sliderX && mouseX < sliderX + sliderWidth && mouseY >= y && mouseY < y + 8) {
                        g.fill(sliderX, y - 1, sliderX + sliderWidth, y + 0, ACCENT);
                        g.fill(sliderX, y + 8, sliderX + sliderWidth, y + 9, ACCENT);
                    }
                }
            }

            int pillX = cx + l.cardW() - 38;
            int pillY = feat == FeatureRegistry.HIT_COLOR ? cy + 8 : cy + l.cardH() - 18;
            g.fill(pillX, pillY, pillX + 30, pillY + 10, on ? GREEN : RED);
            g.drawString(this.font, on ? "ON" : "OFF", pillX + 4, pillY + 1, TEXT_WHITE, false);
        }

        g.disableScissor();

        super.render(g, mouseX, mouseY, delta);
    }

    private boolean isInsideZoomKeybind(double mouseX, double mouseY, int cardX, int cardY, int cardWidth) {
        int kbX1 = cardX + 6;
        int kbY1 = cardY + 32;
        int kbX2 = Math.min(cardX + cardWidth - 46, cardX + 130);
        int kbY2 = cardY + 46;
        return mouseX >= kbX1 && mouseX < kbX2 && mouseY >= kbY1 && mouseY < kbY2;
    }

    private KeyMapping getZoomKeyMapping() {
        try {
            Field field = FeatureRegistry.ZOOM.getClass().getDeclaredField("keyMapping");
            field.setAccessible(true);
            Object value = field.get(FeatureRegistry.ZOOM);
            if (value instanceof KeyMapping keyMapping) {
                return keyMapping;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return null;
    }

    private String getCurrentZoomKeyName() {
        KeyMapping keyMapping = getZoomKeyMapping();
        if (keyMapping == null) {
            return "C";
        }
        return keyMapping.getTranslatedKeyMessage().getString();
    }

    private void applyZoomKeybind(int keyCode) {
        KeyMapping keyMapping = getZoomKeyMapping();
        if (keyMapping == null) {
            return;
        }

        keyMapping.setKey(InputConstants.Type.KEYSYM.getOrCreate(keyCode));
        KeyMapping.resetMapping();
        if (this.minecraft != null && this.minecraft.options != null) {
            this.minecraft.options.save();
        }
        FeatureRegistry.saveStates();
    }
}