package com.zenspawn.zenspawncontroller;

import com.zenspawn.zenspawncontroller.commands.ZenSpawnCommand;
import com.zenspawn.zenspawncontroller.commands.ZenSpawnPaperCommand;
import com.zenspawn.zenspawncontroller.config.ConfigManager;
import com.zenspawn.zenspawncontroller.listeners.SpawnListener;
import com.zenspawn.zenspawncontroller.managers.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ZenSpawnController extends JavaPlugin {
    
    private static ZenSpawnController instance;
    private ConfigManager configManager;
    private SpawnManager spawnManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Initialize spawn manager
        spawnManager = new SpawnManager(this);
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
        
        // Register commands using Bukkit CommandMap (Paper 1.21+)
        ZenSpawnPaperCommand.register(this);
        
        // Start spawn monitoring task
        spawnManager.startSpawnTask();
        
        getLogger().info("ZenSpawnController has been enabled!");
        getLogger().info("Monster spawning is now under control.");
    }
    
    @Override
    public void onDisable() {
        if (spawnManager != null) {
            spawnManager.stopSpawnTask();
        }
        getLogger().info("ZenSpawnController has been disabled!");
    }
    
    public static ZenSpawnController getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
    
    public void reload() {
        configManager.reloadConfig();
        spawnManager.reloadSettings();
        getLogger().info("ZenSpawnController has been reloaded!");
    }
}
