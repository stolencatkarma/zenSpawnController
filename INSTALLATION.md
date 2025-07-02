# Quick Installation Guide

## What You've Built
Your ZenSpawnController plugin is now ready! The compiled JAR file is located at:
`target/zenspawncontroller-1.0.0.jar`

## Installation Steps

1. **Copy the Plugin**
   - Take the `zenspawncontroller-1.0.0.jar` file from the `target` folder
   - Copy it to your Paper server's `plugins` folder

2. **Start/Restart Your Server**
   - If the server is running, restart it
   - If it's not running, start it normally

3. **Verify Installation**
   - Check your server console for: `ZenSpawnController has been enabled!`
   - Run `/zenspawn help` in-game to see available commands

## First Setup

1. **Check Current Status**
   ```
   /zenspawn status
   ```

2. **Get Player Information**
   ```
   /zenspawn info
   ```

3. **Enable Debug Mode (optional)**
   ```
   /zenspawn debug on
   ```

## Quick Configuration Examples

### Reduce Overall Spawning by 50%
Edit `plugins/ZenSpawnController/config.yml`:
```yaml
spawn_control:
  spawn_rate_multiplier: 0.5
```

### Limit Monsters Around Players
```yaml
spawn_control:
  max_monsters_per_player: 5  # Max 5 monsters per player
  check_radius: 24            # Within 24 blocks
```

### Make Nights More Dangerous
```yaml
time_control:
  enabled: true
  day_multiplier: 0.2      # 80% fewer spawns during day
  night_multiplier: 2.0    # Double spawns at night
```

### Reduce Creeper Spawns
```yaml
monster_settings:
  CREEPER:
    enabled: true
    spawn_rate_multiplier: 0.3  # 70% fewer creepers
    max_count_per_player: 1     # Max 1 creeper per player
```

## Commands Reference

| Command | What It Does |
|---------|--------------|
| `/zenspawn help` | Show all commands |
| `/zenspawn status` | Show plugin settings |
| `/zenspawn info` | Show spawn info around you |
| `/zenspawn reload` | Reload config after changes |
| `/zenspawn debug on/off` | Toggle debug messages |

## Troubleshooting

### Plugin Not Loading?
- Make sure you're using Paper 1.21.4 (not Spigot)
- Check server console for error messages
- Verify Java 21+ is installed

### Spawning Still Too High/Low?
- Use `/zenspawn debug on` to see what's happening
- Check the config values are correct
- Remember to `/zenspawn reload` after config changes

### Need to Reset Config?
- Delete `plugins/ZenSpawnController/config.yml`
- Restart server (plugin will create fresh config)

## Next Steps

1. Test the plugin with debug mode on
2. Adjust settings in `config.yml` as needed
3. Use `/zenspawn reload` after each config change
4. Monitor spawn rates and adjust multipliers
5. Set up world-specific settings if needed

---

**Happy monster controlling!** 🎮
