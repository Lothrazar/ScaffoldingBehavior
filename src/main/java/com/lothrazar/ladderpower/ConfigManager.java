package com.lothrazar.ladderpower;

import java.nio.file.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigManager {

  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;
  //  public static BooleanValue FASTUP;
  public static BooleanValue LADDERBUILD;
  public static BooleanValue AUTOBUILDINVALID;
  public static IntValue AUTOBUILDRANGE;
  public static BooleanValue RAILBUILD;
  static {
    initConfig();
  }

  private static void initConfig() {
    COMMON_BUILDER.comment("General settings").push(ExampleMod.MODID);
    //    FASTUP = COMMON_BUILDER.comment(
    //        "While on a ladder and looking up, you will auto climb the ladder.  Still lets you sneak to stop moving.  Disable if you have auto-sprint or overlapping ladder features from other mods.   ")
    //        .define("ladder.autoClimbingUp", true);
    RAILBUILD = COMMON_BUILDER.comment("Auto Rail Building: Place a rail on a rail and it will build out depending where player is facing")
        .define("rails.autoBuild", true);
    LADDERBUILD = COMMON_BUILDER.comment("Auto Ladder Building: Place a ladder on a ladder and it will build up or down depending where player is facing")
        .define("ladder.autoBuild", true);
    AUTOBUILDRANGE = COMMON_BUILDER.comment("Auto Ladder Building: vertical range away from source placement")
        .defineInRange("ladder.autoBuildRange", 16, 1, 64);
    AUTOBUILDINVALID = COMMON_BUILDER.comment("Auto Ladder Building: true means allow invalid building places such as floating in midair")
        .define("ladder.autoBuildInvalid", true);
    //RAILS SAME THING 
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
