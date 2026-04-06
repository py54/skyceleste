package com.skyceleste.client.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skyceleste.client.utils.HitColorHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayTexture.class)
public class OverlayTextureMixin {
    @Shadow
    @Final
    private DynamicTexture texture;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        HitColorHelper.ON_COLOR_UPDATE = () -> {
            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                NativeImage nativeImage = this.texture.getPixels();
                if (nativeImage == null) return;

                int r = HitColorHelper.getRed();
                int g = HitColorHelper.getGreen();
                int b = HitColorHelper.getBlue();
                int a = 178; // Default alpha for hit color

                int color = (a << 24) | (r << 16) | (g << 8) | b; // ARGB for 1.20+ NativeImage

                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (i < 8) {
                            nativeImage.setPixel(j, i, color);
                        }
                    }
                }

                this.texture.upload();
            });
        };
        HitColorHelper.updateColorConfig();
    }
}
