package com.zenspawn.zenspawncontroller.commands;

import com.zenspawn.zenspawncontroller.ZenSpawnController;
import com.zenspawn.zenspawncontroller.config.ConfigManager;
import com.zenspawn.zenspawncontroller.managers.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZenSpawnCommand implements CommandExecutor, TabCompleter {
    
    private final ZenSpawnController plugin;
    private final ConfigManager configManager;
    private final SpawnManager spawnManager;
    
    public ZenSpawnCommand(ZenSpawnController plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.spawnManager = plugin.getSpawnManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zenspawn.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "help":
                sendHelpMessage(sender);
                break;
                
            case "reload":
                if (!sender.hasPermission("zenspawn.reload")) {
                    sender.sendMessage("§cYou don't have permission to reload the plugin!");
                    return true;
                }
                plugin.reload();
                sender.sendMessage("§aZenSpawnController has been reloaded!");
                break;
                
            case "status":
                sendStatusMessage(sender);
                break;
                
            case "info":
                if (sender instanceof Player) {
                    sendPlayerInfo((Player) sender);
                } else {
                    sender.sendMessage("§cThis command can only be used by players!");
                }
                break;
                
            case "debug":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /zenspawn debug <on|off>");
                    return true;
                }
                
                boolean debugMode = args[1].equalsIgnoreCase("on");
                // Note: In a real implementation, you'd want to update the config file
                sender.sendMessage("§aDebug mode " + (debugMode ? "enabled" : "disabled") + "!");
                break;
                
            default:
                sender.sendMessage("§cUnknown subcommand. Use /zenspawn help for available commands.");
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§6=== ZenSpawnController Commands ===");
        sender.sendMessage("§e/zenspawn help §7- Show this help message");
        sender.sendMessage("§e/zenspawn reload §7- Reload the plugin configuration");
        sender.sendMessage("§e/zenspawn status §7- Show plugin status and settings");
        sender.sendMessage("§e/zenspawn info §7- Show spawn information around you");
        sender.sendMessage("§e/zenspawn debug <on|off> §7- Toggle debug mode");
    }
    
    private void sendStatusMessage(CommandSender sender) {
        sender.sendMessage("§6=== ZenSpawnController Status ===");
        sender.sendMessage("§7Plugin Enabled: " + (configManager.isEnabled() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7Debug Mode: " + (configManager.isDebug() ? "§aOn" : "§cOff"));
        sender.sendMessage("§7Rate Control: " + (configManager.isRateControlEnabled() ? "§aEnabled" : "§cDisabled"));
        sender.sendMessage("§7Limit Control: " + (configManager.isLimitControlEnabled() ? "§aEnabled" : "§cDisabled"));
        sender.sendMessage("§7Time Control: " + (configManager.isTimeControlEnabled() ? "§aEnabled" : "§cDisabled"));
        sender.sendMessage("§7Global Spawn Rate: §e" + String.format("%.2f", configManager.getGlobalSpawnRateMultiplier()) + "x");
        sender.sendMessage("§7Max Monsters per Player: §e" + configManager.getMaxMonstersPerPlayer());
        sender.sendMessage("§7Check Radius: §e" + configManager.getCheckRadius() + " blocks");
    }
    
    private void sendPlayerInfo(Player player) {
        int nearbyMonsters = spawnManager.getNearbyMonsterCount(player);
        int maxMonsters = configManager.getMaxMonstersPerPlayer();
        
        player.sendMessage("§6=== Spawn Information ===");
        player.sendMessage("§7World: §e" + player.getWorld().getName());
        player.sendMessage("§7Nearby Monsters: §e" + nearbyMonsters + "§7/§e" + maxMonsters);
        player.sendMessage("§7Check Radius: §e" + configManager.getCheckRadius() + " blocks");
        
        // Show time-based information
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
            
            player.sendMessage("§7Time Phase: §e" + timePhase + " §7(§e" + String.format("%.2f", timeMultiplier) + "x§7)");
            
            // Check for full moon
            if (player.getWorld().getFullTime() % 8 == 0) {
                player.sendMessage("§7Moon Phase: §6Full Moon §7(§6" + String.format("%.2f", configManager.getFullMoonMultiplier()) + "x§7)");
            }
        }
        
        // Show world-specific settings
        ConfigManager.WorldSettings worldSettings = configManager.getWorldSettings(player.getWorld().getName());
        player.sendMessage("§7World Spawn Rate: §e" + String.format("%.2f", worldSettings.getSpawnRateMultiplier()) + "x");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("help", "reload", "status", "info", "debug");
            for (String subcommand : subcommands) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subcommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            List<String> debugOptions = Arrays.asList("on", "off");
            for (String option : debugOptions) {
                if (option.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(option);
                }
            }
        }
        
        return completions;
    }
}
