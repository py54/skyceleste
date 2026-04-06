package com.skyceleste.client.mixin;

import com.skyceleste.client.features.FeatureRegistry;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(method = "clearMessages(Z)V", at = @At("HEAD"), cancellable = true)
    private void onClearMessages(boolean clearSent, CallbackInfo ci) {
        if (FeatureRegistry.INFINITE_CHAT.isEnabled()) {
            ci.cancel();
        }
    }

    @ModifyConstant(method = "*", constant = @Constant(intValue = 100), require = 0)
    private int modifyChatLimit(int original) {
        if (FeatureRegistry.INFINITE_CHAT.isEnabled()) {
            return 10000;
        }
        return original;
    }
}
