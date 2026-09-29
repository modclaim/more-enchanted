package dev.moreenchanted.registry;

import dev.moreenchanted.MoreEnchanted;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class ModItemGroups {
    public static final ResourceKey<CreativeModeTab> MORE_ENCHANTED_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(MoreEnchanted.MOD_ID, "more_enchanted_tab")
    );

    public static final CreativeModeTab MORE_ENCHANTED_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
            .title(Component.translatable("itemGroup.more_enchanted.tab"))
            .displayItems((params, output) -> {
                params.holders().lookup(Registries.ENCHANTMENT).ifPresent(lookup -> {
                    addBooksFor(lookup, output, ModEnchantments.CANOPY_LEAP, 3);
                    addBooksFor(lookup, output, ModEnchantments.CUSHIONING, 1);
                    addBooksFor(lookup, output, ModEnchantments.TIMBER, 3);
                    addBooksFor(lookup, output, ModEnchantments.CAMPFIRE_VITALITY, 2);
                    addBooksFor(lookup, output, ModEnchantments.FORAGER, 3);
                    addBooksFor(lookup, output, ModEnchantments.AUTUMN_GALE, 3);
                    addBooksFor(lookup, output, ModEnchantments.SPORE_BURST, 2);
                    addBooksFor(lookup, output, ModEnchantments.VAMPIRIC_STRIKE, 3);
                    addBooksFor(lookup, output, ModEnchantments.SOLAR_WRATH, 3);
                    addBooksFor(lookup, output, ModEnchantments.MAGNETIC_REACH, 2);
                    addBooksFor(lookup, output, ModEnchantments.CURSE_OF_RUST, 1);
                    addBooksFor(lookup, output, ModEnchantments.CURSE_OF_SPORES, 1);
                });
            })
            .build();

    private static void addBooksFor(HolderLookup.RegistryLookup<Enchantment> lookup,
                                    CreativeModeTab.Output output,
                                    ResourceKey<Enchantment> key,
                                    int maxLevel) {
        lookup.get(key).ifPresent(holder -> {
            for (int lvl = 1; lvl <= maxLevel; lvl++) {
                output.accept(EnchantmentHelper.createBook(new EnchantmentInstance(holder, lvl)));
            }
        });
    }

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MORE_ENCHANTED_TAB_KEY, MORE_ENCHANTED_TAB);
        MoreEnchanted.LOGGER.info("Registered More Enchanted Creative Tab and Book entries");
    }
}
