package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;

public class CheckPluginLoad {

    public static boolean DoIt(String pluginName){
        return MythicTotem.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

}
