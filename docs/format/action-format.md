# 🎬Action Format

The action format will consist of several options.

## Supported Placeholders

MythicTotem supports those placeholders in ActionFormat and ConditionFormat.

* %player%
* %player\_x%
* %player\_y%
* %player\_z%
* %player\_yaw%&#x20;
* %player\_pitch%&#x20;
* %block\_x%
* %block\_y%
* %block\_z%
* %world%
* %totem\_start\_x% (Only support used in totem config's `actions` section)
* %totem\_start\_y% (Only support used in totem config's `actions` section)
* %totem\_start\_z% (Only support used in totem config's `actions` section)
* %totem\_column% (Only support used in totem config's `actions` section)
* %totem\_raw% (Only support used in totem config's `actions` section)
* %totem\_layout% (Only support used in totem config's `actions` section)
* %totem\_id%&#x20;
* %totem\_center\_x% (Only support used in totem config's `actions` section)
* %totem\_center\_y% (Only support used in totem config's `actions` section)
* %totem\_center\_z% (Only support used in totem config's `actions` section)
* %bonus\_uuid% (Only support unsed in action exist in totem config's `bonus-effects` section)
* %bonus\_level% (Only support unsed in action exist in totem config's `bonus-effects` section)

## Message

Send a message to the player, support color code.

```yaml
actions:
  1:
    type: message
    message: 'Hello!'
```

## Announcement <a href="#announcement" id="announcement"></a>

Send a message to all online players, support color code.

```yaml
actions:
  1:
    type: announcement
    message: 'Hello!'
```

## Title <a href="#title" id="title"></a>

Send title to the player, support the color code.

```yaml
actions:
  1:
    type: title
    main-title: 'Good day'
    sub-title: 'Not bad'
    fade-in: 10
    stay: 70
    fade-out: 30
```

## Particle <a href="#particle" id="particle"></a>

```yaml
actions:
  1: 
    type: particle
    particle: HEART
    count: 20
    offset-x: 0.3
    offset-y: 1.0
    offset-z: 0.3
    speed: 0.01
```

## Effect

Give players potion effect.

```yaml
actions:
  1:
    type: effect
    potion: BLINDNESS
    duration: 60
    level: 1
    ambient: true # Optional
    particles: true # Optional
    icon: true # Optional
```

## Teleport

Teleport player to specified location.

```yaml
actions:
  1:
    type: teleport
    world: LobbyWorld
    x: 100
    y: 30
    z: 300
    pitch: 90 # Optional
    yaw: 0 # Optional
```

## Player Command

Make the player excutes a command.

```yaml
actions:
  1:
    type: player_command
    command: 'tell Hello!'
```

## Op Command

Make the player excutes a command as OP.

```yaml
actions:
  1:
    type: op_command
    command: 'tell Hello!'
```

## Console Command

Make the console excutes a command.

```yaml
actions:
  1:
    type: console_command
    command: 'op {player}'
```

## Spawn vanilla mobs

Spawn vanilla mobs.

```yaml
actions:
  1:
    type: entity_spawn
    entity: ZOMBIE
    world: LOBBY # Optional
    x: 100.0 # Optional
    y: 2.0 # Optional
    z: -100.0 # Optional
```

## MythicMobs spawn

Require MythicMobs.

```yaml
actions:
  1:
    type: mythicmobs_spawn
    entity: Super_Skeleton
    level: 1 # Optional
    world: LOBBY # Optional
    x: 100.0 # Optional
    y: 2.0 # Optional
    z: -100.0 # Optional
    block-as-trigger: true # Optional
```

Want to summon mobs at center of totem, try this config!

```yaml
actions:
  1:
    type: mythicmobs_spawn
    entity: Super_Skeleton
    level: 1 # Optional
    world: LOBBY # Optional
    x: '%totem_center_x%' # Optional
    y: '%totem_center_y%' # Optional
    z: '%totem_center_z%' # Optional
    block-as-trigger: true # Optional
```

## Delay <mark style="color:red;">- Premium</mark>

Make the action run after X ticks.

```yaml
actions:
  1:
    type: delay
    time: 50
    actions:
      1:
        type: entity_spawn
        entity: ZOMBIE
```

## Chance <mark style="color:red;">- Premium</mark>

Set the chance the action will be excuted, up to 100. 50 means this action has 50% chance to excute.

```yaml
actions:
  1:
    type: chance
    rate: 50
    actions:
      1:
        type: entity_spawn
        entity: ZOMBIE
```

## Any <mark style="color:red;">- Premium</mark>

Randomly choose specified amount of actions to execute.

```yaml
actions:
  1:
    type: any
    amount: 2
    actions:
      1:
        type: entity_spawn
        entity: ZOMBIE
      2:
        type: entity_spawn
        entity: SKELETON
      3:
        type: entity_spawn
        entity: WITHER
```

## Conditional <mark style="color:red;">- Premium</mark>

Only players meet the conditions you set here will be able to execute the action.

```yaml
general-actions:
  1:
    type: conditional
    conditions:
      1: 
        type: world
        world: lobby
    actions:
      1:
        type: entity_spawn
        entity: ZOMBIE
```

## Give Item <a href="#give-item" id="give-item"></a>

Should use ItemFormat in `item` option. For more info about Item Format, plaese [click here](https://ultimateshop.superiormc.cn/format/itemformat-tm).

```yaml
actions:
  1:
    type: give_item
    item:
      material: apple # Item Format here
```
