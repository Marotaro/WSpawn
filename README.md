# WSpawn

A somewhat customizable spawn plugin for [Paper](https://papermc.io/), made for the [Wondercraft](https://wondercraft.ch) network.

## Features

- `/spawn` with a configurable countdown, cooldown and cancel-on-move
- Force-teleport other players to the spawn
- Automatic teleport on join and on respawn (when the bed or respawn anchor is missing)
- Countdown shown in chat and/or in a boss bar
- Configurable sounds for every step of the teleport
- Fully translatable messages, formatted with [MiniMessage](https://docs.advntr.dev/minimessage/format.html)
- Per-player delay and cooldown through permissions

## Requirements

- Paper (`api-version` 26.1.2)
- Java version required by your Paper version

## Installation

1. Download the latest jar from the [Releases](../../releases) page.
2. Drop it in your server's `plugins/` folder.
3. Start the server, then edit `plugins/WSpawn/config.yml` and the language file.
4. Run `/wspawn reload` to apply changes.

## Commands

| Command | Description | Permission |
|---|---|---|
| `/spawn` | Teleport yourself to the spawn | `wspawn.spawn` |
| `/spawn <player>` | Force-teleport another player | `wspawn.spawn.other` |
| `/setspawn` | Set the spawn at your position | `wspawn.set` |
| `/wspawn reload` | Reload the configs | `wspawn.reload` |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `wspawn.spawn` | Teleport yourself to the spawn | everyone |
| `wspawn.spawn.other` | Force-teleport another player | op |
| `wspawn.set` | Set the server spawn | op |
| `wspawn.reload` | Reload the plugin configs | op |
| `wspawn.teleportation.delay.bypass` | Skip the countdown | op |
| `wspawn.teleportation.delay.<ticks>` | Custom countdown (20 ticks = 1 s) | – |
| `wspawn.teleportation.cooldown.bypass` | Skip the cooldown | – |
| `wspawn.teleportation.cooldown.<ticks>` | Custom cooldown | – |
| `wspawn.teleportation.move` | Allow moving during the countdown | op |
| `wspawn.teleportation.keep-fall-distance` | Keep fall distance after teleport | – |
| `wspawn.teleportation.face-set-orientation` | Face the spawn orientation | – |

If a player has several delay (or cooldown) permissions, `use-max` in `config.yml` decides whether the longest or the shortest one applies.

## Configuration

- `config.yml`: language, automatic teleports, delay/cooldown, countdown display, sounds.
- `<lang>.yml` (e.g. `en.yml`): all messages. The language is selected with the `lang` option.

### Creating your own language

Copy `en.yml`, translate the values only (keep the keys and `<placeholders>`), then set `lang` in `config.yml` and run `/wspawn reload`.
