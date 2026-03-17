package com.github.gtexpert.gtbm.mixins.forestry;

import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import forestry.core.fluids.Fluids;

/**
 * Redirects Forestry's {@link Fluids} enum to return GregTech fluid objects
 * for fluids whose registration was skipped by {@link ModuleFluidsMixin}.
 * <p>
 * BIO_ETHANOL (tag "bio.ethanol") maps to GT's "ethanol",
 * SEED_OIL (tag "seed.oil") maps to GT's "seed_oil".
 * Other skipped fluids (biomass, glass, ice, milk) share the same name
 * and are resolved correctly without redirection.
 * <p>
 * Also extends the {@code tagToFluid} map so that
 * {@link Fluids#getFluidDefinition(FluidStack)} works with GT fluid names.
 */
@Mixin(value = Fluids.class, remap = false)
public class FluidsMixin {

    @Shadow
    @Final
    private static Map<String, Fluids> tagToFluid;

    /**
     * Returns the GregTech fluid name for fluids whose Forestry tag differs
     * from the GregTech registration name, or {@code null} if no redirect is needed.
     */
    private static String gtbm$getRedirectedName(Fluids self) {
        if (self == Fluids.SEED_OIL) {
            return "seed_oil";
        }
        if (self == Fluids.BIO_ETHANOL) {
            return "ethanol";
        }
        return null;
    }

    @Inject(method = "getFluid()Lnet/minecraftforge/fluids/Fluid;", at = @At("HEAD"), cancellable = true)
    private void gtbm$redirectGetFluid(CallbackInfoReturnable<Fluid> cir) {
        String redirected = gtbm$getRedirectedName((Fluids) (Object) this);
        if (redirected != null) {
            cir.setReturnValue(FluidRegistry.getFluid(redirected));
        }
    }

    @Inject(method = "getFluid(I)Lnet/minecraftforge/fluids/FluidStack;", at = @At("HEAD"), cancellable = true)
    private void gtbm$redirectGetFluidStack(int mb, CallbackInfoReturnable<FluidStack> cir) {
        String redirected = gtbm$getRedirectedName((Fluids) (Object) this);
        if (redirected != null) {
            cir.setReturnValue(FluidRegistry.getFluidStack(redirected, mb));
        }
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void gtbm$extendTagToFluid(CallbackInfo ci) {
        tagToFluid.put("seed_oil", Fluids.SEED_OIL);
        tagToFluid.put("ethanol", Fluids.BIO_ETHANOL);
    }
}
