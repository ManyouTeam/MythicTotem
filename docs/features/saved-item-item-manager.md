# 💾Saved Item (Item Manager)

## Create your item <a href="#create-your-item" id="create-your-item"></a>

You can create your own item at `items` folder of plugin, just create a **yml** file and then follow [ItemFormat](https://ultimateshop.superiormc.cn/format/itemformat-tm) in this file. The file name is the item ID.

## Save your item <a href="#save-your-item" id="save-your-item"></a>

You can use command `/mt saveitem <saveItemID> <saveItemMethod>` command to save your hold item. There are 2 methods to save item.

* Bukkit
  * If you are using Spigot version of UltimateShop: Use BukkitAPI's method to save item. The method only support saving vanilla data and persistent data stored through BukkitAPI, and other custom NBT data from other plugins will not be saved.
  * If you are using Paper version of UltimateShop: Use PaperAPI's method to save item, this new method can 100% save item data, no data will lose. **(Paper and 1.15+ server only)**
* ItemFormat: will parse item into [Item Format](https://ultimateshop.superiormc.cn/format/itemformat-tm).

An example for item config file that use Bukkit save item method with Paper version of UltimateShop:

```yaml
item: !!binary |-
  H4sIAAAAAAAA/21RzW4TMRCeJd0oWVB/1IIQXILEG/TGpSDxCKhXa2LPbkxsz8qepUlPPAonXoBn6nsw27TaViBZljzz/XmmAZjBy68oeE25eE4Ap3cLeOEdnEWfyGZs5ZPzGDm5GdSWhyQAMG+gsRx7TpSkNHA8gVtmRS7TINnLvSLUNTQFZcg4Fj7/Aqjg2GIyGG5wXwyhVJrj7SQScWeKoN2a4m9pNFw+9QicqdFqtRgj6QuOuoz7BRwJ7QQuvm18WenJhCHsV8lb+qCeHycFSnaDSaLGN13wevMPytk7qhp4819cmT2diVacT90YAhp4PTXsUISjSRhpSrcOAz2me/Vl1emMVuWGs1Pu+3+4kR0F43QrS5i3gVFKPRpd/dR7Ae8mwvdhS2vemT7gHteB4HzqsWzGnTp6th5hDjM4dRixI9NTNuvAdnvY0oWjFocgRvH6OVN6Inf1u22XUOchUHkY+vyeU+Bk0tXgiSo4s5wzWTEtZ+My96WqoT7o/Lm8VDr8BZMVpHJ0AgAA
```

An example for item config file that use ItemFormat save item method with Paper version of MythicTotem:

```yaml
material: DIAMOND
amount: 6
name: <blue>A good sword
lore:
- <gray>This is really nice!
custom-model-data: 1
max-stack: 6
food:
  nutrition: 5
  saturation: 5.0
tool:
  damage-per-block: 5
  mining-speed: 1.3
  rules:
  - STONE, 1.4, true
song: minecraft:otherside
glow: true
enchants:
  mending: 1
```

## Use saved item <a href="#use-saved-item" id="use-saved-item"></a>

You can use saved item in [ItemFormat](https://ultimateshop.superiormc.cn/format/itemformat-tm). In ItemFormat, there is a option called `material`, by default, you need type vanilla item ID there, but, you can also use saved item id to let plugin directly get the saved item instead of generate a whole new item with that type.

```yaml
display-item:
  material: superior_sword # If saved item id is 'superior_sword'
```

Saved items will be cached in memory continuously after loading to avoid repeatedly reading the saved item file, which may consume too much server performance. However, the cost is that if you have too many saved items, it may correspondingly consume more memory.
