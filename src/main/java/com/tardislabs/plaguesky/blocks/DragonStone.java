package com.tardislabs.plaguesky.blocks;

import com.tardislabs.plaguesky.Config;
import com.tardislabs.plaguesky.PlagueSky;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
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

        Random random = new Random();
        int index = random.nextInt(Config.COMMON.dropBlocks.get().size());

        PlagueSky.mutter(Config.COMMON.dropBlocks.get().get(index));

        BlockState block = BuiltInRegistries.BLOCK.getOrCreateTag(
            BlockTags.create(ResourceLocation.tryParse(Config.COMMON.dropBlocks.get().get(index))))
            .getRandomElement(RandomSource.create()).orElseGet(() -> 
            (
                BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.AIR)
            )).value().defaultBlockState();

        worldIn.setBlock(pos, block,3);
    }
}