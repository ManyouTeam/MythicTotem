# 📚Example: Totem with a Key required

## Config

```yaml
mode: 'HORIZONTAL'
layouts:
  1:
    - 'AAAAA'
    - 'AAAAA'
    - 'AABAA'
    - 'AAAAA'
    - 'AAAAA'
  2:
    - 'CCCCC'
    - 'CCCCC'
    - 'CCCCC'
    - 'CCCCC'
    - 'CCCCC'
explains:
  A: 'minecraft:stone'
  B: 'minecraft:dirt'
  C: 'minecraft:netherrack'
actions:
  1:
    type: message
    message: 'Hello World!'
  2:
    type: mythicmobs_spawn
    entity: 'SkeletalKnight'
conditions:
  1:
    type: trigger
    event: 'PlayerDropItemEvent'
# Core block means what block player should drop item on.
core-blocks:
  - B
prices:
  1:
    material: APPLE
    amount: 1
prices-as-key: true
disappear: true
```

## Info

* We make this totem can only be actived with PlayerDropItemEvent, so player have to drop item to active this totem.
* We also set core block for this totem, so player have to drop item onto specified blocks to active this totem.
* We also set prices for this totem, so player's drop item will consume and disappear because this totem need price to active.
* Price feature is only available for PREMIUM version of MythicTotem.
