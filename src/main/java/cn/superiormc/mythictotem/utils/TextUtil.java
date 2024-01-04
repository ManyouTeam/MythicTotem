package cn.superiormc.mythictotem.utils;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    public static String parse(String text) {
        return ColorParser.parse(text);
    }

    public static List<String> getListWithColor(List<String> inList) {
        List<String> resultList = new ArrayList<>();
        for (String s : inList) {
            resultList.add(TextUtil.parse(s));
        }
        return resultList;
    }
}
