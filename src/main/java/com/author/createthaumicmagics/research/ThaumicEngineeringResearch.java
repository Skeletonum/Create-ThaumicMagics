package com.author.createthaumicmagics.research;

import com.author.createthaumicmagics.Createthaumicmagics;
import com.author.createthaumicmagics.aspect.CreateAspectRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomModelData;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.crafting.InfusionRecipe;
import thaumcraft.crafting.ShapedArcaneRecipe;
import thaumcraft.crafting.TcIngredient;
import thaumcraft.crafting.TcRecipes;
import thaumcraft.crafting.TcStack;
import thaumcraft.research.ResearchCategories;
import thaumcraft.research.ResearchItem;
import thaumcraft.research.ResearchPage;

import java.util.List;
import java.util.Map;

public final class ThaumicEngineeringResearch {
    public static final String CATEGORY = "THAUMICENGINEERING";
    public static final String FOUNDATION = "CTM_THAUMIC_ENGINEERING";
    public static final String GOGGLES = "CTM_ENGINEER_GOGGLES";
    public static final String BURNER = "CTM_THAUMOSTATIC_BLAZE_BURNER";
    public static final String STOCK_CRAFTER = "CTM_THAUMIC_STOCK_CRAFTER";
    public static final String WIRELESS_STOCK_CRAFTER = "CTM_WIRELESS_THAUMIC_STOCK_CRAFTER";

    private static final String GOGGLES_REVEALING_RECIPE =
        "createthaumicmagics:arcane/thaumic_engineer_goggles_revealing";
    private static final String GOGGLES_ENGINEER_RECIPE =
        "createthaumicmagics:arcane/thaumic_engineer_goggles_engineer";
    private static final String BURNER_RECIPE = "createthaumicmagics:infusion/thaumostatic_blaze_burner";
    private static final String STOCK_CRAFTER_RECIPE = "createthaumicmagics:infusion/thaumic_stock_crafter";
    private static final String WIRELESS_STOCK_CRAFTER_RECIPE =
        "createthaumicmagics:infusion/wireless_thaumic_stock_crafter";

    private static boolean registered;

    private ThaumicEngineeringResearch() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        Aspect praecisio = CreateAspectRegistration.praecisio();
        Aspect rotatio = CreateAspectRegistration.rotatio();
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(
            "thaumcraft", "textures/gui/gui_researchback.png"
        );

        ResearchCategories.registerCategory(
            CATEGORY,
            ResourceLocation.fromNamespaceAndPath(
                Createthaumicmagics.MODID, "textures/aspects/praecisio.png"
            ),
            background
        );

