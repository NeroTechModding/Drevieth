package net.nerotechmodding.drevieth.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Snow line: below SNOW_LINE_Y it is never cold enough to snow or freeze water
 * (oceans, rivers, frozen biomes included). At or above it, vanilla logic runs.
 *
 * Hooks the two public checks that shouldFreeze / shouldSnow / getPrecipitationAt
 * all go through, so it does not matter how the temperature is computed.
 */
@Mixin(Biome.class)
public abstract class BiomeMixin {
    private static final int SNOW_LINE_Y = 800;

    @Inject(method = "coldEnoughToSnow", at = @At("HEAD"), cancellable = true)
    private void drevieth$noColdBelowLine(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (pos.getY() < SNOW_LINE_Y) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "warmEnoughToRain", at = @At("HEAD"), cancellable = true)
    private void drevieth$warmBelowLine(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (pos.getY() < SNOW_LINE_Y) {
            cir.setReturnValue(true);
        }
    }
}