package com.lothrazar.scaffoldingpower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DoorEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (ConfigManager.DOUBLEDOOR.get() == false) {
      return;
    }
    Player player = event.getEntity();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    Level world = event.getLevel();
    BlockState stateOG = world.getBlockState(pos);
    if (!player.isShiftKeyDown() && this.isWoodenDoor(stateOG)) {
      //double door
      this.doubleDoors(world, pos, stateOG);
    }
  }

  private boolean isWoodenDoor(BlockState stateOG) {
    return stateOG.getBlock() instanceof DoorBlock && stateOG.getBlock() != Blocks.IRON_DOOR;
  }

  private void doubleDoors(Level world, BlockPos originalDoorPos, BlockState originalDoorState) {
    Direction doorFacing = originalDoorState.getValue(DoorBlock.FACING);
    //where is the other door located
    BlockPos hingeCcw = originalDoorPos.relative(originalDoorState.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT ? doorFacing.getCounterClockWise() : doorFacing.getClockWise());
    BlockPos secondDoorPos = originalDoorState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? hingeCcw : hingeCcw.below();
    BlockState secondDoorState = world.getBlockState(secondDoorPos);
    //are they aligned the same 
    if (secondDoorState.getValue(DoorBlock.HINGE) == originalDoorState.getValue(DoorBlock.HINGE)) {
      return;
    }
    //both match same block  type(both oak, etc)
    if (secondDoorState.getBlock() == originalDoorState.getBlock() &&
    //both have same facing state
        secondDoorState.getValue(DoorBlock.FACING) == doorFacing &&
        //both have same open state
        secondDoorState.getValue(DoorBlock.OPEN) == originalDoorState.getValue(DoorBlock.OPEN)) {
      // so we have two matching doors, such as two oak doors, facing correctly
      //cycle the state 
      world.setBlockAndUpdate(secondDoorPos, secondDoorState.cycle(DoorBlock.OPEN));
    }
  }
}
