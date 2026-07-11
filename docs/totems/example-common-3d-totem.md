# 📚Example: Common 3D Totem

## Config

```yaml
mode: 'HORIZONTAL'
layouts:
  1:
    - 'AAAA'
    - 'BBBB'
    - 'CCCC'
  2:
    - 'CCCC'
    - 'BBBB'
    - 'AAAA'
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
    entity: CAVE_SPIDER
conditions: []
disappear: true
```

## In-game layout

<figure><img src="../.gitbook/assets/image (1).png" alt=""><figcaption></figcaption></figure>
