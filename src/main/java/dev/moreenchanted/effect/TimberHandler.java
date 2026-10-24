package dev.moreenchanted.effect;

import dev.moreenchanted.registry.ModEnchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TimberHandler {
    // Break callbacks run on the server thread; suppress only this player's nested Timber callback.
    private static final Set<UUID> ACTIVE = new HashSet<>();

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)
                    || player.isCreative() || player.isSpectator() || player.isShiftKeyDown()
                    || !state.is(BlockTags.LOGS) || ACTIVE.contains(player.getUUID())) return;
            ItemStack tool = player.getMainHandItem();
            int enchantmentLevel = ModEnchantments.getLevel(tool, ModEnchantments.TIMBER);
            if (enchantmentLevel <= 0) return;
            int limit = enchantmentLevel == 1 ? 16 : enchantmentLevel == 2 ? 32 : 64;
            ACTIVE.add(player.getUUID());
            try {
                breakConnectedLogs(serverLevel, serverPlayer, pos, state.getBlock(), tool, limit - 1);
            } finally {
                ACTIVE.remove(player.getUUID());
            }
        });
    }

    private static void breakConnectedLogs(ServerLevel level, ServerPlayer player, BlockPos start,
                                           Block target, ItemStack tool, int budget) {
        var queue = new ArrayDeque<BlockPos>();
        var visited = new HashSet<BlockPos>();
        queue.add(start.immutable());
        visited.add(start.immutable());
        int broken = 0;
        while (!queue.isEmpty() && broken < budget) {
            BlockPos current = queue.remove();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos next = current.offset(dx, dy, dz);
                        if (!visited.add(next) || !level.hasChunkAt(next)) continue;
                        if (!level.getBlockState(next).is(target)) continue;
                        if (tool.isEmpty() || player.getMainHandItem() != tool) return;
                        // Vanilla player break path preserves tool loot/durability and Fabric protection callbacks.
                        if (player.gameMode.destroyBlock(next)) {
                            queue.add(next);
                            if (++broken >= budget) return;
                        }
                    }
                }
            }
        }
    }
}
