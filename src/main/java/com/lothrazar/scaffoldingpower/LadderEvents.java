package com.lothrazar.scaffoldingpower;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LadderEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (!ConfigManager.LADDERBUILD.get()) {
      return;
    }
    Player player = event.getPlayer();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    Level world = event.getWorld();
    BlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();
    //if i used ladder on top of a ladder
    if (held.getItem() == Items.LADDER && stateOG.getBlock() == Blocks.LADDER) {
      buildLadder(player, pos, world, stateOG, held);
    }
  }

  private void buildLadder(Player player, BlockPos pos, Level world, BlockState stateOG, ItemStack held) {
    //then ooo hot stuff 
    for (int i = 1; i < ConfigManager.LADDERBUILDRANGE.get(); i++) {
      // TODO changed in 1.17.1
      BlockPos posCurrent = (player.getRotationVector().x < 0) ? pos.above(i) : pos.below(i);
      BlockState stateCurrent = world.getBlockState(posCurrent);
      if (//!stateCurrent.isAir() && stateCurrent.getBlock() != Blocks.WATER &&
      stateCurrent.getBlock() == Blocks.LADDER) {
        continue;
        // this is a ladder, skip to next
      }
      BlockState newLadder = Blocks.LADDER.defaultBlockState();
      newLadder = newLadder.setValue(LadderBlock.FACING, stateOG.getValue(LadderBlock.FACING));
      // ok 
      // build it!
      boolean replaceHere = stateCurrent.getMaterial().isReplaceable();
      if (replaceHere && (ConfigManager.LADDERBUILDINVALID.get()
          || newLadder.canSurvive(world, posCurrent))) {
        //water logged if its wet
        boolean isWater = world.getFluidState(posCurrent).getType() == Fluids.WATER
            || world.getFluidState(posCurrent).getType() == Fluids.FLOWING_WATER;
        newLadder = newLadder.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(isWater));
        if (world.setBlockAndUpdate(posCurrent, newLadder)) {
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
