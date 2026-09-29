package dev.moreenchanted.effect;

import dev.moreenchanted.registry.ModEnchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ForagerHandler {
    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide()) return;
            if (state.is(BlockTags.LEAVES) || isWildernessFoliage(state)) {
                ItemStack tool = player.getMainHandItem();
                int foragerLevel = ModEnchantments.getLevel(tool, ModEnchantments.FORAGER);
                if (foragerLevel > 0) {
                    handleForageDrops((ServerLevel) level, player, pos, foragerLevel);
                }
            }
        });
    }

    private static boolean isWildernessFoliage(BlockState state) {
        String name = state.getBlock().getDescriptionId().toLowerCase();
        return name.contains("shrub") || name.contains("bush") || name.contains("mushroom") || name.contains("foliage");
    }

    private static void handleForageDrops(ServerLevel level, Player player, BlockPos pos, int levelMultiplier) {
        RandomSource random = level.getRandom();
        float chance = levelMultiplier * 0.30f; // 30% per level

        if (random.nextFloat() < chance) {
            int roll = random.nextInt(4);
            ItemStack bonusDrop;
            switch (roll) {
                case 0 -> bonusDrop = new ItemStack(Items.APPLE, 1 + random.nextInt(levelMultiplier));
                case 1 -> bonusDrop = new ItemStack(Items.STICK, 1 + random.nextInt(levelMultiplier * 2));
                case 2 -> bonusDrop = new ItemStack(Items.SWEET_BERRIES, 1 + random.nextInt(levelMultiplier));
                default -> bonusDrop = new ItemStack(Items.WHEAT_SEEDS, 1 + random.nextInt(levelMultiplier));
            }
            Block.popResource(level, pos, bonusDrop);
        }

        // Special rare Golden Apple drop on Level 3 (0.5% chance)
        if (levelMultiplier >= 3 && random.nextFloat() < 0.005f) {
            Block.popResource(level, pos, new ItemStack(Items.GOLDEN_APPLE));
        }
    }
}
