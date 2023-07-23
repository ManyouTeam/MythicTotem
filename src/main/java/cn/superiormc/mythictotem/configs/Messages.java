package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.ColorParser;

public class Messages {

    public static String GetMessages(String textName){
        String textValue = MythicTotem.instance.getConfig().getString("messages." + textName);
        if (textValue == null)
            return "§x§9§8§F§B§9§8[MythicTotem] §cThere is something wrong in your message config!";
        else {
            textValue = ColorParser.parse(textValue);
            return textValue;
        }
    }
}
