package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.SchedulerUtil;

public class TaskManager {

    public static TaskManager taskManager;

    private SchedulerUtil bonusEffectsTask;

    private SchedulerUtil loadedChunksTask;

    public TaskManager() {
        taskManager = this;
        if (!MythicTotem.isFolia && ConfigManager.configManager.getBoolean("bonus-effects.enabled", false)) {
            initBonusEffectsTasks();
        }
    }

    public void initBonusEffectsTasks() {
        bonusEffectsTask = SchedulerUtil.runTaskTimer(
                    () -> BonusEffectsManager.bonusEffectsManager.tick(),
                    20L,
                    1L
        );
        loadedChunksTask = SchedulerUtil.runTaskTimer(
                () -> BonusEffectsManager.bonusEffectsManager.tickChunks(),
                20L,
                60L
        );
    }

    public void cancelTask() {
        if (bonusEffectsTask != null) {
            bonusEffectsTask.cancel();
        }
        if (loadedChunksTask != null) {
            loadedChunksTask.cancel();
        }
    }
}
