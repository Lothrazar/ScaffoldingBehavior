package com.lothrazar.scaffoldingpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.scaffoldingpower.events.DoorEvents;
import com.lothrazar.scaffoldingpower.events.LadderEvents;
import com.lothrazar.scaffoldingpower.events.RailEvents;
import com.lothrazar.scaffoldingpower.events.RedstoneEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BuilderMod.MODID)
public class BuilderMod {

  public static final String MODID = "scaffoldingpower";
  public static final Logger LOGGER = LogManager.getLogger();

  public BuilderMod(IEventBus modEventBus, ModContainer modContainer) {
    modContainer.registerConfig(ModConfig.Type.COMMON, ConfigManager.CONFIG);
    modEventBus.addListener(this::setup);
  }

  private void setup(final FMLCommonSetupEvent event) {
    NeoForge.EVENT_BUS.register(new DoorEvents());
    NeoForge.EVENT_BUS.register(new LadderEvents());
    NeoForge.EVENT_BUS.register(new RedstoneEvents());
    NeoForge.EVENT_BUS.register(new RailEvents());
  }
}
