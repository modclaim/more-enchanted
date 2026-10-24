# More Enchanted

Twelve enchantments for Minecraft **26.3**, Fabric Loader **0.19.5+**, Fabric API **0.161.0+26.3** and Java **25**. Install the mod and Fabric API on the server and clients. English and Uzbek enchantment names are included.

## Enchantments

| Enchantment | Levels | Effect |
| --- | --- | --- |
| Canopy Leap | I–III | Jump boost on leaves/mushrooms; protects foliage landings. Level III also blocks ground fall hits up to 6 damage. |
| Cushioning | I | Cancels fall damage and bounces the wearer, consuming 12 boot durability. |
| Timber | I–III | Breaks up to 16/32/64 connected logs of the same type, counting the original log. Sneak to disable. Uses vanilla player block breaking, normal loot, durability and Fabric protection callbacks. |
| Campfire Vitality | I–II | Regeneration and a saturation floor near a loaded, lit campfire within 8 blocks. |
| Forager | I–III | 30/60/100 percent bonus-drop chance on foliage, with a rare golden apple chance at III. Creative breaking gives no bonus drops. |
| Autumn Gale | I–III | Melee hits push and slow nearby hostile targets; allies and spectators are excluded. |
| Spore Burst | I–II | Arrows from enchanted bows or crossbows apply an immediate area poison burst, using the weapon stored on the projectile. |
| Vampiric Strike | I–III | Melee hits on non-undead targets heal 8/15/22 percent of actual damage. Blocked/zero-damage hits give no healing. |
| Solar Wrath | I–III | Burns undead targets under open daylight. |
| Magnetic Reach | I–II | Pulls nearby dropped items and XP orbs toward the wearer. |
| Curse of Rust | I | Adds armor durability loss when its wearer takes damage while wet. |
| Curse of Spores | I | Blinds its wearer after taking damage. |

Normal enchantments are included in vanilla acquisition tags and the creative book tab. Curses use treasure/curse tags. Data packs can adjust definitions and acquisition. Table odds depend on the item, level and other available enchantments; fixed percentage claims are not provided.

Timber is an explicit area-breaking ability: avoid using it on connected wooden builds. Sneaking disables the extra breaks. Protection depends on protection mods using Fabric's block-break callbacks; compatibility with every third-party protection system is not established.

## Install

Put `more_enchanted-1.0.1.jar` and Fabric API in the Minecraft 26.3 `mods` folder on clients and the server.

## Build and runtime regression checks

Use JDK 25:

```bash
./gradlew clean build
./gradlew runSmokeServer
```

The separate test mod starts a local Fabric server, checks the actual enchantment registry, combat callbacks, projectile weapon retention, Timber protection, durability, limits and sneaking, then stops the server. It binds to localhost on an ephemeral port. Its source is under `src/testmod` and is excluded from release JARs.

## Links and license

- [Modrinth](https://modrinth.com/mod/moreenchanted)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/more-enchanted)
- [Source and issues](https://github.com/modclaim/more-enchanted)
- MIT; see [LICENSE](LICENSE).

AI assisted code maintenance and this documentation. The repository contains AI-generated artwork; it is not embedded in this project description. The mod does not use an AI service at runtime.
