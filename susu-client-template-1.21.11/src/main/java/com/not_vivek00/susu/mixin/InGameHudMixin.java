package com.notvivek00.susu.mixin;

import com.notvivek00.susu.SuSuClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCustomCrosshair(DrawContext context, CallbackInfo ci) {
        if (!SuSuClient.customCrosshair) return;
        ci.cancel();

        MinecraftClient client = MinecraftClient.getInstance();
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;

        int color = 0xFFFFAA00; // Dark Yellow Crosshair
        int outline = 0xFF000000;

        // Center dot + clean gap crosshair
        context.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
        context.fill(cx - 5, cy, cx - 2, cy + 1, color); // Left
        context.fill(cx + 2, cy, cx + 5, cy + 1, color); // Right
        context.fill(cx, cy - 5, cx + 1, cy - 2, color); // Top
        context.fill(cx, cy + 2, cx + 1, cy + 5, color); // Bottom
    }
}