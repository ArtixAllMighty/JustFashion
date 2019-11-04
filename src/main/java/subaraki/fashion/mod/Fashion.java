package subaraki.fashion.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.client.ClientReferences;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.client.event.EventRegistryClient;
import subaraki.fashion.client.eventforge_bus.KeyRegistry;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.screen.WardrobeContainer;
import subaraki.fashion.screen.WardrobeScreen;
import subaraki.fashion.server.event.EventRegistryServer;

@Mod(Fashion.MODID)
@EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class Fashion {

    public static final String MODID = "fashion";
    public static final String FASHIONPACK = "fashion packs";

    public static Logger log = LogManager.getLogger(MODID);

    public Fashion() {

        // Register doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register commonSetup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        new FashionCapability().register();
        new EventRegistryServer();
        new NetworkHandler();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

        new EventRegistryClient();
        new ResourcePackReader();
        new KeyRegistry().registerKey();
        ScreenManager.registerFactory(ObjectHolders.WARDROBECONTAINER, WardrobeScreen::new);
        ClientReferences.loadLayers();
    }
    
    @ObjectHolder(Fashion.MODID)
    public static class ObjectHolders {

        @ObjectHolder("wardrobe_container")
        public static final ContainerType<WardrobeContainer> WARDROBECONTAINER = null;
    }
    
    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new WardrobeContainer(ObjectHolders.WARDROBECONTAINER, windowId))
                .setRegistryName("wardrobe_container"));

    }
}
