package com.author.createthaumicmagics;

import com.author.createthaumicmagics.aspect.CreateAspectRegistration;
import com.author.createthaumicmagics.client.ThaumostaticBlazeBurnerRenderer;
import com.author.createthaumicmagics.client.ThaumostaticBlazeBurnerHud;
import com.author.createthaumicmagics.client.ThaumicStockCrafterScreen;
import com.author.createthaumicmagics.config.ThaumostaticBlazeBurnerConfig;
import com.author.createthaumicmagics.item.ThaumicEngineerGogglesItem;
import com.author.createthaumicmagics.research.ThaumicEngineeringResearch;
import com.author.createthaumicmagics.research.ThaumicMaterialRecipes;
import com.author.createthaumicmagics.registry.ModItems;
import com.author.createthaumicmagics.registry.ThaumostaticBlazeBurnerRegistry;
import com.author.createthaumicmagics.registry.ThaumicStockCrafterRegistry;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Createthaumicmagics.MODID)
public class Createthaumicmagics {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "createthaumicmagics";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "createthaumicmagics" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "createthaumicmagics" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "createthaumicmagics" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<ThaumicEngineerGogglesItem> THAUMIC_ENGINEER_GOGGLES = ITEMS.register(
        "thaumic_engineer_goggles",
        () -> new ThaumicEngineerGogglesItem(new Item.Properties().durability(350))
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATE_THAUMIC_MAGICS_TAB =
        CREATIVE_MODE_TABS.register("create_thaumic_magics", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.createthaumicmagics"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> THAUMIC_ENGINEER_GOGGLES.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(THAUMIC_ENGINEER_GOGGLES.get());
                output.accept(ThaumostaticBlazeBurnerRegistry.BLOCK_ITEM.get());
                output.accept(ThaumicStockCrafterRegistry.BLOCK_ITEM.get());
                output.accept(ThaumicStockCrafterRegistry.WIRELESS_ITEM.get());
                output.accept(ModItems.THAUMIC_BRASS_INGOT.get());
                output.accept(ModItems.THAUMIC_BRASS_SHEET.get());
                output.accept(ModItems.THAUMIUM_SHEET.get());
            })
            .build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Createthaumicmagics(IEventBus modEventBus, ModContainer modContainer) {
        CreateAspectRegistration.register();
        GogglesItem.addIsWearingPredicate(player ->
            player.getItemBySlot(EquipmentSlot.HEAD).is(THAUMIC_ENGINEER_GOGGLES.get())
        );

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(Config::onLoad);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientModEvents::onClientSetup);
            modEventBus.addListener(ClientModEvents::registerRenderers);
            modEventBus.addListener(ClientModEvents::registerMenuScreens);
            modEventBus.addListener(ClientModEvents::registerGuiLayers);
        }

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        ThaumostaticBlazeBurnerRegistry.BLOCKS.register(modEventBus);
        ThaumostaticBlazeBurnerRegistry.ITEMS.register(modEventBus);
        ThaumostaticBlazeBurnerRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        ThaumicStockCrafterRegistry.BLOCKS.register(modEventBus);
        ThaumicStockCrafterRegistry.ITEMS.register(modEventBus);
        ThaumicStockCrafterRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        ThaumicStockCrafterRegistry.MENUS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Createthaumicmagics) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ThaumostaticBlazeBurnerConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ThaumicEngineeringResearch.register();
            ThaumicMaterialRecipes.register();
        });

        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static class ClientModEvents {
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    MODID, "thaumostatic_blaze_burner_hud"
                ),
                ThaumostaticBlazeBurnerHud::render
            );
        }

        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                ThaumostaticBlazeBurnerRegistry.BLOCK_ENTITY.get(), ThaumostaticBlazeBurnerRenderer::new
            );
        }

        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ThaumicStockCrafterRegistry.MENU.get(), ThaumicStockCrafterScreen::new);
        }
    }
}
