package com.author.createthaumicmagics.item;

import com.byflx.stockinstantcraft.wireless.WirelessStockCrafterItem;
import net.minecraft.world.item.ItemStack;

public final class WirelessThaumicStockCrafterItem extends WirelessStockCrafterItem {
    public WirelessThaumicStockCrafterItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
