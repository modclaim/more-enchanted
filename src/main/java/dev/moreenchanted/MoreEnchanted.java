package dev.moreenchanted;

import dev.moreenchanted.effect.ForagerHandler;
import dev.moreenchanted.effect.MovementAndCombatHandlers;
import dev.moreenchanted.effect.TimberHandler;
import dev.moreenchanted.effect.WatermarkHandler;
import dev.moreenchanted.registry.ModItemGroups;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreEnchanted implements ModInitializer {
    public static final String MOD_ID = "more_enchanted";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("More Enchanted v1.0.0 by modclaim initializing for Minecraft 26.3...");

        // Register Creative Tab and Books
        ModItemGroups.register();

        // Register Game Event Handlers
        TimberHandler.register();
        ForagerHandler.register();
        MovementAndCombatHandlers.register();
        WatermarkHandler.register();

        LOGGER.info("More Enchanted successfully initialized with 12 custom enchantments!");
    }
}
