package cn.superiormc.mythictotem.managers;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TotemDebugManager {

    public static TotemDebugManager manager;

    private final Map<UUID, String> debuggingTotems = new ConcurrentHashMap<>();

    public TotemDebugManager() {
        manager = this;
    }

    public void startDebug(Player player, String totemId) {
        debuggingTotems.put(player.getUniqueId(), totemId);
    }

    public void stopDebug(Player player) {
        debuggingTotems.remove(player.getUniqueId());
    }

    public boolean isDebugging(Player player, String totemId) {
        if (player == null || totemId == null) {
            return false;
        }
        String watchingTotem = debuggingTotems.get(player.getUniqueId());
        return totemId.equalsIgnoreCase(watchingTotem);
    }

    public String getWatchingTotem(Player player) {
        if (player == null) {
            return null;
        }
        return debuggingTotems.get(player.getUniqueId());
    }
}
