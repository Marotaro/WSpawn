# WSpawn

A somewhat customizable spawn plugin for [Paper](https://papermc.io/) servers, made for the [Wondercraft](https://wondercraft.ch) network.

## Features

- Teleport to the server spawn with `/spawn`
- Configurable teleportation countdown, with a boss bar timer
- Per-player delays through permissions
- Cancel or allow movement during the countdown
- Staff tools to set the spawn and send other players to it
- Configurable sounds for each step of the teleport, including custom resource pack sounds
- Fully customizable messages in MiniMessage format (no per player language (for now))
- Reload the configs with `/wspawn reload`, no restart needed

## Requirements

- Paper `26.1.x` or higher
- Java version required by your Paper version

## Installation

1. Download the latest jar from the [Releases](../../releases) page.
2. Drop it in your server's `plugins/` folder.
3. Restart the server.
4. Edit `plugins/WSpawn/config.yml` to your liking.

## Commands

| Command | Description | Permission |
|---|---|---|
| `/spawn` | Teleport yourself to spawn | `wspawn.spawn` |
| `/spawn <player>` | Force a player to teleport to spawn | `wspawn.spawn.other` |
| `/setspawn` | Set the server spawn to your position | `wspawn.set` |
| `/wspawn:reload` | Reload the config and language files | `wspawn.reload` |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `wspawn.spawn` | Teleport yourself to spawn | everyone |
| `wspawn.spawn.other` | Force another player to teleport to spawn | op |
| `wspawn.set` | Set the new spawn of the server | op |
| `wspawn.teleportation.delay.bypass` | Skip the teleportation countdown | op |
| `wspawn.teleportation.move` | Allow moving during the countdown | op |
| `wspawn.teleportation.delay.<ticks>` | Custom countdown for a player (in ticks) | none |

### Custom delays

Give a player `wspawn.teleportation.delay.<ticks>` to override the default delay, for example `wspawn.teleportation.delay.40` for a 2-second countdown (20 ticks = 1 second).

If a player has several of these permissions, `teleportation.use-min` decides which one applies: the smallest if `true`, the biggest if `false`. Without any of them, the default `teleportation.delay` is used.

## Messages and languages

All messages use [MiniMessage](https://docs.advntr.dev/minimessage/format.html). The active language is chosen with `lang` in `config.yml`, then apply it with `/wspawn reload`.

To add a language, copy an existing language file, translate the values (keep the keys and `<placeholders>`), and set `lang` to its name.

Global placeholders usable in every message: `<prefix>`, `<spawn-x>`, `<spawn-y>`, `<spawn-z>`, `<spawn-world>`, `<spawn-yaw>`, `<spawn-pitch>`. Each message also lists its own placeholders in the file.

## Custom sounds

Sound names are `namespace:path`. Vanilla sounds use `minecraft:`. For custom sounds, register your `.ogg` files in a resource pack's `sounds.json` and make players download it via `server.properties` (`resource-pack`, `resource-pack-sha1`, `require-resource-pack`). Then use your sound key as `name`.

## Configuration

```yaml
lang: "en"

teleportation:
  # Default countdown in ticks (20 ticks = 1 second)
  delay: 100
  # Whether players may move during the countdown
  default-can-move: false
  # Several delay permissions: true = smallest, false = biggest
  use-min: false

timer-display:
  # Boss bar color: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
  color: "BLUE"
  active: true
  # Update every second instead of every tick
  per-second-update: true
  # Boss bar flags, remove any you don't want
  flag:
    - DARKEN_SCREEN
    - PLAY_BOSS_MUSIC
    - CREATE_WORLD_FOG
```

## Building

```bash
./gradlew build
```

The jar is generated in `build/libs/`.
