package com.lothrazar.scaffoldingpower;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RailEvents {

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    if (!ConfigManager.RAILBUILD.get()) {
      return;
    }
    PlayerEntity player = event.getPlayer();
    //vine, ironbars, powered rails
    BlockPos pos = event.getPos();
    World world = event.getWorld();
    BlockState stateOG = world.getBlockState(pos);
    ItemStack held = event.getItemStack();
    if (this.isRail(held.getItem()) &&
        this.isRail(stateOG.getBlock().asItem())) {
      BlockPos endPos = buildRails(player, pos, world, held);
      if (endPos != null) {
        BlockPos fromUp = buildRails(player, endPos.up(), world, held);
        if (fromUp != null) {
          buildRails(player, endPos.down(), world, held);
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
  private BlockPos buildRails(PlayerEntity player, BlockPos pos, World world, ItemStack held) {
    Direction facing = player.getHorizontalFacing();
    //build 
    BlockPos posCurrent = pos;
    BlockPos previous = pos;
    for (int i = 1; i < ConfigManager.RAILSAUTOBUILDRANGE.get(); i++) {
      previous = posCurrent;
      posCurrent = pos.offset(facing, i);
      BlockState newRail = Block.getBlockFromItem(held.getItem()).getDefaultState();
      BlockState stateCurrent = world.getBlockState(posCurrent);
      if (this.isRail(stateCurrent)) {
        continue;
        //can skip past rails
      }
      boolean replaceHere = stateCurrent.getMaterial().isReplaceable();
      if (replaceHere
          && newRail.isValidPosition(world, posCurrent)) {
        if (world.setBlockState(posCurrent, newRail)) {
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

  private boolean isRail(BlockState item) {
    return item.getBlock().isIn(BlockTags.RAILS);
  }

  private boolean isRail(Item item) {
    return item.isIn(ItemTags.RAILS);
  }
}
