package com.skyceleste.client.mixin;

import com.skyceleste.client.features.FeatureRegistry;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Inject(method = "getBrightness(FI)F", at = @At("RETURN"), cancellable = true)
    private static void skyceleste$forceBlockSkyBrightness(float ambientDarkness, int lightLevel,
            CallbackInfoReturnable<Float> cir) {
        if (FeatureRegistry.FULLBRIGHT.isEnabled()) {
            cir.setReturnValue(1.0F);
        }
    }

    @Inject(method = "getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F", at = @At("RETURN"), cancellable = true)
    private static void skyceleste$forceDimensionBrightness(DimensionType dimensionType, int lightLevel,
            CallbackInfoReturnable<Float> cir) {
        if (FeatureRegistry.FULLBRIGHT.isEnabled()) {
            cir.setReturnValue(1.0F);
        }
    }
}