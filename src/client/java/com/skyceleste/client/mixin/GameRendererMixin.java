package com.skyceleste.client.mixin;

import com.skyceleste.client.features.FeatureRegistry;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getDarkenWorldAmount", at = @At("RETURN"), cancellable = true)
    private void skyceleste$forceDarkenWorldAmount(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (FeatureRegistry.FULLBRIGHT.isEnabled()) {
            cir.setReturnValue(0.0F);
        }
    }
}