package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
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

    private final List<String> totemCoreBlocks;

    private final boolean totemDisappear;

    private final String totemCheckMode;

    private ConfigurationSection totemSection;

    public TotemManager(String id, YamlConfiguration section){
        this.totemDisappear = section.getBoolean("disappear", true);
        this.totemAction = section.getStringList("actions");
        this.totemCondition = section.getStringList("conditions");
        this.totemCheckMode = section.getString("mode", "VERTICAL").toUpperCase();
        this.totemCoreBlocks = section.getStringList("core-blocks");
        this.totemSection = section;
        ConfigurationSection totemLayoutsExplainConfig = section.getConfigurationSection("explains");
        if (totemLayoutsExplainConfig == null) {
            MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not found any explains option in totem: " + section.getName() + ".");
            return;
        }
        Set<String> totemLayoutsExplainList = totemLayoutsExplainConfig.getKeys(false);
        Map<String, String> totemLayoutsExplain = new HashMap<>();
        for (String totemLayoutsChar : totemLayoutsExplainList) {
            if (totemLayoutsChar.length() > 1) {
                MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Totem " + section.getName() + "'s layout explain config keys must be a char, like A.");
                return;
            }
            String totemLayoutsMaterial = totemLayoutsExplainConfig.getString(totemLayoutsChar).toLowerCase();
            totemLayoutsExplain.put(totemLayoutsChar, totemLayoutsMaterial);
        }
        if (!(section.getConfigurationSection("layouts") == null)) {
            MythicTotem.threeDtotemAmount++;
            if (MythicTotem.freeVersion && MythicTotem.threeDtotemAmount > 3) {
                MythicTotem.checkError("§x§9§8§F§B§9§8[MythicTotem] §cError: Free version" +
                        " can only create up to 3 3D totems, but your totem configs have more then 3 3D totems, please" +
                        " remove, otherwise plugin won't check 3D totems!");
                return;
            }
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
                        if (totemCoreBlocks.isEmpty() || totemCoreBlocks.contains(String.valueOf(realChar))) {
                            if (MythicTotem.getTotemMaterial.containsKey(realString)) {
                                MythicTotem.getTotemMaterial.get(realString).add(
                                        new PlacedBlockCheckManager(this,
                                                totemRow,
                                                totemColumn,
                                                i));
                            } else {
                                List<PlacedBlockCheckManager> placedBlockCheckManagers = new ArrayList<>();
                                placedBlockCheckManagers.add(new PlacedBlockCheckManager(this, totemRow, totemColumn, i));
                                MythicTotem.getTotemMaterial.put(realString, placedBlockCheckManagers);
                            }
                        }
                        this.totemLocationMaterial.put(GenerateID(i, totemRow, totemColumn), realString);
                    }
                    this.totemRow++;
                }
            }
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fLoaded 3D Totem: §e" + id +
                    " §fwith size: " + totemRow + "x" + totemColumn);
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
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fLoaded 2D Totem: §e" + id +
                    " §fwith size: " + totemRow + "x" + totemColumn);
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

    public ConfigurationSection GetSection() {
        return this.totemSection;
    }

    public boolean GetKeyMode() {
        return totemSection.getBoolean("prices-as-key", false);
    }
}
