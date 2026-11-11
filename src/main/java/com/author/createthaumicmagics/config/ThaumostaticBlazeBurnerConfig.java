package com.author.createthaumicmagics.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import thaumcraft.aspect.Aspect;

import java.util.List;

public final class ThaumostaticBlazeBurnerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_ESSENCE_BUFFER;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> HEATED_ESSENCES;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> SUPERHEATED_ESSENCES;
    private static final ModConfigSpec.DoubleValue TICKS_PER_ESSENCE_UNIT;
    private static final ModConfigSpec.BooleanValue ALLOW_FLUID_FALLBACK;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("thaumostaticBlazeBurner");
        MAX_ESSENCE_BUFFER = BUILDER
            .comment("Maximum number of whole essentia units held by a Thaumostatic Blaze Burner.")
            .defineInRange("maxEssenceBuffer", 50, 1, 1024);
        HEATED_ESSENCES = BUILDER
            .comment("Aspect tags accepted as Heated fuel. 'ignis' is an alias for Thaumcraft's fire aspect.")
            .defineListAllowEmpty("heatedEssences", List.of("ignis"), null,
                ThaumostaticBlazeBurnerConfig::isValidAspect);
        SUPERHEATED_ESSENCES = BUILDER
            .comment("Aspect tags accepted as Superheated fuel.")
            .defineListAllowEmpty("superheatedEssences", List.of("praecantatio", "auram"), null,
                ThaumostaticBlazeBurnerConfig::isValidAspect);
        TICKS_PER_ESSENCE_UNIT = BUILDER
            .comment("Burn duration in ticks for one Heated essentia unit.")
            .defineInRange("ticksPerEssenceUnit", 76.8, 0.1, 72000.0);
        ALLOW_FLUID_FALLBACK = BUILDER
            .comment("Reserved for compatibility; fluid fuel integration is intentionally unsupported.")
            .define("allowFluidFallback", false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private ThaumostaticBlazeBurnerConfig() {
    }

    public static int maxEssenceBuffer() {
        return MAX_ESSENCE_BUFFER.get();
    }

    public static List<Aspect> heatedEssences() {
        return resolveAspects(HEATED_ESSENCES.get());
    }

    public static List<Aspect> superheatedEssences() {
        return resolveAspects(SUPERHEATED_ESSENCES.get());
    }

    public static double ticksPerEssenceUnit() {
        return TICKS_PER_ESSENCE_UNIT.get();
    }

    public static boolean allowFluidFallback() {
        return ALLOW_FLUID_FALLBACK.get();
    }

    public static Aspect resolveAspect(String tag) {
        return "ignis".equalsIgnoreCase(tag) ? Aspect.FIRE : Aspect.getAspect(tag);
    }

    private static List<Aspect> resolveAspects(List<? extends String> tags) {
        return tags.stream().map(tag -> {
            Aspect aspect = resolveAspect(tag);
            if (aspect == null) {
                throw new IllegalStateException("Unknown Thaumcraft aspect in burner config: " + tag);
            }
            return aspect;
        }).toList();
    }

    private static boolean isValidAspect(Object value) {
        return value instanceof String tag && resolveAspect(tag) != null;
    }
}
