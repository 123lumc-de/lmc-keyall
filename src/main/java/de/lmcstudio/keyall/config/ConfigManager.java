package de.lmcstudio.keyall.config;

import de.lmcstudio.keyall.LMCKeyall;
import de.lmcstudio.keyall.key.KeyEntry;
import de.lmcstudio.keyall.key.KeyRegistry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final LMCKeyall plugin;
    private KeyRegistry registry;

    public ConfigManager(LMCKeyall plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        List<KeyEntry> entries = new ArrayList<>();
        List<?> rawEntries = plugin.getConfig().getList("keyall.entries");
        if (rawEntries != null) {
            for (Object raw : rawEntries) {
                if (!(raw instanceof ConfigurationSection sec)) continue;
                String id = sec.getString("id", "unknown");
                String display = sec.getString("display", id);
                String color = sec.getString("color", "WHITE");
                double chance = sec.getDouble("chance", 1);
                boolean giveNothing = sec.getBoolean("give-nothing", false);
                entries.add(new KeyEntry(id, display, color, chance, giveNothing));
            }
        }
        this.registry = new KeyRegistry(entries);
    }

    public KeyRegistry registry() {
        return registry;
    }
}