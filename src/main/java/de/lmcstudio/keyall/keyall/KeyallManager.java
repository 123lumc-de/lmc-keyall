package de.lmcstudio.keyall.keyall;

import de.lmcstudio.keyall.LMCKeyall;
import de.lmcstudio.keyall.key.KeyEntry;
import de.lmcstudio.keyall.key.KeyRegistry;
import de.lmcstudio.keyall.roulette.RouletteManager;
import de.lmcstudio.keyall.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KeyallManager {

    private final LMCKeyall plugin;
    private final KeyRegistry registry;
    private final RouletteManager roulette;

    public KeyallManager(LMCKeyall plugin, KeyRegistry registry, RouletteManager roulette) {
        this.plugin = plugin;
        this.registry = registry;
        this.roulette = roulette;
    }

    /**
     * Führt einen Keyall aus.
     * Jeder Online-Spieler würfelt INDIVIDUELL nach den Gewichten in der Config.
     */
    public void executeKeyall() {
        var players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            plugin.getLogger().info("Keyall skipped – no players online.");
            return;
        }

        Component startMsg = TextUtil.parse(
                plugin.getConfig().getString("messages.keyall-start", "<yellow>Ein Keyall wurde gestartet!")
        );
        Bukkit.broadcast(startMsg);

        String rewardTemplate = plugin.getConfig().getString("keyall.reward-command", "say %player%");
        boolean broadcastWinners = plugin.getConfig().getBoolean("keyall.broadcast-winners", false);

        for (Player player : players) {
            KeyEntry result = registry.roll(); // Pro Spieler würfeln

            if (result.giveNothing()) {
                continue; // Spieler hat nichts gewonnen
            }

            // 1. Console-Command ausführen
            String cmd = rewardTemplate
                    .replace("%player%", player.getName())
                    .replace("%key%", result.id());
            Bukkit.getScheduler().runTask(plugin, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
            );

            // 2. Persönliche Chat-Nachricht
            String personalRaw = plugin.getConfig().getString(
                    "messages.winner-personal",
                    "<gold>Du hast <white>%key% <gold>gewonnen!"
            );
            player.sendMessage(TextUtil.parse(personalRaw.replace("%key%", result.display())));

            // 3. Optional: Broadcast
            if (broadcastWinners) {
                String broadcastRaw = plugin.getConfig().getString(
                        "messages.winner-broadcast",
                        "<gold>%player% <yellow>hat <white>%key% <gold>gewonnen!"
                );
                Bukkit.broadcast(TextUtil.parse(
                        broadcastRaw
                                .replace("%player%", player.getName())
                                .replace("%key%", result.display())
                ));
            }

            // 4. Roulette-Title pro Spieler
            roulette.playRoulette(player, registry.realEntries(), result);

            // 5. Datenbank-Log
            plugin.getDatabase().logWin(player.getUniqueId(), player.getName(), result.id());
        }
    }
}