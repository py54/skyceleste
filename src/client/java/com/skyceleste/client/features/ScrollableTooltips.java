package com.skyceleste.client.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ScrollableTooltips implements FeatureRegistry.Feature {

    private static final int SCROLL_STEP = 12;

    private boolean enabled = false;

    private int scrollPixels = 0;
    private int currentTooltipHash = 0;
    private boolean currentTooltipScrollable = false;

    private int fullTooltipHeight = 0;
    private int visibleHeight = 0;

    private Screen lastScreen = null;

    @Override
    public String id() {
        return "scrollable_tooltips";
    }

    @Override
    public String name() {
        return "Scrollable Tooltips";
    }

    @Override
    public String category() {
        return "Quality Of Life";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) reset();
    }

    @Override
    public void onClientTick(Minecraft minecraft) {
        if (minecraft == null) return;
        Screen current = minecraft.screen;
        if (current != lastScreen) {
            reset();
            lastScreen = current;
        }
    }

    public void markTooltipRendered(boolean scrollable, int tooltipHash, int fullHeight, int visibleHeight) {
        if (!enabled) return;

        if (tooltipHash != currentTooltipHash) {
            scrollPixels = 0;
            currentTooltipHash = tooltipHash;
        }

        this.currentTooltipScrollable = scrollable;
        this.fullTooltipHeight = Math.max(0, fullHeight);
        this.visibleHeight = Math.max(0, visibleHeight);

        clampOffset();
    }

    public boolean shouldCaptureScroll() {
        return enabled && currentTooltipScrollable && fullTooltipHeight > visibleHeight;
    }

    public void scrollBy(double verticalAmount) {
        if (!shouldCaptureScroll() || verticalAmount == 0.0) return;

        scrollPixels -= (int) Math.round(verticalAmount * SCROLL_STEP);
        clampOffset();
    }

    public int tooltipOffsetY() {
        return shouldCaptureScroll() ? -scrollPixels : 0;
    }

    private void clampOffset() {
        if (fullTooltipHeight <= visibleHeight) {
            scrollPixels = 0;
            return;
        }

        int maxScroll = fullTooltipHeight - visibleHeight;
        scrollPixels = Math.max(0, Math.min(maxScroll, scrollPixels));
    }

    private void reset() {
        scrollPixels = 0;
        currentTooltipHash = 0;
        currentTooltipScrollable = false;
        fullTooltipHeight = 0;
        visibleHeight = 0;
    }
}