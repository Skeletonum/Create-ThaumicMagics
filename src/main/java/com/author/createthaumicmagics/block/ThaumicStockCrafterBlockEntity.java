package com.author.createthaumicmagics.block;

import com.author.createthaumicmagics.menu.ThaumicStockCrafterMenu;
import com.author.createthaumicmagics.registry.ThaumicStockCrafterRegistry;
import com.byflx.stockinstantcraft.block.StockCrafterBlockEntity;
import com.byflx.stockinstantcraft.menu.StockCrafterMenu;
import com.byflx.stockinstantcraft.wireless.RemoteSessions;
import com.byflx.stockinstantcraft.wireless.RemoteState;
import com.simibubi.create.Create;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public final class ThaumicStockCrafterBlockEntity extends StockCrafterBlockEntity {
    public ThaumicStockCrafterBlockEntity(BlockPos pos, BlockState state) {
        this(ThaumicStockCrafterRegistry.BLOCK_ENTITY.get(), pos, state);
    }

    public ThaumicStockCrafterBlockEntity(
        BlockEntityType<?> type, BlockPos pos, BlockState state
    ) {
        super(type, pos, state);
    }

    @Override
    public void openCraftingMenu(ServerPlayer player) {
        boolean admin = behaviour.mayAdministrate(player)
            && Create.LOGISTICS.isLockable(behaviour.freqId);
        boolean locked = Create.LOGISTICS.isLocked(behaviour.freqId);
        player.openMenu(
            new SimpleMenuProvider(
                (id, inventory, ignored) -> new ThaumicStockCrafterMenu(
                    ThaumicStockCrafterRegistry.MENU.get(), id, inventory, this
                ),
                Component.empty()
            ),
            buffer -> buffer.writeBoolean(admin)
                .writeBoolean(locked)
                .writeBlockPos(getBlockPos())
                .writeBoolean(false)
        );
        getRecentSummary().divideAndSendTo(player, getBlockPos());
    }

    @Override
    public StockCrafterMenu openRemoteCraftingMenu(ServerPlayer player, GlobalPos binding) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        RemoteState state = remoteState(player);
        boolean locked = Create.LOGISTICS.isLocked(behaviour.freqId);
        RemoteSessions.Session session = RemoteSessions.prepare(player, serverLevel, getBlockPos());
        player.openMenu(
            new SimpleMenuProvider(
                (id, inventory, ignored) -> new ThaumicStockCrafterMenu(
                    ThaumicStockCrafterRegistry.MENU.get(), id, inventory, this, binding
                ),
                Component.empty()
            ),
            buffer -> {
                buffer.writeBoolean(false)
                    .writeBoolean(locked)
                    .writeBlockPos(getBlockPos())
                    .writeBoolean(true);
                GlobalPos.STREAM_CODEC.encode(buffer, binding);
                RemoteState.STREAM_CODEC.encode(buffer, state);
            }
        );

        if (player.containerMenu instanceof ThaumicStockCrafterMenu menu
            && menu.isRemote() && menu.crafter() == this) {
            RemoteSessions.start(session, menu);
            return menu;
        }
        return null;
    }
}
