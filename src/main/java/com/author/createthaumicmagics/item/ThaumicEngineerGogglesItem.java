package com.author.createthaumicmagics.item;

import java.util.List;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.aspect.Aspect;
import thaumcraft.item.gear.Goggles;
import thaumcraft.item.gear.RunicArmor;
import thaumcraft.node.Revealer;
import thaumcraft.wand.VisDiscountGear;

public final class ThaumicEngineerGogglesItem extends GogglesItem implements Revealer, Goggles, VisDiscountGear, RunicArmor {
    public ThaumicEngineerGogglesItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean showNodes(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player, Aspect aspect) {
        return 5;
    }

    @Override
    public int getRunicCharge(ItemStack stack) {
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.createthaumicmagics.thaumic_engineer_goggles.tooltip"));
    }
}
