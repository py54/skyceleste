package com.skyceleste.client.mixin;

import com.skyceleste.client.features.FeatureRegistry;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class AbstractContainerScreenMixin {

    private int skyceleste$pendingTooltipHeight = 0;
    private int skyceleste$pendingTooltipHash = 0;
    private boolean skyceleste$hasPendingTooltip = false;

    @Shadow
    public abstract int guiHeight();

    private int skyceleste$heightFromLines(List<?> lines) {
        if (lines.isEmpty()) return 0;
        return 8 + (lines.size() * 12) + 8;
    }

    private int skyceleste$stableHash(List<?> lines) {
        if (lines == null || lines.isEmpty()) return 0;
        int hash = 1;
        for (Object line : lines) {
            hash = 31 * hash + (line == null ? 0 : String.valueOf(line).hashCode());
        }
        return hash;
    }

    private void skyceleste$trackLines(List<?> lines) {
        skyceleste$pendingTooltipHeight = skyceleste$heightFromLines(lines);
        skyceleste$pendingTooltipHash = skyceleste$stableHash(lines);
        skyceleste$hasPendingTooltip = true;
    }

    private void skyceleste$flush() {
        if (!skyceleste$hasPendingTooltip) return;
        int visible = guiHeight() - 40;
        boolean scrollable = skyceleste$pendingTooltipHeight > visible;
        FeatureRegistry.SCROLLABLE_TOOLTIPS.markTooltipRendered(scrollable, skyceleste$pendingTooltipHash, skyceleste$pendingTooltipHeight, visible);
        skyceleste$hasPendingTooltip = false;
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private List<?> skyceleste$trackLines$list(List<?> lines) {
        skyceleste$trackLines(lines);
        return lines;
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$list(int y) {
        skyceleste$flush();
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private List<?> skyceleste$trackLines$listWithStyle(List<?> lines) {
        skyceleste$trackLines(lines);
        return lines;
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$listWithStyle(int y) {
        skyceleste$flush();
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }

    @ModifyVariable(method = "setComponentTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private List<?> skyceleste$trackLines$component(List<?> lines) {
        skyceleste$trackLines(lines);
        return lines;
    }

    @ModifyVariable(method = "setComponentTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$component(int y) {
        skyceleste$flush();
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }

    @ModifyVariable(method = "setComponentTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private List<?> skyceleste$trackLines$componentWithStyle(List<?> lines) {
        skyceleste$trackLines(lines);
        return lines;
    }

    @ModifyVariable(method = "setComponentTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$componentWithStyle(int y) {
        skyceleste$flush();
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private List<?> skyceleste$trackLines$formatted(List<?> lines) {
        skyceleste$trackLines(lines);
        return lines;
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$formatted(int y) {
        skyceleste$flush();
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private List<?> skyceleste$trackLines$formattedWithStyle(List<?> lines) {
        skyceleste$trackLines(lines);
        return lines;
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$formattedWithStyle(int y) {
        skyceleste$flush();
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;IIZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private List<?> skyceleste$trackLines$positioned(List<?> lines) {
        skyceleste$trackLines(lines);
        return lines;
    }

    @ModifyVariable(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;IIZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$positioned(int y) {
        skyceleste$flush();
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }

    @ModifyVariable(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int skyceleste$offsetY$renderTooltip(int y) {
        return y + FeatureRegistry.SCROLLABLE_TOOLTIPS.tooltipOffsetY();
    }
}