package de.lmcstudio.keyall.placeholder;

import de.lmcstudio.keyall.LMCKeyall;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class KeyallExpansion extends PlaceholderExpansion {

    private final LMCKeyall plugin;

    public KeyallExpansion(LMCKeyall plugin) {
        this.plugin = plugin;
    }

    @Override public @NotNull String getIdentifier() { return "lmckeyall"; }
    @Override public @NotNull String getAuthor()   { return "LMC Studio"; }
    @Override public @NotNull String getVersion()  { return plugin.getPluginMeta().getVersion(); }
    @Override public boolean persist()             { return true; }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("next")) {
            return plugin.getNextKeyallFormatted();
        }
        if (params.equalsIgnoreCase("next_seconds")) {
            return String.valueOf(plugin.getNextKeyallSeconds());
        }
        return null;
    }
}