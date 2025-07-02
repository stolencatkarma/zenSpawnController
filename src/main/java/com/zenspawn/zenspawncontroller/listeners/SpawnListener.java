package com.zenspawn.zenspawncontroller.listeners;

import com.zenspawn.zenspawncontroller.ZenSpawnController;
import com.zenspawn.zenspawncontroller.config.ConfigManager;
import com.zenspawn.zenspawncontroller.managers.SpawnManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;

public class SpawnListener implements Listener {
    
    private final ZenSpawnController plugin;
    private final ConfigManager configManager;
    private final SpawnManager spawnManager;
    private final Random random;
    
    public SpawnListener(ZenSpawnController plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.spawnManager = plugin.getSpawnManager();
        this.random = new Random();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Only handle natural spawns and monster spawns
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }
        
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        
        EntityType entityType = event.getEntityType();
        
        // Check if this monster type can spawn at this location
        if (!spawnManager.canSpawn(entityType, event.getLocation())) {
            event.setCancelled(true);
            return;
        }
        
        // Apply spawn rate control
        if (configManager.isRateControlEnabled()) {
            double spawnRateMultiplier = spawnManager.getSpawnRateMultiplier(entityType, event.getLocation());
            
            // Convert multiplier to probability
            // If multiplier is 1.0, always spawn (100% chance)
            // If multiplier is 0.5, spawn 50% of the time
            // If multiplier is 2.0, always spawn (can't exceed 100%)
            double spawnChance = Math.min(1.0, spawnRateMultiplier);
            
            if (random.nextDouble() > spawnChance) {
                event.setCancelled(true);
                
                if (configManager.isDebug()) {
                    plugin.getLogger().info("Spawn cancelled for " + entityType.name() + 
                        " at " + event.getLocation() + " (chance: " + String.format("%.2f", spawnChance * 100) + "%)");
                }
                return;
            }
        }
        
        if (configManager.isDebug()) {
            plugin.getLogger().info("Spawn allowed for " + entityType.name() + 
                " at " + event.getLocation());
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player data when they join
        if (configManager.isDebug()) {
            event.getPlayer().sendMessage("§7[ZenSpawnController] Debug mode is enabled. You will see spawn information.");
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up player data when they leave
        spawnManager.clearPlayerData(event.getPlayer().getUniqueId());
    }
}
