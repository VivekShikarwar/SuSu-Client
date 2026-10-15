package com.notvivek00.susu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SuSuClient implements ClientModInitializer {

    // Feature Toggles
    public static boolean showHud = true;
    public static boolean customCrosshair = true;
    public static boolean customHitbox = true;
    public static boolean guiBlur = true;

    // CPS Counter Tracking
    private static final List<Long> leftClicks = new ArrayList<>();
    private static final List<Long> rightClicks = new ArrayList<>();
    private boolean wasLeftPressed = false;
    private boolean wasRightPressed = false;

    private static KeyBinding toggleHudKey;

    @Override
    public void onInitializeClient() {
        // Register Right-Shift to toggle HUD
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.susuclient.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "SuSu Client"
        ));

        // Apply low-end laptop FPS presets on startup
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleHudKey.wasPressed()) {
                showHud = !showHud;
            }
            applyLowEndOptimizations(client);
        });

        // Register HUD Overlay (Logo, FPS, CPS, Armor, Potions)
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            updateClicks();
            if (showHud) {
                SuSuHudRenderer.render(drawContext, getLeftCps(), getRightCps());
            }
        });
    }

    private void updateClicks() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getWindow() == null) return;

        long window = client.getWindow().getHandle();
        long now = System.currentTimeMillis();

        boolean leftPressed = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (leftPressed && !wasLeftPressed) {
            leftClicks.add(now);
        }
        wasLeftPressed = leftPressed;

        boolean rightPressed = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (rightPressed && !wasRightPressed) {
            rightClicks.add(now);
        }
        wasRightPressed = rightPressed;

        leftClicks.removeIf(time -> now - time > 1000);
        rightClicks.removeIf(time -> now - time > 1000);
    }

    public static int getLeftCps() {
        return leftClicks.size();
    }

    public static int getRightCps() {
        return rightClicks.size();
    }

    private boolean optimizedOnce = false;
    private void applyLowEndOptimizations(MinecraftClient client) {
        if (optimizedOnce || client.options == null) return;
        // Optimizes settings for low-end laptops automatically
        client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
        client.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        client.options.getParticles().setValue(ParticlesMode.MINIMAL);
        client.options.getEntityShadows().setValue(false);
        client.options.getBiomeBlendRadius().setValue(0);
        optimizedOnce = true;
    }
}