# Prices Option - Premium

Prices option is a little complex, so I introduce it on a separate page.

**Free version can not use this feature!**

**This option is optional, if you didn't add this, this means totem price is free.**

## Item Price

You can set item as price, should use Item Format at [this](https://ultimateshop.superiormc.cn/base/item-format) page.

**Example:**

```yaml
    1:
      material: APPLE
      name: 'Magic Apple'
      custom-model-data: 5
      amount: 10
```

## Item Match - Require MythicChanger <a href="#item-match-require-mythicchanger" id="item-match-require-mythicchanger"></a>

Item Match has those options:

* match-item: Determine the match rule of the price item.

For more info, please view [this page](../../features/custom-item-match-method.md).

**Example:**

```yaml
    1:
      match-item:
        items:
          - 'ender_pearl'
        has-name: true
```

## Hook Economy

Hook economy has those options:

* economy-plugin: What plugin you want this price economy hook into, for now, **MythicTotem** supports `Vault, GamePoints, PlayerPoints, CoinsEngine, UltraEconomy, EcoBits, RedisEconomy, PEconomy`.
* economy-type: If economy plugin is multi-currency economy plugin, you have to type currency name here.

**Example:**

```yaml
  1:
    economy-plugin: Vault
    # If you set Economy plugin to CoinsEngine, then:
    # economy-plugin: CoinsEngine
    # economy-type: Coin
    # Yeah, you need add economy-type option here because its a multi-currency plugin.
```

## Vanilla Economy

Vanilla economy has those options:

* economy-type: Supports `exp, levels`.

**Example:**

```yaml
  1:
    economy-type: levels
```

## Free

Just set `free: true` here.

## General Options

Those options can be used in the 4 types of price. **All of them are optional.**

* amount: The amount of items or economy price values. Like `1`.&#x20;
