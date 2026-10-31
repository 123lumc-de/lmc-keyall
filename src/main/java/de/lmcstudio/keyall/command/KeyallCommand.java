package de.lmcstudio.keyall.command;

import de.lmcstudio.keyall.LMCKeyall;
import de.lmcstudio.keyall.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeyallCommand implements CommandExecutor, TabCompleter {

    private final LMCKeyall plugin;

    public KeyallCommand(LMCKeyall plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("lmc.keyall.admin")) {
            sender.sendMessage(TextUtil.parse(
                    plugin.getConfig().getString("messages.no-permission", "<red>Keine Berechtigung.")
            ));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(TextUtil.parse("<gray>Nutzung: <yellow>/keyall <start|reload>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                plugin.getKeyallManager().executeKeyall();
                sender.sendMessage(TextUtil.parse("<green>Keyall ausgeführt."));
            }
            case "reload" -> {
                plugin.reloadPluginConfig();
                sender.sendMessage(TextUtil.parse(
                        plugin.getConfig().getString("messages.reloaded", "<green>Config neu geladen.")
                ));
            }
            default -> sender.sendMessage(TextUtil.parse("<gray>Nutzung: <yellow>/keyall <start|reload>"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("start", "reload").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}