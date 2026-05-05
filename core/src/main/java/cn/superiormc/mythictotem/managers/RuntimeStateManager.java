package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeStateManager {

    public static RuntimeStateManager runtimeStateManager;

    private final Map<Location, ObjectCheck> blocks = new ConcurrentHashMap<>();

    private final List<Item> droppedItems = Collections.synchronizedList(new ArrayList<>());

    private final Map<UUID, SchedulerUtil> playerCooldownTasks = new ConcurrentHashMap<>();

    public RuntimeStateManager() {
        runtimeStateManager = this;
    }

    public boolean isCheckingBlock(Location location, ObjectCheck check) {
        return blocks.containsKey(location) && !blocks.get(location).equals(check);
    }

    public void markCheckingBlock(Location location, ObjectCheck check) {
        blocks.put(location, check);
        SchedulerUtil.runTaskLater(() -> blocks.remove(location), 2L);
    }

    public boolean isDroppedItem(Item item) {
        return droppedItems.contains(item);
    }

    public void addDroppedItem(Item item) {
        droppedItems.add(item);
    }

    public void removeDroppedItem(Item item) {
        droppedItems.remove(item);
    }

    public boolean isPlayerCoolingDown(Player player) {
        return playerCooldownTasks.containsKey(player.getUniqueId());
    }

    public void startPlayerCooldown(Player player) {
        long time = ConfigManager.configManager.getLong("cooldown-tick", 5L);
        if (time <= 0) {
            return;
        }
        UUID uuid = player.getUniqueId();
        SchedulerUtil previousTask = playerCooldownTasks.remove(uuid);
        if (previousTask != null) {
            previousTask.cancel();
        }

        SchedulerUtil task = SchedulerUtil.runTaskLater(() -> playerCooldownTasks.remove(uuid), time);
        playerCooldownTasks.put(uuid, task);
    }
}
