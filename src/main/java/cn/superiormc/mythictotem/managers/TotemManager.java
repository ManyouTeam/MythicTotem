package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static cn.superiormc.mythictotem.MythicTotem.SetErrorValue;

public class TotemManager {

    // 图腾的行和列
    private int totemRow;

    private int totemColumn;

    private int totemLayer;

    // 图腾各处的方块
    // 行列，Material
    private final Map<String, String> totemLocationMaterial = new HashMap<>();

    private final List<String> totemAction;

    private final List<String> totemCondition;

    private final boolean totemDisappear;

    private final String totemCheckMode;

    private ConfigurationSection totemSection;

    public TotemManager(ConfigurationSection section){
        this.totemDisappear = section.getBoolean("disappear");
        this.totemAction = section.getStringList("actions");
        this.totemCondition = section.getStringList("conditions");
        this.totemCheckMode = section.getString("mode", "VERTICAL").toUpperCase();
        this.totemSection = section;
        ConfigurationSection totemLayoutsExplainConfig = section.getConfigurationSection("explains");
        Set<String> totemLayoutsExplainList = totemLayoutsExplainConfig.getKeys(false);
        Map<String, String> totemLayoutsExplain = new HashMap<>();
        for (String totemLayoutsChar : totemLayoutsExplainList) {
            if (totemLayoutsChar.length() > 1) {
                SetErrorValue();
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Totem layout explain config keys must be a char, like A.");
                return;
            }
            String totemLayoutsMaterial = totemLayoutsExplainConfig.getString(totemLayoutsChar).toLowerCase();
            totemLayoutsExplain.put(totemLayoutsChar, totemLayoutsMaterial);
        }
        if (!(section.getConfigurationSection("layouts") == null)) {
            Map<Integer, List<String>> totemLayouts = new HashMap<>();
            for (int i = 1 ; i <= section.getConfigurationSection("layouts").getKeys(false).size() ; i++) {
                if (!section.getStringList("layouts." + i).isEmpty()) {
                    totemLayouts.put(i, section.getStringList("layouts." + i));
                    this.totemLayer = i;
                } else {
                    break;
                }
            }
            for (int i = 1 ; i <= totemLayouts.keySet().size(); i++) {
                this.totemRow = 0;
                for (String s : totemLayouts.get(i)) {
                    for (this.totemColumn = 0; this.totemColumn < s.length(); this.totemColumn++) {
                        char realChar = s.charAt(this.totemColumn);
                        // realString 代表对应 layout 中对应 realChar 字节的 material
                        String realString = totemLayoutsExplain.get(String.valueOf(realChar));
                        // 放置到方块表中
                        // 插件的方块表是玩家放置方块时查询这个方块是否是图腾方块一部分使用的
                        if (MythicTotem.getTotemMaterial.containsKey(realString)) {
                            MythicTotem.getTotemMaterial.get(realString).add(new PlacedBlockCheckManager(this, totemRow, totemColumn));
                        } else {
                            List<PlacedBlockCheckManager> placedBlockCheckManagers = new ArrayList<>();
                            placedBlockCheckManagers.add(new PlacedBlockCheckManager(this, totemRow, totemColumn));
                            MythicTotem.getTotemMaterial.put(realString, placedBlockCheckManagers);
                        }
                        this.totemLocationMaterial.put(GenerateID(i, totemRow, totemColumn), realString);
                    }
                    this.totemRow++;
                }
            }
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fLoaded 3D Totem: " + section.getName() +
                    "with size: " + totemRow + "x" + totemColumn);
        }
        else {
            List<String> totemLayout = section.getStringList("layout");
            this.totemRow = 0;
            for (String s : totemLayout){
                for (this.totemColumn = 0 ; this.totemColumn < s.length() ; this.totemColumn++){
                    char realChar = s.charAt(this.totemColumn);
                    // realString 代表对应 layout 中对应 realChar 字节的 material
                    String realString = totemLayoutsExplain.get(String.valueOf(realChar));
                    // 放置到方块表中
                    // 插件的方块表是玩家放置方块时查询这个方块是否是图腾方块一部分使用的
                    if (MythicTotem.getTotemMaterial.containsKey(realString)){
                        MythicTotem.getTotemMaterial.get(realString).add(new PlacedBlockCheckManager(this, totemRow, totemColumn));
                    }
                    else{
                        List<PlacedBlockCheckManager> placedBlockCheckManagers = new ArrayList<>();
                        placedBlockCheckManagers.add(new PlacedBlockCheckManager(this, totemRow, totemColumn));
                        MythicTotem.getTotemMaterial.put(realString, placedBlockCheckManagers);
                    }
                    this.totemLocationMaterial.put(GenerateID(1, totemRow, totemColumn), realString);
                }
                this.totemRow++;
            }
            this.totemLayer = 1;
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fLoaded 2D Totem: " + section.getName() +
                    "with size: " + totemRow + "x" + totemColumn);
        }
    }

    private String GenerateID(int layer, int raw, int column){
        return layer + ";;" + raw + ";;" + column;
    }

    public int GetRealRow(){
        return this.totemRow;
    }

    public int GetRealColumn(){
        return this.totemColumn;
    }

    public String GetRealMaterial(int layer, int raw, int column){
        return totemLocationMaterial.get(layer + ";;" + raw + ";;" + column);
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

    public String GetCheckMode() {
        return this.totemCheckMode;
    }

    public int GetTotemLayer() {
        return this.totemLayer;
    }
}
