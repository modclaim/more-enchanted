package dev.moreenchanted.registry;

import dev.moreenchanted.MoreEnchanted;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> CANOPY_LEAP = of("canopy_leap");
    public static final ResourceKey<Enchantment> CUSHIONING = of("cushioning");
    public static final ResourceKey<Enchantment> TIMBER = of("timber");
    public static final ResourceKey<Enchantment> CAMPFIRE_VITALITY = of("campfire_vitality");
    public static final ResourceKey<Enchantment> FORAGER = of("forager");
    public static final ResourceKey<Enchantment> AUTUMN_GALE = of("autumn_gale");
    public static final ResourceKey<Enchantment> SPORE_BURST = of("spore_burst");
    public static final ResourceKey<Enchantment> VAMPIRIC_STRIKE = of("vampiric_strike");
    public static final ResourceKey<Enchantment> SOLAR_WRATH = of("solar_wrath");
    public static final ResourceKey<Enchantment> MAGNETIC_REACH = of("magnetic_reach");
    public static final ResourceKey<Enchantment> CURSE_OF_RUST = of("curse_of_rust");
    public static final ResourceKey<Enchantment> CURSE_OF_SPORES = of("curse_of_spores");

    public static ResourceKey<Enchantment> of(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MoreEnchanted.MOD_ID, name));
    }

    public static int getLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack == null || stack.isEmpty()) return 0;
        ItemEnchantments enchants = stack.getEnchantments();
        if (enchants.isEmpty()) return 0;
        for (var entry : enchants.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (holder.is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getEquipmentLevel(LivingEntity entity, EquipmentSlot slot, ResourceKey<Enchantment> key) {
        if (entity == null) return 0;
        return getLevel(entity.getItemBySlot(slot), key);
    }
}
