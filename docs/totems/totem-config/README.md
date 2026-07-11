# 📝Totem Config

You can find all totem configs in `totems` folder.&#x20;

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
core-blocks:
  - B
prices:
  1:
    material: APPLE
    prices-as-key: true
    disappear: true
prices-as-key: true
disappear: true
```

All totem must have its unique ID.

## Mode

Set the mode for totem placement. Support:

* Horizontal&#x20;
* Vertical (DEFAULT)

## Layout

Layout is a string list option, the length of the string in each line must be the same. String on each line consist of multiple characters in `explains` option.

The player must place the blocks according to this layout to activate the totem.

## Layouts (3D Totem) <mark style="color:red;">- Premium</mark>

You can also use `layouts` option to make this totem be a 3D totem. For now, only horizontal mode totem suppot 3D totem. &#x20;

**Free version can only create up to 3 3D totems!**

For example:

1 is the top, 2 is below 1.

```yaml
  layouts:
    1:
      - 'AAAA'
      - 'BBBB'
      - 'CCCC'
    2:
      - 'CCCC'
      - 'BBBB'
      - 'AAAA'
```

## Explains

Each line for this option consists of `key: value`. The `key` is a character which is used in layout option. The `value` is which material is the character mean.

The material can be set to:

* `none`, this means this position block is not limited, and it will also not be removed if disappear option is enabled. You can regard it as a position not in the totem.
* `minecraft:<Minecraft Block ID>`, like minecraft:stone.
* `minecraft:<Minecraft Entity ID>:<Check Distance>`, like `minecraft:ENDER_CRYSTAL` or `minecraft:ENDER_CRYSTAL:1` **.**

{% hint style="warning" %}
Only <mark style="color:red;">**PREMIUM**</mark> version of MythicTotem allows you use entities as totem layout.

Only <mark style="color:red;">**PREMIUM**</mark> version of MythicTotem allows for the use of custom blocks as totem layouts, while the **free** version can only use up to **3** types of custom blocks.
{% endhint %}

* `itemsadder:<NamespaceID>:<Block ID>`, like `itemsadder:blocks:block_1`.
* `itemsadder_furniture:<NamespaceID>:<Furniture ID>:<Check Distance>`, like `itemsadder_furniture:highitems:corrupted_head:0.5`.
* `itemsadder_mob:<NamespaceID>:<Mob ID>:<Check Distance>`**.**
* `oraxen:<Item ID>`, like `oraxen:block_1`.
* `oraxen_furniture:<Furniture ID>:<Check Distance>`**.**
* `mmoitems:<Block ID>`, like `mmoitems:10`, **block id (is a number, not item id)** is a number been set by your block configs, not item ID.
* `craftengine:<NamespaceID>:<Block Item ID>`, like `craftengine:default::palm_log`.
* `nexo:<Item ID>`, like `nexo:block_2`.

Totem now work for you? Check your `config.yml` file, your `black-creative-mode` maybe enabled!

For `<Check Distance>` is mean check the distance of nearby entities at this location, which defaults to 0.5, representing the distance of exactly one block.

{% hint style="info" %}
If you are using entity as totem layout, don't forgot change those things in config.yml file, otherwise after you place the "entity", plugin won't check them.

```yaml
  PlayerInteractEvent:
    enabled: true # <-- Change this to true
    require-shift: false # <-- Change this to false
    black-creative-mode: false
```
{% endhint %}

## Actions

The actions performed after the totem is activated.

Use **Action Format** here, for more info, please view [this page](../../format/itemformat-tm.md).

## Conditions

Players or totems must meet these conditions to activate.

Use **Condition Format** here, for more info, please view [this page](../../format/itemformat-tm.md).

## Prices <mark style="color:red;">- Premium</mark>

Players will cost prices to build totems, if players didn't meet the prices (all prices should be meet), the totems won't active!

See [Prices](prices-option-premium.md) to learn more.

## Prices as Key <mark style="color:red;">- Premium</mark>

You MUST set only 1 price in prices option, and this only price MUST be a item price, like:

```yaml
prices:
  1:
    material: APPLE
```

**Do not make more than 1 price! Otherwise it won't work!**

After enable this, player must hold or drop price item to active totem. In this way, prices will make a feature like: 'Totem Active Key'!

## Core Block

This totem will have "core block" if this option exists, player must last place this block, interact this block or drop item on this block to active totem.

## Disappear

Does the totem disappear after activation?

## Bonus Effects

```yml
bonus-effects:
  enabled: true
  range: 16
  effects:
    enabled: true
    1:
      type: MythicMobs
      modifier-type: SET # ADD, SET, MULTIPLY, COMPOUND
      stat: HEALTH
      value: 100
  apply-actions:
    1:
      type: message
      message: 'Totem effect actived!'
  remove-actions:
    1:
      type: message
      message: 'Totem effect removed!'
  circle-actions:
    1:
      type: give_item
      item:
        material: apple
    2:
      type: message
      message: 'Totem effect executed!'
```

For more info, please view [this page](../bonus-effects-for-totem.md).
