package com.author.createthaumicmagics.block;

import com.author.createthaumicmagics.registry.ThaumostaticBlazeBurnerRegistry;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.aspect.AspectProvidingItem;
import thaumcraft.essentia.FilledJarItem;
import thaumcraft.essentia.PhialItem;
import thaumcraft.registry.ModItems;

public final class ThaumostaticBlazeBurnerBlock extends BlazeBurnerBlock {
    public ThaumostaticBlazeBurnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends BlazeBurnerBlockEntity> getBlockEntityType() {
        return ThaumostaticBlazeBurnerRegistry.BLOCK_ENTITY.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThaumostaticBlazeBurnerBlockEntity(
            ThaumostaticBlazeBurnerRegistry.BLOCK_ENTITY.get(), pos, state
        );
    }

    @Override
    public ItemStack getCloneItemStack(
        BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player
    ) {
        return ThaumostaticBlazeBurnerRegistry.BLOCK_ITEM.get().getDefaultInstance();
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        net.minecraft.world.level.Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if ((stack.getItem() instanceof PhialItem || stack.getItem() instanceof FilledJarItem)
            && stack.getItem() instanceof AspectProvidingItem aspectItem) {
            AspectList aspects = aspectItem.getAspects(stack);
            if (level.getBlockEntity(pos) instanceof ThaumostaticBlazeBurnerBlockEntity burner
                && aspects != null) {
                Aspect fuelAspect = burner.getAcceptedAspects().stream()
                    .filter(aspect -> aspects.getAmount(aspect) > 0)
                    .findFirst()
                    .orElse(null);
                int availableEssentia = fuelAspect == null ? 0 : aspects.getAmount(fuelAspect);
                if (availableEssentia <= 0) {
                    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
                }
                int requested = stack.getItem() instanceof PhialItem
                    ? Math.min(8, availableEssentia)
                    : availableEssentia;
                int accepted = Math.min(requested,
                    burner.getEssentiaCapacity() - burner.getStoredEssentia());
                if (accepted <= 0) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (!burner.pourEssentia(fuelAspect, accepted)) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }

                if (!player.isCreative()) {
                    AspectList remaining = aspects.remove(fuelAspect, accepted);
                    if (stack.getItem() == ModItems.ESSENTIA_PHIAL.get() && remaining.isEmpty()) {
                        ItemStack emptyPhial = new ItemStack(ModItems.GLASS_PHIAL.get());
                        if (stack.getCount() == 1) {
                            player.setItemInHand(hand, emptyPhial);
                        } else {
                            stack.shrink(1);
                            if (!player.getInventory().add(emptyPhial)) {
                                player.drop(emptyPhial, false);
                            }
                        }
                    } else {
                        aspectItem.setAspects(stack, remaining);
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
