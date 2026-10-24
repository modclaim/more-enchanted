package dev.moreenchanted;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.entity.EquipmentSlotGroup;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class EnchantmentDataTest {
    @Test void everyEnchantmentSlotParsesWithTheActualMinecraftCodec() throws Exception {
        int count = 0;
        try (var files = Files.list(Path.of("src/main/resources/data/more_enchanted/enchantment"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".json")).toList()) {
                var json = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                for (var slot : json.getAsJsonArray("slots")) {
                    assertTrue(EquipmentSlotGroup.CODEC.parse(JsonOps.INSTANCE, slot).result().isPresent(),
                            file.getFileName() + ": invalid Minecraft slot " + slot);
                }
                count++;
            }
        }
        assertEquals(12, count);
    }
}
