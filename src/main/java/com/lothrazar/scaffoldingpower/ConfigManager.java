package com.lothrazar.scaffoldingpower;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ConfigManager {

  static final ModConfigSpec CONFIG;
  public static BooleanValue LADDERBUILD;
  public static BooleanValue LADDERBUILDINVALID;
  public static IntValue LADDERBUILDRANGE;
  public static BooleanValue RAILBUILD;
  public static IntValue RAILSAUTOBUILDRANGE;
  public static BooleanValue REDSTONEBUILD;
  public static BooleanValue DOUBLEDOOR;
  public static IntValue REDSTONEBUILDRANGE;
  static {
    final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    BUILDER.comment("General settings").push(BuilderMod.MODID);
    DOUBLEDOOR = BUILDER.comment("Enable DoubleDoor opening feature")
        .define("doors.doubleOpen", true);
    REDSTONEBUILD = BUILDER.comment("Auto redstone Building: Place dust on existing dust and it will build out depending where player is facing")
        .define("redstone.autoBuild", true);
    REDSTONEBUILDRANGE = BUILDER.comment("Auto redstone Building: range away from source placement")
        .defineInRange("redstone.autoBuildRange", 128, 1, 256);
    RAILSAUTOBUILDRANGE = BUILDER.comment("Auto Rails Building: horizontal range away from source placement")
        .defineInRange("rails.autoBuildRange", 128, 1, 256);
    RAILBUILD = BUILDER.comment("Auto Rail Building: Place a rail on a rail and it will build out depending where player is facing")
        .define("rails.autoBuild", true);
    LADDERBUILD = BUILDER.comment("Auto Ladder Building: Place a ladder on a ladder and it will build up or down depending where player is facing")
        .define("ladder.autoBuild", true);
    LADDERBUILDRANGE = BUILDER.comment("Auto Ladder Building: vertical range away from source placement")
        .defineInRange("ladder.autoBuildRange", 128, 1, 256);
    LADDERBUILDINVALID = BUILDER.comment("Auto Ladder Building: true means allow invalid building places such as floating in midair")
        .define("ladder.autoBuildInvalid", false);
    BUILDER.pop();
    CONFIG = BUILDER.build();
  }
}
