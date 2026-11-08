package net.nerotechmodding.drevieth;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Drevieth.MOD_ID, dist =  Dist.CLIENT)
public class DreviethClient {
    public DreviethClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    static void onClientSetup(FMLClientSetupEvent event) {
        Drevieth.LOGGER.info("HELLO FROM CLIENT SETUP");
        Drevieth.LOGGER.info("MINECRAFT NAME >> {}");
    }
}
