package com.tardislabs.plaguesky.blocks;

import com.tardislabs.plaguesky.Config;
import com.tardislabs.plaguesky.Data;
import com.tardislabs.plaguesky.PlagueSky;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Vector;

@Mod.EventBusSubscriber(modid = PlagueSky.MODID)
public class DragonSkin extends GrassBlock {
    /**
     * Game time when plague last actually spread
     */
    public static long lastSpread = 0;
    /**
     * Game time when plague was last checked for spread
     */
    public static long lastSpreadCheck = 0;
    /**
     * Number of growths to perform at next update
     */
    public static long growthQueue = 0;
    /**
     * Time of next decay
     */
    public static long nextDecay = 0;
    /**
     * Number of decays since decay started
     */
    public static long decayCount = 0;

    public DragonSkin(Properties props) {
        super(props);
    }

    @SubscribeEvent
    public static void onWorldTick( TickEvent.ServerTickEvent event) {
        doTick(event);
    }

    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource rand) {

        // Block doesn't have a .tick, so nevermind this
        // super.tick( state, worldIn, pos, rand );

        long now = world.getGameTime();
        PlagueSky.mutter("Tick at " + now);

        String levelPath = world.getServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().toString();
        Data data = new Data(levelPath);

        if (data.isHealing()) {
            if (now < nextDecay) return;
            if (rand.nextInt(100) >= Config.COMMON.decayPercent.get()) return;

            if (rand.nextInt(100) < Config.COMMON.sloughPercent.get()) {
                world.setBlock(pos, BlockRegistry.DRAGONSTONE.get().defaultBlockState(), 3);
            } else {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }

            decayCount++;
            // PlagueSky.mutter( "decayCount=" + decayCount + " config=" + Config.COMMON.spreadCap.get() );
            if (decayCount > Config.COMMON.spreadCap.get() &&
                    Config.COMMON.spreadCap.get() != 0) {
                nextDecay = now + Config.COMMON.spreadDelay.get() * 20;
                decayCount = 0;
                PlagueSky.mutter("Decay limit exceeded; waiting " +
                        Config.COMMON.spreadDelay.get() + "s");

            }
            return;
        }

			/*
			if( Config.COMMON.patchyDecay.get() )
			{
				/* When a block decays, schedule its neighbors to decay. When this
				 * happens recursively, blocks will decay in 'patches', rather than
				 * individual blocks.
				 *
				 * There is only a 50% chance for a given neighbor to have a decay
				 * scheduled. This gives the patches a more 'organic' shape
				 * ///
				 // For now, we're just going to disable all this, since the effect
				 // doesn't really seem to work properly

				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX() + 1, pos.getY(), pos.getZ() ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX() - 1, pos.getY(), pos.getZ() ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX(), pos.getY(), pos.getZ() + 1 ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX(), pos.getY(), pos.getZ() - 1 ), this, 1, 1 );
				*  //
				// /fill ~-32 255 ~-32 ~32 255 ~32 plaguesky:dragonskin
			}
			*/

        // return;

        lastSpreadCheck = now;

        // int batchSize = Config.spreadBatch;
        // if( rand.nextInt( batchSize ) > 0 ) return;
        // PlagueSky.mutter( "Pct:" + Config.growthPercent );
        long growths = Config.COMMON.growthPercent.get() / 100;
        // PlagueSky.mutter( "Growths:" + growths );
        int fraction = Config.COMMON.growthPercent.get() % 100;

        if (rand.nextInt(100) < fraction) growths++;
        growthQueue += growths;
        PlagueSky.mutter("Queueing " + growths + " growths");

//        PlagueSky.mutter( "lastSpread: " + lastSpread + "; spreadDelay: " + Config.COMMON.spreadDelay.get() + "; Now: " + now );
        if (lastSpread + Config.COMMON.spreadDelay.get() * 20 > now) {
            return;
        }
        PlagueSky.mutter("SPREADING");

        // growths *= batchSize;

        // Don't try to spread into unloaded chunks
        if (!world.isLoaded(pos)) return;

        growths = growthQueue;
        if (Config.COMMON.spreadCap.get() != 0 && growths > Config.COMMON.spreadCap.get()) {
            growths = Config.COMMON.spreadCap.get();
        }

        PlagueSky.mutter("Growing " + growths + " blocks queued over past " +
                (now - lastSpread) + " ticks");
        for (int i = 0; i < growths; i++) {
            pos = spread(world, pos, rand);
        }

        growthQueue = 0;
        lastSpread = now;
    }

    public BlockPos spread(ServerLevel world, BlockPos pos, RandomSource rand) {
        PlagueSky.mutter("Spreading skin at " + pos.getX() + "x" + Config.COMMON.plagueHeight.get() + "x" + pos.getZ());
        // Pick a random direction; N/E/S/W/UP

        /*
        Upwards spread has been added as a way to block the player(s) from building above the plague
        The only differences are:
        A) if we have spread above the configured plague height, don't spread horizontally
        B) Only replace non-air blocks

        - Mineman
         */

        int dir = rand.nextInt(5);
        int dx = 0;
        int dz = 0;
        if (dir == 0) dx = 1;
        if (dir == 1) dx = -1;
        if (dir == 2) dz = 1;
        if (dir == 3) dz = -1;
        if( dir == 4) {
            BlockPos newPos = null;
            for( int i = Config.COMMON.plagueHeight.get(); i < 319; i++ ) {
                newPos = new BlockPos(pos.getX(), i, pos.getZ());
                if(!world.getBlockState(newPos).getBlock().equals(Blocks.AIR)) {
                    world.setBlock(newPos, BlockRegistry.DRAGONSKIN.get().defaultBlockState(), 3);
                }
            }
            return newPos;
        }


        BlockPos newPos = new BlockPos(pos.getX() + dx, Config.COMMON.plagueHeight.get(), pos.getZ() + dz);

        world.setBlock(newPos, BlockRegistry.DRAGONSKIN.get().defaultBlockState(), 3);
        return newPos;
    }

    /**
     * Do the actual seeding for onWorldTick
     *
     * @param event Information about the event
     * @return Status string for debugging purposes
     */
    private static String doTick(TickEvent.ServerTickEvent event) {
        String status = "";

        // I hope this is correct.
        ServerLevel world = event.getServer().overworld();
        String levelPath = event.getServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().toString();

        status = status + "Tick.";
        Data data = new Data(levelPath);


        if (data.isHealing()) return status + " Healing.";

        status = status + " Not healing.";
        long time = world.getGameTime();
        if (lastSpreadCheck == 0) {
            lastSpreadCheck = time;
            return status + " First check.";
        }

        // Check if it's time for a new seed
        if (time < lastSpreadCheck + Config.COMMON.seedTime.get() * 20L) {
            PlagueSky.mutter("Not time. (Spread " + ((time - lastSpreadCheck) / 20) + "s ago)");
            return status + " Not time. (Spread " +
                    ((time - lastSpreadCheck) / 20) + "s ago)";
        }
        status = status + " Time to seed.";

        lastSpreadCheck = time;
        /*
         * Ok, new strategy for seeding new growths. Pick a player in the overworld
         * (if any), pick some random blocks within X radius, and seed them.
         *
         * We used to be able to pick loaded chunks in the overworld, but that
         * functionality is now behind private methods, and I can't be fucked to
         * try to hack around it with reflection.
         *
         * TLDR: private is evil
         */
        Vector<ServerPlayer> online = new Vector<>();
        PlayerList players = world.getServer().getPlayerList();

        for (ServerPlayer player : players.getPlayers()) {
            if (player.level().dimension().equals(Level.OVERWORLD)) {
                online.add(player);
                PlagueSky.mutter("Considering player " + player);
            }
        }

        if (online.isEmpty()) return status + " No players in overworld";

        Random rand = new Random();
        ServerPlayer player = online.get(rand.nextInt(online.size()));
        int r = Config.COMMON.seedRadius.get();
        for (int i = 0; i < Config.COMMON.seedChunks.get(); i++) {
            BlockPos pos = new BlockPos((int) Math.floor(player.position().x), (int) Math.floor(player.position().y), (int) Math.floor(player.position().z));
            int x = pos.getX();
            int z = pos.getZ();

            int tz = z + rand.nextInt(2 * r) - r;
            int tx = x + rand.nextInt(2 * r) - r;

            world.setBlock(new BlockPos(tx, Config.COMMON.plagueHeight.get(), tz),
                    BlockRegistry.DRAGONSKIN.get().defaultBlockState(), 3);
        }

        return status + " Seeded chunks";
    }

    /**
     * Reset internal data when the world is loaded.
     * <p>
     * This prevents stale data if the player switches worlds
     *
     * @param event Information about the event
     */
    @SubscribeEvent
    public void OnWorldLoad(PlayerEvent.PlayerChangedDimensionEvent event) {
        if ((event.getFrom().equals(Level.OVERWORLD))) {
            lastSpread = 0;
            lastSpreadCheck = 0;
            growthQueue = 0;
            nextDecay = 0;

            PlagueSky.mutter("Resetting internal data ");
        }
    }
}
