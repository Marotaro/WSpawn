<p align="center">
  <img src="assets/logo.png" alt="WSpawn logo" width="200">
</p>

<h1 align="center">WSpawn</h1>

A somewhat customizable spawn plugin for [Paper](https://papermc.io/), made for the [Wondercraft](https://wondercraft.ch) network.

## Features

- `/spawn` with a configurable countdown, cooldown and cancel-on-move
- Force-teleport other players to the spawn
- Automatic teleport on join and on respawn (when the bed or respawn anchor is missing)
- Countdown shown in chat and/or in a boss bar
- Configurable sounds for every step of the teleport, including custom sounds from a resource pack (e.g. `mypack:teleport.whoosh`)
- Fully translatable messages, formatted with [MiniMessage](https://docs.advntr.dev/minimessage/format.html)
- Per-player language: each player gets the messages in their own game language, with a fallback for missing entries (can be forced to a single language for everyone)
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
| `wspawn.teleportation.cooldown.bypass` | Skip the cooldown | op |
| `wspawn.teleportation.cooldown.<ticks>` | Custom cooldown | – |
| `wspawn.teleportation.move` | Allow moving during the countdown | op |
| `wspawn.teleportation.keep-fall-distance` | Keep fall distance after teleport | – |
| `wspawn.teleportation.face-set-orientation` | Face the spawn orientation | – |

If a player has several delay (or cooldown) permissions, `use-max` in `config.yml` decides whether the longest or the shortest one applies.

## Configuration

- `config.yml`: language, automatic teleports, delay/cooldown, countdown display, sounds.
- `lang/<language>.yml` (e.g. `en.yml`, `fr.yml`, `fr_ch.yml`): all messages. Players see the file matching their game language; `language.default` in `config.yml` is used for the console and for players whose language has no file.

### Sounds

Each step of the teleport (`initial`, `interval`, `final`, `canceled`, `error`) has its own sound in the `sounds` section of `config.yml`. Use any name suggested by Minecraft's `/playsound` command (e.g. `minecraft:block.note_block.pling`), or a custom sound added through a resource pack (e.g. `mypack:teleport.whoosh`). Set `name: ""` to mute a sound.

### Languages

Messages are sent in each player's game language (e.g. a player using `fr_CH` gets `fr_ch.yml`, then `fr.yml`). If no file matches, the file selected by `language.default` in `config.yml` is used, and the built-in `en.yml` as a last resort. The console always uses the default language.

To use a single language for everyone, set `language.force: true` in `config.yml`: every player then gets the default language, whatever their game language.

```yaml
language:
  default: "en"
  force: false
```

A language file doesn't have to be complete: any missing entry falls back to the default language, then to the built-in one.

To add a language, copy `en.yml` into `plugins/WSpawn/lang/`, name it after the language (`de.yml`, `fr.yml`, `fr_ch.yml`...), translate the values only (keep the keys and `<placeholders>`), then run `/wspawn reload`.
