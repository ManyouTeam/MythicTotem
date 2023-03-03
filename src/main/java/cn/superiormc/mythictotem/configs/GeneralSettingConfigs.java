package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;

public class GeneralSettingConfigs {

    public static boolean GetBlockPlaceEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.BlockPlaceEvent.enabled");
    }

    public static boolean GetBlockPlaceEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.BlockPlaceEvent.require-shift");
    }

    public static boolean GetPlayerInteractEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.PlayerInteractEvent.enabled");
    }

    public static boolean GetPlayerInteractEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.PlayerInteractEvent.require-shift");
    }

}
