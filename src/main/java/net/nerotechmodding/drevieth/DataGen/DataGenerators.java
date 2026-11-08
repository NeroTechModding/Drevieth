package net.nerotechmodding.drevieth.DataGen;


import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.nerotechmodding.drevieth.Drevieth;
import net.nerotechmodding.drevieth.worldTypes.WorldTypes;

@EventBusSubscriber(modid = Drevieth.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gather(GatherDataEvent event) {
        var gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        // Dimension type (tall overworld)
        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookup,
                new RegistrySetBuilder().add(Registries.DIMENSION_TYPE, WorldTypes::bootstrapDimensionTypes),
                Set.of(Drevieth.MOD_ID)));

        // Display name in the world type list
        gen.addProvider(event.includeClient(), new LanguageProvider(output, Drevieth.MOD_ID, "en_us") {
            @Override
            protected void addTranslations() {
                add("generator.preset.myworld.drevieth", "Drevieth");
            }
        });
    }
}