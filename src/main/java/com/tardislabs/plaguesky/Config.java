package com.tardislabs.plaguesky;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class Config {
    public static class Common {
        public final IntValue seedTime;
        public final IntValue seedChunks;
        public final IntValue seedRadius;
        public final IntValue growthPercent;
        public final IntValue decayPercent;
        public final IntValue sloughPercent;
        public final IntValue orePercent;
        public final IntValue plagueHeight;
        // public static int spreadBatch;
        public final IntValue spreadDelay;
        public final IntValue spreadCap;
        public final IntValue beaconBlastRadius;
        public final BooleanValue healDefault;
        public final BooleanValue debug;
        public final BooleanValue patchyDecay;
        public final ConfigValue<List<? extends String>> dropBlocks;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            seedTime = builder
                    .comment("How long to wait (in seconds) before seeding new growths " +
                            "if no existing spread is detected")
                    .defineInRange("seedTime", 60, 1, Integer.MAX_VALUE);
            seedChunks = builder
                    .comment("How many chunks to seed when seeding new growths")
                    .defineInRange("seedChunks", 50, 1, Integer.MAX_VALUE);
            seedRadius = builder
                    .comment("Maximum distance from a player to seed growths")
                    .defineInRange("seedRadius", 128, 1, Integer.MAX_VALUE);
            growthPercent = builder
                    .comment("The speed at which dragonskin grows. 100 is roughly the speed " +
                            "of spreading grass")
                    .defineInRange("growthPercent", 100, 0, Integer.MAX_VALUE);
            spreadDelay = builder
                    .comment("This causes plague block spread to be batched and performed " +
                            "at once, with an interval of this many seconds. The overall growth " +
                            "rate should be roughly the same, but it might help with lag.")
                    .defineInRange("spreadDelay", 60, 0, Integer.MAX_VALUE);
            spreadCap = builder
                    .comment("The maximum number of block spreads performed in a " +
                            "single update. This limits the total spread to an average " +
                            "of spreadCap per spreadDelay seconds. Lower this if you're " +
                            "getting a lot of lag/dropped ticks on spreading. (0=No limit)")
                    .defineInRange("spreadCap", 500, 0, Integer.MAX_VALUE);
            beaconBlastRadius = builder
                    .comment("The radius, in blocks, that a beacon will blast dragonskin " +
                            "blocks overhead when placed.")
                    .defineInRange("beaconBlastRadius", 8, 0, Integer.MAX_VALUE);
            decayPercent = builder
                    .comment("The speed at which dragonskin decays in heal mode. 100 is " +
                            "roughtly the speed that grass dies when covered. If decaying " +
                            "too much at once lags, try lowering this.")
                    .defineInRange("decayPercent", 100, 0, 100);
            sloughPercent = builder
                    .comment("How often (percentage) a decaying skin block should drop a " +
                            "piece of dragonsscale (as opposed to just disappearing). Too high " +
                            "might lag the server.")
                    .defineInRange("sloughPercent", 10, 0, 100);
            orePercent = builder
                    .comment("How quickly a decaying dragonscale block should turn into an ore. " +
                            "100 means 'the moment it lands'. Technically this is the chance it " +
                            "has to change each time the block ticks.")
                    .defineInRange("orePercent", 10, 0, 100);
            plagueHeight = builder
                    .comment("The height that the plague seeds at")
                    .defineInRange("plagueHeight", 255, 0, 319);
            healDefault = builder
                    .comment("Start the world in heal mode. Perhaps you want to configure " +
                            "your pack to start the plague when a certain event happens. " +
                            "(The command /healplague off can start it.)")
                    .define("healDefault", false);
            patchyDecay = builder
                    .comment("When dragonskin blocks decay, they decay in patches of " +
                            "connected blocks, rather than individual blocks")
                    .define("patchyDecay", true);

            // This is also somewhat black magic to me
            dropBlocks = builder
                    .comment("The blocks that might be released from Dragonscale. " +
                            "This can be a resource ID (minecraft:stone) or a tag" +
                            "entry (forge:ores/iron)")
                    .defineList("dropBlocks", ImmutableList.of(
                            "forge:blocks/iron_ores",
                            "forge:blocks/diamond_ores",
                            "forge:blocks/gold_ores"
                    ), (val) -> true);

            debug = builder
                    .comment("Display debugging data in the console")
                    .define("debug", false);
            builder.pop();
        }

    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        // This is all still black magic to me - NR
        Pair<Common, ForgeConfigSpec> commonSpecPair =
                new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
