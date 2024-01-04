package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;

public class GeneralSettingConfigs {

    public static boolean GetEntityPlaceEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.EntityPlaceEvent.enabled", true);
    }

    public static boolean GetEntityPlaceEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.EntityPlaceEvent.require-shift", false);
    }

    public static boolean GetEntityPlaceEventBlackCreative(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.EntityPlaceEvent.black-creative-mode", false);
    }

    public static boolean GetBlockPlaceEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.BlockPlaceEvent.enabled", true);
    }

    public static boolean GetBlockPlaceEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.BlockPlaceEvent.require-shift", false);
    }

    public static boolean GetPlayerPlaceEventBlackCreative(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.BlockPlaceEvent.black-creative-mode", true);
    }

    public static boolean GetPlayerInteractEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.PlayerInteractEvent.enabled", true);
    }

    public static boolean GetPlayerInteractEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.PlayerInteractEvent.require-shift", true);
    }

    public static boolean GetPlayerInteractEventBlackCreative(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.PlayerInteractEvent.black-creative-mode", true);
    }

    public static boolean GetPlayerDropEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.PlayerDropItemEvent.enabled", true);
    }

    public static boolean GetPlayerDropEventRequireShift(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.PlayerDropItemEvent.require-shift", true);
    }

    public static boolean GetPlayerDropEventBlackCreative(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.PlayerDropItemEvent.black-creative-mode", true);
    }

    public static boolean GetBlockRedstoneEventEnabled(){
        return MythicTotem.instance.getConfig().getBoolean("trigger.BlockRedstoneEvent.enabled", true);
    }

    public static boolean GetBlockBreakEventCancel(){
        return MythicTotem.instance.getConfig().getBoolean("disappear.BlockBreakEvent.enabled", false);
    }

    public static boolean GetBlockDamageEventCancel(){
        return MythicTotem.instance.getConfig().getBoolean("disappear.BlockDamageEvent.enabled", false);
    }

    public static boolean GetRegisterLibreforge(){
        return MythicTotem.instance.getConfig().getBoolean("register-libreforge", false);
    }

}
