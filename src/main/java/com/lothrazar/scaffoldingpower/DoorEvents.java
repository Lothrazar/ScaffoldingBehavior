package com.lothrazar.scaffoldingpower;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DoorEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (ConfigManager.DOUBLEDOOR.get() == false) {
      return;
    }
    PlayerEntity player = event.getPlayer();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    World world = event.getWorld();
    BlockState stateOG = world.getBlockState(pos);
    if (!player.isSneaking() && this.isWoodenDoor(stateOG)) {
      //double door
      this.doubleDoors(world, pos, stateOG);
    }
  }

  private boolean isWoodenDoor(BlockState stateOG) {
    return stateOG.getBlock() instanceof DoorBlock && stateOG.getBlock() != Blocks.IRON_DOOR;
  }

  private void doubleDoors(World world, BlockPos originalDoorPos, BlockState originalDoorState) {
    Direction doorFacing = originalDoorState.get(DoorBlock.FACING);
    //where is the other door located
    BlockPos hingeCcw = originalDoorPos.offset(originalDoorState.get(DoorBlock.HINGE) == DoorHingeSide.RIGHT ? doorFacing.rotateYCCW() : doorFacing.rotateY());
    BlockPos secondDoorPos = originalDoorState.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? hingeCcw : hingeCcw.down();
    BlockState secondDoorState = world.getBlockState(secondDoorPos);
    //are they aligned the same 
    if (secondDoorState.get(DoorBlock.HINGE) == originalDoorState.get(DoorBlock.HINGE)) {
      return;
    }
    //both match same block  type(both oak, etc)
    if (secondDoorState.getBlock() == originalDoorState.getBlock() &&
    //both have same facing state
        secondDoorState.get(DoorBlock.FACING) == doorFacing &&
        //both have same open state
        secondDoorState.get(DoorBlock.OPEN) == originalDoorState.get(DoorBlock.OPEN)) {
      // so we have two matching doors, such as two oak doors, facing correctly
      //cycle the state 
      world.setBlockState(secondDoorPos, secondDoorState.func_235896_a_(DoorBlock.OPEN));
    }
  }
}
