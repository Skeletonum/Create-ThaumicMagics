package com.author.createthaumicmagics.registry;

import com.author.createthaumicmagics.Createthaumicmagics;
import com.author.createthaumicmagics.block.ThaumicStockCrafterBlock;
import com.author.createthaumicmagics.block.ThaumicStockCrafterBlockEntity;
import com.author.createthaumicmagics.item.WirelessThaumicStockCrafterItem;
import com.author.createthaumicmagics.menu.ThaumicStockCrafterMenu;
import com.byflx.stockinstantcraft.block.StockCrafterBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThaumicStockCrafterRegistry {
    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(Createthaumicmagics.MODID);
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(Createthaumicmagics.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Createthaumicmagics.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(Registries.MENU, Createthaumicmagics.MODID);

    public static final DeferredBlock<ThaumicStockCrafterBlock> BLOCK = BLOCKS.register(
        "thaumic_stock_crafter",
        () -> new ThaumicStockCrafterBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F).noOcclusion()
        )
    );
    public static final DeferredItem<BlockItem> BLOCK_ITEM = ITEMS.register(
        "thaumic_stock_crafter",
        () -> new LogisticallyLinkedBlockItem(BLOCK.get(), new Item.Properties()) {
            @Override
            public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
                return true;
            }
        }
    );
    public static final DeferredItem<WirelessThaumicStockCrafterItem> WIRELESS_ITEM = ITEMS.register(
        "wireless_thaumic_stock_crafter",
        () -> new WirelessThaumicStockCrafterItem(new Item.Properties())
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThaumicStockCrafterBlockEntity>> BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "thaumic_stock_crafter",
            () -> BlockEntityType.Builder.of(ThaumicStockCrafterBlockEntity::new, BLOCK.get()).build(null)
        );
    public static final DeferredHolder<MenuType<?>, MenuType<ThaumicStockCrafterMenu>> MENU =
        MENUS.register(
            "thaumic_stock_crafter",
            () -> new MenuType<>(
                (IContainerFactory<ThaumicStockCrafterMenu>) ThaumicStockCrafterMenu::new,
                net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS
            )
        );

    private ThaumicStockCrafterRegistry() {
    }
}
