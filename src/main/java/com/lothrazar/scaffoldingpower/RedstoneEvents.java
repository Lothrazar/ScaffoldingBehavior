package com.lothrazar.scaffoldingpower;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RedstoneEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (!ConfigManager.REDSTONEBUILD.get()) {
      return;
    }
    PlayerEntity player = event.getPlayer();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    World world = event.getWorld();
    BlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();
    //redstone time
    if (held.getItem() == Items.REDSTONE && stateOG.getBlock() == Blocks.REDSTONE_WIRE) {
      buildRedstone(player, pos, world, stateOG, held);
    }
  }

  private void buildRedstone(PlayerEntity player, BlockPos pos, World world, BlockState stateOG, ItemStack held) {
    //then ooo hot stuff 
    Direction facing = player.getHorizontalFacing();
    for (int i = 1; i < ConfigManager.REDSTONEBUILDRANGE.get(); i++) {
      // 
      BlockPos posCurrent = pos.offset(facing, i);
      BlockState stateCurrent = world.getBlockState(posCurrent);
      if (stateCurrent.getBlock() == Blocks.REDSTONE_WIRE) {
        continue;
        //keep going to the next one this is ok to pass
      }
      BlockState newWire = Blocks.REDSTONE_WIRE.getDefaultState();
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
}
