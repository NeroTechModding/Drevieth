package net.nerotechmodding.drevieth.mixin;

import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.nerotechmodding.drevieth.Config;
import net.nerotechmodding.drevieth.Drevieth;
import net.nerotechmodding.drevieth.worldTypes.WorldTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ore compatibility: other mods tune their ore heights for the vanilla overworld (-64 to 320).
 * In the Drevieth dimension (-64 to 1024) those ores would all bunch up at the bottom, so we
 * sample their height as if the world were vanilla-sized, then stretch it proportionally to
 * the real world height. Anchors such as "below_top" therefore keep their meaning too.
 *
 * Only ore features (OreFeature / ScatteredOreFeature) from namespaces other than minecraft
 * and drevieth are touched; vanilla ores are configured by our own placed features.
 */
@Mixin(HeightRangePlacement.class)
public abstract class HeightRangePlacementMixin {
    private static final int VANILLA_MIN_Y = -64;
    private static final int VANILLA_HEIGHT = 384;
    private static final LevelHeightAccessor VANILLA_HEIGHT_ACCESSOR =
            LevelHeightAccessor.create(VANILLA_MIN_Y, VANILLA_HEIGHT);

    @Shadow @Final private HeightProvider height;

    @Inject(method = "getPositions", at = @At("HEAD"), cancellable = true)
    private void drevieth$scaleModdedOres(PlacementContext context, RandomSource random, BlockPos pos,
                                          CallbackInfoReturnable<Stream<BlockPos>> cir) {
        if (!Config.SCALE_MODDED_ORES.getAsBoolean() || !drevieth$isDrevieth(context)) {
            return;
        }
        Optional<PlacedFeature> top = context.topFeature();
        if (top.isEmpty() || !drevieth$isModdedOre(context, top.get())) {
            return;
        }

        int realMin = context.getMinGenY();
        int realHeight = context.getGenDepth();
        if (realHeight <= VANILLA_HEIGHT) {
            return; // nothing to stretch
        }

        WorldGenerationContext vanilla = new WorldGenerationContext(context.generator(), VANILLA_HEIGHT_ACCESSOR);
        int vanillaY = this.height.sample(random, vanilla);

        // Stretch, with a random offset inside the stretched band so ores fill every layer
        // instead of only every ~3rd one.
        double scale = (double) realHeight / VANILLA_HEIGHT;
        double offset = (vanillaY - vanilla.getMinGenY()) + random.nextDouble();
        int y = realMin + (int) Math.floor(offset * scale);
        y = Math.max(realMin, Math.min(realMin + realHeight - 1, y));

        cir.setReturnValue(Stream.of(pos.atY(y)));
    }

    private static boolean drevieth$isDrevieth(PlacementContext context) {
        Holder<DimensionType> type = context.getLevel().getLevel().dimensionTypeRegistration();
        return type.is(WorldTypes.DREVIETH);
    }

    private static boolean drevieth$isModdedOre(PlacementContext context, PlacedFeature placed) {
        Feature<?> feature = placed.feature().value().feature();
        if (!(feature instanceof OreFeature) && !(feature instanceof ScatteredOreFeature)) {
            return false;
        }
        ResourceLocation id = context.getLevel().registryAccess()
                .registryOrThrow(Registries.PLACED_FEATURE).getKey(placed);
        if (id == null) {
            return false; // inline/unregistered feature, leave it alone
        }
        String ns = id.getNamespace();
        if (ns.equals("minecraft") || ns.equals(Drevieth.MOD_ID)) {
            return false;
        }
        var excluded = Config.SCALE_MODDED_ORES_EXCLUDE.get();
        return !excluded.contains(ns) && !excluded.contains(id.toString());
    }
}
