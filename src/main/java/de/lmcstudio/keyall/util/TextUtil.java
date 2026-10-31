package de.lmcstudio.keyall.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class TextUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMP = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

    private TextUtil() {}

    /**
     * Parst einen String primär als MiniMessage.
     * Schlägt das fehl, wird er als Legacy (&) geparst.
     * § -Codes werden vorher zu & normalisiert.
     */
    public static Component parse(String input) {
        if (input == null || input.isEmpty()) return Component.empty();
        try {
            return MM.deserialize(input);
        } catch (Exception ignored) {
            try {
                return LEGACY_AMP.deserialize(input.replace('§', '&'));
            } catch (Exception e) {
                return Component.text(input);
            }
        }
    }

    /** Entfernt alle Farben und gibt Plain-Text zurück. */
    public static String strip(Component component) {
        return LEGACY_SECTION.serialize(component);
    }
}