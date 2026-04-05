package com.skyceleste.client.mixin;

import com.skyceleste.client.features.FeatureRegistry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @ModifyVariable(method = "mouseScrolled", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double skyceleste$tooltipScroll(double verticalAmount) {
        if (FeatureRegistry.SCROLLABLE_TOOLTIPS.shouldCaptureScroll()) {
            FeatureRegistry.SCROLLABLE_TOOLTIPS.scrollBy(verticalAmount);
            return 0.0;
        }
        return verticalAmount;
    }
}