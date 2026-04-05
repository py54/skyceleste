package com.skyceleste.client.mixin;

import com.skyceleste.client.features.FeatureRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void skyceleste$onScroll(long window, double horizontalOffset, double verticalOffset, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }

        if (FeatureRegistry.ZOOM.isZoomKeyDown()) {
            FeatureRegistry.ZOOM.adjustZoomByScroll(verticalOffset);
            ci.cancel();
            return;
        }

        if (minecraft.screen != null && FeatureRegistry.SCROLLABLE_TOOLTIPS.shouldCaptureScroll()) {
            FeatureRegistry.SCROLLABLE_TOOLTIPS.scrollBy(verticalOffset);
            ci.cancel();
        }
    }
}
