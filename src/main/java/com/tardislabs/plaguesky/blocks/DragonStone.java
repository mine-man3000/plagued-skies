package com.tardislabs.plaguesky.blocks;

import com.tardislabs.plaguesky.Config;
import com.tardislabs.plaguesky.PlagueSky;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.tags.BlockTags;

import java.util.*;

public class DragonStone extends FallingBlock {

    Vector<BlockState> oreBlocks = null;

    public DragonStone(Properties props) {
        super(props);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        // Call this so the block falls
        super.tick(state, worldIn, pos, rand);
        // Don't decay if we're not on something solid
        if (pos.getY() > 0 && worldIn.getBlockState(
                        new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()))
                .getBlock() == Blocks.AIR) return;

        if (rand.nextInt(100) >= Config.COMMON.orePercent.get()) return;


        worldIn.setBlock(pos, BuiltInRegistries.BLOCK.getOrCreateTag(BlockTags.create(ResourceLocation.tryParse("forge:ores/diamond")))
        .getRandomElement(RandomSource.create()).orElseGet(() -> 
        (
            BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.AIR)
        )).value().defaultBlockState(),3);
    }

    // public void getOres() {
    //     if (oreBlocks != null) return;
    //     oreBlocks = new Vector<BlockState>();

    //     List<? extends String> dropBlocks = Config.COMMON.dropBlocks.get();

    //       ForgeRegistries.BLOCKS.tags().getTag(BlockTags.DIAMOND_ORES);

    //     List<? extends TagKey<Block>> tags = Collections.singletonList(BlockTags.DIAMOND_ORES);
    //     Hashtable<String, TagKey<Block>> tagList = new Hashtable<>();


    //     for (TagKey<Block> tag : tags) {
    //         tagList.put(tag.toString(), tag);
    //     }

    //     for (String b : dropBlocks) {
    //         if (tagList.containsKey(b)) {
    //             List<Block> blocks = new ArrayList<>();

    //             if (blocks.size() > 0) {
    //                 Block block = blocks.get(0);
    //                 oreBlocks.add(block.defaultBlockState());
    //             } else {
    //                 PlagueSky.LOGGER.warn("Tag " + b + " does not contain any blocks");
    //             }
    //         } else if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(b))) {
    //             Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(b));
    //             oreBlocks.add(block.defaultBlockState());
    //         } else {
    //             PlagueSky.LOGGER.warn("Can't resolve block " + b);
    //         }
    //     }
    // }
}