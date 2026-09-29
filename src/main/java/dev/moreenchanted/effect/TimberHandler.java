package dev.moreenchanted.effect;

import dev.moreenchanted.registry.ModEnchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class TimberHandler {
    private static final Direction[] DIRECTIONS = Direction.values();

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide()) return;
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (!state.is(BlockTags.LOGS)) return;

            ItemStack axe = player.getMainHandItem();
            int timberLevel = ModEnchantments.getLevel(axe, ModEnchantments.TIMBER);
            if (timberLevel <= 0) return;

            int maxBlocks = timberLevel == 1 ? 16 : (timberLevel == 2 ? 32 : 64);
            breakConnectedLogs((ServerLevel) level, serverPlayer, pos, state, axe, maxBlocks);
        });
    }

    private static void breakConnectedLogs(ServerLevel level, ServerPlayer player, BlockPos startPos, BlockState originalState, ItemStack tool, int maxBlocks) {
        Block targetBlock = originalState.getBlock();
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);
        int brokenCount = 0;

        while (!queue.isEmpty() && brokenCount < maxBlocks) {
            BlockPos current = queue.poll();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos neighbor = current.offset(dx, dy, dz);
                        if (visited.contains(neighbor)) continue;

                        BlockState neighborState = level.getBlockState(neighbor);
                        if (neighborState.is(BlockTags.LOGS) || neighborState.is(targetBlock)) {
                            visited.add(neighbor);
                            queue.add(neighbor);

                            // Break the block and drop loot
                            level.destroyBlock(neighbor, true, player);
                            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                            brokenCount++;

                            if (tool.isEmpty()) {
                                return; // Axe broke
                            }
                            if (brokenCount >= maxBlocks) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
