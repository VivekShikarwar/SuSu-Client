package com.notvivek00.susu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Collection;

public class SuSuHudRenderer {

    // Colors
    private static final int DARK_YELLOW = 0xFFFFAA00; // Dark Yellow / Gold
    private static final int SUBTITLE_GRAY = 0xFFAAAAAA;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int HUD_BG = 0x66000000; // Clean translucent dark background
    private static final int HUD_BORDER = 0xFFFFAA00; // Dark yellow accent bar

    public static void render(DrawContext context, int leftCps, int rightCps) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        TextRenderer font = client.textRenderer;

        // 1. LOGO & AUTHOR TEXT (Top-Left)
        context.fill(8, 8, 132, 34, HUD_BG);
        context.fill(8, 8, 10, 34, HUD_BORDER); // Left yellow accent strip
        context.drawTextWithShadow(font, "§lSuSu Client", 14, 12, DARK_YELLOW);
        context.drawTextWithShadow(font, "made by Not_Vivek00", 14, 22, SUBTITLE_GRAY);

        // 2. FPS COUNTER MODULE
        int fps = client.getCurrentFps();
        String fpsText = "FPS: " + fps;
        drawModuleBox(context, font, fpsText, 8, 40);

        // 3. CPS COUNTER MODULE
        String cpsText = "CPS: " + leftCps + " | " + rightCps;
        drawModuleBox(context, font, cpsText, 8, 60);

        // 4. POTION EFFECT TIMER HUD
        renderPotionHud(context, font, client, 8, 84);

        // 5. ARMOR HUD (Bottom-Right near hotbar)
        renderArmorHud(context, font, client);
    }

    private static void drawModuleBox(DrawContext context, TextRenderer font, String text, int x, int y) {
        int width = font.getWidth(text) + 12;
        context.fill(x, y, x + width, y + 16, HUD_BG);
        context.fill(x, y, x + 2, y + 16, HUD_BORDER);
        context.drawTextWithShadow(font, text, x + 6, y + 4, WHITE);
    }

    private static void renderPotionHud(DrawContext context, TextRenderer font, MinecraftClient client, int x, int startY) {
        Collection<StatusEffectInstance> effects = client.player.getStatusEffects();
        if (effects.isEmpty()) return;

        int y = startY;
        context.drawTextWithShadow(font, "§lActive Potions", x, y, DARK_YELLOW);
        y += 12;

        for (StatusEffectInstance effect : effects) {
            String name = Text.translatable(effect.getTranslationKey()).getString();
            int amplifier = effect.getAmplifier() + 1;
            String duration = StatusEffectUtil.getDurationText(effect, 1.0F, client.world.getTickManager().getTickRate()).getString();
            String line = name + " " + amplifier + " §7(" + duration + ")";

            context.fill(x, y - 2, x + font.getWidth(line) + 8, y + 10, HUD_BG);
            context.drawTextWithShadow(font, line, x + 4, y, WHITE);
            y += 14;
        }
    }

    private static void renderArmorHud(DrawContext context, TextRenderer font, MinecraftClient client) {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = screenWidth / 2 + 98; // Right of hotbar
        int y = screenHeight - 68;

        for (int i = 3; i >= 0; i--) {
            ItemStack armor = client.player.getInventory().armor.get(i);
            if (armor.isEmpty()) continue;

            context.drawItem(armor, x, y);
            context.drawItemInSlot(font, armor, x, y);

            if (armor.isDamageable()) {
                int remaining = armor.getMaxDamage() - armor.getDamage();
                context.drawTextWithShadow(font, String.valueOf(remaining), x + 20, y + 4, WHITE);
            }
            y += 16;
        }
    }
}