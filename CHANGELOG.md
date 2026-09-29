# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-09-29

### Added
- **Initial Release** targeting Minecraft 26.3 ("Wilderness Bound") on the Fabric mod loader.
- **12 Custom Enchantments:**
  - `Canopy Leap` (Boots, I-III): Jump boost on leaves and shelf mushrooms, full fall immunity on foliage.
  - `Cushioning` (Boots, I): Trampoline-like bounce on fall impact with 100% fall damage negation.
  - `Timber` (Axe, I-III): Fells connected tree trunks in a single chop (up to 64 logs).
  - `Campfire Vitality` (Chestplate, I-II): Regeneration and saturation preservation near lit campfires or abandoned camps.
  - `Forager` (Hoe/Axe, I-III): Extra apples, seeds, sticks, and rare Golden Apple harvests from wilderness foliage.
  - `Autumn Gale` (Sword/Mace, I-III): Wind gusts and leaf swirls pushing enemies back with Slowness.
  - `Spore Burst` (Bow/Crossbow, I-II): Lingering area poison spore clouds on impact.
  - `Vampiric Strike` (Sword, I-III): Life steal against living entities.
  - `Solar Wrath` (Sword/Trident, I-III): Daytime radiant bonus damage and ignition against undead mobs.
  - `Magnetic Reach` (Helmet, I-II): Automatically attracts dropped items and XP orbs.
  - `Curse of Rust` (Tools/Armor, I): Accelerated durability loss in water or rain (Treasure only).
  - `Curse of Spores` (Armor, I): Spore-induced blindness upon taking combat damage (Treasure only).
- **Dedicated Creative Mode Tab:**
  - Automatically lists all enchantment tiers as Enchanted Books in a custom "More Enchanted" tab.
- **Exclusivity Tags (`exclusive_set`):**
  - Mutually exclusive pairings properly defined (e.g. `Canopy Leap` vs `Feather Falling`, `Forager` vs `Silk Touch`, `Autumn Gale` vs `Knockback`, `Solar Wrath` vs `Smite`).
- **Full Localization:**
  - English (`en_us.json`)
  - Uzbek (`uz_uz.json`)
