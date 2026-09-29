package dev.moreenchanted;

import dev.moreenchanted.effect.ForagerHandler;
import dev.moreenchanted.effect.MovementAndCombatHandlers;
import dev.moreenchanted.effect.TimberHandler;
import dev.moreenchanted.registry.ModItemGroups;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreEnchanted implements ModInitializer {
    public static final String MOD_ID = "more_enchanted";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("More Enchanted initializing for Minecraft 26.3...");

        // Register Creative Tab and Books
        ModItemGroups.register();

        // Register Game Event Handlers
        TimberHandler.register();
        ForagerHandler.register();
        MovementAndCombatHandlers.register();

        LOGGER.info("More Enchanted successfully initialized with 12 custom enchantments!");
    }
}
