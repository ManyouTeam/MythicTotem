package cn.superiormc.mythictotem.libreforge;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.libreforge.ConfigArguments;
import com.willfp.libreforge.ConfigArgumentsBuilder;
import com.willfp.libreforge.NoCompileData;
import com.willfp.libreforge.effects.Effect;
import com.willfp.libreforge.triggers.TriggerData;
import com.willfp.libreforge.triggers.TriggerParameter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.MythicUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EffectCastMythicSkill extends Effect<NoCompileData> {

    public EffectCastMythicSkill() {
        super("cast_mythic_skill");
    }

    @Override
    public boolean isPermanent() {
        return false;
    }

    @Override
    protected boolean onTrigger(@NotNull Config config, @NotNull TriggerData data, NoCompileData compileData) {
        if (data.getPlayer() == null) {
            return false;
        }
        Player player = data.getPlayer();
        LivingEntity victim = MythicUtil.getTargetedEntity(player);
        if (data.getVictim() != null) {
            victim = data.getVictim();
        }
        if (config.getBoolOrNull("victim_to_player") != null && config.getBoolOrNull("victim_to_player")) {
            victim = player;
        }
        String skill = config.getString("skill");
        List<Entity> targets = new ArrayList();
        targets.add(victim);
        MythicBukkit.inst().getAPIHelper().castSkill(player, skill, player, player.getLocation(), targets, (Collection)null, 1.0F);
        return true;
    }

    @NotNull
    @Override
    protected Set<TriggerParameter> getParameters() {
        Set<TriggerParameter> data = new HashSet<>();
        data.add(TriggerParameter.PLAYER);
        return data;
    }

    @NotNull
    @Override
    public ConfigArguments getArguments() {
        ConfigArgumentsBuilder builder = new ConfigArgumentsBuilder();
        builder.require("skill", "You must specify the skill to cast!");
        return builder.build$core();
    }
}
