package com.author.createthaumicmagics.menu;

import com.author.createthaumicmagics.registry.ThaumicStockCrafterRegistry;
import com.byflx.stockinstantcraft.block.StockCrafterBlockEntity;
import com.byflx.stockinstantcraft.menu.StockCrafterMenu;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.crafting.ArcaneCrafting;
import thaumcraft.crafting.ArcaneCrafting.Match;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;

import java.util.List;

public final class ThaumicStockCrafterMenu extends StockCrafterMenu {
    public static final int WAND_SLOT = INVENTORY_END_SLOT;

    private ItemStackHandler wandHandler;
    private Slot vanillaResultSlot;

    public ThaumicStockCrafterMenu(
        int id, Inventory inventory, RegistryFriendlyByteBuf buffer
    ) {
        this(ThaumicStockCrafterRegistry.MENU.get(), id, inventory, buffer);
    }

    public ThaumicStockCrafterMenu(
        net.minecraft.world.inventory.MenuType<?> type,
        int id,
        Inventory inventory,
        RegistryFriendlyByteBuf buffer
    ) {
        super(type, id, inventory, buffer);
    }

    public ThaumicStockCrafterMenu(
        net.minecraft.world.inventory.MenuType<?> type,
        int id,
        Inventory inventory,
        StockCrafterBlockEntity crafter
    ) {
        super(type, id, inventory, crafter);
    }

    public ThaumicStockCrafterMenu(
        net.minecraft.world.inventory.MenuType<?> type,
        int id,
        Inventory inventory,
        StockCrafterBlockEntity crafter,
        GlobalPos binding
    ) {
        super(type, id, inventory, crafter, binding);
    }

    @Override
    protected void addSlots() {
        super.addSlots();

        vanillaResultSlot = slots.get(RESULT_SLOT);
        slots.set(RESULT_SLOT, new Slot(
            vanillaResultSlot.container,
            vanillaResultSlot.index,
            vanillaResultSlot.x,
            vanillaResultSlot.y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                Match match = currentArcaneMatch(player);
                return match == null
                    ? vanillaResultSlot.mayPickup(player)
                    : hasRequiredVis(player, match);
            }

            @Override
            public void onQuickCraft(ItemStack stack, ItemStack originalStack) {
                vanillaResultSlot.onQuickCraft(stack, originalStack);
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                Match match = currentArcaneMatch(player);
                if (match == null) {
                    vanillaResultSlot.onTake(player, stack);
                } else {
                    craftArcane(player, match);
                }
            }
        });

        wandHandler = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot == 0 && isWand(stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                updateArcaneResult(playerInventory.player);
            }
        };
        addSlot(new WandSlot(wandHandler, 0, 0, 0));
        updateArcaneResult(playerInventory.player);
    }

    public void positionWandSlot(int x, int y) {
        slots.set(WAND_SLOT, new WandSlot(wandHandler, 0, x, y));
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateArcaneResult(playerInventory.player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex == WAND_SLOT) {
            Slot wandSlot = getSlot(WAND_SLOT);
            ItemStack wand = wandSlot.getItem();
            if (wand.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack moved = wand.copy();
            if (!moveItemStackTo(wand, INVENTORY_FIRST_SLOT, INVENTORY_END_SLOT, true)) {
                return ItemStack.EMPTY;
            }
            if (wand.isEmpty()) {
                wandSlot.set(ItemStack.EMPTY);
            } else {
                wandSlot.setChanged();
            }
            wandSlot.onTake(player, wand);
            return moved;
        }

        if (slotIndex >= INVENTORY_FIRST_SLOT && slotIndex < INVENTORY_END_SLOT) {
            ItemStack source = getSlot(slotIndex).getItem();
            if (isWand(source)) {
                ItemStack moved = source.copy();
                if (!moveItemStackTo(source, WAND_SLOT, WAND_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
                if (source.isEmpty()) {
                    getSlot(slotIndex).set(ItemStack.EMPTY);
                } else {
                    getSlot(slotIndex).setChanged();
                }
                return moved;
            }
        }

        return super.quickMoveStack(player, slotIndex);
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide && wandHandler != null) {
            returnToPlayer(player, wandHandler.extractItem(0, 1, false));
        }
        super.removed(player);
    }

    private Match currentArcaneMatch(Player player) {
        List<ItemStack> grid = java.util.stream.IntStream.range(0, GRID_SLOT_COUNT)
            .mapToObj(index -> getSlot(GRID_FIRST_SLOT + index).getItem())
            .toList();
        return ArcaneCrafting.find(grid, player);
    }

    private boolean hasRequiredVis(Player player, Match match) {
        ItemStack wand = wandHandler.getStackInSlot(0);
        if (!(wand.getItem() instanceof WandItem wandItem) || !isWand(wand)) {
            return false;
        }

        AspectList aspects = match.aspects();
        for (Aspect aspect : aspects.getAspects()) {
            int requiredVis = (int) (aspects.getAmount(aspect) * 100
                * wandItem.getConsumptionModifier(wand, player, aspect, true));
            if (Wands.getVis(wand, aspect) < requiredVis) {
                return false;
            }
        }
        return !aspects.isEmpty();
    }

    private void updateArcaneResult(Player player) {
        if (vanillaResultSlot == null || wandHandler == null) {
            return;
        }
        Match match = currentArcaneMatch(player);
        if (match != null) {
            vanillaResultSlot.set(hasRequiredVis(player, match)
                ? match.result().copy()
                : ItemStack.EMPTY);
            broadcastChanges();
        }
    }

    private void craftArcane(Player player, Match match) {
        if (player.level().isClientSide || !hasRequiredVis(player, match)) {
            return;
        }

        ItemStack wand = wandHandler.getStackInSlot(0);
        if (!(wand.getItem() instanceof WandItem wandItem)) {
            return;
        }
        if (!wandItem.consumeAllVisCrafting(wand, player, match.aspects(), true)) {
            updateArcaneResult(player);
            return;
        }

        for (int index = 0; index < GRID_SLOT_COUNT; index++) {
            Slot input = getSlot(GRID_FIRST_SLOT + index);
            if (!input.getItem().isEmpty()) {
                input.remove(1);
                input.setChanged();
            }
        }
        wandHandler.setStackInSlot(0, wand);
    }

    private static void returnToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static boolean isWand(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof WandItem;
    }

    private static final class WandSlot extends SlotItemHandler {
        private WandSlot(ItemStackHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isWand(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 1;
        }
    }
}
