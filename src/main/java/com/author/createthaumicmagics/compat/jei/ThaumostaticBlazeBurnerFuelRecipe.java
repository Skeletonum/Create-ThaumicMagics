package com.author.createthaumicmagics.compat.jei;

import thaumcraft.aspect.Aspect;

public record ThaumostaticBlazeBurnerFuelRecipe(Aspect aspect, int amount, double ticksPerUnit,
                                                 boolean superheated) {
    public double burnTimeSeconds() {
        return amount * ticksPerUnit / 20.0;
    }
}
