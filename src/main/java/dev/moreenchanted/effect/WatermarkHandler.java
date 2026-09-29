package dev.moreenchanted.effect;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.net.URI;

public class WatermarkHandler {
    private static final URI REPO_URI = URI.create("https://github.com/modclaim/more-enchanted");

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (player == null) return;

            MutableComponent prefix = Component.literal("[More Enchanted] ")
                    .withStyle(style -> style.withColor(0xFFA500).withBold(true));

            MutableComponent version = Component.literal("v1.0.0 ")
                    .withStyle(style -> style.withColor(0xE0E0E0));

            MutableComponent author = Component.literal("by modclaim")
                    .withStyle(style -> style.withColor(0xBA55D3).withUnderlined(true)
                            .withClickEvent(new ClickEvent.OpenUrl(REPO_URI))
                            .withHoverEvent(new HoverEvent.ShowText(Component.literal("§6More Enchanted §7— §fmodclaim\n§eClick to open GitHub!"))));

            MutableComponent suffix = Component.literal(" — 12 Wilderness Enchantments Active")
                    .withStyle(style -> style.withColor(0x808080).withItalic(true));

            player.sendSystemMessage(prefix.append(version).append(author).append(suffix));
        });
    }
}
