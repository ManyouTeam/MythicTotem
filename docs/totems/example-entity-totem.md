# 📚Example: Entity Totem

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
    message: 'Hello!'
  2:
    type: mythicmobs_spawn
    entity: SkeletalKnight'
conditions: []
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

* We make ender crystal be a part of this totem instead of make it be active requirement.
* You can also set ender crystal to other entities.
* Totem with entities as layout require PREMIUM version of MythicTotem.
