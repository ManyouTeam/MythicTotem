# ⚖️Condition Format

The condition format will consist of several options.

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

## World <a href="#world" id="world"></a>

Player must be in the world.

<pre class="language-yaml"><code class="lang-yaml"><strong>conditions:
</strong>  1:
    type: world
    world: lobby
</code></pre>

## Biome

Player must be in the biome.

```yaml
conditions:
  1:
    type: biome
    biome: oraxen
```

## Permission

Player must has the permission.

**Remember that OP players will always have all permissions unless plugin set it not by default, so if you want to test this condition, you have to deop yourself.**

```yaml
conditions:
  1:
    type: permission
    permission: 'group.vip'
```

## Placeholder

Player must be meet the placeholder condition.

Rule can be set to:

* \>=
* <=
* \>
* <
* \== (String)
* \= (Number)
* != (Number or string)
* !\*= (Number or string) Not contains.
* \*= (String) Contains, for example, str \*= string is true, but example \*= ple is false.

```yaml
conditions:
  1:
    type: placeholder
    placeholder: '%player_health%'
    rule: '<='
    value: 5
```

## Trigger <a href="#trigger" id="trigger"></a>

This totem can only be actived with specified trigger. For available trigger event, please view `config.yml` file.

(Added in 2.5.2)

```yaml
conditions:
  1:
    type: trigger
    event: 'PlayerInteractEvent' 
```

## Trigger Item <a href="#trigger-item" id="trigger-item"></a>

This totem can only be actived with specified item.

```yaml
conditions:
  1:
    type: trigger_item
    item: 
      material: 'stone' # Use Item Format
```

## Near Mobs <mark style="color:red;">- Premium</mark> <a href="#near-mobs-premium-version-only" id="near-mobs-premium-version-only"></a>

If there is no corresponding mob within a nearby distance, the condition can be met. It supports both the vanilla mob ID and MythicMobs mob ID.

```yaml
conditions:
  1:
    type: mobs_near
    entity: SkeletonKing
    distance: 50
```

Do not use this condition in many totems and do not make distance too far otherwise this maybe lead to server lag.

## Any <mark style="color:red;">- Premium</mark>

```yaml
conditions:
  1:
    type: any
    conditions:
      1:
        type: placeholder
        placeholder: '%eco_balance%'
        rule: '>='
        value: 200
      2:
        type: placeholder
        placeholder: '%player_points%'
        rule: '>='
        value: 400
```

## Not <mark style="color:red;">- Premium</mark>

```yaml
conditions:
  1:
    type: not
    conditions:
      1:
        type: placeholder
        placeholder: '%eco_balance%'
        rule: '>='
        value: 200
```

