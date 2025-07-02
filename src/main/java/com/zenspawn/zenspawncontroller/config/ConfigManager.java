package com.zenspawn.zenspawncontroller.config;

import com.zenspawn.zenspawncontroller.ZenSpawnController;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    
    private final ZenSpawnController plugin;
    private FileConfiguration config;
    
    // Configuration values
    private boolean enabled;
    private boolean debug;
    private boolean rateControlEnabled;
    private boolean limitControlEnabled;
    private double globalSpawnRateMultiplier;
    private int maxMonstersPerPlayer;
    private int checkRadius;
    private int spawnCheckDelay;
    private boolean timeControlEnabled;
    private double nightMultiplier;
    private double dayMultiplier;
    private double fullMoonMultiplier;
    
    // Per-monster settings
    private Map<EntityType, MonsterSettings> monsterSettings;
    
    // Per-world settings
    private Map<String, WorldSettings> worldSettings;
    
    public ConfigManager(ZenSpawnController plugin) {
        this.plugin = plugin;
        this.monsterSettings = new HashMap<>();
        this.worldSettings = new HashMap<>();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        reloadConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // Load global settings
        enabled = config.getBoolean("global.enabled", true);
        debug = config.getBoolean("global.debug", false);
        
        // Load spawn control settings
        rateControlEnabled = config.getBoolean("spawn_control.rate_control_enabled", true);
        limitControlEnabled = config.getBoolean("spawn_control.limit_control_enabled", true);
        globalSpawnRateMultiplier = config.getDouble("spawn_control.spawn_rate_multiplier", 1.0);
        maxMonstersPerPlayer = config.getInt("spawn_control.max_monsters_per_player", 8);
        checkRadius = config.getInt("spawn_control.check_radius", 32);
        spawnCheckDelay = config.getInt("spawn_control.spawn_check_delay", 20);
        
        // Load time control settings
        timeControlEnabled = config.getBoolean("time_control.enabled", true);
        nightMultiplier = config.getDouble("time_control.night_multiplier", 1.5);
        dayMultiplier = config.getDouble("time_control.day_multiplier", 0.3);
        fullMoonMultiplier = config.getDouble("time_control.full_moon_multiplier", 2.0);
        
        // Load monster-specific settings
        loadMonsterSettings();
        
        // Load world-specific settings
        loadWorldSettings();
    }
    
    private void loadMonsterSettings() {
        monsterSettings.clear();
        ConfigurationSection monsterSection = config.getConfigurationSection("monster_settings");
        
        if (monsterSection != null) {
            for (String key : monsterSection.getKeys(false)) {
                try {
                    EntityType entityType = EntityType.valueOf(key.toUpperCase());
                    ConfigurationSection settings = monsterSection.getConfigurationSection(key);
                    
                    if (settings != null) {
                        MonsterSettings monsterSetting = new MonsterSettings(
                            settings.getBoolean("enabled", true),
                            settings.getDouble("spawn_rate_multiplier", 1.0),
                            settings.getInt("max_count_per_player", 3)
                        );
                        monsterSettings.put(entityType, monsterSetting);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid entity type in config: " + key);
                }
            }
        }
    }
    
    private void loadWorldSettings() {
        worldSettings.clear();
        ConfigurationSection worldSection = config.getConfigurationSection("world_settings");
        
        if (worldSection != null) {
            for (String worldName : worldSection.getKeys(false)) {
                ConfigurationSection settings = worldSection.getConfigurationSection(worldName);
                
                if (settings != null) {
                    WorldSettings worldSetting = new WorldSettings(
                        settings.getBoolean("enabled", true),
                        settings.getDouble("spawn_rate_multiplier", 1.0),
                        settings.getInt("max_monsters_per_player", 8)
                    );
                    worldSettings.put(worldName, worldSetting);
                }
            }
        }
    }
    
    // Getters
    public boolean isEnabled() { return enabled; }
    public boolean isDebug() { return debug; }
    public boolean isRateControlEnabled() { return rateControlEnabled; }
    public boolean isLimitControlEnabled() { return limitControlEnabled; }
    public double getGlobalSpawnRateMultiplier() { return globalSpawnRateMultiplier; }
    public int getMaxMonstersPerPlayer() { return maxMonstersPerPlayer; }
    public int getCheckRadius() { return checkRadius; }
    public int getSpawnCheckDelay() { return spawnCheckDelay; }
    public boolean isTimeControlEnabled() { return timeControlEnabled; }
    public double getNightMultiplier() { return nightMultiplier; }
    public double getDayMultiplier() { return dayMultiplier; }
    public double getFullMoonMultiplier() { return fullMoonMultiplier; }
    
    public MonsterSettings getMonsterSettings(EntityType entityType) {
        return monsterSettings.getOrDefault(entityType, new MonsterSettings(true, 1.0, 3));
    }
    
    public WorldSettings getWorldSettings(String worldName) {
        return worldSettings.getOrDefault(worldName, new WorldSettings(true, 1.0, 8));
    }
    
    public List<String> getEnabledRules() {
        return config.getStringList("spawn-rules.advanced-rules.enabled");
    }

    public boolean isAdvancedRuleEnabled(String rule) {
        return getEnabledRules().contains(rule);
    }
    
    // Inner classes for settings
    public static class MonsterSettings {
        private final boolean enabled;
        private final double spawnRateMultiplier;
        private final int maxCountPerPlayer;
        
        public MonsterSettings(boolean enabled, double spawnRateMultiplier, int maxCountPerPlayer) {
            this.enabled = enabled;
            this.spawnRateMultiplier = spawnRateMultiplier;
            this.maxCountPerPlayer = maxCountPerPlayer;
        }
        
        public boolean isEnabled() { return enabled; }
        public double getSpawnRateMultiplier() { return spawnRateMultiplier; }
        public int getMaxCountPerPlayer() { return maxCountPerPlayer; }
    }
    
    public static class WorldSettings {
        private final boolean enabled;
        private final double spawnRateMultiplier;
        private final int maxMonstersPerPlayer;
        
        public WorldSettings(boolean enabled, double spawnRateMultiplier, int maxMonstersPerPlayer) {
            this.enabled = enabled;
            this.spawnRateMultiplier = spawnRateMultiplier;
            this.maxMonstersPerPlayer = maxMonstersPerPlayer;
        }
        
        public boolean isEnabled() { return enabled; }
        public double getSpawnRateMultiplier() { return spawnRateMultiplier; }
        public int getMaxMonstersPerPlayer() { return maxMonstersPerPlayer; }
    }
}
