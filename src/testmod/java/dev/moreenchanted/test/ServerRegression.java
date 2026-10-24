package dev.moreenchanted.test;

import com.mojang.authlib.GameProfile;
import dev.moreenchanted.registry.ModEnchantments;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class ServerRegression implements ModInitializer {
    private static BlockPos protectedLog;
    private static int checks;
    private static net.minecraft.world.entity.LivingEntity attacker;
    private static net.minecraft.world.entity.LivingEntity victim;
    private static void check(boolean value, String label) {
        if (!value) throw new AssertionError(label);
        checks++;
        System.out.println("[MORE-REGRESSION] PASS " + label);
    }

    @Override public void onInitialize() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> !pos.equals(protectedLog));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            server.overworld().setChunkForced(0, 0, true);
            server.overworld().setChunkForced(0, 1, true);
        });
        final int[] ticks = {0};
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ticks[0]++;
            if (ticks[0] == 10) {
                ServerLevel level = server.overworld();
                level.getChunk(0, 0);
                attacker = EntityTypes.HUSK.create(level, EntitySpawnReason.COMMAND);
                victim = EntityTypes.PIG.create(level, EntitySpawnReason.COMMAND);
                attacker.setPos(0, 100, 0);
                victim.setPos(1, 100, 0);
                attacker.setNoGravity(true);
                victim.setNoGravity(true);
                ((net.minecraft.world.entity.Mob) attacker).setNoAi(true);
                ((net.minecraft.world.entity.Mob) victim).setNoAi(true);
                level.addFreshEntity(attacker);
                level.addFreshEntity(victim);
            }
            if (ticks[0] != 20) return;
            try {
                run(server);
                Files.writeString(Path.of("smoke-result.txt"), "PASS " + checks + " runtime checks\n");
            } catch (Throwable error) {
                error.printStackTrace();
                try { Files.writeString(Path.of("smoke-result.txt"), "FAIL " + error + "\n"); }
                catch (Exception ignored) { }
            } finally { server.halt(false); }
        });
    }

    private static void run(MinecraftServer server) {
        ServerLevel level = server.overworld();
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        check(registry.listElements().filter(e -> e.unwrapKey().orElseThrow().identifier().getNamespace().equals("more_enchanted")).count() == 12,
                "all 12 enchantments load in the actual server registry");
        check(level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, victim.getBoundingBox().inflate(4)).contains(victim),
                "fixture target is visible to normal server entity queries");
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(registry.getOrThrow(ModEnchantments.VAMPIRIC_STRIKE), 3);
        attacker.setItemSlot(EquipmentSlot.MAINHAND, sword);
        attacker.setHealth(10.0f);
        ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(victim, level.damageSources().mobAttack(attacker), .5f, .5f, false);
        check(Math.abs(attacker.getHealth() - 10.11f) < .001f, "lifesteal follows 22 percent without a one-heart minimum");
        float health = attacker.getHealth();
        ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(victim, level.damageSources().mobAttack(attacker), 5, 0, true);
        check(attacker.getHealth() == health, "blocked zero-damage hits do not heal");
        ItemStack corruptSword = new ItemStack(Items.DIAMOND_SWORD);
        corruptSword.enchant(registry.getOrThrow(ModEnchantments.VAMPIRIC_STRIKE), 99);
        check(ModEnchantments.getLevel(corruptSword, ModEnchantments.VAMPIRIC_STRIKE) == 3, "overlevel enchantments are bounded by their definition");

        ItemStack bow = new ItemStack(Items.BOW);
        bow.enchant(registry.getOrThrow(ModEnchantments.SPORE_BURST), 2);
        Arrow arrow = new Arrow(level, attacker, new ItemStack(Items.ARROW), bow);
        attacker.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        check(ModEnchantments.getLevel(arrow.getWeaponItem(), ModEnchantments.SPORE_BURST) == 2, "arrow retains the original bow enchantment");
        ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(victim, level.damageSources().arrow(arrow, attacker), 4, 4, false);
        check(victim.hasEffect(MobEffects.POISON), "arrow uses its original enchanted bow after the shooter switches items");
        check(registry.getOrThrow(ModEnchantments.SPORE_BURST).value().isSupportedItem(new ItemStack(Items.CROSSBOW)),
                "Spore Burst can be applied to crossbows");

        GameProfile profile = new GameProfile(UUID.randomUUID(), "TimberRegression");
        ServerPlayer player = new ServerPlayer(server, level, profile, ClientInformation.createDefault());
        player.connection = new ServerGamePacketListenerImpl(server, new Connection(PacketFlow.SERVERBOUND), player,
                CommonListenerCookie.createInitial(profile, false));
        player.setGameMode(GameType.SURVIVAL);
        BlockPos origin = new BlockPos(10, 100, 10);
        player.setPos(10.5, 100, 11.5);
        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        axe.enchant(registry.getOrThrow(ModEnchantments.TIMBER), 1);
        player.setItemSlot(EquipmentSlot.MAINHAND, axe);
        for (int y = 0; y < 20; y++) level.setBlock(origin.above(y), Blocks.OAK_LOG.defaultBlockState(), 3);
        protectedLog = origin.above(3);
        check(player.gameMode.destroyBlock(origin), "original Timber log is broken by a survival player");
        check(level.getBlockState(protectedLog).is(Blocks.OAK_LOG), "Timber respects a protection callback on connected logs");
        check(level.getBlockState(origin.above(4)).is(Blocks.OAK_LOG), "Timber does not traverse through a denied block");
        check(axe.getDamageValue() == 3, "Timber applies normal durability exactly once per broken log");
        protectedLog = null;
        for (int y = 0; y < 20; y++) level.setBlock(origin.above(y), Blocks.OAK_LOG.defaultBlockState(), 3);
        axe.setDamageValue(0);
        check(player.gameMode.destroyBlock(origin), "second Timber tree starts normally");
        int remaining = 0;
        for (int y = 0; y < 20; y++) if (level.getBlockState(origin.above(y)).is(Blocks.OAK_LOG)) remaining++;
        check(remaining == 4, "Timber I limit counts the initial log within 16 total blocks");
        check(axe.getDamageValue() == 16, "bounded Timber operation has no recursive extra breaks or double durability");
        for (int y = 0; y < 4; y++) level.setBlock(origin.above(y), Blocks.OAK_LOG.defaultBlockState(), 3);
        player.setShiftKeyDown(true);
        player.gameMode.destroyBlock(origin);
        check(level.getBlockState(origin.above()).is(Blocks.OAK_LOG), "sneaking disables Timber for precision building");
    }
}
