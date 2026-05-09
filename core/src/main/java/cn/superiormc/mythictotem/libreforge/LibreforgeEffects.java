package cn.superiormc.mythictotem.libreforge;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.BonusEffectsManager;
import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import cn.superiormc.mythictotem.utils.TextUtil;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.Configs;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.libreforge.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LibreforgeEffects {

    public static LibreforgeEffects libreforgeEffects;

    private Map<String, LibreforgeEffect> libreforgeEffectMap;

    public LibreforgeEffects() {
        libreforgeEffects = this;
        cleanMap();
        initLibreforgeHook();
    }

    public void cleanMap() {
        libreforgeEffectMap = new HashMap<>();
    }

    private void initLibreforgeHook() {
        HolderProviderKt.registerHolderProvider((HolderProvider) dispatcher -> {
            Collection<ProvidedHolder> tempVal1 = new HashSet<>();
            if (dispatcher.getDispatcher() instanceof Player player) {
                for (BonusTotemData tempVal2: BonusEffectsManager.bonusEffectsManager.getPlayerActivedBonus(player)) {
                    LibreforgeEffect tempVal3 = libreforgeEffectMap.get(tempVal2.totemId);
                    if (tempVal3 == null) {
                        continue;
                    }
                    tempVal1.add(new SimpleProvidedHolder(tempVal3.getHolder()));
                }
            }
            return tempVal1;
        });
    }

    public void registerLibreforgeEffect(String id) {
        File file = new File(MythicTotem.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            return;
        }
        Config config = Configs.fromFile(file, ConfigType.YAML);
        for (Config tempVal1 : config.getSubsections("libreforge-effects")) {
            String tempVal2 = tempVal1.getString("id");
            if (id.equals(tempVal2)) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fSuccessfully added " + id + " " +
                        "effects to libreforge!");
                libreforgeEffectMap.put(id,
                new LibreforgeEffect(id, tempVal1));
                break;
            }
        }
    }
}
