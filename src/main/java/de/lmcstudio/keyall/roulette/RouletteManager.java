package de.lmcstudio.keyall.roulette;

import de.lmcstudio.keyall.LMCKeyall;
import de.lmcstudio.keyall.key.KeyEntry;
import de.lmcstudio.keyall.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.List;

public class RouletteManager {

    private final LMCKeyall plugin;

    public RouletteManager(LMCKeyall plugin) {
        this.plugin = plugin;
    }

    /**
     * Spielt die Roulette-Animation für einen bestimmten Spieler ab.
     * Die Animation rotiert durch alle echten Keys und endet beim Ergebnis.
     */
    public void playRoulette(Player winner, List<KeyEntry> entries, KeyEntry finalEntry) {
        if (entries.isEmpty()) return;

        int interval = plugin.getConfig().getInt("roulette.interval-ticks", 4);
        int duration = plugin.getConfig().getInt("roulette.duration-ticks", 60);
        int fadeIn = plugin.getConfig().getInt("roulette.title.fade-in", 10);
        int stay = plugin.getConfig().getInt("roulette.title.stay", 70);
        int fadeOut = plugin.getConfig().getInt("roulette.title.fade-out", 20);

        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
        );

        new BukkitRunnable() {
            int ticks = 0;
            int index = 0;

            @Override
            public void run() {
                if (!winner.isOnline()) {
                    cancel();
                    return;
                }
                if (ticks >= duration) {
                    Component finalTitle = TextUtil.parse(finalEntry.display());
                    winner.showTitle(Title.title(finalTitle, Component.empty(), times));
                    cancel();
                    return;
                }
                KeyEntry current = entries.get(index % entries.size());
                Component title = TextUtil.parse(current.display());
                winner.showTitle(Title.title(title, Component.empty(), times));
                index++;
                ticks += interval;
            }
        }.runTaskTimer(plugin, 0L, interval);
    }
}