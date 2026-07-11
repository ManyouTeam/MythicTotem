# 🛠️Configuration files

The plugin generates the following configuration files, some of which will only be generated after you first use this feature.

* `items`: The location for storing saved item files. It will only be generated after save any item with `/mt saveitem` command. <mark style="color:red;">Do not modify any content here</mark>.
* `languages`: The location for storing language files. You can set the language file used by the plugin through the `config-files.language` option in the `config.yml` file. You can customize various messages within the plugin game through language files. It is not supported to display the corresponding language file based on the player client language. You can only display the same language for all players.
* `totems`: The location for storing totem configuration files.
* `config.yml` file: The location for main common settings for plugins.
* `generated-item-format.yml` file: When using the `/mc generateeitemformat` command, we will parse the item you are holding into an **ItemFormat** and store the parsed **ItemFormat** content in this file.

## Config.yml file content <a href="#config.yml-file-content" id="config.yml-file-content"></a>

CommentIt is recommend that you view this file at GitHub, becuase Wiki's `config.yml` maybe not **latest**. Click [here](https://github.com/PQguanfang/MythicTotem) to view this file on **Github.**

```yaml
# MythicTotem by @PQguanfang
#
# READ THE WIKI: mythictotem.superiormc.cn

debug: false

language: en_US

# Item Price
item-price:
  # Support Value: Bukkit, ItemFormat.
  check-method: Bukkit
  item-format:
    ignore-key:
      - 'lore'
      - 'damage'
      - 'tool.damage-per-block'

cooldown-tick: 5

# Paper only feature.
paper-api:
  save-item: true
  # For paper users, enable this option can use their API to directly get the skull, have the performance improve.
  skull: true

trigger:
  BlockPlaceEvent:
    enabled: true
    require-shift: false
    black-creative-mode: false
  PlayerInteractEvent:
    enabled: false
    require-shift: true
    black-creative-mode: false
  PlayerDropItemEvent:
    enabled: false
    require-shift: false
    black-creative-mode: false
  # Will check end crystal only.
  EntityPlaceEvent:
    enabled: true
    require-shift: false
    black-creative-mode: false
  # This event does not support get the player object, so cooldown-tick option does not effect this trigger.
  # And all actions and conditions that related to player is can not be used.
  # And all placeholders that related to player also can not be used.
  # Otherwise you will get tons of errors on console!!!
  BlockRedstoneEvent:
    enabled: true
  # This event does not support get the player object, so cooldown-tick option does not effect this trigger.
  # And all actions and conditions that related to player is can not be used.
  # And all placeholders that related to player also can not be used.
  # Otherwise you will get tons of errors on console!!!
  # Premium version only.
  BlockPistonEvent:
    enabled: true
```

### Debug

Only enable this if you know what are you doing! It will print tons of debug info on console.

### Cooldown Tick

This means totem check system now have a cooldown system for per player, this can avoid server lag if you have much online players.

Bump it to 20+ if you are facing double action issue.

### Trigger

What events will be listened to check if a valid totem has been placeed correctly.

For now it has 5 events:

* BlockPlaceEvent: will be called when players place the block.
* PlayerInteractEvent: will be called when players click the block.
* PlayerDropItemEvent: will be called when players drop the item on the block. **If you set core block for a totem, player must stand on the center of the core block then drop the item onto the center of the block to active totem.**
* EntityPlaceEvent: will be called when player place ender crystal on the block.
* BlockRedstoneEvent: will be called when redstone actived (the redstone block actived must be a part of the totem layout). **This event does not support get the player object, so cooldown-tick option does not effect this trigger.**
* BlockPistonEvent: will be called when you use piston extend the block, the extened block must be a part of the tote&#x6D;**. This event does not support get the player object, so cooldown-tick option does not effect this trigger.&#x20;**<mark style="color:red;">**Premium version only.**</mark>

All events except **BlockRedstoneEvent** have those options:

* enabled: enable or disable this event trigger feature.
* require-shift: we only check the totem if player is shifting.
* black-creative-mode: we only check the totem if player is not in creative game mode.