        registerArcaneGogglesRecipes();
        registerInfusionRecipes(praecisio, rotatio);
        registerResearch(praecisio, rotatio);
    }

    private static void registerArcaneGogglesRecipes() {
        TcStack.registerDataHandler(
            "custom_model_data",
            (stack, value) -> stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(Integer.parseInt(value))
            )
        );

        AspectList costs = new AspectList()
            .add(Aspect.MIND, 20)
            .add(Aspect.MECHANISM, 12);
        TcRecipes.register(new ShapedArcaneRecipe(
            GOGGLES_REVEALING_RECIPE,
            GOGGLES,
            TcStack.withData(
                "createthaumicmagics:thaumic_engineer_goggles",
                1,
                Map.of("custom_model_data", "1")
            ),
            costs.copy(),
            3,
            2,
            List.of(
                TcIngredient.item("thaumcraft:goggles_of_revealing"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("create:goggles"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet")
            )
        ));
        TcRecipes.register(new ShapedArcaneRecipe(
            GOGGLES_ENGINEER_RECIPE,
            GOGGLES,
            TcStack.withData(
                "createthaumicmagics:thaumic_engineer_goggles",
                1,
                Map.of("custom_model_data", "2")
            ),
            costs.copy(),
            3,
            2,
            List.of(
                TcIngredient.item("create:goggles"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("thaumcraft:goggles_of_revealing"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet")
            )
        ));
    }

    private static void registerInfusionRecipes(Aspect praecisio, Aspect rotatio) {
        TcRecipes.register(new InfusionRecipe(
            BURNER_RECIPE,
            BURNER,
            TcStack.of("createthaumicmagics:thaumostatic_blaze_burner"),
            6,
            new AspectList()
                .add(praecisio, 48)
                .add(rotatio, 48)
                .add(Aspect.FIRE, 64)
                .add(Aspect.MOTION, 32),
            TcIngredient.item("create:blaze_burner"),
            List.of(
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_ingot"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_ingot")
            )
        ));

        TcRecipes.register(new InfusionRecipe(
            STOCK_CRAFTER_RECIPE,
            STOCK_CRAFTER,
            TcStack.of("createthaumicmagics:thaumic_stock_crafter"),
            7,
            new AspectList()
                .add(praecisio, 40)
                .add(Aspect.MIND, 48)
                .add(Aspect.ORDER, 32),
            TcIngredient.item("stockinstantcraft:stock_crafter"),
            List.of(
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumic_brass_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("createthaumicmagics:thaumium_sheet"),
                TcIngredient.item("thaumcraft:arcane_worktable")
            )
        ));

        TcRecipes.register(new InfusionRecipe(
            WIRELESS_STOCK_CRAFTER_RECIPE,
            WIRELESS_STOCK_CRAFTER,
            TcStack.of("createthaumicmagics:wireless_thaumic_stock_crafter"),
            8,
            new AspectList()
                .add(praecisio, 48)
                .add(rotatio, 48)
                .add(Aspect.EXCHANGE, 64)
                .add(Aspect.MECHANISM, 40),
            TcIngredient.item("stockinstantcraft:wireless_stock_crafter"),
            List.of(
                TcIngredient.item("create:transmitter"),
                TcIngredient.item("create:precision_mechanism"),
                TcIngredient.item("create:brass_sheet"),
                TcIngredient.item("create:brass_casing"),
                TcIngredient.item("create:electron_tube"),
                TcIngredient.item("create:large_cogwheel"),
                TcIngredient.item("thaumcraft:void_seed"),
                TcIngredient.item("thaumcraft:salis_mundus")
            )
        ));
    }

    private static void registerResearch(Aspect praecisio, Aspect rotatio) {
        new ResearchItem(
            FOUNDATION, CATEGORY,
            new AspectList().add(praecisio, 2).add(rotatio, 2).add(Aspect.MECHANISM, 2),
            0, 0, 1, TcStack.of("create:precision_mechanism")
        ).setParents("INFUSION")
            .setPages(ResearchPage.text("tc.research_page." + FOUNDATION + ".1"))
            .registerResearchItem();

        new ResearchItem(
            GOGGLES, CATEGORY,
            new AspectList().add(Aspect.MIND, 3).add(praecisio, 2).add(rotatio, 2),
            -2, 2, 2, TcStack.of("createthaumicmagics:thaumic_engineer_goggles")
        ).setParents(FOUNDATION)
            .setPages(
                ResearchPage.text("tc.research_page." + GOGGLES + ".1"),
                ResearchPage.arcane(GOGGLES_REVEALING_RECIPE, GOGGLES_ENGINEER_RECIPE)
            )
            .registerResearchItem();

        new ResearchItem(
            BURNER, CATEGORY,
            new AspectList().add(Aspect.FIRE, 4).add(praecisio, 4).add(rotatio, 4),
            0, 3, 3, TcStack.of("createthaumicmagics:thaumostatic_blaze_burner")
        ).setParents(FOUNDATION)
            .setPages(
                ResearchPage.text("tc.research_page." + BURNER + ".1"),
                ResearchPage.infusion(BURNER_RECIPE)
            )
            .registerResearchItem();

        new ResearchItem(
            STOCK_CRAFTER, CATEGORY,
            new AspectList().add(Aspect.EXCHANGE, 3).add(praecisio, 3).add(rotatio, 3),
            2, 2, 2, TcStack.of("createthaumicmagics:thaumic_stock_crafter")
        ).setParents(FOUNDATION)
            .setPages(
                ResearchPage.text("tc.research_page." + STOCK_CRAFTER + ".1"),
                ResearchPage.infusion(STOCK_CRAFTER_RECIPE)
            )
            .registerResearchItem();

        new ResearchItem(
            WIRELESS_STOCK_CRAFTER, CATEGORY,
            new AspectList().add(Aspect.EXCHANGE, 4).add(praecisio, 4).add(rotatio, 4),
            4, 2, 3, TcStack.of("createthaumicmagics:wireless_thaumic_stock_crafter")
        ).setParents(STOCK_CRAFTER)
            .setPages(
                ResearchPage.text("tc.research_page." + WIRELESS_STOCK_CRAFTER + ".1"),
                ResearchPage.infusion(WIRELESS_STOCK_CRAFTER_RECIPE)
            )
            .registerResearchItem();
    }
}
