package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;

public class GeneralSettingConfigs {

    public static boolean GetBlockPlaceEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.BlockPlaceEvent.enabled", true);
    }

    public static boolean GetBlockPlaceEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.BlockPlaceEvent.require-shift", false);
    }

    public static boolean GetPlayerPlaceEventBlackCreative(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.PlayerInteractEvent.black-creative-mode", true);
    }

    public static boolean GetPlayerInteractEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.PlayerInteractEvent.enabled", true);
    }


    public static boolean GetPlayerInteractEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.PlayerInteractEvent.require-shift", true);
    }

    public static boolean GetPlayerInteractEventBlackCreative(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.PlayerInteractEvent.black-creative-mode", true);
    }

    public static boolean GetBlockRedstoneEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("settings.trigger.BlockRedstoneEvent.enabled", true);
    }

    public static boolean GetBlockBreakEventCancel(){
        return MythicTotem.instance.getConfig().getBoolean("settings.disappear.BlockBreakEvent.enabled", false);
    }

    public static boolean GetBlockDamageEventCancel(){
        return MythicTotem.instance.getConfig().getBoolean("settings.disappear.BlockDamageEvent.enabled", false);
    }

}
