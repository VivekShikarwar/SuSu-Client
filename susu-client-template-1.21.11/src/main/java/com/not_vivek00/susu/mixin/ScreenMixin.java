package com.notvivek00.susu.mixin;

import com.notvivek00.susu.SuSuClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    private void renderCleanFadeBlur(DrawContext context, CallbackInfo ci) {
        if (!SuSuClient.guiBlur) return;
        Screen self = (Screen) (Object) this;
        // Clean Feather-style dark translucent backdrop + subtle yellow bottom glow
        context.fillGradient(0, 0, self.width, self.height, 0xAA080808, 0xCC1A1405);
        ci.cancel();
    }
}