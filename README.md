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

## Compatibility

| Requirement | Version |
|-------------|---------|
| Server software | [Paper](https://papermc.io) 26.1.2 or newer |
| Java | 25 or newer |

- **Minimum version:** 26.1.2. Older Paper versions will refuse to load the plugin.
- **Newer versions:** the plugin should work on newer Paper releases, but only 26.1.2 is officially tested. If something breaks on a newer version, please open an issue.
- **Not supported:** Spigot and Bukkit, because the plugin uses `paper-plugin.yml` and the Paper API.

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
| `/wspawn set` | Set the spawn at your position | `wspawn.set` |
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
- `lang/<language>.yml` (e.g. `en.yml`, `fr.yml`, `fr_ch.yml`): all messages.

Everything is shown below with its default value, so you can check at a glance what can be changed.

<details>
<summary><code>config.yml</code> (default)</summary>

```yaml
# ──────────────────────────────────────────────
#  General
# ──────────────────────────────────────────────

# Language
language:
  # Language used for the console and for players whose game language has no file
  # in the lang/ folder (e.g. "en", "fr", "fr_ch"). Missing entries fall back to the
  # built-in en.yml, see the header of the language files.
  default: "en"
  # true = ignore the players' game language and use the default language for everyone
  force: false

# ──────────────────────────────────────────────
#  Automatic teleports
# ──────────────────────────────────────────────
events:
  # Teleport players to spawn the first time they join
  on-first-join: false
  # Teleport players to spawn every time they join (first join excluded)
  on-join: true
  respawn:
    if-missing: true
    override-bed: false
    override-anchor: false

# ──────────────────────────────────────────────
#  Teleportation behaviour
# ──────────────────────────────────────────────
teleportation:
  # Countdown before the teleport, in ticks (20 ticks = 1 second, so 100 = 5 s)
  # Used when the player has no wspawn.teleportation.delay.<ticks> permission
  # Players with wspawn.teleportation.delay.bypass skip the countdown entirely

  # If a player has several wspawn.teleportation.delay.<ticks> permissions:
  # true = use the longest delay, false = use the shortest one
  delay:
    default-tick: 100
    use-max: false
  cooldown:
    default-tick: 100
    use-max: false
  # Allow players to move during the countdown
  # (the wspawn.teleportation.move permission allows it regardless of this value)
  default-can-move: false
  # Keep the player's fall distance after teleporting (otherwise it is reset)
  # (wspawn.teleportation.keep-fall-distance overrides this value)
  default-keep-fall-distance: false
  # Make the player face the orientation defined by the spawn point
  # (wspawn.teleportation.face-set-orientation overrides this value)
  default-face-set-orientation: true

# ──────────────────────────────────────────────
#  Countdown display
# ──────────────────────────────────────────────
timer:
  # Show the remaining time as text
  chat:
    active: true
  # Show the remaining time as a boss bar
  bossbar:
    # Enable or disable the boss bar
    active: true
    # Bar color (Adventure BossBar.Color: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE)
    color: "BLUE"
    # Refresh the displayed time every second instead of every tick
    per-second-update: true
    # Boss bar flags (Adventure BossBar.Flag), remove any you don't want
    flags:
      - DARKEN_SCREEN      # darkens the sky
      - PLAY_BOSS_MUSIC    # plays boss music (doesn't work for some reason...)
      - CREATE_WORLD_FOG   # adds fog

# ──────────────────────────────────────────────
#  Sounds
# ──────────────────────────────────────────────
# Sound names are the ones suggested by Minecraft's /playsound command
# (e.g. "minecraft:block.note_block.pling").
# You can also use custom sounds added through a resource pack
# (e.g. "mypack:teleport.whoosh").
# To mute a sound, set its name entry to "" (e.g. name: "").
sounds:
  # Played when the countdown starts
  initial:
    name: "minecraft:block.beacon.activate"
    volume: 1
    pitch: 1.5
  # Played every second during the countdown
  interval:
    name: "minecraft:block.note_block.pling"
    volume: 1
    pitch: 1.5
  # Played when the player is teleported
  final:
    name: "minecraft:entity.enderman.teleport"
    volume: 1
    pitch: 1
  # Played when the player requests a new teleport while another is pending
  canceled:
    name: "minecraft:block.wet_sponge.break"
    volume: 1
    pitch: 1
  # Played when the teleport fails or is cancelled (e.g. the player moved)
  error:
    name: "minecraft:entity.villager.no"
    volume: 1
    pitch: 1
```

</details>

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

<details>
<summary><code>en.yml</code> (built-in messages)</summary>

```yaml
# ──────────────────────────────────────────────
#  WSpawn - messages
# ──────────────────────────────────────────────
# Format: MiniMessage (https://docs.advntr.dev/minimessage/format.html)
#
# Language
#   Each player gets the messages in their game language (e.g. fr_ch.yml, then fr.yml).
#   "language.default" in config.yml is used for the console and for players whose
#   language has no file. Change it there, then run /wspawn reload.
#
# Creating your own language
#   Copy this file, name it after the language (fr.yml, de.yml, fr_ch.yml...),
#   translate the values only (keep the keys and the <placeholders> unchanged).
#
# Entries fallback
#   A language file doesn't have to contain every entry. An entry is taken from:
#     1. the player's language file (fr_ch.yml, then fr.yml)
#     2. the file selected by "language.default" in config.yml
#     3. the built-in en.yml inside the plugin (not editable, not the one in the "lang" folder)
#   Useful tip: if you want a single prefix for all languages, define it only in the
#   default language file and leave it out of the others. If it isn't found there
#   either, the built-in one is used.
#
# Empty messages
#   Any message set to "" is simply not sent (no chat message, no error).
#   Use this to disable a message you don't want.
#
# Global placeholders (usable in every message)
#   <prefix>                        The prefix defined below
#   <spawn-x> <spawn-y> <spawn-z>   Spawn coordinates
#   <spawn-world>                   Spawn world name
#   <spawn-yaw> <spawn-pitch>       Spawn rotation
#
# Placeholders specific to a message are listed above that message.
# ──────────────────────────────────────────────

# Prefix prepended to most chat messages (gradient + dark gray brackets)
prefix: "<dark_gray>[<gradient:#55ffff:#5555ff>WSpawn</gradient><dark_gray>] <gray>"

# ──────────────────────────────────────────────
#  Commands
# ──────────────────────────────────────────────

# /spawn
spawn:
  # A player teleports themselves to the spawn
  self:
    # Sent when the countdown starts
    # Placeholders: <remaining-second>
    countdown-start: "<prefix><green>You will be teleported to the spawn in <white><remaining-second><green>s."
    # Sent when a new request replaces a pending one (the countdown restarts)
    # Placeholders: <remaining-second>
    countdown-restart: "<prefix><green>Previous request canceled, you will now be teleported in <white><remaining-second><green>s."
    # Sent after the teleport succeeded
    confirmation: "<prefix><green>You were teleported to the spawn <dark_gray>(<yellow><spawn-x><gray>, <yellow><spawn-y><gray>, <yellow><spawn-z><dark_gray>)<green>."
    error:
      # Sent when the player moves during the countdown (teleport cancelled)
      moved: "<prefix><red>You moved, please try again."
      # Sent when the teleport fails for an unexpected reason
      unknown: "<prefix><red>Something went wrong, please try again."

  forced:
    # Received by the teleported player
    # Placeholders: <sender> (player name, or "CONSOLE" when run from the console)
    #               <head> (only in "player", the console has no skin)
    target:
      console: "<prefix><green>You were teleported to the spawn by <white><sender><green>."
      player: "<prefix><green>You were teleported to the spawn by <reset><head> <white><sender><green>."
    # Received by whoever ran the command
    # Placeholders: <head> <target>
    sender: "<prefix><green>You teleported <reset><head><white><target> <green>to the spawn."
    error:
      # Sent when the command targets several players (e.g. @a or another selector)
      multiple-targets: "<prefix><red>You can only teleport one player at a time."


  # Sent when the player uses /spawn while on cooldown
  # Placeholders: <remaining-second>
  cooldown: "<prefix><green>You are on cooldown for <white><remaining-second><green>s."

# /setspawn
# Here <spawn-*> is the NEW location, and <old-*> is the previous one.
set-spawn:
  # Sent after the spawn is changed
  # Placeholders: <old-x> <old-y> <old-z> <old-world> <old-yaw> <old-pitch>
  confirmation: "<prefix><green>The spawn location was set successfully <dark_gray>(<yellow><spawn-x><gray>, <yellow><spawn-y><gray>, <yellow><spawn-z><dark_gray>)<green>. Previous location: <dark_gray>(<yellow><old-x><gray>, <yellow><old-y><gray>, <yellow><old-z><dark_gray>)<green>."
  error:
    # Sent when the console runs the command without explicit coordinates
    console-needs-coordinates: "<prefix><red>From the console, you must specify the coordinates."

# /wspawn reload
reload:
  # Sent when the reload starts
  start: "<prefix><green>Reloading configs..."
  # Sent when the reload finished without errors
  confirmation: "<prefix><green>Configs reloaded successfully."
  error:
    # Sent when a config file fails to load
    # Placeholders: <error-file> <error-message>
    failed: "<prefix><red>Something went wrong while reloading <red>: <white><error><red>."

# ──────────────────────────────────────────────
#  Countdown display
# ──────────────────────────────────────────────
# Shown while the player must stand still before being teleported
# (each one is enabled in the "timer" section of config.yml)
# Placeholders: <remaining-second>
timer:
  # Boss bar title
  bossbar:
    cannot-move: "<red>Stay still for <white><remaining-second><red>s"
    can-move: "<green>Teleportation in <white><remaining-second><green>s"
  # Chat message
  chat:
    cannot-move: "<prefix><red>Stay still for <white><remaining-second><red>s."
    can-move: "<prefix><green>Teleportation in <white><remaining-second><green>s"

# ──────────────────────────────────────────────
#  Automatic teleports
# ──────────────────────────────────────────────
# Messages sent when a player is automatically teleported to the spawn,
# on join or on respawn
events:
  # Sent when a player is teleported on every join (first join excluded)
  on-join: ""
  # Sent when a player is teleported on their first join
  on-first-join: ""
  # Sent when a player respawns at the spawn because their respawn block
  # (bed or respawn anchor) is missing or was destroyed
  on-respawn-block-missing: "<prefix><green>You were teleported to spawn because you are missing an respawn point."
  # Sent when a player with a bed respawn point is sent to the spawn instead
  on-respawn-override-bed: "<prefix><green>You were teleported to spawn, but you still have a bed placed and accessible."
  # Sent when a player with a respawn anchor is sent to the spawn instead
  on-respawn-override-anchor: "<prefix><green>You were teleported to spawn, but you still have an respawn anchor active."
```

</details>
