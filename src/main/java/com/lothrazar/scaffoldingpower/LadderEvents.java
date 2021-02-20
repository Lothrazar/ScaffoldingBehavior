package com.lothrazar.scaffoldingpower;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LadderEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (!ConfigManager.LADDERBUILD.get()) {
      return;
    }
    PlayerEntity player = event.getPlayer();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    World world = event.getWorld();
    BlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();
    //if i used ladder on top of a ladder
    if (held.getItem() == Items.LADDER && stateOG.getBlock() == Blocks.LADDER) {
      buildLadder(player, pos, world, stateOG, held);
    }
  }

  private void buildLadder(PlayerEntity player, BlockPos pos, World world, BlockState stateOG, ItemStack held) {
    //then ooo hot stuff 
    for (int i = 1; i < ConfigManager.LADDERBUILDRANGE.get(); i++) {
      // 
      BlockPos posCurrent = (player.rotationPitch < 0) ? pos.up(i) : pos.down(i);
      BlockState stateCurrent = world.getBlockState(posCurrent);
      if (//!stateCurrent.isAir() && stateCurrent.getBlock() != Blocks.WATER &&
      stateCurrent.getBlock() == Blocks.LADDER) {
        continue;
        // this is a ladder, skip to next
      }
      BlockState newLadder = Blocks.LADDER.getDefaultState();
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
}
