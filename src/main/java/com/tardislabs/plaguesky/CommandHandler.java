package com.tardislabs.plaguesky;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;

public class CommandHandler {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("healplague")
                .then(Commands.literal( "on" ).executes(HealCommand::healOn))
                .then(Commands.literal( "off" ).executes(HealCommand::healOff))
                .then(Commands.literal("get").executes(HealCommand::getHeal)));
    }
}

class HealCommand
{
    enum Mode {
        OFF, ON, GET
    }

    static int healOn( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException
    {
        setHeal( context, Mode.ON );
        return Command.SINGLE_SUCCESS;
    }

    static int healOff( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException
    {
        setHeal( context, Mode.OFF );
        return Command.SINGLE_SUCCESS;
    }

    static int getHeal( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException
    {
        setHeal( context, Mode.GET );
        return Command.SINGLE_SUCCESS;
    }
    /**
     * Get or set the healing mode
     * @param context	Information about the source of the command
     * @param mode		Whether to turn heal on or off, or just display status
     */
    static void setHeal( CommandContext<CommandSourceStack> context, Mode mode ) {
        String levelPath = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().toString();
        Data data = new Data(levelPath);
        if( mode == Mode.ON )
        {
            data.setHealing( true );
        }
        if( mode == Mode.OFF )
        {
            data.setHealing( false );
        }

        if(context.getSource().getEntity() instanceof Player player) {
            player.sendSystemMessage(Component.literal("Healing is " + (data.isHealing() ? "on" : "off"))); // Test response
        }
    }
}