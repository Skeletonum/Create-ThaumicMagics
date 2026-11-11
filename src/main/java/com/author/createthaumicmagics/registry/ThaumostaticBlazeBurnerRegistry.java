package com.author.createthaumicmagics.registry;

import com.author.createthaumicmagics.Createthaumicmagics;
import com.author.createthaumicmagics.block.ThaumostaticBlazeBurnerBlock;
import com.author.createthaumicmagics.block.ThaumostaticBlazeBurnerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ThaumostaticBlazeBurnerRegistry {
    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(Createthaumicmagics.MODID);
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(Createthaumicmagics.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Createthaumicmagics.MODID);

    public static final DeferredBlock<ThaumostaticBlazeBurnerBlock> BLOCK = BLOCKS.register(
        "thaumostatic_blaze_burner",
        () -> new ThaumostaticBlazeBurnerBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .noOcclusion()
                .lightLevel(ThaumostaticBlazeBurnerBlock::getLight)
        )
    );
    public static final DeferredItem<BlockItem> BLOCK_ITEM = ITEMS.register(
        "thaumostatic_blaze_burner",
        () -> new BlockItem(BLOCK.get(), new Item.Properties()) {
            @Override
            public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
                return true;
            }
        }
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThaumostaticBlazeBurnerBlockEntity>> BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "thaumostatic_blaze_burner",
            () -> BlockEntityType.Builder.of(ThaumostaticBlazeBurnerBlockEntity::new, BLOCK.get()).build(null)
        );

    private ThaumostaticBlazeBurnerRegistry() {
    }
}
