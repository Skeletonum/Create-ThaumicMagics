package com.author.createthaumicmagics.registry;

import com.author.createthaumicmagics.Createthaumicmagics;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(Createthaumicmagics.MODID);

    public static final DeferredItem<Item> THAUMIC_BRASS_INGOT =
        ITEMS.registerSimpleItem("thaumic_brass_ingot");
    public static final DeferredItem<Item> THAUMIC_BRASS_SHEET =
        ITEMS.registerSimpleItem("thaumic_brass_sheet");
    public static final DeferredItem<Item> THAUMIUM_SHEET =
        ITEMS.registerSimpleItem("thaumium_sheet");

    private ModItems() {
    }
}
