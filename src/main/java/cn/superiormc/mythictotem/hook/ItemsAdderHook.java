package cn.superiormc.mythictotem.hook;

import cn.superiormc.mythictotem.MythicTotem;

public class ItemsAdderHook {

    public static boolean CheckLoad(){
        return MythicTotem.instance.getServer().getPluginManager().isPluginEnabled("ItemsAdder");
    }

}
