# More Enchanted 🌟

[![Minecraft](https://img.shields.io/badge/Minecraft-26.3%20%22Wilderness%20Bound%22-green.svg)](https://minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.160.7%2B26.3-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**More Enchanted** is a modern, high-performance Fabric mod designed for **Minecraft 26.3 ("Wilderness Bound")**. It expands the game's enchantment mechanics with **12 wilderness-themed enchantments**, dynamic combat effects, parkour maneuvers, foraging enhancements, dedicated creative mode tabs, and full Enchanted Books support!

---

## 🌲 Tailored for Minecraft 26.3 ("Wilderness Bound")

Minecraft 26.3 introduced the **Dappled Forest**, **Shelf Mushrooms**, **Poplar Trees**, **Abandoned Camps**, and **Cushions**. **More Enchanted** seamlessly synergizes with these new features:
* **Canopy Leap** turns treetop canopies and bouncy shelf mushrooms into aerial parkour pathways with zero fall damage.
* **Cushioning** brings elastic trampoline-like mechanics to boots inspired by the 26.3 Cushions.
* **Timber** fells massive autumn poplar trees in a single swing.
* **Campfire Vitality** turns abandoned camps into safe havens of healing and warmth.

---

## ⚡ Enchantment Overview

| Enchantment | Target Item | Levels | Incompatible With | Rarity (Table Chance) | Description |
| :--- | :--- | :---: | :--- | :---: | :--- |
| **🥾 Canopy Leap** | Boots | I - III | `Feather Falling`, `Cushioning` | Rare (~6%) | Grants jump boost on leaves & shelf mushrooms. 100% negates fall damage on foliage; reduces ground fall damage. |
| **🪶 Cushioning** | Boots | I | `Feather Falling`, `Canopy Leap` | Very Rare (~2.5%) | Absorbs 100% of all fall damage, bouncing the player upwards on impact at the expense of boot durability. |
| **🪓 Timber** | Axe | I - III | — | Uncommon (~13%) | Fells up to 16 / 32 / 64 connected logs in a single chop. |
| **🏕️ Campfire Vitality**| Chestplate | I - II | — | Uncommon (~11%) | Grants Regeneration and prevents hunger loss within 8 blocks of a lit campfire or abandoned camp. |
| **🌾 Forager** | Hoe, Axe | I - III | `Silk Touch` | Common (~22%) | Harvesters gain +30% / +60% / +100% extra drops (apples, seeds, sticks) and a rare chance for Golden Apples. |
| **🍂 Autumn Gale** | Sword, Mace | I - III | `Knockback`, `Wind Burst` | Rare (~5%) | Heavy hits unleash wind blasts and foliage spirals, pushing nearby enemies back and inflicting Slowness. |
| **☣️ Spore Burst** | Bow, Crossbow | I - II | `Flame` | Uncommon (~10%) | Projectiles produce lingering toxic spore clouds that poison targets within a 3x3 or 5x5 area. |
| **🩸 Vampiric Strike** | Sword | I - III | `Fire Aspect`, `Smite` | Rare (~4.5%) | Siphons 8% / 15% / 22% of dealt damage to restore the player's health when attacking living mobs. |
| **☀️ Solar Wrath** | Sword, Trident | I - III | `Smite` | Uncommon (~13%) | Deals extra radiant damage and permanently sets undead mobs on fire under open daylight. |
| **🧲 Magnetic Reach** | Helmet | I - II | — | Rare (~5.5%) | Magnetically draws dropped items and experience (XP) orbs within 5.5 to 9.5 blocks towards the player. |
| **🌧️ Curse of Rust** | Any Tool/Armor | I | — | Treasure (0% Table) | Causes items to lose durability twice as fast when immersed in water or rain. |
| **🍄 Curse of Spores**| Armor | I | — | Treasure (0% Table) | Blinds the wearer with dense mushroom spores upon taking combat damage. |

---

## 📚 Dedicated Creative Tab & Enchanted Books

* **Creative Tab:** Adds a dedicated **"More Enchanted"** Creative Inventory tab containing all enchantment levels as standalone **Enchanted Books**.
* **Vanilla Integration:** Fully configured with vanilla enchantment tags (`#minecraft:in_enchanting_table`, `#minecraft:on_random_loot`, `#minecraft:on_traded_equipment`), ensuring seamless appearance in librarian villager trades, dungeon/camp chests, and anvil combination.
* **Strict Exclusivity (`exclusive_set`):** Enforces native incompatibility tags (e.g. `Canopy Leap` vs `Feather Falling`), preventing conflicting enchantments from being merged via anvils or generated on the same item.

---

## 🌐 Localization (Multi-Language)

* 🇺🇸 **English (`en_us.json`)**: Full names, lore tooltips, and creative tab entries.
* 🇺🇿 **Uzbek (`uz_uz.json`)**: Native, natural Uzbek translations for all enchantments and descriptions.

---

## 📦 Installation

1. Install **Minecraft 26.3**.
2. Install **[Fabric Loader](https://fabricmc.net/)** (version `0.19.5` or higher).
3. Download **[Fabric API](https://modrinth.com/mod/fabric-api)** (`0.160.7+26.3` or higher).
4. Download `more_enchanted-1.0.0.jar` from the [Releases](https://github.com/modclaim/more-enchanted/releases) tab.
5. Place both JAR files into your `.minecraft/mods` directory.

---

## 🛠️ Building from Source

Requires **JDK 25** installed.

```bash
git clone https://github.com/modclaim/more-enchanted.git
cd more-enchanted
./gradlew build
```

The compiled mod JAR will be located at `build/libs/more_enchanted-1.0.0.jar`.

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.
