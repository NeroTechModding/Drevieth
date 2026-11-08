package net.nerotechmodding.drevieth.worldTypes;
import java.util.OptionalLong;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.nerotechmodding.drevieth.Drevieth;

public class WorldTypes {
    public static final ResourceKey<WorldPreset> DREVIETHTYPE = ResourceKey.create(
            Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath(Drevieth.MOD_ID, "my_world"));

    public static final ResourceKey<DimensionType> DREVIETH = ResourceKey.create(
            Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(Drevieth.MOD_ID, "drevieth")
    );

    public static final void bootstrapDimensionTypes(BootstrapContext<DimensionType> ctx) {
        ctx.register(DREVIETH, new DimensionType(
                OptionalLong.empty(),
                true,
                false,
                false,
                true,
                2.5,
                true,
                true,
                -64,
                1088,
                1088,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                2.0f,
                new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 5), 0)
        ));
    }
}