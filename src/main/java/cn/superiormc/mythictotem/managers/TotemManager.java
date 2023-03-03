package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotemManager {

    // 图腾的行和列
    private int totemRow;

    private int totemColumn;

    // 图腾各处的方块
    // 行列，Material
    private final Map<String, String> totemLocationMaterial = new HashMap<>();

    private final List<String> totemAction;

    private final List<String> totemCondition;

    private final boolean totemDisappear;

    public TotemManager(boolean totemDisappear, List<String> totemLayout, List<String> totemAction, List<String> totemCondition, Map<String, String> totemLayoutsExplain){
        this.totemDisappear = totemDisappear;
        this.totemAction = totemAction;
        this.totemCondition = totemCondition;
        this.totemRow = 0;
        for(String s : totemLayout){
            for(this.totemColumn = 0 ; this.totemColumn < s.length() ; this.totemColumn++){
                char realChar = s.charAt(this.totemColumn);
                // realString 代表对应 layout 中对应 realChar 字节的 material
                String realString = totemLayoutsExplain.get(String.valueOf(realChar));
                // 放置到方块表中
                // 插件的方块表是玩家放置方块时查询这个方块是否是图腾方块一部分使用的
                if(MythicTotem.getTotemMaterial.containsKey(realString)){
                    MythicTotem.getTotemMaterial.get(realString).add(new PlacedBlockCheckManager(this, totemRow, totemColumn));
                }
                else{
                    List<PlacedBlockCheckManager> placedBlockCheckManagers = new ArrayList<>();
                    placedBlockCheckManagers.add(new PlacedBlockCheckManager(this, totemRow, totemColumn));
                    MythicTotem.getTotemMaterial.put(realString, placedBlockCheckManagers);
                }
                this.totemLocationMaterial.put(GenerateID(totemRow, totemColumn), realString);
            }
            this.totemRow++;
        }
    }

    private String GenerateID(int raw, int column){
        return raw + ";;" + column;
    }

    public int GetRealRow(){
        return this.totemRow;
    }

    public int GetRealColumn(){
        return this.totemColumn;
    }

    public String GetRealMaterial(int raw, int column){
        return totemLocationMaterial.get(raw + ";;" + column);
    }

    public List<String> GetTotemAction(){
        return this.totemAction;
    }

    public List<String> GetTotemCondition(){
        return this.totemCondition;
    }

    public boolean GetTotemDisappear(){
        return this.totemDisappear;
    }
}
