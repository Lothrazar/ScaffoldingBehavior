package com.lothrazar.scaffoldingpower.events;

import com.lothrazar.scaffoldingpower.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RedstoneEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (!ConfigManager.REDSTONEBUILD.get()) {
      return;
    }
    Player player = event.getEntity();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    Level world = event.getLevel();
    BlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();
    //redstone time
    if (held.getItem() == Items.REDSTONE && stateOG.getBlock() == Blocks.REDSTONE_WIRE) {
      buildRedstone(player, pos, world, stateOG, held);
    }
  }

  private void buildRedstone(Player player, BlockPos pos, Level world, BlockState stateOG, ItemStack held) {
    //then ooo hot stuff 
    Direction facing = player.getDirection();
    for (int i = 1; i < ConfigManager.REDSTONEBUILDRANGE.get(); i++) {
      // 
      BlockPos posCurrent = pos.relative(facing, i);
      BlockState stateCurrent = world.getBlockState(posCurrent);
      if (stateCurrent.getBlock() == Blocks.REDSTONE_WIRE) {
        continue;
        //keep going to the next one this is ok to pass
      }
      BlockState newWire = Blocks.REDSTONE_WIRE.defaultBlockState();
      newWire = newWire.setValue(RedStoneWireBlock.NORTH, stateOG.getValue(RedStoneWireBlock.NORTH));
      newWire = newWire.setValue(RedStoneWireBlock.EAST, stateOG.getValue(RedStoneWireBlock.EAST));
      newWire = newWire.setValue(RedStoneWireBlock.SOUTH, stateOG.getValue(RedStoneWireBlock.SOUTH));
      newWire = newWire.setValue(RedStoneWireBlock.WEST, stateOG.getValue(RedStoneWireBlock.WEST));
      // ok 
      // build it!
      if (newWire.canSurvive(world, posCurrent) && stateCurrent.getMaterial().isReplaceable()) {
        if (world.setBlockAndUpdate(posCurrent, newWire)) {
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
