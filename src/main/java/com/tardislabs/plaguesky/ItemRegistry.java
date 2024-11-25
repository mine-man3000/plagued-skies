package com.tardislabs.plaguesky;

import com.tardislabs.plaguesky.blocks.BlockRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PlagueSky.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PlagueSky.MODID);

    public static final RegistryObject<CreativeModeTab> COSTCO_TAB = CREATIVE_MODE_TABS.register("costco_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(BlockRegistry.DRAGONSKIN.get()))
                    .title(Component.translatable("creativetab.plaguesky"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(BlockRegistry.DRAGONSKIN.get());
                        pOutput.accept(BlockRegistry.DRAGONSTONE.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
