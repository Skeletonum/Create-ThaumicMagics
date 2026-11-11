package com.author.createthaumicmagics.client;

import com.author.createthaumicmagics.block.ThaumostaticBlazeBurnerBlockEntity;
import com.author.createthaumicmagics.config.ThaumostaticBlazeBurnerConfig;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.aspect.Aspect;

public final class ThaumostaticBlazeBurnerHud {
    private ThaumostaticBlazeBurnerHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null
            || !GogglesItem.isWearingGoggles(minecraft.player)
            || !(minecraft.hitResult instanceof BlockHitResult hit)
            || hit.getType() != HitResult.Type.BLOCK
            || !(minecraft.level.getBlockEntity(hit.getBlockPos())
                instanceof ThaumostaticBlazeBurnerBlockEntity burner)) {
            return;
        }

        int x = 8;
        int y = 8;
        int color = 0xFFFFFF;
        int capacity = ThaumostaticBlazeBurnerConfig.maxEssenceBuffer();
        if (burner.getStoredEssentia() == 0) {
            drawLine(graphics, "Эссенция: 0/" + capacity, x, y, color);
            return;
        }

        Aspect aspect = burner.getAcceptedAspects().stream()
            .filter(candidate -> burner.getStoredEssentia(candidate) > 0)
            .findFirst()
            .orElse(Aspect.FIRE);
        drawLine(graphics,
            (aspect == Aspect.FIRE ? "Ignis" : aspect.getName()) + ": "
                + burner.getStoredEssentia(aspect) + "/" + capacity,
            x, y, color);
        boolean superheated = burner.getThaumostaticHeatKey().equals("superheated");
        drawLine(graphics, superheated ? "Superheated" : "Heated", x, y + 12,
            superheated ? 0xFF7040 : 0xFFB347);
        drawLine(graphics, "Осталось: " + burner.getRemainingBurnTimeSeconds() + " с",
            x, y + 24, color);
    }

    private static void drawLine(GuiGraphics graphics, String text, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, Component.literal(text), x, y, color, true);
    }
}
