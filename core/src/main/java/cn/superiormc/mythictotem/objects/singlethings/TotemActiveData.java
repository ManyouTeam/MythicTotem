package cn.superiormc.mythictotem.objects.singlethings;

import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;

public class TotemActiveData extends AbstractThingData {

    public Location startLocation;

    public ObjectCheck check;

    public ObjectPlaceCheck totem;

    private final Location centerLocation;

    public TotemActiveData(Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        this(startLocation, check, totem, null);
    }

    public TotemActiveData(Location startLocation, ObjectCheck check, ObjectPlaceCheck totem, Location oppositeCorner) {
        this.startLocation = startLocation;
        this.check = check;
        this.totem = totem;
        this.centerLocation = oppositeCorner == null ? null : startLocation.clone().add(oppositeCorner)
                .multiply(0.5).add(0.5, 0.5, 0.5);
    }

    public Location getCenterLocation() {
        return centerLocation == null ? null : centerLocation.clone();
    }

}
