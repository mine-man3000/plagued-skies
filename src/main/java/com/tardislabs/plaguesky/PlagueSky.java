package com.tardislabs.plaguesky;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import com.tardislabs.plaguesky.blocks.BlockRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(PlagueSky.MODID)
public class PlagueSky {
    /**
     * The unique identifier for this mod
     */
    public static final String MODID = "plaguesky";
    /**
     * The name of this mod
     */
    public static final String NAME = "Plagued Skies";
    /**
     * The version of this mod
     */
    public static final String VERSION = "1.5";
    /**
     * Event handler for dragon egg being placed
     */

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public PlagueSky() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockRegistry.register(eventBus);
        ItemRegistry.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    public static void mutter(String message) {
        LOGGER.info(message);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void RegisterClientCommandsEvent(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

            CommandHandler.register(dispatcher);
        }
    }
}
