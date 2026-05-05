package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.singlethings.BonusTotemData;
import cn.superiormc.mythictotem.objects.singlethings.AbstractThingData;
import cn.superiormc.mythictotem.objects.singlethings.TotemActiveData;
import cn.superiormc.mythictotem.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ActionParticle extends AbstractRunAction {

    public ActionParticle() {
        super("particle");
        setRequiredArgs("particle", "count", "offset-x", "offset-y", "offset-z", "speed");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, AbstractThingData thingData) {
        String worldName = singleAction.getString("world");
        Location location = null;
        if (player == null || singleAction.getBoolean("block-as-trigger", false)) {
            if (thingData instanceof TotemActiveData totemActiveData) {
                location = totemActiveData.check.getBlock().getLocation();
            } else if (thingData instanceof BonusTotemData bonusTotemData) {
                location = bonusTotemData.location;
            }
        } else if (worldName == null) {
            location = player.getLocation();
        } else {
            World world = Bukkit.getWorld(worldName);
            location = new Location(world,
                    singleAction.getDouble("x", player, thingData),
                    singleAction.getDouble("y", player, thingData),
                    singleAction.getDouble("z", player, thingData));

        }
        if (location != null) {
            location = location.add(0, 1, 0); // 在玩家头顶播放

            // 读取参数
            String particleName = singleAction.getString("particle", player, thingData);
            int count = singleAction.getInt("count");
            double offsetX = singleAction.getDouble("offset-x", player, thingData);
            double offsetY = singleAction.getDouble("offset-y", player, thingData);
            double offsetZ = singleAction.getDouble("offset-z", player, thingData);
            double speed = singleAction.getDouble("speed", player, thingData);

            try {
                Particle particle = Particle.valueOf(particleName.toUpperCase());
                Location finalLocation = location;
                SchedulerUtil.runSync(finalLocation, () -> finalLocation.getWorld().spawnParticle(particle, finalLocation, count, offsetX, offsetY, offsetZ, speed));
            } catch (IllegalArgumentException e) {
                ErrorManager.errorManager.sendErrorMessage("§cInvalid particle name: " + particleName);
            }
        }
    }
}