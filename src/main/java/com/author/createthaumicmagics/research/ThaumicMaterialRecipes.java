package com.author.createthaumicmagics.research;

import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.crafting.CrucibleRecipe;
import thaumcraft.crafting.TcIngredient;
import thaumcraft.crafting.TcRecipes;
import thaumcraft.crafting.TcStack;

public final class ThaumicMaterialRecipes {
    private static final String THAUMIC_BRASS_RECIPE =
        "createthaumicmagics:crucible/thaumic_brass_ingot";

    private static boolean registered;

    private ThaumicMaterialRecipes() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        Aspect praecantatio = Aspect.getAspect("praecantatio");
        if (praecantatio == null) {
            throw new IllegalStateException("Thaumcraft aspect is not initialized: praecantatio");
        }

        TcRecipes.register(new CrucibleRecipe(
            THAUMIC_BRASS_RECIPE,
            "",
            TcStack.of("createthaumicmagics:thaumic_brass_ingot"),
            TcIngredient.item("minecraft:copper_ingot"),
            new AspectList()
                .add(praecantatio, 5)
                .add(Aspect.METAL, 5)
        ));
        registered = true;
    }
}
