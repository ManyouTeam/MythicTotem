package cn.superiormc.mythictotem.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TotemActivedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final Location location;

    private final String totemID;

    public TotemActivedEvent(String totemID, Player player, Location location) {
        this.player = player;
        this.location = location;
        this.totemID = totemID;
    }

    public Player GetPlayer() {
        return player;
    }

    public Location GetLocation() {
        return location;
    }

    public String GetTotemID() {
        return totemID;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
