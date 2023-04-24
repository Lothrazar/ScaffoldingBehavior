package com.lothrazar.scaffoldingpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.scaffoldingpower.events.DoorEvents;
import com.lothrazar.scaffoldingpower.events.LadderEvents;
import com.lothrazar.scaffoldingpower.events.RailEvents;
import com.lothrazar.scaffoldingpower.events.RedstoneEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BuilderMod.MODID)
public class BuilderMod {

  public static final String MODID = "scaffoldingpower";
  public static final Logger LOGGER = LogManager.getLogger();

  public BuilderMod() {
    new ConfigManager();
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  }

  private void setup(final FMLCommonSetupEvent event) {
    MinecraftForge.EVENT_BUS.register(new DoorEvents());
    MinecraftForge.EVENT_BUS.register(new LadderEvents());
    MinecraftForge.EVENT_BUS.register(new RedstoneEvents());
    MinecraftForge.EVENT_BUS.register(new RailEvents());
  }
}
