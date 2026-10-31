package de.lmcstudio.keyall;

import de.lmcstudio.keyall.command.KeyallCommand;
import de.lmcstudio.keyall.config.ConfigManager;
import de.lmcstudio.keyall.database.DatabaseManager;
import de.lmcstudio.keyall.keyall.KeyallManager;
import de.lmcstudio.keyall.placeholder.KeyallExpansion;
import de.lmcstudio.keyall.roulette.RouletteManager;
import de.lmcstudio.keyall.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class LMCKeyall extends JavaPlugin {

    private ConfigManager configManager;
    private DatabaseManager database;
    private RouletteManager roulette;
    private KeyallManager keyallManager;

    private BukkitTask keyallTask;
    private long nextKeyallMillis = 0L;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.database = new DatabaseManager(this);
        this.database.init();

        this.roulette = new RouletteManager(this);
        this.keyallManager = new KeyallManager(this, configManager.registry(), roulette);

        var cmd = getCommand("keyall");
        if (cmd != null) {
            KeyallCommand executor = new KeyallCommand(this);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KeyallExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }

        scheduleKeyall();

        getLogger().info("LMC Keyall enabled. Keys loaded: " + configManager.registry().entries().size());
    }

    @Override
    public void onDisable() {
        if (keyallTask != null) keyallTask.cancel();
        if (database != null) database.close();
        getLogger().info("LMC Keyall disabled.");
    }

    private void scheduleKeyall() {
        if (keyallTask != null) {
            keyallTask.cancel();
            keyallTask = null;
        }
        long seconds = getConfig().getLong("keyall.interval-seconds", 3600);
        if (seconds <= 0) {
            nextKeyallMillis = 0L;
            return;
        }
        nextKeyallMillis = System.currentTimeMillis() + seconds * 1000L;
        keyallTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            keyallManager.executeKeyall();
            nextKeyallMillis = System.currentTimeMillis() + seconds * 1000L;
        }, seconds * 20L, seconds * 20L);
    }

    public void reloadPluginConfig() {
        configManager.load();
        scheduleKeyall();
        // KeyallManager mit neuer Registry neu bauen
        this.keyallManager = new KeyallManager(this, configManager.registry(), roulette);
    }

    public String getNextKeyallFormatted() {
        if (nextKeyallMillis <= 0) return "-";
        long remaining = Math.max(0, nextKeyallMillis - System.currentTimeMillis());
        long hours = remaining / 3_600_000;
        long minutes = (remaining % 3_600_000) / 60_000;
        long seconds = (remaining % 60_000) / 1_000;
        return String.format("%dH, %dMin, %dSek", hours, minutes, seconds);
    }

    public long getNextKeyallSeconds() {
        return nextKeyallMillis <= 0 ? 0 : Math.max(0, (nextKeyallMillis - System.currentTimeMillis()) / 1000);
    }

    public KeyallManager getKeyallManager() {
        return keyallManager;
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}