package com.lothrazar.ladderpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.ladderpower.setup.ClientProxy;
import com.lothrazar.ladderpower.setup.IProxy;
import com.lothrazar.ladderpower.setup.ServerProxy;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

// TODO: The value here should match an entry in the META-INF/mods.toml file
// TODO: Also search and replace it in build.gradle
@Mod(ExampleMod.MODID)
public class ExampleMod {

  public static final String MODID = "ladderpower";
  public static final String certificateFingerprint = "@FINGERPRINT@";
  public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigManager config;

  public ExampleMod() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    //only for server starting
    MinecraftForge.EVENT_BUS.register(this);
    config = new ConfigManager(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
  }

  private void setup(final FMLCommonSetupEvent event) {
    //now all blocks/items exist 
  }

  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent event) {
    //you probably will not need this
  }

  private static final int LADDER_ROTATIONLIMIT = -78;
  private static final double LADDER_SPEED = 0.10;
  //
  //
  //

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    //
    PlayerEntity player = event.getPlayer();
    //
    BlockPos pos = event.getPos();
    World world = event.getWorld();
    BlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();//player.getHeldItem(event.getHand());
    if (ConfigManager.RAILBUILD.get() && held.getItem() == Items.RAIL && stateOG.getBlock() == Blocks.RAIL) {
      Direction facing = player.getHorizontalFacing();
      // ok then 
      //build 
      for (int i = 1; i < ConfigManager.AUTOBUILDRANGE.get(); i++) {
        BlockPos posCurrent = pos.offset(facing, i);
        //here? 
        BlockState newLadder = Blocks.RAIL.getDefaultState();
        //        newLadder = newLadder.with(RailBlock.SHAPE, stateOG.get(RailBlock.SHAPE));
        GrassBlock x;
        boolean replaceHere = world.getBlockState(posCurrent).getMaterial().isReplaceable();
        //        System.out.println(replaceHere + " can be replaced " + world.getBlockState(posCurrent));
        if (replaceHere
            && newLadder.isValidPosition(world, posCurrent)) {
          //water logged if its wet
          //          boolean isWater = world.getFluidState(posCurrent).getFluid() == Fluids.WATER
          //              || world.getFluidState(posCurrent).getFluid() == Fluids.FLOWING_WATER;
          //          newLadder = newLadder.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(isWater));
          if (world.setBlockState(posCurrent, newLadder)) {
            if (!player.isCreative()) {
              held.shrink(1);
            }
            break;
          }
        }
      }
    }
    //if i used ladder on top of a ladder
    if (ConfigManager.LADDERBUILD.get() && held.getItem() == Items.LADDER && stateOG.getBlock() == Blocks.LADDER) {
      //then ooo hot stuff 
      for (int i = 1; i < ConfigManager.AUTOBUILDRANGE.get(); i++) {
        // 
        BlockPos posCurrent = (player.rotationPitch < 0) ? pos.up(i) : pos.down(i);
        BlockState stateCurrent = world.getBlockState(posCurrent);
        if (!stateCurrent.isAir() && stateCurrent.getBlock() != Blocks.WATER) {
          continue;
        }
        LadderBlock b;
        BlockState newLadder = Blocks.LADDER.getDefaultState();
        newLadder = newLadder.with(LadderBlock.FACING, stateOG.get(LadderBlock.FACING));
        // ok 
        // build it!
        if (ConfigManager.AUTOBUILDINVALID.get()
            || newLadder.isValidPosition(world, posCurrent)) {
          //water logged if its wet
          boolean isWater = world.getFluidState(posCurrent).getFluid() == Fluids.WATER
              || world.getFluidState(posCurrent).getFluid() == Fluids.FLOWING_WATER;
          newLadder = newLadder.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(isWater));
          if (world.setBlockState(posCurrent, newLadder)) {
            if (!player.isCreative()) {
              held.shrink(1);
            }
            break;
          }
        }
      }
    }
  }
  //
  //  @SubscribeEvent
  //  public void onPlayerTick(LivingUpdateEvent event) {
  //    if (ConfigManager.FASTUP.get()) {
  //      if (event.getEntityLiving() instanceof PlayerEntity) {
  //        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
  //        if (player.isOnLadder() && !player.isCrouching() && player.moveForward == 0) {
  //          //move up faster without 'w'
  //          if (ConfigManager.FASTUP.get() &&
  //              player.rotationPitch < LADDER_ROTATIONLIMIT) {
  //            //up 
  //            player.addVelocity(0, LADDER_SPEED, 0);
  //          }
  //        }
  //      }
  //    }
  //  }
}
