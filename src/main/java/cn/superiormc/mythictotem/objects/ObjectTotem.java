package cn.superiormc.mythictotem.objects;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ConfigManager;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
public class ObjectTotem {

    // 图腾的行和列
    private int totemRow;

    private int totemColumn;

    private int totemLayer;

    // 图腾各处的方块
    // 行列，Material
    private final Map<String, String> totemLocationMaterial = new HashMap<>();

    private final ObjectAction totemAction;

    private final ObjectCondition totemCondition;

    private final List<String> totemCoreBlocks;

    private final boolean totemDisappear;

    private final String totemCheckMode;

    private final ConfigurationSection totemSection;
    
    private final String totemID;

    public ObjectTotem(String id, YamlConfiguration section) {
        this.totemID = id;
        this.totemDisappear = section.getBoolean("disappear", true);
        this.totemAction = new ObjectAction(section.getConfigurationSection("actions"));
        this.totemCondition = new ObjectCondition(section.getConfigurationSection("conditions"));
        this.totemCheckMode = section.getString("mode", "VERTICAL").toUpperCase();
        this.totemCoreBlocks = section.getStringList("core-blocks");
        this.totemSection = section;
        ConfigurationSection totemLayoutsExplainConfig = section.getConfigurationSection("explains");
        if (totemLayoutsExplainConfig == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Can not found any explains option in totem: " + id + ".");
            return;
        }
        Set<String> totemLayoutsExplainList = totemLayoutsExplainConfig.getKeys(false);
        Map<String, String> totemLayoutsExplain = new HashMap<>();
        for (String totemLayoutsChar : totemLayoutsExplainList) {
            if (totemLayoutsChar.length() > 1) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Totem " + id + "'s layout explain config keys must be a char, like A.");
                return;
            }
            String totemLayoutsMaterial = totemLayoutsExplainConfig.getString(totemLayoutsChar);
            if (totemLayoutsMaterial == null) {
                totemLayoutsMaterial = "none";
            } else {
                totemLayoutsMaterial = totemLayoutsMaterial.toLowerCase();
            }
            totemLayoutsExplain.put(totemLayoutsChar, totemLayoutsMaterial);
        }
        ConfigurationSection layoutsSection = section.getConfigurationSection("layouts");
        if (layoutsSection != null) {
            ConfigManager.configManager.plus3DTotem();
            if (MythicTotem.freeVersion && ConfigManager.configManager.getThreeDtotemAmount() > 3) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicTotem] §cError: Free version" +
                        " can only create up to 3 3D totems, but your totem configs have more then 3 3D totems, please" +
                        " remove, otherwise plugin won't check 3D totems!");
                return;
            }
            Map<Integer, List<String>> totemLayouts = new HashMap<>();
            for (int i = 1 ; i <= layoutsSection.getKeys(false).size() ; i++) {
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
                            if (ConfigManager.configManager.getTotemMaterial.containsKey(realString)) {
                                ConfigManager.configManager.getTotemMaterial.get(realString).add(
                                        new ObjectPlaceCheck(this,
                                                totemRow,
                                                totemColumn,
                                                i));
                            } else {
                                List<ObjectPlaceCheck> placedBlockCheckManagers = new ArrayList<>();
                                placedBlockCheckManagers.add(new ObjectPlaceCheck(this, totemRow, totemColumn, i));
                                ConfigManager.configManager.getTotemMaterial.put(realString, placedBlockCheckManagers);
                            }
                        }
                        this.totemLocationMaterial.put(generateID(i, totemRow, totemColumn), realString);
                    }
                    this.totemRow++;
                }
            }
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fLoaded 3D Totem: §e" + totemID +
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
                    if (ConfigManager.configManager.getTotemMaterial.containsKey(realString)){
                        ConfigManager.configManager.getTotemMaterial.get(realString).add(new ObjectPlaceCheck(this, totemRow, totemColumn));
                    }
                    else{
                        List<ObjectPlaceCheck> placedBlockCheckManagers = new ArrayList<>();
                        placedBlockCheckManagers.add(new ObjectPlaceCheck(this, totemRow, totemColumn));
                        ConfigManager.configManager.getTotemMaterial.put(realString, placedBlockCheckManagers);
                    }
                    this.totemLocationMaterial.put(generateID(1, totemRow, totemColumn), realString);
                }
                this.totemRow++;
            }
            this.totemLayer = 1;
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[MythicTotem] §fLoaded 2D Totem: §e" + totemID +
                    " §fwith size: " + totemRow + "x" + totemColumn);
        }
    }

    private String generateID(int layer, int row, int column){
        return layer + ";;" + row + ";;" + column;
    }

    public int getRealRow(){
        return this.totemRow;
    }

    public int getRealColumn(){
        return this.totemColumn;
    }

    public String getRealMaterial(int layer, int row, int column){
        return totemLocationMaterial.get(layer + ";;" + row + ";;" + column);
    }

    public ObjectAction getTotemAction(){
        return this.totemAction;
    }

    public ObjectCondition getTotemCondition(){
        return this.totemCondition;
    }

    public boolean getTotemDisappear(){
        return this.totemDisappear;
    }

    public String getCheckMode() {
        return this.totemCheckMode;
    }

    public int getTotemLayer() {
        return this.totemLayer;
    }

    public ConfigurationSection getSection() {
        return this.totemSection;
    }

    public boolean getKeyMode() {
        return totemSection.getBoolean("prices-as-key", false);
    }
    
    public String getTotemID() {
        return totemID;
    }
}
