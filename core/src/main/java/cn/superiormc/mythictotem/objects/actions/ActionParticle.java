package cn.superiormc.mythictotem.objects.actions;

import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.objects.checks.ObjectCheck;
import cn.superiormc.mythictotem.objects.checks.ObjectPlaceCheck;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ActionParticle extends AbstractRunAction {

    public ActionParticle() {
        super("particle");
        setRequiredArgs("particle", "count", "offset-x", "offset-y", "offset-z", "speed");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player, Location startLocation, ObjectCheck check, ObjectPlaceCheck totem) {
        Location loc = player.getLocation().add(0, 1, 0); // 在玩家头顶播放

        // 读取参数
        String particleName = singleAction.getString("particle", player, startLocation, check, totem);
        int count = singleAction.getInt("count");
        double offsetX = singleAction.getDouble("offset-x", player, startLocation, check, totem);
        double offsetY = singleAction.getDouble("offset-y", player, startLocation, check, totem);
        double offsetZ = singleAction.getDouble("offset-z", player, startLocation, check, totem);
        double speed = singleAction.getDouble("speed", player, startLocation, check, totem);

        try {
            Particle particle = Particle.valueOf(particleName.toUpperCase());
            player.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed);
        } catch (IllegalArgumentException e) {
            ErrorManager.errorManager.sendErrorMessage("§cInvalid particle name: " + particleName);
        }
    }
}