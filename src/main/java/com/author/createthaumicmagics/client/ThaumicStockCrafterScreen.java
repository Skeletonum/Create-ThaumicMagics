package com.author.createthaumicmagics.client;

import com.author.createthaumicmagics.menu.ThaumicStockCrafterMenu;
import com.byflx.stockinstantcraft.client.StockCrafterScreen;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class ThaumicStockCrafterScreen extends StockCrafterScreen {
    public ThaumicStockCrafterScreen(
        StockKeeperRequestMenu menu, Inventory inventory, Component title
    ) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        Slot result = menu.getSlot(ThaumicStockCrafterMenu.RESULT_SLOT);
        if (menu instanceof ThaumicStockCrafterMenu thaumicMenu) {
            thaumicMenu.positionWandSlot(result.x + 18, result.y - 10);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        Slot wand = menu.getSlot(ThaumicStockCrafterMenu.WAND_SLOT);
        int x = leftPos + wand.x;
        int y = topPos + wand.y;
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF29251F);
        graphics.fill(x, y, x + 16, y + 16, 0xFF51483B);
    }

    @Override
    protected void renderForeground(
        GuiGraphics graphics, int mouseX, int mouseY, float partialTick
    ) {
        super.renderForeground(graphics, mouseX, mouseY, partialTick);
        Slot wand = menu.getSlot(ThaumicStockCrafterMenu.WAND_SLOT);
        graphics.drawString(
            font,
            Component.translatable("menu.createthaumicmagics.wand"),
            wand.x - 4,
            wand.y - 11,
            0xD8C8A2,
            false
        );
    }
}
