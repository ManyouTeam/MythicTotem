package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.api.TotemActivedEvent;
import cn.superiormc.mythictotem.utils.CommonUtil;
import dev.lone.itemsadder.api.CustomBlock;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ValidManager {

    private Block block;

    private Player player;

    private Event event;

    private ItemStack item;

    public ValidManager(EntityPlaceEvent event){
        this.event = event;
        this.block = event.getBlock();
        this.player = event.getPlayer();
        if (this.player != null) {
            this.item = this.player.getInventory().getItemInMainHand();
        } else {
            this.item = null;
        }
        CheckTotem();
    }

    public ValidManager(BlockPlaceEvent event){
        this.event = event;
        this.block = event.getBlockPlaced();
        this.player = event.getPlayer();
        this.item = event.getItemInHand();
        CheckTotem();
    }

    public ValidManager(PlayerInteractEvent event){
        if (event.getClickedBlock() == null) {
            return;
        }
        this.event = event;
        this.block = event.getClickedBlock();
        this.player = event.getPlayer();
        this.item = event.getItem();
        CheckTotem();
    }

    public ValidManager(BlockRedstoneEvent event){
        this.event = event;
        this.block = event.getBlock();
        this.player = null;
        this.item = null;
        CheckTotem();
    }

    public ValidManager(PlayerDropItemEvent event){
        this.event = event;
        this.block = event.getItemDrop().getLocation().subtract(new Vector(0, 1, 0)).getBlock();
        if (block.isEmpty() || block.getBoundingBox().getHeight() >= 1) {
            this.block = event.getItemDrop().getLocation().subtract(new Vector(0, 2, 0)).getBlock();
        }
        if (block.isEmpty()) {
            if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cSkipped becuase block is air!");
            }
            return;
        }
        this.player = event.getPlayer();
        this.item = event.getItemDrop().getItemStack();
        MythicTotem.getDroppedItems.add(event.getItemDrop());
        CheckTotem();
    }

    public void CheckTotem() {
        if (MythicTotem.getCheckingBlock.contains(block)) {
            if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eSkipped checking block!");
            }
            return;
        }
        List<PlacedBlockCheckManager> placedBlockCheckManagers = new ArrayList<>();
        MythicTotem.getCheckingBlock.add(block);
        // 处理 ItemsAdder 方块
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            if (CustomBlock.byAlreadyPlaced(block) != null &&
                    MythicTotem.getTotemMaterial.containsKey("itemsadder:" + CustomBlock.byAlreadyPlaced(block).getNamespacedID())) {
                placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("itemsadder:" + CustomBlock.byAlreadyPlaced(block).getNamespacedID());
            }
        }
        // 处理 Oraxen 方块
        if (placedBlockCheckManagers.isEmpty() && CommonUtil.checkPluginLoad("Oraxen") && OraxenBlocks.isOraxenBlock(block)) {
            NoteBlockMechanic noteBlockMechanic = OraxenBlocks.getNoteBlockMechanic(block);
            StringBlockMechanic stringBlockMechanic = OraxenBlocks.getStringMechanic(block);
            if (noteBlockMechanic != null && noteBlockMechanic.getItemID() != null &&
                (MythicTotem.getTotemMaterial.containsKey("oraxen:" + OraxenBlocks.getNoteBlockMechanic(block).getItemID()))){
                placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("oraxen:" + OraxenBlocks.getNoteBlockMechanic(block).getItemID());
            }
            else if (stringBlockMechanic != null && stringBlockMechanic.getItemID() != null &&
                (MythicTotem.getTotemMaterial.containsKey("oraxen:" + OraxenBlocks.getStringMechanic(block).getItemID()))) {
                placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("oraxen:" + OraxenBlocks.getStringMechanic(block).getItemID());
            }
        }
        // 处理 MMOItems 方块
        if (placedBlockCheckManagers.isEmpty() && CommonUtil.checkPluginLoad("MMOItems")) {
            if (MMOItems.plugin.getCustomBlocks().getFromBlock(block.getBlockData()).isPresent() &&
                    (MythicTotem.getTotemMaterial.containsKey("mmoitems:" + MMOItems.plugin.getCustomBlocks().
                            getFromBlock(block.getBlockData()).get().getId()))) {
                placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("mmoitems:" + MMOItems.plugin.getCustomBlocks().
                        getFromBlock(block.getBlockData()).get().getId());
            }
        }
        // 处理原版方块
        if (placedBlockCheckManagers.isEmpty() && MythicTotem.getTotemMaterial.containsKey("minecraft:" + block.getType().toString().toLowerCase())) {
            placedBlockCheckManagers = MythicTotem.getTotemMaterial.get("minecraft:" + block.getType().toString().toLowerCase());
        }
        if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eGet material: " + block.getType().toString().toLowerCase() + " - " + placedBlockCheckManagers);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §c-------------Checking Info-------------");
        }
        for (PlacedBlockCheckManager singleTotem : placedBlockCheckManagers) {
            // 条件
            ConditionManager conditionManager = new ConditionManager(singleTotem.GetTotemManager().getTotemCondition(),
                    this);
            if (!conditionManager.CheckCondition()) {
                if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eSkipped " + singleTotem.GetTotemManager().getTotemID() +
                            " because conditions not meet!");
                }
                continue;
            }
            // 价格
            boolean usePrice = !MythicTotem.freeVersion &&
                    MythicTotem.instance.getConfig().getBoolean("check-prices", true) &&
                    singleTotem.GetTotemManager().getSection().contains("prices");
            if (usePrice) {
                if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eChecking " + singleTotem.GetTotemManager().getTotemID() +
                            " prices...");
                }
                int i = 0;
                for (String singleSection : singleTotem.GetTotemManager().getSection().getConfigurationSection("prices").getKeys(false)) {
                    PriceManager priceManager = new PriceManager(singleTotem.GetTotemManager().getSection().getConfigurationSection("prices." + singleSection), player, block);
                    if (!singleTotem.GetTotemManager().getKeyMode()) {
                        item = null;
                        if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eSet item to null!");
                        }
                    }
                    if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eItem: " + item + "!");
                    }
                    if (!priceManager.CheckPrice(false, item)) {
                        i++;
                    }
                }
                if (i > 0) {
                    if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eSkipped " + singleTotem.GetTotemManager().getTotemID() +
                                " because prices not meet!");
                    }
                    continue;
                }
            }
            if (singleTotem.GetTotemManager().getCheckMode().equals("VERTICAL")) {
                if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eStarted " + singleTotem.GetTotemManager().getTotemID() +
                            " VERTICAL totem check!");
                }
                if (VerticalTotem(singleTotem)) {
                    if (event instanceof PlayerDropItemEvent && usePrice) {
                        Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                            ((PlayerDropItemEvent) event).getItemDrop().remove();
                        });
                    }
                    break;
                }
            } else {
                if (MythicTotem.instance.getConfig().getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §eStarted " + singleTotem.GetTotemManager().getTotemID() +
                            " HORIZONTAL totem check!");
                }
                if (HorizontalTotem(singleTotem)) {
                    if (event instanceof PlayerDropItemEvent && usePrice) {
                        Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
                            ((PlayerDropItemEvent) event).getItemDrop().remove();
                        });
                    }
                    break;
                }
            }
        }
        if (event instanceof PlayerDropItemEvent) {
            MythicTotem.getDroppedItems.remove(((PlayerDropItemEvent) event).getItemDrop());
        }
        MythicTotem.getCheckingBlock.remove(block);
    }
    private boolean VerticalTotem(PlacedBlockCheckManager singleTotem) {
        // 玩家放置的方块的坐标的偏移
        int offset_y = singleTotem.GetRow();
        int offset_x_or_z = singleTotem.GetColumn();
        // 初始坐标
        // 例如这个方块在某个图腾中在第一行第一列、第二列和第三列
        // 那么这里的 offset_y 和 offset_x_or_z 应该分别为 0，0 0，1 0，2
        // 初始坐标为第一行第一列的坐标，通过这个offset的值偏移到正确的初始坐标
        Location startLocation_1 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offset_y, block.getLocation().getZ() - offset_x_or_z);
        Location startLocation_2 = new Location(block.getWorld(), block.getLocation().getX(), block.getLocation().getY() + offset_y, block.getLocation().getZ() + offset_x_or_z);
        Location startLocation_3 = new Location(block.getWorld(), block.getLocation().getX() - offset_x_or_z, block.getLocation().getY() + offset_y, block.getLocation().getZ());
        Location startLocation_4 = new Location(block.getWorld(), block.getLocation().getX() + offset_x_or_z, block.getLocation().getY() + offset_y, block.getLocation().getZ());
        // 图腾的行列，例如 3 x 3 的图腾这两个值就分别是 3 和 3 了
        int base_row = singleTotem.GetTotemManager().getRealRow();
        int base_column = singleTotem.GetTotemManager().getRealColumn();
        // 这种带 None 的是空白方块数量
        // 可以通过这种空白方块配置不是矩形的图腾，空白方块所在位置不视为图腾的一部分
        int validXNoneBlockAmount1 = 0;
        int validXNoneBlockAmount2 = 0;
        int validZNoneBlockAmount1 = 0;
        int validZNoneBlockAmount2 = 0;
        // 存放实际方块摆放位置和图腾配置一致的 List
        List<Location> validXTotemBlockLocation1 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validXTotemBlockLocation2 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validZTotemBlockLocation1 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validZTotemBlockLocation2 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validXTotemEntity1 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validXTotemEntity2 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validZTotemEntity1 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validZTotemEntity2 = Collections.synchronizedList(new ArrayList<>());
        boolean checkXTrueOrFalse1 = true;
        boolean checkXTrueOrFalse2 = true;
        boolean checkZTrueOrFalse1 = true;
        boolean checkZTrueOrFalse2 = true;
        // 四种遍历规则
        for (int i = 0; i < base_row; i++) {
            for (int b = 0; b < base_column; b++) {
                Location nowLocation_1 = startLocation_1.clone().add(0, -i, b);
                if (!CommonUtil.checkProtection(player, nowLocation_1)) {
                    checkXTrueOrFalse1 = false;
                }
                Location nowLocation_2 = startLocation_2.clone().add(0, -i, -b);
                if (!CommonUtil.checkProtection(player, nowLocation_2)) {
                    checkXTrueOrFalse2 = false;
                }
                Location nowLocation_3 = startLocation_3.clone().add(b, -i, 0);
                if (!CommonUtil.checkProtection(player, nowLocation_3)) {
                    checkZTrueOrFalse1 = false;
                }
                Location nowLocation_4 = startLocation_4.clone().add(-b, -i, 0);
                if (!CommonUtil.checkProtection(player, nowLocation_4)) {
                    checkZTrueOrFalse2 = false;
                }
                String material = singleTotem.GetTotemManager().getRealMaterial(1, i, b);
                MaterialManager materialManager_1 = new MaterialManager(material, nowLocation_1, 1);
                MaterialManager materialManager_2 = new MaterialManager(material, nowLocation_2, 2);
                MaterialManager materialManager_3 = new MaterialManager(material, nowLocation_3, 3);
                MaterialManager materialManager_4 = new MaterialManager(material, nowLocation_4, 4);
                if (!checkXTrueOrFalse1 && !checkXTrueOrFalse2 && !checkZTrueOrFalse1 && !checkZTrueOrFalse2) {
                    return false;
                }
                //1
                if (checkXTrueOrFalse1 && materialManager_1.checkMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount1++;
                    } else {
                        validXTotemBlockLocation1.add(nowLocation_1);
                        if (materialManager_1.getEntityNeedRemove() != null) {
                          validXTotemEntity1.add(materialManager_1.getEntityNeedRemove());
                        }
                    }
                } else if (checkXTrueOrFalse1 && !materialManager_1.checkMaterial()) {
                    checkXTrueOrFalse1 = false;
                }
                //2
                if (checkXTrueOrFalse2 && materialManager_2.checkMaterial()) {
                    if (material.equals("none")) {
                        validXNoneBlockAmount2++;
                    } else {
                        validXTotemBlockLocation2.add(nowLocation_2);
                        if (materialManager_2.getEntityNeedRemove() != null) {
                            validXTotemEntity2.add(materialManager_2.getEntityNeedRemove());
                        }
                    }
                } else if (checkXTrueOrFalse2 && !materialManager_2.checkMaterial()) {
                    checkXTrueOrFalse2 = false;
                }
                //3
                if (checkZTrueOrFalse1 && materialManager_3.checkMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount1++;
                    } else {
                        validZTotemBlockLocation1.add(nowLocation_3);
                        if (materialManager_3.getEntityNeedRemove() != null) {
                            validZTotemEntity1.add(materialManager_3.getEntityNeedRemove());
                        }
                    }
                } else if (checkZTrueOrFalse1 && !materialManager_3.checkMaterial()) {
                    checkZTrueOrFalse1 = false;
                }
                //4
                if (checkZTrueOrFalse2 && materialManager_4.checkMaterial()) {
                    if (material.equals("none")) {
                        validZNoneBlockAmount2++;
                    } else {
                        validZTotemBlockLocation2.add(nowLocation_4);
                        if (materialManager_4.getEntityNeedRemove() != null) {
                            validZTotemEntity2.add(materialManager_4.getEntityNeedRemove());
                        }
                    }
                } else if (checkZTrueOrFalse2 && !materialManager_4.checkMaterial()) {
                    checkZTrueOrFalse2 = false;
                }
                // 条件满足
                if (validXTotemBlockLocation1.size() == (base_row * base_column - validXNoneBlockAmount1)) {
                    AfterCheck(singleTotem, startLocation_1, validXTotemBlockLocation1, validXTotemEntity1);
                    return true;
                } else if (validXTotemBlockLocation2.size() == (base_row * base_column - validXNoneBlockAmount2)) {
                    AfterCheck(singleTotem, startLocation_2, validXTotemBlockLocation2, validXTotemEntity2);
                    return true;
                } else if (validZTotemBlockLocation1.size() == (base_row * base_column - validZNoneBlockAmount1)) {
                    AfterCheck(singleTotem, startLocation_3, validZTotemBlockLocation1, validZTotemEntity1);
                    return true;
                } else if (validZTotemBlockLocation2.size() == (base_row * base_column - validZNoneBlockAmount2)) {
                    AfterCheck(singleTotem, startLocation_4, validZTotemBlockLocation2, validZTotemEntity2);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean HorizontalTotem(PlacedBlockCheckManager singleTotem) {
        // 玩家放置的方块的坐标的偏移
        int offset_row = singleTotem.GetRow();
        int offset_column = singleTotem.GetColumn();
        int offset_layer = singleTotem.GetLayer();
        if (MythicTotem.instance.getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cChecking: " + offset_row
                    + " - " +  offset_column
                    + " - " +  offset_layer + "!");
        }
        // 初始坐标
        // 例如这个方块在某个图腾中在第一行第一列、第二列和第三列
        // 那么这里的 offset_y 和 offset_x_or_z 应该分别为 0，0 0，1 0，2
        // 初始坐标为第一行第一列的坐标，通过这个offset的值偏移到正确的初始坐标
        Location startLocation_1 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_row);
        Location startLocation_2 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_row);
        Location startLocation_3 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_row);
        Location startLocation_4 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_column,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_row);
        Location startLocation_5 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_column);
        Location startLocation_6 = new Location(block.getWorld(),
                block.getLocation().getX() + offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_column);
        Location startLocation_7 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() + offset_column);
        Location startLocation_8 = new Location(block.getWorld(),
                block.getLocation().getX() - offset_row,
                block.getLocation().getY() + offset_layer - 1,
                block.getLocation().getZ() - offset_column);
        // 图腾的行列，例如 3 x 3 的图腾这两个值就分别是 3 和 3 了
        int base_row = singleTotem.GetTotemManager().getRealRow();
        int base_column = singleTotem.GetTotemManager().getRealColumn();
        int base_layer = singleTotem.GetTotemManager().getTotemLayer();
        // 这种带 None 的是空白方块数量
        // 可以通过这种空白方块配置不是矩形的图腾，空白方块所在位置不视为图腾的一部分
        int validNoneBlockAmount1 = 0;
        int validNoneBlockAmount2 = 0;
        int validNoneBlockAmount3 = 0;
        int validNoneBlockAmount4 = 0;
        int validNoneBlockAmount5 = 0;
        int validNoneBlockAmount6 = 0;
        int validNoneBlockAmount7 = 0;
        int validNoneBlockAmount8 = 0;
        // 存放实际方块摆放位置和图腾配置一致的 List
        List<Location> validTotemBlockLocation1 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation2 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation3 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation4 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation5 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation6 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation7 = Collections.synchronizedList(new ArrayList<>());
        List<Location> validTotemBlockLocation8 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity1 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity2 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity3 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity4 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity5 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity6 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity7 = Collections.synchronizedList(new ArrayList<>());
        List<Entity> validTotemEntity8 = Collections.synchronizedList(new ArrayList<>());
        boolean checkTrueOrFalse1 = true;
        boolean checkTrueOrFalse2 = true;
        boolean checkTrueOrFalse3 = true;
        boolean checkTrueOrFalse4 = true;
        boolean checkTrueOrFalse5 = true;
        boolean checkTrueOrFalse6 = true;
        boolean checkTrueOrFalse7 = true;
        boolean checkTrueOrFalse8 = true;
        // 八种遍历规则
        for (int a = 1; a <= base_layer ; a++) {
            for (int i = 0; i < base_row; i++) {
                for (int b = 0; b < base_column; b++) {
                    Location nowLocation_1 = startLocation_1.clone().add(-b, 1 - a, -i);
                    if (!CommonUtil.checkProtection(player, nowLocation_1)) {
                        checkTrueOrFalse1 = false;
                    }
                    Location nowLocation_2 = startLocation_2.clone().add(-b, 1 - a, i);
                    if (!CommonUtil.checkProtection(player, nowLocation_2)) {
                        checkTrueOrFalse2 = false;
                    }
                    Location nowLocation_3 = startLocation_3.clone().add(b, 1 - a, -i);
                    if (!CommonUtil.checkProtection(player, nowLocation_3)) {
                        checkTrueOrFalse3 = false;
                    }
                    Location nowLocation_4 = startLocation_4.clone().add(b, 1 - a, i);
                    if (!CommonUtil.checkProtection(player, nowLocation_4)) {
                        checkTrueOrFalse4 = false;
                    }
                    Location nowLocation_5 = startLocation_5.clone().add(-i, 1 - a, -b);
                    if (!CommonUtil.checkProtection(player, nowLocation_5)) {
                        checkTrueOrFalse5 = false;
                    }
                    Location nowLocation_6 = startLocation_6.clone().add(-i, 1 - a, b);
                    if (!CommonUtil.checkProtection(player, nowLocation_6)) {
                        checkTrueOrFalse6 = false;
                    }
                    Location nowLocation_7 = startLocation_7.clone().add(i, 1 - a, -b);
                    if (!CommonUtil.checkProtection(player, nowLocation_7)) {
                        checkTrueOrFalse7 = false;
                    }
                    Location nowLocation_8 = startLocation_8.clone().add(i, 1 - a, b);
                    if (!CommonUtil.checkProtection(player, nowLocation_8)) {
                        checkTrueOrFalse8 = false;
                    }
                    String material = singleTotem.GetTotemManager().getRealMaterial(a, i, b);
                    if (MythicTotem.instance.getConfig().getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cMaterial should be: " + material);
                    }
                    //1
                    MaterialManager materialManager_1 = new MaterialManager(material, nowLocation_1, 1);
                    MaterialManager materialManager_2 = new MaterialManager(material, nowLocation_2, 2);
                    MaterialManager materialManager_3 = new MaterialManager(material, nowLocation_3, 3);
                    MaterialManager materialManager_4 = new MaterialManager(material, nowLocation_4, 4);
                    MaterialManager materialManager_5 = new MaterialManager(material, nowLocation_5, 5);
                    MaterialManager materialManager_6 = new MaterialManager(material, nowLocation_6, 6);
                    MaterialManager materialManager_7 = new MaterialManager(material, nowLocation_7, 7);
                    MaterialManager materialManager_8 = new MaterialManager(material, nowLocation_8, 8);
                    if (!checkTrueOrFalse1 && !checkTrueOrFalse2 && !checkTrueOrFalse3 && !checkTrueOrFalse4 &&
                            !checkTrueOrFalse5 && !checkTrueOrFalse6 && !checkTrueOrFalse7 && !checkTrueOrFalse8) {
                        return false;
                    }
                    if (checkTrueOrFalse1 && materialManager_1.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount1++;
                        } else {
                            validTotemBlockLocation1.add(nowLocation_1);
                            if (materialManager_1.getEntityNeedRemove() != null) {
                                validTotemEntity1.add(materialManager_1.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse1 && !materialManager_1.checkMaterial()) {
                        checkTrueOrFalse1 = false;
                    }
                    //2
                    if (checkTrueOrFalse2 && materialManager_2.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount2++;
                        } else {
                            validTotemBlockLocation2.add(nowLocation_2);
                            if (materialManager_2.getEntityNeedRemove() != null) {
                                validTotemEntity2.add(materialManager_2.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse2 && !materialManager_2.checkMaterial()) {
                        checkTrueOrFalse2 = false;
                    }
                    //3
                    if (checkTrueOrFalse3 && materialManager_3.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount3++;
                        } else {
                            validTotemBlockLocation3.add(nowLocation_3);
                            if (materialManager_3.getEntityNeedRemove() != null) {
                                validTotemEntity3.add(materialManager_3.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse3 && !materialManager_3.checkMaterial()) {
                        checkTrueOrFalse3 = false;
                    }
                    //4
                    if (checkTrueOrFalse4 && materialManager_4.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount4++;
                        } else {
                            validTotemBlockLocation4.add(nowLocation_4);
                            if (materialManager_4.getEntityNeedRemove() != null) {
                                validTotemEntity4.add(materialManager_4.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse4 && !materialManager_4.checkMaterial()) {
                        checkTrueOrFalse4 = false;
                    }
                    //5
                    if (checkTrueOrFalse5 && materialManager_5.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount5++;
                        } else {
                            validTotemBlockLocation5.add(nowLocation_5);
                            if (materialManager_5.getEntityNeedRemove() != null) {
                                validTotemEntity5.add(materialManager_5.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse5 && !materialManager_5.checkMaterial()) {
                        checkTrueOrFalse5 = false;
                    }
                    //6
                    if (checkTrueOrFalse6 && materialManager_6.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount6++;
                        } else {
                            validTotemBlockLocation6.add(nowLocation_6);
                            if (materialManager_6.getEntityNeedRemove() != null) {
                                validTotemEntity6.add(materialManager_6.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse6 && !materialManager_6.checkMaterial()) {
                        checkTrueOrFalse6 = false;
                    }
                    //7
                    if (checkTrueOrFalse7 && materialManager_7.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount7++;
                        } else {
                            validTotemBlockLocation7.add(nowLocation_7);
                            if (materialManager_7.getEntityNeedRemove() != null) {
                                validTotemEntity7.add(materialManager_7.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse7 && !materialManager_7.checkMaterial()) {
                        checkTrueOrFalse7 = false;
                    }
                    //8
                    if (checkTrueOrFalse8 && materialManager_8.checkMaterial()) {
                        if (material.equals("none")) {
                            validNoneBlockAmount8++;
                        } else {
                            validTotemBlockLocation8.add(nowLocation_8);
                            if (materialManager_8.getEntityNeedRemove() != null) {
                                validTotemEntity8.add(materialManager_8.getEntityNeedRemove());
                            }
                        }
                    } else if (checkTrueOrFalse8 && !materialManager_8.checkMaterial()) {
                        checkTrueOrFalse8 = false;
                    }
                    // 条件满足
                    if (validTotemBlockLocation1.size() == (base_row * base_column) * base_layer - validNoneBlockAmount1) {
                        AfterCheck(singleTotem, startLocation_1, validTotemBlockLocation1, validTotemEntity1);
                        return true;
                    } else if (validTotemBlockLocation2.size() == (base_row * base_column) * base_layer - validNoneBlockAmount2) {
                        AfterCheck(singleTotem, startLocation_2, validTotemBlockLocation2, validTotemEntity2);
                        return true;
                    } else if (validTotemBlockLocation3.size() == (base_row * base_column) * base_layer - validNoneBlockAmount3) {
                        AfterCheck(singleTotem, startLocation_3, validTotemBlockLocation3, validTotemEntity3);
                        return true;
                    } else if (validTotemBlockLocation4.size() == (base_row * base_column) * base_layer - validNoneBlockAmount4) {
                        AfterCheck(singleTotem, startLocation_4, validTotemBlockLocation4, validTotemEntity4);
                        return true;
                    } else if (validTotemBlockLocation5.size() == (base_row * base_column) * base_layer - validNoneBlockAmount5) {
                        AfterCheck(singleTotem, startLocation_5, validTotemBlockLocation5, validTotemEntity5);
                        return true;
                    } else if (validTotemBlockLocation6.size() == (base_row * base_column) * base_layer - validNoneBlockAmount6) {
                        AfterCheck(singleTotem, startLocation_6, validTotemBlockLocation6, validTotemEntity6);
                        return true;
                    } else if (validTotemBlockLocation7.size() == (base_row * base_column) * base_layer - validNoneBlockAmount7) {
                        AfterCheck(singleTotem, startLocation_7, validTotemBlockLocation7, validTotemEntity7);
                        return true;
                    } else if (validTotemBlockLocation8.size() == (base_row * base_column) * base_layer - validNoneBlockAmount8) {
                        AfterCheck(singleTotem, startLocation_8, validTotemBlockLocation8, validTotemEntity8);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void AfterCheck(PlacedBlockCheckManager singleTotem,
                            Location startLocation,
                            List<Location> validTotemBlockLocation,
                            Collection<Entity> needRemoveEntities) {
        MythicTotem.getCheckingBlock.remove(block);
        if (!MythicTotem.freeVersion &&
                MythicTotem.instance.getConfig().getBoolean("check-prices", true) &&
                singleTotem.GetTotemManager().getSection().contains("prices")) {
            for (String singleSection : singleTotem.GetTotemManager().getSection().getConfigurationSection("prices").getKeys(false)) {
                PriceManager priceManager = new PriceManager(singleTotem.GetTotemManager().getSection().getConfigurationSection("prices." + singleSection),
                        player,
                        block);
                if (!singleTotem.GetTotemManager().getKeyMode()) {
                    item = null;
                }
                priceManager.CheckPrice(true, item);
            }
        }
        Bukkit.getScheduler().runTask(MythicTotem.instance, () -> {
            if (singleTotem.GetTotemManager().getTotemDisappear()) {
                for (Location loc : validTotemBlockLocation) {
                    CommonUtil.removeBlock(player, loc);
                }
            }
            if (event instanceof EntityPlaceEvent) {
                ((EntityPlaceEvent) event).getEntity().remove();
            }
            for (Entity singleEntity : needRemoveEntities) {
                singleEntity.remove();
            }
            ActionManager actionManager = new ActionManager(startLocation, singleTotem, singleTotem.GetTotemManager().getTotemAction(), this);
            actionManager.CheckAction();
            TotemActivedEvent totemActivedEvent = new TotemActivedEvent(
                    singleTotem.GetTotemManager().getTotemID(),
                    this.player,
                    this.block.getLocation());
            Bukkit.getPluginManager().callEvent(totemActivedEvent);
        });
    }

    public Player getPlayer() {
        return player;
    }

    public String getEvent() {
        return event.getEventName();
    }

    public ItemStack getItem() {
        return item;
    }

    public Block getBlock() {
        return block;
    }

}
