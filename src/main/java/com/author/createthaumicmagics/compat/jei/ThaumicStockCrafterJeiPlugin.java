package com.author.createthaumicmagics.compat.jei;

import com.author.createthaumicmagics.config.ThaumostaticBlazeBurnerConfig;
import com.author.createthaumicmagics.menu.ThaumicStockCrafterMenu;
import com.author.createthaumicmagics.registry.ThaumostaticBlazeBurnerRegistry;
import com.author.createthaumicmagics.registry.ThaumicStockCrafterRegistry;
import com.byflx.stockinstantcraft.menu.StockCrafterMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.recipeview.ArcaneView;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public final class ThaumicStockCrafterJeiPlugin implements IModPlugin {
    private static final RecipeType<ArcaneView> ARCANE_RECIPE_TYPE = new RecipeType<>(
        ResourceLocation.fromNamespaceAndPath("thaumcraft", "arcane"),
        ArcaneView.class
    );

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("createthaumicmagics", "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ThaumostaticBlazeBurnerFuelCategory(
            registration.getJeiHelpers().getGuiHelper()
        ));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ThaumostaticBlazeBurnerFuelRecipe> recipes = new ArrayList<>();
        double ticksPerUnit = ThaumostaticBlazeBurnerConfig.ticksPerEssenceUnit();
        for (var aspect : ThaumostaticBlazeBurnerConfig.heatedEssences()) {
            recipes.add(new ThaumostaticBlazeBurnerFuelRecipe(aspect, 25, ticksPerUnit, false));
        }
        for (var aspect : ThaumostaticBlazeBurnerConfig.superheatedEssences()) {
            recipes.add(new ThaumostaticBlazeBurnerFuelRecipe(aspect, 1, 100, true));
        }
        registration.addRecipes(ThaumostaticBlazeBurnerFuelCategory.RECIPE_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
            new ItemStack(ThaumostaticBlazeBurnerRegistry.BLOCK_ITEM.get()),
            ThaumostaticBlazeBurnerFuelCategory.RECIPE_TYPE
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
            ThaumicStockCrafterMenu.class,
            ThaumicStockCrafterRegistry.MENU.get(),
            ARCANE_RECIPE_TYPE,
            StockCrafterMenu.GRID_FIRST_SLOT,
            StockCrafterMenu.GRID_SLOT_COUNT,
            StockCrafterMenu.INVENTORY_FIRST_SLOT,
            StockCrafterMenu.INVENTORY_END_SLOT - StockCrafterMenu.INVENTORY_FIRST_SLOT
        );
    }
}
