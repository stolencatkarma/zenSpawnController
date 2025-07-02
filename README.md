# ZenSpawnController

A powerful Paper plugin for Minecraft 1.21.4 that gives you complete control over monster spawning mechanics.

## Features

### 🎯 Precise Spawn Control
- **Rate Control**: Adjust spawn rates globally, per-monster type, per-world, and by time of day
- **Limit Control**: Set maximum number of monsters that can spawn around each player
- **Monster-Specific Settings**: Individual settings for each monster type (zombies, skeletons, creepers, etc.)

### 🌍 World-Specific Configuration
- Different spawn settings for different worlds
- Support for Overworld, Nether, and End dimensions
- Custom settings for any custom worlds

### 🌙 Time-Based Spawning
- Different spawn rates for day and night
- Special full moon spawn multipliers
- Automatic time detection and adjustment

### 📊 Real-Time Monitoring
- Live tracking of nearby monsters per player
- Debug mode for detailed spawn information
- Admin commands for monitoring and configuration

## Installation

1. Download the latest release from the releases page
2. Place the `.jar` file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin using `/zenspawn` commands or edit `config.yml`

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/zenspawn help` | `zenspawn.admin` | Show help message |
| `/zenspawn reload` | `zenspawn.reload` | Reload plugin configuration |
| `/zenspawn status` | `zenspawn.admin` | Show plugin status and settings |
| `/zenspawn info` | `zenspawn.admin` | Show spawn information around you |
| `/zenspawn debug <on\|off>` | `zenspawn.admin` | Toggle debug mode |

**Aliases:** `/zsc`, `/spawncontrol`

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `zenspawn.admin` | `op` | Access to all plugin commands |
| `zenspawn.reload` | `op` | Reload plugin configuration |
| `zenspawn.config` | `op` | View and modify configurations |

## Configuration

The plugin comes with a comprehensive configuration file that allows you to customize every aspect of monster spawning:

### Global Settings
```yaml
global:
  enabled: true          # Enable/disable the plugin
  debug: false          # Show debug information
```

### Spawn Control
```yaml
spawn_control:
  rate_control_enabled: true     # Enable spawn rate control
  limit_control_enabled: true    # Enable spawn limit control
  spawn_rate_multiplier: 1.0     # Global spawn rate (1.0 = normal, 0.5 = half, 2.0 = double)
  max_monsters_per_player: 8     # Max monsters around each player
  check_radius: 32               # Radius to check for monsters (blocks)
  spawn_check_delay: 20          # Check frequency (ticks, 20 = 1 second)
```

### Monster-Specific Settings
```yaml
monster_settings:
  ZOMBIE:
    enabled: true
    spawn_rate_multiplier: 1.0
    max_count_per_player: 3
  CREEPER:
    enabled: true
    spawn_rate_multiplier: 0.8    # Fewer creepers
    max_count_per_player: 2
  # ... more monster types
```

### Time-Based Control
```yaml
time_control:
  enabled: true
  night_multiplier: 1.5          # 50% more spawns at night
  day_multiplier: 0.3            # 70% fewer spawns during day
  full_moon_multiplier: 2.0      # Double spawns during full moon
```

## How It Works

### Spawn Rate Control
The plugin intercepts natural monster spawn events and applies probability-based rate control. For example:
- `spawn_rate_multiplier: 1.0` = 100% of normal spawns (no change)
- `spawn_rate_multiplier: 0.5` = 50% of normal spawns (half rate)
- `spawn_rate_multiplier: 2.0` = Allows all spawns but can't exceed natural limits

### Spawn Limit Control
The plugin continuously monitors the area around each player and prevents new spawns when limits are reached:
- Counts all monsters within the configured radius
- Applies both global and monster-specific limits
- Updates in real-time as monsters move or die

### Multiplier Stacking
Different multipliers stack together:
```
Final Rate = Global × World × Monster × Time Multipliers
```

Example: Global(1.0) × World(1.2) × Zombie(0.8) × Night(1.5) = 1.44x spawn rate

## Examples

### Peaceful Daytime, Dangerous Nights
```yaml
time_control:
  enabled: true
  day_multiplier: 0.1      # Very few spawns during day
  night_multiplier: 2.0    # Double spawns at night
```

### Limit Specific Monsters
```yaml
monster_settings:
  CREEPER:
    spawn_rate_multiplier: 0.3   # 70% fewer creepers
    max_count_per_player: 1      # Max 1 creeper per player
  ENDERMAN:
    spawn_rate_multiplier: 0.1   # 90% fewer endermen
    max_count_per_player: 1      # Max 1 enderman per player
```

### World-Specific Settings
```yaml
world_settings:
  world:                    # Overworld
    spawn_rate_multiplier: 0.8
    max_monsters_per_player: 6
  world_nether:            # Nether
    spawn_rate_multiplier: 1.5
    max_monsters_per_player: 12
```

## Building from Source

### Requirements
- Java 21 or higher
- Maven 3.6 or higher
- Paper API 1.21.4

### Build Steps
```bash
git clone https://github.com/yourusername/zenspawncontroller.git
cd zenspawncontroller
mvn clean package
```

The compiled plugin will be in `target/zenspawncontroller-1.0.0.jar`

## Compatibility

- **Minecraft Version**: 1.21.4
- **Server Software**: Paper (recommended), Spigot
- **Java Version**: 21+

## Support

- Create an issue on GitHub for bug reports
- Join our Discord for support and discussion
- Check the wiki for advanced configuration examples

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please read our contributing guidelines before submitting pull requests.

---

**Note**: This plugin is designed for Paper servers and may not work correctly on vanilla Spigot due to Paper-specific optimizations and APIs.
