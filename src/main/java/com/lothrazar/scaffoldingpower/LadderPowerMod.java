package com.lothrazar.scaffoldingpower;

import io.netty.handler.codec.http2.Http2FrameLogger.Direction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

// TODO: The value here should match an entry in the META-INF/mods.toml file
// TODO: Also search and replace it in build.gradle
@Mod(modid = LadderPowerMod.MODID, certificateFingerprint = "@FINGERPRINT@", updateJSON = "https://raw.githubusercontent.com/Lothrazar/ScaffoldingBehavior/trunk/1.12/update.json")
public class LadderPowerMod {

  public static final String MODID = "scaffoldingpower";
  public static final String certificateFingerprint = "@FINGERPRINT@";
  public static ConfigManager config;

  public LadderPowerMod() {
    // Register the setup method for modloading 
    //only for server starting
    MinecraftForge.EVENT_BUS.register(this);
    config = new ConfigManager(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
  }

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    EntityPlayer player = event.getEntityPlayer();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    World world = event.getWorld();
    IBlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();
    if (ConfigManager.RAILBUILD.get() && this.isRail(held.getItem()) &&
        this.isRail(stateOG.getBlock().asItem())) {
      buildRails(player, pos, world, held);
    }
    //if i used ladder on top of a ladder
    if (ConfigManager.LADDERBUILD.get() && held.getItem() == Items.LADDER && stateOG.getBlock() == Blocks.LADDER) {
      buildLadder(player, pos, world, stateOG, held);
    }
    //redstone time
    if (ConfigManager.REDSTONEBUILD.get() && held.getItem() == Items.REDSTONE && stateOG.getBlock() == Blocks.REDSTONE_WIRE) {
      buildRedstone(player, pos, world, stateOG, held);
    }
  }

  private void buildRedstone(PlayerEntity player, BlockPos pos, World world, BlockState stateOG, ItemStack held) {
    //then ooo hot stuff 
    Direction facing = player.getHorizontalFacing();
    for (int i = 1; i < ConfigManager.REDSTONEBUILDRANGE.get(); i++) {
      // 
      BlockPos posCurrent = pos.offset(facing, i);
      IBlockState stateCurrent = world.getBlockState(posCurrent);
      if (stateCurrent.getBlock() == Blocks.REDSTONE_WIRE) {
        continue;//keep going to the next one this is ok to pass
      }
      IBlockState newWire = Blocks.REDSTONE_WIRE.getDefaultState();
      newWire = newWire.with(RedstoneWireBlock.NORTH, stateOG.get(RedstoneWireBlock.NORTH));
      newWire = newWire.with(RedstoneWireBlock.EAST, stateOG.get(RedstoneWireBlock.EAST));
      newWire = newWire.with(RedstoneWireBlock.SOUTH, stateOG.get(RedstoneWireBlock.SOUTH));
      newWire = newWire.with(RedstoneWireBlock.WEST, stateOG.get(RedstoneWireBlock.WEST));
      // ok 
      // build it!
      if (newWire.isValidPosition(world, posCurrent) && stateCurrent.getMaterial().isReplaceable()) {
        if (world.setBlockState(posCurrent, newWire)) {
          if (!player.isCreative()) {
            held.shrink(1);
          }
          break;
        }
      }
      else {
        // its not valid here, so break out, dont skip gaps
        break;
      }
    }
  }

  private void buildLadder(PlayerEntity player, BlockPos pos, World world, BlockState stateOG, ItemStack held) {
    //then ooo hot stuff 
    for (int i = 1; i < ConfigManager.LADDERBUILDRANGE.get(); i++) {
      // 
      BlockPos posCurrent = (player.rotationPitch < 0) ? pos.up(i) : pos.down(i);
      IBlockState stateCurrent = world.getBlockState(posCurrent);
      if (//!stateCurrent.isAir() && stateCurrent.getBlock() != Blocks.WATER &&
      stateCurrent.getBlock() == Blocks.LADDER) {
        continue;// this is a ladder, skip to next
      }
      IBlockState newLadder = Blocks.LADDER.getDefaultState();
      newLadder = newLadder.with(LadderBlock.FACING, stateOG.get(LadderBlock.FACING));
      // ok 
      // build it!
      boolean replaceHere = stateCurrent.getMaterial().isReplaceable();
      if (replaceHere && (ConfigManager.LADDERBUILDINVALID.get()
          || newLadder.isValidPosition(world, posCurrent))) {
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
      else {
        //config is false AND its not valid here, so break out, dont skip gaps
        break;
      }
    }
  }

  private void buildRails(PlayerEntity player, BlockPos pos, World world, ItemStack held) {
    Direction facing = player.getHorizontalFacing();
    // ok then 
    //      int yLevel = pos.getY();
    //build 
    for (int i = 1; i < ConfigManager.RAILSAUTOBUILDRANGE.get(); i++) {
      BlockPos posCurrent = pos.offset(facing, i);
      //UP AND DOWN HILLS
      //        if(this.isRail(item))
      //here? 
      IBlockState newRail = Block.getBlockFromItem(held.getItem()).getDefaultState();
      IBlockState stateCurrent = world.getBlockState(posCurrent);
      if (this.isRail(stateCurrent)) {
        continue;//can skip past rails
      }
      boolean replaceHere = stateCurrent.getMaterial().isReplaceable();
      if (replaceHere
          && newRail.isValidPosition(world, posCurrent)) {
        if (world.setBlockState(posCurrent, newRail)) {
          if (!player.isCreative()) {
            held.shrink(1);
          }
          break;
        }
      }
      else {
        // its not valid here, so break out, dont skip gaps
        break;
      }
    }
  }

  private boolean isRail(Item item) {
    return item.isIn(ItemTags.RAILS);
  }

  private boolean isRail(BlockState item) {
    return item.getBlock().isIn(BlockTags.RAILS);
  }
}
