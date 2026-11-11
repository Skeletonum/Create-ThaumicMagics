package com.author.createthaumicmagics.aspect;

import com.author.createthaumicmagics.Createthaumicmagics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import thaumcraft.aspect.AddonAspects;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.aspect.AspectRegistry;
import thaumcraft.aspect.AspectSync;

public final class CreateAspectRegistration {
    private static Aspect praecisio;
    private static Aspect rotatio;

    private CreateAspectRegistration() {
    }

    public static void register() {
        // Thaumcraft invokes these hooks during common setup and each server aspect-registry rebuild.
        AddonAspects.addRegistrar(addon -> {
            praecisio = addon.register(
                "praecisio",
                0xD6A94D,
                new Aspect[]{Aspect.ORDER, Aspect.MIND},
                1,
                texture("praecisio")
            );
            rotatio = addon.register(
                "rotatio",
                0x6E9FD2,
                new Aspect[]{Aspect.MOTION, Aspect.ORDER},
                1,
                texture("rotatio")
            );
        });

        AspectSync.addRegistrar(CreateAspectRegistration::registerItemAspects);
    }

    public static Aspect praecisio() {
        return requireAspect(praecisio, "praecisio");
    }

    public static Aspect rotatio() {
        return requireAspect(rotatio, "rotatio");
    }

    private static Aspect requireAspect(Aspect aspect, String name) {
        if (aspect == null) {
            throw new IllegalStateException("Thaumcraft add-on aspect has not been initialized: " + name);
        }
        return aspect;
    }

    private static void registerItemAspects(AspectRegistry registry) {
        addAspect(registry, "shaft", rotatio, 2);
        addAspect(registry, "cogwheel", rotatio, 3);
        addAspect(registry, "large_cogwheel", rotatio, 4);
        addAspect(registry, "mechanical_press", praecisio, 3);
        addAspect(registry, "mechanical_press", rotatio, 2);
        addAspect(registry, "deployer", praecisio, 3);
        addAspect(registry, "deployer", rotatio, 2);
        addAspect(registry, Createthaumicmagics.MODID, "thaumic_brass_ingot", Aspect.METAL, 3);
        addAspect(registry, Createthaumicmagics.MODID, "thaumic_brass_ingot",
            requireAspect(praecisio, "praecisio"), 1);
        addAspect(registry, Createthaumicmagics.MODID, "thaumic_brass_sheet", Aspect.METAL, 2);
        addAspect(registry, Createthaumicmagics.MODID, "thaumium_sheet", Aspect.METAL, 2);
    }

    private static void addAspect(AspectRegistry registry, String itemPath, Aspect aspect, int amount) {
        addAspect(registry, "create", itemPath, aspect, amount);
    }

    private static void addAspect(
        AspectRegistry registry, String namespace, String itemPath, Aspect aspect, int amount
    ) {
        if (aspect == null) {
            throw new IllegalStateException("Thaumcraft add-on aspects were not initialized before aspect sync");
        }

        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(namespace, itemPath);
        Item item = BuiltInRegistries.ITEM.getOptional(itemId)
            .orElseThrow(() -> new IllegalStateException("Required Create item is missing: " + itemId));

        AspectList aspects = registry.getObjectTags(item);
        if (aspects == null) {
            aspects = new AspectList();
        } else {
            aspects = aspects.copy();
        }
        registry.register(item, aspects.add(aspect, amount));
    }

    private static ResourceLocation texture(String aspect) {
        return ResourceLocation.fromNamespaceAndPath(
            Createthaumicmagics.MODID,
            "textures/aspects/" + aspect + ".png"
        );
    }
}
