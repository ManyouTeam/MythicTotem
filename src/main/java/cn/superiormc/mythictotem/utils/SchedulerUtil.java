package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil {

    private BukkitTask bukkitTask;

    private ScheduledTask scheduledTask;

    public SchedulerUtil(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public SchedulerUtil(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public void cancel() {
        if (MythicTotem.isFolia) {
            scheduledTask.cancel();
        } else {
            bukkitTask.cancel();
        }
    }

    // 在主线程上运行任务
    public static void runSync(Runnable task) {
        if (MythicTotem.isFolia) {
            Bukkit.getGlobalRegionScheduler().execute(MythicTotem.instance, task);
        } else {
            Bukkit.getScheduler().runTask(MythicTotem.instance, task);
        }
    }

    // 在异步线程上运行任务
    public static void runTaskAsynchronously(Runnable task) {
        if (MythicTotem.isFolia) {
            task.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(MythicTotem.instance, task);
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLater(Runnable task, long delayTicks) {
        if (MythicTotem.isFolia) {
            if (delayTicks <= 0) {
                delayTicks = 1;
            }
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runDelayed(MythicTotem.instance,
                    scheduledTask -> task.run(), delayTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskLater(MythicTotem.instance, task, delayTicks));
        }
    }

    // 定时循环任务
    public static SchedulerUtil runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        if (MythicTotem.isFolia) {
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runAtFixedRate(MythicTotem.instance,
                    scheduledTask -> task.run(), delayTicks, periodTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskTimer(MythicTotem.instance, task, delayTicks, periodTicks));
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLaterAsynchronously(Runnable task, long delayTicks) {
        if (MythicTotem.isFolia) {
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runDelayed(MythicTotem.instance,
                    scheduledTask -> task.run(), delayTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskLaterAsynchronously(MythicTotem.instance, task, delayTicks));
        }
    }

}
