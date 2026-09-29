package dev.moreenchanted.effect;

import dev.moreenchanted.registry.ModEnchantments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MovementAndCombatHandlers {

    public static void register() {
        registerFallDamageHandlers();
        registerCombatHandlers();
        registerTickHandlers();
    }

    private static void registerFallDamageHandlers() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, damageSource, amount) -> {
            if (damageSource.is(DamageTypeTags.IS_FALL)) {
                // 1. Cushioning Check
                int cushioning = ModEnchantments.getEquipmentLevel(entity, EquipmentSlot.FEET, ModEnchantments.CUSHIONING);
                if (cushioning > 0) {
                    Vec3 vel = entity.getDeltaMovement();
                    entity.setDeltaMovement(vel.x, 0.75, vel.z);

                    if (entity.level() instanceof ServerLevel sLevel) {
                        sLevel.sendParticles(ParticleTypes.SMALL_GUST, entity.getX(), entity.getY(), entity.getZ(), 10, 0.3, 0.1, 0.3, 0.05);
                        sLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SLIME_BLOCK_FALL, SoundSource.PLAYERS, 1.0f, 1.2f);
                    }

                    ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
                    boots.hurtAndBreak(12, entity, EquipmentSlot.FEET);
                    return false; // Complete immunity to fall damage!
                }

                // 2. Canopy Leap Check
                int canopyLevel = ModEnchantments.getEquipmentLevel(entity, EquipmentSlot.FEET, ModEnchantments.CANOPY_LEAP);
                if (canopyLevel > 0) {
                    BlockPos pos = entity.blockPosition();
                    BlockState at = entity.level().getBlockState(pos);
                    BlockState below = entity.level().getBlockState(pos.below());

                    boolean onWildernessSurface = at.is(BlockTags.LEAVES) || below.is(BlockTags.LEAVES)
                            || at.getBlock().getDescriptionId().contains("mushroom")
                            || below.getBlock().getDescriptionId().contains("mushroom")
                            || at.getBlock().getDescriptionId().contains("shelf")
                            || below.getBlock().getDescriptionId().contains("shelf");

                    if (onWildernessSurface) {
                        if (entity.level() instanceof ServerLevel sLevel) {
                            sLevel.sendParticles(ParticleTypes.FALLING_SPORE_BLOSSOM, entity.getX(), entity.getY(), entity.getZ(), 8, 0.2, 0.1, 0.2, 0.05);
                        }
                        return false; // 100% immunity when landing on leaves or mushrooms
                    } else if (canopyLevel >= 3 && amount <= 6.0f) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    private static void registerCombatHandlers() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((target, damageSource, baseDamageDealt, damageTaken, blocked) -> {
            if (target.level().isClientSide()) return;
            ServerLevel sLevel = (ServerLevel) target.level();

            // Curse of Spores (When bearer takes damage)
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.isArmor() && ModEnchantments.getEquipmentLevel(target, slot, ModEnchantments.CURSE_OF_SPORES) > 0) {
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                    sLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, target.getX(), target.getY() + 1.0, target.getZ(), 15, 0.3, 0.5, 0.3, 0.05);
                    break;
                }
            }

            // Curse of Rust (Double durability loss in water or rain)
            if (target.isInWaterOrRain()) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.isArmor() && ModEnchantments.getEquipmentLevel(target, slot, ModEnchantments.CURSE_OF_RUST) > 0) {
                        target.getItemBySlot(slot).hurtAndBreak(2, target, slot);
                    }
                }
            }

            if (!(damageSource.getEntity() instanceof LivingEntity attacker)) return;
            ItemStack weapon = attacker.getMainHandItem();

            // Autumn Gale (Sweep/Smash wind storm + knockback + slowness)
            int galeLevel = ModEnchantments.getLevel(weapon, ModEnchantments.AUTUMN_GALE);
            if (galeLevel > 0) {
                Vec3 center = target.position();
                double radius = 3.0 + galeLevel * 1.5;
                double pushForce = 0.5 + galeLevel * 0.35;
                AABB box = new AABB(center.x - radius, center.y - 2, center.z - radius, center.x + radius, center.y + 2, center.z + radius);

                for (LivingEntity nearby : sLevel.getEntitiesOfClass(LivingEntity.class, box)) {
                    if (nearby != attacker) {
                        Vec3 diff = nearby.position().subtract(center).normalize();
                        if (diff.lengthSqr() < 0.001) diff = new Vec3(0, 1, 0);
                        nearby.push(diff.x * pushForce, 0.35, diff.z * pushForce);
                        nearby.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, galeLevel > 2 ? 1 : 0));
                    }
                }
                sLevel.sendParticles(ParticleTypes.GUST, center.x, center.y + 0.5, center.z, 6 + galeLevel * 3, 0.5, 0.5, 0.5, 0.1);
                sLevel.playSound(null, center.x, center.y, center.z, SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 1.0f, 1.1f);
            }

            // Vampiric Strike (Lifesteal on living mobs)
            int vampLevel = ModEnchantments.getLevel(weapon, ModEnchantments.VAMPIRIC_STRIKE);
            if (vampLevel > 0 && !target.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)) {
                float percent = vampLevel == 1 ? 0.08f : (vampLevel == 2 ? 0.15f : 0.22f);
                float heal = Math.max(1.0f, damageTaken * percent);
                attacker.heal(heal);
                sLevel.sendParticles(ParticleTypes.HEART, attacker.getX(), attacker.getY() + 1.2, attacker.getZ(), 3, 0.3, 0.3, 0.3, 0.02);
                sLevel.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.GENERIC_DRINK.value(), SoundSource.PLAYERS, 0.7f, 1.6f);
            }

            // Solar Wrath (Daytime radiant burn to undead mobs)
            int solarLevel = ModEnchantments.getLevel(weapon, ModEnchantments.SOLAR_WRATH);
            if (solarLevel > 0 && sLevel.isBrightOutside() && sLevel.canSeeSky(target.blockPosition()) && target.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)) {
                float extraDamage = solarLevel * 2.5f;
                target.hurtServer(sLevel, sLevel.damageSources().onFire(), extraDamage);
                target.igniteForSeconds(4 * solarLevel);
                sLevel.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY() + 0.8, target.getZ(), 8 * solarLevel, 0.3, 0.5, 0.3, 0.05);
            }

            // Spore Burst (Area poison cloud from projectile or weapon)
            int sporeLevel = ModEnchantments.getLevel(weapon, ModEnchantments.SPORE_BURST);
            if (sporeLevel > 0) {
                Vec3 center = target.position();
                double radius = sporeLevel == 1 ? 2.5 : 4.0;
                AABB sporeBox = new AABB(center.x - radius, center.y - 1, center.z - radius, center.x + radius, center.y + 2, center.z + radius);
                for (LivingEntity nearby : sLevel.getEntitiesOfClass(LivingEntity.class, sporeBox)) {
                    if (nearby != attacker) {
                        nearby.addEffect(new MobEffectInstance(MobEffects.POISON, 40 * sporeLevel + 40, sporeLevel > 1 ? 1 : 0));
                    }
                }
                sLevel.sendParticles(ParticleTypes.FALLING_SPORE_BLOSSOM, center.x, center.y + 0.8, center.z, 20 * sporeLevel, 0.8, 0.5, 0.8, 0.03);
            }
        });
    }

    private static void registerTickHandlers() {
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            long time = level.getGameTime();

            // Run Campfire Vitality & Canopy Leap jump boost every 20 ticks (1 sec)
            if (time % 20 == 0) {
                for (ServerPlayer player : level.players()) {
                    // Canopy Leap: Jump boost on leaves/mushrooms
                    int canopy = ModEnchantments.getEquipmentLevel(player, EquipmentSlot.FEET, ModEnchantments.CANOPY_LEAP);
                    if (canopy > 0) {
                        BlockPos pos = player.blockPosition();
                        BlockState below = level.getBlockState(pos.below());
                        BlockState at = level.getBlockState(pos);
                        if (below.is(BlockTags.LEAVES) || at.is(BlockTags.LEAVES)
                                || below.getBlock().getDescriptionId().contains("mushroom")
                                || at.getBlock().getDescriptionId().contains("mushroom")
                                || below.getBlock().getDescriptionId().contains("shelf")
                                || at.getBlock().getDescriptionId().contains("shelf")) {
                            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 30, canopy - 1, true, false, true));
                        }
                    }

                    // Campfire Vitality
                    int vitality = ModEnchantments.getEquipmentLevel(player, EquipmentSlot.CHEST, ModEnchantments.CAMPFIRE_VITALITY);
                    if (vitality > 0) {
                        BlockPos pPos = player.blockPosition();
                        boolean nearFire = false;
                        for (BlockPos check : BlockPos.betweenClosed(pPos.offset(-8, -3, -8), pPos.offset(8, 3, 8))) {
                            BlockState st = level.getBlockState(check);
                            if (st.is(BlockTags.CAMPFIRES) && st.hasProperty(CampfireBlock.LIT) && st.getValue(CampfireBlock.LIT)) {
                                nearFire = true;
                                break;
                            }
                        }
                        if (nearFire) {
                            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 45, vitality - 1, true, false, true));
                            player.getFoodData().setSaturation(Math.max(player.getFoodData().getSaturationLevel(), 4.0f));
                        }
                    }
                }
            }

            // Run Magnetic Reach every 5 ticks (0.25 sec)
            if (time % 5 == 0) {
                for (ServerPlayer player : level.players()) {
                    int magnet = ModEnchantments.getEquipmentLevel(player, EquipmentSlot.HEAD, ModEnchantments.MAGNETIC_REACH);
                    if (magnet > 0 && player.isAlive() && !player.isSpectator()) {
                        double radius = magnet == 1 ? 5.5 : 9.5;
                        AABB box = player.getBoundingBox().inflate(radius);

                        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && !e.hasPickUpDelay())) {
                            Vec3 diff = player.position().add(0, 0.4, 0).subtract(item.position());
                            double dist = diff.length();
                            if (dist > 0.4) {
                                Vec3 pull = diff.normalize().scale(Math.min(0.35, 0.7 / (dist + 0.1)));
                                item.setDeltaMovement(item.getDeltaMovement().add(pull));
                            }
                        }

                        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, box, Entity::isAlive)) {
                            Vec3 diff = player.position().add(0, 0.4, 0).subtract(orb.position());
                            double dist = diff.length();
                            if (dist > 0.4) {
                                Vec3 pull = diff.normalize().scale(Math.min(0.35, 0.7 / (dist + 0.1)));
                                orb.setDeltaMovement(orb.getDeltaMovement().add(pull));
                            }
                        }
                    }
                }
            }
        });
    }
}
