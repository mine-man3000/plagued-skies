package com.tardislabs.plaguesky.events;

import com.tardislabs.plaguesky.Config;
import com.tardislabs.plaguesky.Data;
import com.tardislabs.plaguesky.PlagueSky;
import com.tardislabs.plaguesky.blocks.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = PlagueSky.MODID)
public class TestEvent {
    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onEntityPlace(BlockEvent.EntityPlaceEvent event) {
        int x = event.getPos().getX();
        int y = event.getPos().getY();
        int z = event.getPos().getZ();

        ServerLevel world = (ServerLevel) event.getLevel();
        PlayerList players = world.getServer().getPlayerList();

        if( !world.dimension().equals( Level.OVERWORLD )) {
            return;
        }
        if( event.getPlacedBlock().getBlock().equals( Blocks.DRAGON_EGG ) )
        {
            Data data = new Data(world.getServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().toString());
            if( data.isHealing() ) return;
            data.setHealing( true );
            for( ServerPlayer player: players.getPlayers() )
            {
                player.sendSystemMessage(Component.literal("The healing has begun."), false);
            }
        }
        else if( event.getPlacedBlock().getBlock().equals( Blocks.BEACON ))
        {
            for( int y1 = y + 1; y1 < 256; y1++ )
            {
                Block b = world.getBlockState( new BlockPos( x, y1, z )).getBlock();
                // PlagueSky.mutter( "Checking " + b.toString() + "at Y" + y1 );
                if( b.equals( BlockRegistry.DRAGONSKIN.get() ))
                {
                    // PlagueSky.mutter( "Hit dragonskin" );

                    int r = Config.COMMON.beaconBlastRadius.get();
                    for( int x1 = x - r; x1 < x + r; x1++ )
                    {
                        for( int z1 = z - r; z1 < z + r; z1++ )
                        {
                            // PlagueSky.mutter( "Checking " + x1 + "x" + z1 );
                            if( Math.pow( x1 - x, 2 ) + Math.pow( z1 - z , 2 ) < Math.pow( r, 2 ))
                            {
                                Block b1 = world.getBlockState( new BlockPos( x1, y1, z1 ) ).getBlock();
                                // PlagueSky.mutter( "In circle: " + b1.toString() );
                                if( b1.equals( BlockRegistry.DRAGONSKIN.get() ))
                                {
                                    // PlagueSky.mutter( "Is dragonskin" );
                                    world.setBlock( new BlockPos( x1, y1, z1 ),
                                            Blocks.AIR.defaultBlockState(), 3 );
                                }
                            }
                        }
                    }
                    return;
                }
                else if( !b.equals( Blocks.AIR ))
                {
                    // PlagueSky.mutter( "Hit a block" );
                    return;
                }
            }
            // PlagueSky.mutter( "Hit sky" );
        }
    }

    @SubscribeEvent
    public static void onEntityBreak(BlockEvent.BreakEvent event) {
        // Make sure we're running on the server side
        if( event.getLevel().isClientSide() ) return;

        ServerLevel world = (ServerLevel) event.getLevel();
        if( !world.dimension().equals( Level.OVERWORLD )) return;
        if( !event.getState().getBlock().equals( Blocks.DRAGON_EGG )) return;

        Data data = new Data(world.getServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().toString());
        if( !data.isHealing() ) return;
        data.setHealing( false );
        List<ServerPlayer> players = world.getServer().getPlayerList().getPlayers();
        for( ServerPlayer player: players )
        {
            player.sendSystemMessage(Component.literal( "The healing has stopped." ), false );
        }
    }
}
