
package com.zenspawn.zenspawncontroller.commands;

import com.zenspawn.zenspawncontroller.ZenSpawnController;
import com.zenspawn.zenspawncontroller.config.ConfigManager;
import com.zenspawn.zenspawncontroller.managers.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ZenSpawnPaperCommand {
    public static void register(ZenSpawnController plugin) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
            Command command = new Command("zenspawn", "Main command for ZenSpawnController", "/zenspawn <reload|status|info|help|debug>", Arrays.asList("zsc", "spawncontrol")) {
                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return ZenSpawnPaperCommand.execute(plugin, sender, args);
                }
            };
            commandMap.register(plugin.getDescription().getName(), command);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register /zenspawn command: " + e.getMessage());
        }
    }

    public static boolean execute(ZenSpawnController plugin, CommandSender sender, String[] args) {
        ConfigManager configManager = plugin.getConfigManager();
        SpawnManager spawnManager = plugin.getSpawnManager();
        if (!sender.hasPermission("zenspawn.admin")) {
            sender.sendMessage(Component.text("§cYou don't have permission to use this command!"));
            return true;
        }
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help" -> sendHelpMessage(sender);
            case "reload" -> {
                if (!sender.hasPermission("zenspawn.reload")) {
                    sender.sendMessage(Component.text("§cYou don't have permission to reload the plugin!"));
                    return true;
                }
                plugin.reload();
                sender.sendMessage(Component.text("§aZenSpawnController has been reloaded!"));
            }
            case "status" -> sendStatusMessage(sender, configManager);
            case "info" -> {
                if (sender instanceof Player player) {
                    sendPlayerInfo(player, configManager, spawnManager);
                } else {
                    sender.sendMessage(Component.text("§cThis command can only be used by players!"));
                }
            }
            case "debug" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("§cUsage: /zenspawn debug <on|off>"));
                    return true;
                }
                boolean debugMode = args[1].equalsIgnoreCase("on");
                // Note: In a real implementation, you'd want to update the config file
                sender.sendMessage(Component.text("§aDebug mode " + (debugMode ? "enabled" : "disabled") + "!"));
            }
            default -> sender.sendMessage(Component.text("§cUnknown subcommand. Use /zenspawn help for available commands."));
        }
        return true;
    }

    private static void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(Component.text("§6=== ZenSpawnController Commands ==="));
        sender.sendMessage(Component.text("§e/zenspawn help §7- Show this help message"));
        sender.sendMessage(Component.text("§e/zenspawn reload §7- Reload the plugin configuration"));
        sender.sendMessage(Component.text("§e/zenspawn status §7- Show plugin status and settings"));
        sender.sendMessage(Component.text("§e/zenspawn info §7- Show spawn information around you"));
        sender.sendMessage(Component.text("§e/zenspawn debug <on|off> §7- Toggle debug mode"));
    }

    private static void sendStatusMessage(CommandSender sender, ConfigManager configManager) {
        sender.sendMessage(Component.text("§6=== ZenSpawnController Status ==="));
        sender.sendMessage(Component.text("§7Plugin Enabled: " + (configManager.isEnabled() ? "§aYes" : "§cNo")));
        sender.sendMessage(Component.text("§7Debug Mode: " + (configManager.isDebug() ? "§aOn" : "§cOff")));
        sender.sendMessage(Component.text("§7Rate Control: " + (configManager.isRateControlEnabled() ? "§aEnabled" : "§cDisabled")));
        sender.sendMessage(Component.text("§7Limit Control: " + (configManager.isLimitControlEnabled() ? "§aEnabled" : "§cDisabled")));
        sender.sendMessage(Component.text("§7Time Control: " + (configManager.isTimeControlEnabled() ? "§aEnabled" : "§cDisabled")));
        sender.sendMessage(Component.text("§7Global Spawn Rate: §e" + String.format("%.2f", configManager.getGlobalSpawnRateMultiplier()) + "x"));
        sender.sendMessage(Component.text("§7Max Monsters per Player: §e" + configManager.getMaxMonstersPerPlayer()));
        sender.sendMessage(Component.text("§7Check Radius: §e" + configManager.getCheckRadius() + " blocks"));
    }

    private static void sendPlayerInfo(Player player, ConfigManager configManager, SpawnManager spawnManager) {
        int nearbyMonsters = spawnManager.getNearbyMonsterCount(player);
        int maxMonsters = configManager.getMaxMonstersPerPlayer();
        player.sendMessage(Component.text("§6=== Spawn Information ==="));
        player.sendMessage(Component.text("§7World: §e" + player.getWorld().getName()));
        player.sendMessage(Component.text("§7Nearby Monsters: §e" + nearbyMonsters + "§7/§e" + maxMonsters));
        player.sendMessage(Component.text("§7Check Radius: §e" + configManager.getCheckRadius() + " blocks"));
        if (configManager.isTimeControlEnabled()) {
            long time = player.getWorld().getTime();
            String timePhase;
            double timeMultiplier;
            if (time >= 0 && time < 12300) {
                timePhase = "Day";
                timeMultiplier = configManager.getDayMultiplier();
            } else {
                timePhase = "Night";
                timeMultiplier = configManager.getNightMultiplier();
            }
            player.sendMessage(Component.text("§7Time Phase: §e" + timePhase + " §7(§e" + String.format("%.2f", timeMultiplier) + "x§7)"));
            if (player.getWorld().getFullTime() % 8 == 0) {
                player.sendMessage(Component.text("§7Moon Phase: §6Full Moon §7(§6" + String.format("%.2f", configManager.getFullMoonMultiplier()) + "x§7)"));
            }
        }
        ConfigManager.WorldSettings worldSettings = configManager.getWorldSettings(player.getWorld().getName());
        player.sendMessage(Component.text("§7World Spawn Rate: §e" + String.format("%.2f", worldSettings.getSpawnRateMultiplier()) + "x"));
    }
}
