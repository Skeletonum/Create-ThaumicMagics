package com.author.createthaumicmagics.block;

import com.author.createthaumicmagics.config.ThaumostaticBlazeBurnerConfig;
import com.author.createthaumicmagics.registry.ThaumostaticBlazeBurnerRegistry;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.aspect.Aspect;
import thaumcraft.essentia.EssentiaTransport;
import thaumcraft.essentia.JarBlockEntity;
import thaumcraft.vis.VisNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ThaumostaticBlazeBurnerBlockEntity extends BlazeBurnerBlockEntity
    implements EssentiaTransport, IHaveGoggleInformation {
    private static final int SUPERHEAT_FUEL_TIME = 100;
    private static final int LEGACY_ESSENTIA_CAPACITY = 64;
    private static final int JAR_SEARCH_RADIUS = 8;
    private static final int JAR_SEARCH_INTERVAL = 20;
    private int ignisEssentia;
    private final Map<Aspect, Integer> otherEssentia = new HashMap<>();
    private int superheatTicks;
    private double burnTickRemainder;

    public ThaumostaticBlazeBurnerBlockEntity(BlockPos pos, BlockState state) {
        this(ThaumostaticBlazeBurnerRegistry.BLOCK_ENTITY.get(), pos, state);
    }

    public ThaumostaticBlazeBurnerBlockEntity(
        BlockEntityType<?> type, BlockPos pos, BlockState state
    ) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) {
            return;
        }

        if (level.getGameTime() % JAR_SEARCH_INTERVAL == 0
            && getStoredEssentia() < ThaumostaticBlazeBurnerConfig.maxEssenceBuffer()) {
            pullIgnisFromNearbyJars();
        }

        if (remainingBurnTime <= 0 && consumeEssentia(ThaumostaticBlazeBurnerConfig.heatedEssences())) {
            activeFuel = FuelType.NORMAL;
            double ticksPerUnit = ThaumostaticBlazeBurnerConfig.ticksPerEssenceUnit();
            remainingBurnTime = (int) Math.floor(ticksPerUnit);
            burnTickRemainder += ticksPerUnit - remainingBurnTime;
            if (burnTickRemainder >= 1.0) {
                remainingBurnTime++;
                burnTickRemainder -= 1.0;
            }
            updateBlockState();
            setChanged();
            notifyUpdate();
        }

        if (superheatTicks > 0) {
            superheatTicks--;
            if (superheatTicks == 0 && consumeSuperheatFuel(true)) {
                superheatTicks = SUPERHEAT_FUEL_TIME;
            }
            if (superheatTicks == 0 || superheatTicks == SUPERHEAT_FUEL_TIME) {
                updateBlockState();
                setChanged();
                notifyUpdate();
            }
        } else if (consumeSuperheatFuel(level.getGameTime() % 20 == 0)) {
            superheatTicks = SUPERHEAT_FUEL_TIME;
            updateBlockState();
            setChanged();
            notifyUpdate();
        }
    }

    private boolean consumeSuperheatFuel(boolean checkVis) {
        if (consumeEssentia(ThaumostaticBlazeBurnerConfig.superheatedEssences())) {
            return true;
        }
        return checkVis && VisNet.drainVis(level, worldPosition, Aspect.FIRE, 1) > 0;
    }

    private boolean consumeEssentia(List<Aspect> acceptedAspects) {
        for (Aspect aspect : acceptedAspects) {
            if (getEssentiaAmount(aspect) <= 0) {
                continue;
            }
            if (aspect == Aspect.FIRE) {
                ignisEssentia--;
            } else {
                int remaining = otherEssentia.get(aspect) - 1;
                if (remaining == 0) {
                    otherEssentia.remove(aspect);
                } else {
                    otherEssentia.put(aspect, remaining);
                }
            }
            return true;
        }
        return false;
    }

    private void storeEssentia(Aspect aspect, int amount) {
        if (aspect == Aspect.FIRE) {
            ignisEssentia += amount;
        } else {
            otherEssentia.merge(aspect, amount, Integer::sum);
        }
    }

    private void pullIgnisFromNearbyJars() {
        int remainingCapacity = ThaumostaticBlazeBurnerConfig.maxEssenceBuffer() - getStoredEssentia();
        if (remainingCapacity <= 0) {
            return;
        }
        boolean transferred = false;
        List<Aspect> acceptedAspects = getAcceptedAspects();

        for (int x = -JAR_SEARCH_RADIUS; x <= JAR_SEARCH_RADIUS && remainingCapacity > 0; x++) {
            for (int y = -JAR_SEARCH_RADIUS; y <= JAR_SEARCH_RADIUS && remainingCapacity > 0; y++) {
                for (int z = -JAR_SEARCH_RADIUS; z <= JAR_SEARCH_RADIUS && remainingCapacity > 0; z++) {
                    if (x * x + y * y + z * z > JAR_SEARCH_RADIUS * JAR_SEARCH_RADIUS) {
                        continue;
                    }

                    BlockPos jarPos = worldPosition.offset(x, y, z);
                    if (!level.hasChunkAt(jarPos)
                        || !(level.getBlockEntity(jarPos) instanceof JarBlockEntity jar)) {
                        continue;
                    }

                    Aspect aspect = jar.getAspect();
                    if (aspect == null || !acceptedAspects.contains(aspect)) {
                        continue;
                    }

                    int amount = Math.min(remainingCapacity, jar.getAmount());
                    if (amount > 0 && jar.takeFromContainer(aspect, amount)) {
                        storeEssentia(aspect, amount);
                        remainingCapacity -= amount;
                        transferred = true;
                    }
                }
            }
        }

        if (transferred) {
            setChanged();
            notifyUpdate();
        }
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("ThaumostaticSuperheatTicks", superheatTicks);
        tag.putInt("ThaumostaticIgnisEssentia", ignisEssentia);
        tag.putInt("ThaumostaticEssentia", ignisEssentia);
        tag.putDouble("ThaumostaticBurnTickRemainder", burnTickRemainder);
        CompoundTag storedAspects = new CompoundTag();
        otherEssentia.forEach((aspect, amount) -> storedAspects.putInt(aspect.getTag(), amount));
        tag.put("ThaumostaticAspectBuffer", storedAspects);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        remainingBurnTime = Math.min(remainingBurnTime,
            (int) Math.ceil(ThaumostaticBlazeBurnerConfig.ticksPerEssenceUnit()));
        superheatTicks = Math.max(0, Math.min(SUPERHEAT_FUEL_TIME,
            tag.getInt("ThaumostaticSuperheatTicks")));
        int capacity = ThaumostaticBlazeBurnerConfig.maxEssenceBuffer();
        ignisEssentia = Math.min(capacity, Math.max(0, tag.contains("ThaumostaticIgnisEssentia")
            ? tag.getInt("ThaumostaticIgnisEssentia")
            : Math.min(LEGACY_ESSENTIA_CAPACITY, tag.getInt("ThaumostaticEssentia"))));
        burnTickRemainder = Math.max(0, Math.min(0.999999, tag.getDouble("ThaumostaticBurnTickRemainder")));
        otherEssentia.clear();
        CompoundTag storedAspects = tag.getCompound("ThaumostaticAspectBuffer");
        int remainingCapacity = capacity - ignisEssentia;
        for (String aspectTag : storedAspects.getAllKeys()) {
            Aspect aspect = ThaumostaticBlazeBurnerConfig.resolveAspect(aspectTag);
            if (aspect == null || aspect == Aspect.FIRE || remainingCapacity <= 0) {
                continue;
            }
            int amount = Math.min(remainingCapacity, Math.max(0, storedAspects.getInt(aspectTag)));
            if (amount > 0) {
                otherEssentia.put(aspect, amount);
                remainingCapacity -= amount;
            }
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        String heatKey = getThaumostaticHeatKey();
        tooltip.add(Component.translatable(
            "goggle.createthaumicmagics.thaumostatic_blaze_burner.heat",
            Component.translatable(
                "goggle.createthaumicmagics.thaumostatic_blaze_burner.heat." + heatKey
            ).withStyle(ChatFormatting.GOLD)
        ));
        tooltip.add(Component.translatable(
            "goggle.createthaumicmagics.thaumostatic_blaze_burner.essentia",
            getStoredEssentia(),
            getEssentiaCapacity()
        ));
        tooltip.add(Component.translatable(
            "goggle.createthaumicmagics.thaumostatic_blaze_burner.burn_time",
            (Math.max(0, Math.max(remainingBurnTime, superheatTicks)) + 19) / 20
        ));
        return true;
    }

    @Override
    public boolean isConnectable(Direction direction) {
        return true;
    }

    @Override
    public boolean canInputFrom(Direction direction) {
        return direction != null && !getAcceptedAspects().isEmpty()
            && getStoredEssentia() < getEssentiaCapacity();
    }

    @Override
    public boolean canOutputTo(Direction direction) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public Aspect getSuctionType(Direction direction) {
        if (!canInputFrom(direction)) {
            return null;
        }
        List<Aspect> acceptedAspects = getAcceptedAspects();
        return acceptedAspects.get(direction.get3DDataValue() % acceptedAspects.size());
    }

    @Override
    public int getSuctionAmount(Direction direction) {
        return canInputFrom(direction)
            ? Math.min(64, getEssentiaCapacity() - getStoredEssentia())
            : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction direction) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction direction) {
        if (!canInputFrom(direction) || amount <= 0) {
            return 0;
        }

        if (!getAcceptedAspects().contains(aspect)) {
            return 0;
        }
        int accepted = Math.min(amount, getEssentiaCapacity() - getStoredEssentia());
        if (accepted > 0) {
            storeEssentia(aspect, accepted);
            setChanged();
            notifyUpdate();
        }
        return accepted;
    }

    @Override
    public Aspect getEssentiaType(Direction direction) {
        return null;
    }

    @Override
    public int getEssentiaAmount(Direction direction) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 32;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    public boolean pourEssentia(Aspect aspect, int requestedAmount) {
        if (level == null || level.isClientSide || requestedAmount <= 0
            || !getAcceptedAspects().contains(aspect)) {
            return false;
        }

        int accepted = Math.min(requestedAmount, getEssentiaCapacity() - getStoredEssentia());
        if (accepted <= 0) {
            return false;
        }

        storeEssentia(aspect, accepted);
        setChanged();
        notifyUpdate();
        return true;
    }

    public int getStoredEssentia() {
        return ignisEssentia + otherEssentia.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getStoredEssentia(Aspect aspect) {
        return getEssentiaAmount(aspect);
    }

    public int getEssentiaCapacity() {
        return ThaumostaticBlazeBurnerConfig.maxEssenceBuffer();
    }

    public int getRemainingBurnTimeSeconds() {
        return (Math.max(0, Math.max(remainingBurnTime, superheatTicks)) + 19) / 20;
    }

    public boolean acceptsAspect(Aspect aspect) {
        return getAcceptedAspects().contains(aspect);
    }

    public List<Aspect> getAcceptedAspects() {
        List<Aspect> aspects = new ArrayList<>(ThaumostaticBlazeBurnerConfig.heatedEssences());
        for (Aspect aspect : ThaumostaticBlazeBurnerConfig.superheatedEssences()) {
            if (!aspects.contains(aspect)) {
                aspects.add(aspect);
            }
        }
        return List.copyOf(aspects);
    }

    private int getEssentiaAmount(Aspect aspect) {
        if (aspect == Aspect.FIRE) {
            return ignisEssentia;
        }
        return otherEssentia.getOrDefault(aspect, 0);
    }

    public String getThaumostaticHeatKey() {
        BlazeBurnerBlock.HeatLevel heat = getHeatLevelFromBlock();
        if (superheatTicks > 0 || heat == BlazeBurnerBlock.HeatLevel.SEETHING) {
            return "superheated";
        }
        if (heat == BlazeBurnerBlock.HeatLevel.FADING) {
            return "kindled";
        }
        return heat.isAtLeast(BlazeBurnerBlock.HeatLevel.KINDLED) ? "heated" : "unlit";
    }

    @Override
    protected BlazeBurnerBlock.HeatLevel getHeatLevel() {
        return superheatTicks > 0
            ? BlazeBurnerBlock.HeatLevel.SEETHING
            : super.getHeatLevel();
    }
}
