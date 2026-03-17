package com.github.gtexpert.gtbm.mixins.forestry;

import java.util.EnumSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import forestry.core.ModuleFluids;
import forestry.core.fluids.Fluids;

/**
 * Prevents Forestry from registering fluids that GregTech will register,
 * so that GregTech's Fluid objects are used instead of Forestry's.
 * This avoids NPE crashes caused by Forestry fluid item model issues in JEI.
 */
@Mixin(value = ModuleFluids.class, remap = false)
public class ModuleFluidsMixin {

    private static final EnumSet<Fluids> SKIPPED_FLUIDS = EnumSet.of(
            Fluids.BIO_ETHANOL,
            Fluids.BIOMASS,
            Fluids.GLASS,
            Fluids.ICE,
            Fluids.MILK,
            Fluids.SEED_OIL);

    @Inject(method = "createFluid", at = @At("HEAD"), cancellable = true)
    private static void gtbm$skipGregTechFluids(Fluids fluidDefinition, CallbackInfo ci) {
        if (SKIPPED_FLUIDS.contains(fluidDefinition)) {
            ci.cancel();
        }
    }
}
