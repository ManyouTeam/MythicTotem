# ❓FAQ

## I have add new totems, but it does not work for me!

* Are you in creative mode? Plugin won't check creative players placed block by default, you can change this in `config.yml` file.

## I have purcahsed premium version, but prices system does not work for me!

* Are you using 2.6.0+ version? 2.5.x has some problems for it.
* Plugin won't enable price system by default, you can enable it at `config.yml` file.

## PlayerDropItemEvent with core block does not work well.

* If you set core block for a totem, player must stand on the center of the core block then drop the item onto the center of the block to active totem with PlayerDropItemEvent.
