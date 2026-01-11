package cn.superiormc.mythictotem.utils;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.managers.ErrorManager;
import cn.superiormc.mythictotem.managers.LanguageManager;
import dev.lone.itemsadder.api.CustomBlock;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static String modifyString(Player player, String text, String... args) {
        text = CommonUtil.parseLang(player, text);
        for (int i = 0 ; i < args.length ; i += 2) {
            String var1 = "{" + args[i] + "}";
            String var2 = "%" + args[i] + "%";
            if (args[i + 1] == null) {
                text = text.replace(var1, "").replace(var2, "");
            } else {
                text = text.replace(var1, args[i + 1]).replace(var2, args[i + 1]);
            }
        }
        return text;
    }

    public static List<String> modifyList(Player player, List<String> config, String... args) {
        List<String> resultList = new ArrayList<>();
        for (String s : config) {
            s = CommonUtil.parseLang(player, s);
            for (int i = 0 ; i < args.length ; i += 2) {
                String var1 = "{" + args[i] + "}";
                String var2 = "%" + args[i] + "%";
                if (args[i + 1] == null) {
                    s = s.replace(var1, "").replace(var2, "");
                } else {
                    s = s.replace(var1, args[i + 1]).replace(var2, args[i + 1]);
                }
            }
            String[] tempVal1 = s.split(";;");
            if (tempVal1.length > 1) {
                for (String string : tempVal1) {
                    resultList.add(TextUtil.withPAPI(string, player));
                }
                continue;
            }
            resultList.add(TextUtil.withPAPI(s, player));
        }
        return resultList;
    }

    public static String parseLang(Player player, String text) {
        Pattern pattern8 = Pattern.compile("\\{lang:(.*?)}");
        Matcher matcher8 = pattern8.matcher(text);
        while (matcher8.find()) {
            String placeholder = matcher8.group(1);
            text = text.replace("{lang:" + placeholder + "}", LanguageManager.languageManager.getStringText(player, "override-lang." + placeholder));
        }
        return text;
    }

    public static boolean checkPluginLoad(String pluginName){
        return MythicTotem.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean getClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean getMajorVersion(int version) {
        return MythicTotem.majorVersion >= version;
    }

    public static boolean getMinorVersion(int majorVersion, int minorVersion) {
        return MythicTotem.majorVersion > majorVersion || (MythicTotem.majorVersion == majorVersion &&
                MythicTotem.minorVersion >= minorVersion);
    }

    public static void giveOrDrop(Player player, ItemStack... item) {
        if (player == null) {
            return;
        }
        HashMap<Integer, ItemStack> result = player.getInventory().addItem(item);
        if (!result.isEmpty()) {
            for (int id : result.keySet()) {
                player.getWorld().dropItem(player.getLocation(), result.get(id));
            }
        }
    }

    public static Collection<Entity> getNearbyEntity(Location location, double distance) {
        Collection<Entity> tempVal2 = new HashSet<>();
        if (MythicTotem.isFolia) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Folia servers do not support entity as totem layout, skipping totem check.");
        } else {
            try {
                tempVal2 = Bukkit.getScheduler().callSyncMethod(MythicTotem.instance, () -> location.getWorld().getNearbyEntities(location, distance,
                        distance, distance)).get();
            } catch (InterruptedException | ExecutionException e) {
                ErrorManager.errorManager.sendErrorMessage("§cError: There is something wrong when get nearby entities.");
                return new HashSet<>();
            }
        }
        return tempVal2;
    }

    public static void summonMythicMobs(Location location, String mobID, int level) {
        if (!CommonUtil.checkPluginLoad("MythicMobs")) {
            return;
        }
        try {
            MythicBukkit.inst().getMobManager().getMythicMob(mobID).ifPresent(mob -> mob.spawn(BukkitAdapter.adapt(location), level));
        } catch (NoClassDefFoundError ep) {
            io.lumine.xikage.mythicmobs.mobs.MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(mobID);
            if (mob != null) {
                mob.spawn(io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter.adapt(location), level);
            }
        }
    }

    public static void removeBlock(Block block) {
        if (block == null) {
            return;
        }
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
            if (customBlock != null) {
                customBlock.remove();
            }
        }
        block.setType(Material.AIR);
    }

    public static void mkDir(File dir) {
        if (!dir.exists()) {
            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }
            String parentPath = parentFile.getPath();
            mkDir(new File(parentPath));
            dir.mkdir();
        }
    }

    public static NamespacedKey parseNamespacedKey(String key) {
        String[] keySplit = key.split(":");
        if (keySplit.length == 1) {
            return NamespacedKey.minecraft(key.toLowerCase());
        }
        if (CommonUtil.getMajorVersion(16)) {
            return NamespacedKey.fromString(key);
        }
        return new NamespacedKey("mythictotem", "unknown");
    }

    public static Color parseColor(String color) {
        if (color == null || color.isEmpty()) {
            return Color.fromRGB(0, 0, 0);
        }

        color = color.trim();

        // 支持 #RRGGBB
        if (color.startsWith("#")) {
            return Color.fromRGB(Integer.parseInt(color.substring(1), 16));
        }

        // 支持 R,G,B
        String[] keySplit = color.replace(" ", "").split(",");
        if (keySplit.length == 3) {
            return Color.fromRGB(
                    Integer.parseInt(keySplit[0]),
                    Integer.parseInt(keySplit[1]),
                    Integer.parseInt(keySplit[2])
            );
        }

        // 默认：单值 RGB int
        return Color.fromRGB(Integer.parseInt(color));
    }

    public static String colorToString(Color color) {
        if (color == null) {
            return "0,0,0";
        }
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    public static List<Color> parseColorList(List<String> rawList) {
        List<Color> colors = new ArrayList<>();

        for (String value : rawList) {
            try {
                colors.add(parseColor(value));
            } catch (Exception e) {
                return colors;
            }
        }

        return colors;
    }

    public static boolean checkClass(String className, String methodName) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method[] methods = targetClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }

            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
