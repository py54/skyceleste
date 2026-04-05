package com.skyceleste.client.mixin;

import com.skyceleste.client.gui.SkyCelesteScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends net.minecraft.client.gui.screens.Screen {

    protected PauseScreenMixin() {
        super(Component.empty());
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addSkyCelesteButton(CallbackInfo ci) {
        int buttonY = this.height / 4 + 96 + 30;
        this.addRenderableWidget(
                Button.builder(Component.literal("SkyCeleste Menu"),
                        btn -> this.minecraft.setScreen(new SkyCelesteScreen()))
                        .bounds(this.width / 2 - 102, buttonY, 204, 20)
                        .build());
    }
}