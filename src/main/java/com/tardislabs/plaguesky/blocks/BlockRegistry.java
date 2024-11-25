package com.tardislabs.plaguesky.blocks;

import com.tardislabs.plaguesky.ItemRegistry;
import com.tardislabs.plaguesky.PlagueSky;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PlagueSky.MODID);

    public static final RegistryObject<Block> DRAGONSTONE = registerBlock("dragonstone",
            () -> new DragonStone(BlockBehaviour.Properties.copy(Blocks.STONE).isValidSpawn((blockState, blockGetter, blockPos, o) -> false)));

    public static final RegistryObject<Block> DRAGONSKIN = registerBlock("dragonskin",
            () -> new DragonSkin(BlockBehaviour.Properties.copy(Blocks.BEDROCK).randomTicks().isValidSpawn((blockState, blockGetter, blockPos, o) -> false)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ItemRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus event) {
        BLOCKS.register(event);
    }
}
