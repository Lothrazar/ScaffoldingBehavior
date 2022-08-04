package com.lothrazar.scaffoldingpower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RailEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (!ConfigManager.RAILBUILD.get()) {
      return;
    }
    Player player = event.getEntity();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    Level world = event.getLevel();
    BlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();
    if (this.isRail(held) &&
        this.isRail(stateOG)) {
      BlockPos endPos = buildRails(player, pos, world, held);
      if (endPos != null) {
        BlockPos fromUp = buildRails(player, endPos.above(), world, held);
        if (fromUp != null) {
          buildRails(player, endPos.below(), world, held);
        }
      }
    }
  }

  /**
   * 
   * @return NULL if its a either success or out of config range,
   * 
   *         returns position if it failed
   */
  private BlockPos buildRails(Player player, BlockPos pos, Level world, ItemStack held) {
    Direction facing = player.getDirection();
    //build 
    BlockPos posCurrent = pos;
    BlockPos previous = pos;
    for (int i = 1; i < ConfigManager.RAILSAUTOBUILDRANGE.get(); i++) {
      previous = posCurrent;
      posCurrent = pos.relative(facing, i);
      BlockState newRail = Block.byItem(held.getItem()).defaultBlockState();
      BlockState stateCurrent = world.getBlockState(posCurrent);
      if (this.isRail(stateCurrent)) {
        continue;
        //can skip past rails
      }
      boolean replaceHere = stateCurrent.getMaterial().isReplaceable();
      if (replaceHere
          && newRail.canSurvive(world, posCurrent)) {
        if (world.setBlockAndUpdate(posCurrent, newRail)) {
          if (!player.isCreative()) {
            held.shrink(1);
          }
          return null;
          //          break;
        }
      }
      else {
        // its not valid here, so break out, dont skip gaps
        return previous;
        //        break;
      }
    }
    return null;
  }

  private boolean isRail(BlockState block) {
    return block.is(BlockTags.RAILS);
  }

  private boolean isRail(ItemStack item) {
    return item.is(ItemTags.RAILS);
  }
}
