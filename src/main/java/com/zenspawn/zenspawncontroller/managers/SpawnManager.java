package com.zenspawn.zenspawncontroller.managers;

import com.zenspawn.zenspawncontroller.ZenSpawnController;
import com.zenspawn.zenspawncontroller.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpawnManager {
    
    private final ZenSpawnController plugin;
    private final ConfigManager configManager;
    private BukkitTask spawnTask;
    
    // Track spawn attempts per player
    private final Map<UUID, Long> lastSpawnCheck = new HashMap<>();
    private final Map<UUID, Integer> nearbyMonsterCount = new HashMap<>();
    
    public SpawnManager(ZenSpawnController plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    public void startSpawnTask() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }
        
        spawnTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!configManager.isEnabled()) return;
            
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                updateNearbyMonsterCount(player);
                
                if (configManager.isDebug()) {
                    int count = nearbyMonsterCount.getOrDefault(player.getUniqueId(), 0);
                    if (count > 0) {
                        player.sendMessage("§7[Debug] Nearby monsters: " + count);
                    }
                }
            }
        }, 0L, configManager.getSpawnCheckDelay());
    }
    
    public void stopSpawnTask() {
        if (spawnTask != null) {
            spawnTask.cancel();
            spawnTask = null;
        }
    }
    
    public void reloadSettings() {
        stopSpawnTask();
        startSpawnTask();
    }
    
    private void updateNearbyMonsterCount(Player player) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        int radius = configManager.getCheckRadius();
        
        List<Entity> nearbyEntities = world.getNearbyEntities(playerLoc, radius, radius, radius)
                .stream()
                .filter(entity -> entity instanceof Monster)
                .toList();
        
        nearbyMonsterCount.put(player.getUniqueId(), nearbyEntities.size());
        lastSpawnCheck.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public boolean canSpawn(EntityType entityType, Location location) {
        if (!configManager.isEnabled()) return true;
        if (!configManager.isLimitControlEnabled()) return true;
        
        World world = location.getWorld();
        if (world == null) return true;
        
        // Check world settings
        ConfigManager.WorldSettings worldSettings = configManager.getWorldSettings(world.getName());
        if (!worldSettings.isEnabled()) return true;
        
        // Check monster settings
        ConfigManager.MonsterSettings monsterSettings = configManager.getMonsterSettings(entityType);
        if (!monsterSettings.isEnabled()) return false;
        
        // Find nearest player
        Player nearestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Player player : world.getPlayers()) {
            double distance = player.getLocation().distance(location);
            if (distance < nearestDistance && distance <= configManager.getCheckRadius()) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }
        
        if (nearestPlayer == null) return true;
        
        // Check total monster limit around player
        int totalNearby = nearbyMonsterCount.getOrDefault(nearestPlayer.getUniqueId(), 0);
        int maxTotal = Math.min(worldSettings.getMaxMonstersPerPlayer(), configManager.getMaxMonstersPerPlayer());
        
        if (totalNearby >= maxTotal) {
            if (configManager.isDebug()) {
                nearestPlayer.sendMessage("§7[Debug] Spawn blocked: Total limit reached (" + totalNearby + "/" + maxTotal + ")");
            }
            return false;
        }
        
        // Check specific monster type limit
        int specificCount = countNearbyMonstersOfType(nearestPlayer, entityType);
        int maxSpecific = monsterSettings.getMaxCountPerPlayer();
        
        if (specificCount >= maxSpecific) {
            if (configManager.isDebug()) {
                nearestPlayer.sendMessage("§7[Debug] Spawn blocked: " + entityType.name() + " limit reached (" + specificCount + "/" + maxSpecific + ")");
            }
            return false;
        }
        
        return true;
    }
    
    public double getSpawnRateMultiplier(EntityType entityType, Location location) {
        if (!configManager.isEnabled() || !configManager.isRateControlEnabled()) {
            return 1.0;
        }
        
        World world = location.getWorld();
        if (world == null) return 1.0;
        
        double multiplier = 1.0;
        
        // Apply global multiplier
        multiplier *= configManager.getGlobalSpawnRateMultiplier();
        
        // Apply world-specific multiplier
        ConfigManager.WorldSettings worldSettings = configManager.getWorldSettings(world.getName());
        multiplier *= worldSettings.getSpawnRateMultiplier();
        
        // Apply monster-specific multiplier
        ConfigManager.MonsterSettings monsterSettings = configManager.getMonsterSettings(entityType);
        multiplier *= monsterSettings.getSpawnRateMultiplier();
        
        // Apply time-based multiplier
        if (configManager.isTimeControlEnabled()) {
            long time = world.getTime();
            boolean isDay = time >= 0 && time < 12300;
            boolean isNight = time >= 12300 && time < 24000;
            
            if (isDay) {
                multiplier *= configManager.getDayMultiplier();
            } else if (isNight) {
                multiplier *= configManager.getNightMultiplier();
            }
            
            // Check for full moon
            if (world.getFullTime() % 8 == 0) { // Full moon occurs every 8 minecraft days
                multiplier *= configManager.getFullMoonMultiplier();
            }
        }
        
        return Math.max(0.0, multiplier); // Ensure non-negative
    }
    
    private int countNearbyMonstersOfType(Player player, EntityType entityType) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        int radius = configManager.getCheckRadius();
        
        return (int) world.getNearbyEntities(playerLoc, radius, radius, radius)
                .stream()
                .filter(entity -> entity.getType() == entityType && entity instanceof Monster)
                .count();
    }
    
    public int getNearbyMonsterCount(Player player) {
        return nearbyMonsterCount.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void clearPlayerData(UUID playerId) {
        lastSpawnCheck.remove(playerId);
        nearbyMonsterCount.remove(playerId);
    }
}
