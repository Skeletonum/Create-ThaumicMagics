package com.author.createthaumicmagics.compat.jei;

import com.author.createthaumicmagics.Createthaumicmagics;
import com.author.createthaumicmagics.registry.ThaumostaticBlazeBurnerRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.aspect.Aspect;

public final class ThaumostaticBlazeBurnerFuelCategory
    implements IRecipeCategory<ThaumostaticBlazeBurnerFuelRecipe> {
    public static final RecipeType<ThaumostaticBlazeBurnerFuelRecipe> RECIPE_TYPE = new RecipeType<>(
        ResourceLocation.fromNamespaceAndPath(Createthaumicmagics.MODID, "thaumostatic_blaze_burner_fuel"),
        ThaumostaticBlazeBurnerFuelRecipe.class
    );

    private static final int WIDTH = 160;
    private static final int HEIGHT = 58;
    private final IDrawable background;
    private final IDrawable icon;

    public ThaumostaticBlazeBurnerFuelCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        icon = guiHelper.createDrawableItemStack(
            new ItemStack(ThaumostaticBlazeBurnerRegistry.BLOCK_ITEM.get())
        );
    }

    @Override
    public RecipeType<ThaumostaticBlazeBurnerFuelRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.createthaumicmagics.thaumostatic_blaze_burner.title");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ThaumostaticBlazeBurnerFuelRecipe recipe,
                          IFocusGroup focuses) {
    }

    @Override
    public void draw(ThaumostaticBlazeBurnerFuelRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        var aspect = recipe.aspect();
        graphics.blit(aspect.getImage(), 8, 8, 0, 0, 16, 16, 16, 16);

        var font = Minecraft.getInstance().font;
        graphics.drawString(font, Component.literal(
            aspect == Aspect.FIRE ? "Ignis" : aspect.getName()
        ), 32, 7, 0xFFFFFF, false);
        graphics.drawString(font,
            Component.translatable("jei.createthaumicmagics.thaumostatic_blaze_burner.amount",
                recipe.amount()),
            32, 19, 0xD0D0D0, false);

        String duration = recipe.burnTimeSeconds() >= 60
            ? "%.0f s (%.1f min)".formatted(recipe.burnTimeSeconds(), recipe.burnTimeSeconds() / 60.0)
            : "%.1f s".formatted(recipe.burnTimeSeconds());
        graphics.drawString(font,
            Component.translatable("jei.createthaumicmagics.thaumostatic_blaze_burner.duration", duration),
            32, 31, 0xD0D0D0, false);
        graphics.drawString(font,
            Component.translatable(recipe.superheated()
                ? "jei.createthaumicmagics.thaumostatic_blaze_burner.superheated"
                : "jei.createthaumicmagics.thaumostatic_blaze_burner.heated"),
            32, 43, recipe.superheated() ? 0xFF7040 : 0xFFB347, false);
    }
}
