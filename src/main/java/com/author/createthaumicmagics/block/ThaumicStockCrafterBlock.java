package com.author.createthaumicmagics.block;

import com.author.createthaumicmagics.registry.ThaumicStockCrafterRegistry;
import com.byflx.stockinstantcraft.block.StockCrafterBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ThaumicStockCrafterBlock extends StockCrafterBlock {
    public ThaumicStockCrafterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends com.byflx.stockinstantcraft.block.StockCrafterBlockEntity> getBlockEntityType() {
        return ThaumicStockCrafterRegistry.BLOCK_ENTITY.get();
    }
}
