package com.notvivek00.susu.mixin;

import com.notvivek00.susu.SuSuClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "renderEntityHitbox", at = @At("HEAD"), cancellable = true)
    private static void customHitboxColor(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        if (!SuSuClient.customHitbox) return;
        // Draws custom Dark Yellow (Red: 1.0f, Green: 0.66f, Blue: 0.0f) hitbox
        WorldRenderer.drawBox(
                matrices,
                vertices,
                entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ()),
                1.0F, 0.66F, 0.0F, 1.0F
        );
        ci.cancel();
    }
}