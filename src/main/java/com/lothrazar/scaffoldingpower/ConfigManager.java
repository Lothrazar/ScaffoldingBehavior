package com.lothrazar.scaffoldingpower;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigManager {

  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;
  //  public static BooleanValue FASTUP;
  public static BooleanValue LADDERBUILD;
  public static BooleanValue LADDERBUILDINVALID;
  public static IntValue LADDERBUILDRANGE;
  public static BooleanValue RAILBUILD;
  public static IntValue RAILSAUTOBUILDRANGE;
  public static BooleanValue REDSTONEBUILD;
  public static BooleanValue DOUBLEDOOR;
  public static IntValue REDSTONEBUILDRANGE;
  static {
    initConfig();
  }

  private static void initConfig() {
    COMMON_BUILDER.comment("General settings").push(LadderPowerMod.MODID);
    DOUBLEDOOR = COMMON_BUILDER.comment("Enable DoubleDoor opening feature")
        .define("doors.doubleOpen", true);
    REDSTONEBUILD = COMMON_BUILDER.comment("Auto redstone Building: Place dust on existing dust and it will build out depending where player is facing")
        .define("redstone.autoBuild", true);
    REDSTONEBUILDRANGE = COMMON_BUILDER.comment("Auto redstone Building: range away from source placement")
        .defineInRange("redstone.autoBuildRange", 128, 1, 256);
    RAILSAUTOBUILDRANGE = COMMON_BUILDER.comment("Auto Rails Building: horizontal range away from source placement")
        .defineInRange("rails.autoBuildRange", 128, 1, 256);
    RAILBUILD = COMMON_BUILDER.comment("Auto Rail Building: Place a rail on a rail and it will build out depending where player is facing")
        .define("rails.autoBuild", true);
    LADDERBUILD = COMMON_BUILDER.comment("Auto Ladder Building: Place a ladder on a ladder and it will build up or down depending where player is facing")
        .define("ladder.autoBuild", true);
    LADDERBUILDRANGE = COMMON_BUILDER.comment("Auto Ladder Building: vertical range away from source placement")
        .defineInRange("ladder.autoBuildRange", 128, 1, 256);
    LADDERBUILDINVALID = COMMON_BUILDER.comment("Auto Ladder Building: true means allow invalid building places such as floating in midair")
        .define("ladder.autoBuildInvalid", false);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public ConfigManager(Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path)
        .sync()
        .autosave()
        .writingMode(WritingMode.REPLACE)
        .build();
    configData.load();
    COMMON_CONFIG.setConfig(configData);
  }
}
