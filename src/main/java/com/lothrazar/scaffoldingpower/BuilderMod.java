package com.lothrazar.scaffoldingpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(BuilderMod.MODID)
public class BuilderMod {

  public static final String MODID = "scaffoldingpower";
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigManager CONFIG;

  public BuilderMod() {
    CONFIG = new ConfigManager(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  }

  private void setup(final FMLCommonSetupEvent event) {
    MinecraftForge.EVENT_BUS.register(new DoorEvents());
    MinecraftForge.EVENT_BUS.register(new LadderEvents());
    MinecraftForge.EVENT_BUS.register(new RedstoneEvents());
    MinecraftForge.EVENT_BUS.register(new RailEvents());
  }
}
