package cn.superiormc.mythictotem.objects.conditions;

import cn.superiormc.mythicchanger.manager.MatchItemManager;
import cn.superiormc.mythictotem.managers.HookManager;
import cn.superiormc.mythictotem.methods.BuildItem;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import cn.superiormc.mythictotem.utils.CommonUtil;
import cn.superiormc.mythictotem.utils.ItemUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ConditionTriggerItem extends AbstractCheckCondition {

    public ConditionTriggerItem() {
        super("trigger_item");
        setRequiredArgs("item");
        setRequirePlayer(false);
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        if (check.getItem() == null) {
            return false;
        }
        ConfigurationSection section = singleCondition.getSection().getConfigurationSection("item");
        if (section == null) {
            return true;
        } else if (section.contains("hook-plugin") && section.contains("hook-item")) {
            String tempVal1 = HookManager.hookManager.getHookItemID(section.getString("hook-plugin"), check.getItem());
            if (tempVal1 == null) {
                return false;
            }
            return tempVal1.equals(section.getString("hook-item"));
        } else if (section.contains("match-item") && CommonUtil.checkPluginLoad("MythicChanger")) {
            return MatchItemManager.matchItemManager.getMatch(section, player, check.getItem());
        } else if (section.contains("material")) {
            return ItemUtil.isSameItem(BuildItem.buildItemStack(player, section, section.getInt("amount")), check.getItem());
        }
        return check.getEvent().equals(singleCondition.getString("item").toUpperCase());
    }
}
