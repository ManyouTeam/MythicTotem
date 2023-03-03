package cn.superiormc.mythictotem.configs;

import cn.superiormc.mythictotem.MythicTotem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages {

    public static String GetMessages(String textName){
        String textValue = MythicTotem.instance.getConfig().getString("messages." + textName);

        if (textValue == null)
            return "§x§9§8§F§B§9§8[MythicTotem] §cThere is something wrong in your config file!";
        else {
            textValue = HexColor(textValue);
            return textValue.replace("&", "§");
        }
    }

    public static String GetActionMessages(String textName){
        textName = HexColor(textName);
        return textName.replace("&", "§");
    }

    private static String HexColor(String textName) {
        Pattern hexColorReplace = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher hexColorCode = hexColorReplace.matcher(textName);
        while (hexColorCode.find()) {
            String s = hexColorCode.group();
            char code1 = s.charAt(2);
            char code2 = s.charAt(3);
            char code3 = s.charAt(4);
            char code4 = s.charAt(5);
            char code5 = s.charAt(6);
            char code6 = s.charAt(7);
            textName = textName.replace(s,"&x&" + code1 + "&" + code2 + "&" + code3 + "&" + code4 + "&" + code5 + "&" + code6);
        }
        return textName;
    }
}
