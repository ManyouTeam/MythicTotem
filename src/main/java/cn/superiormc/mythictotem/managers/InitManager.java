package cn.superiormc.mythictotem.managers;

import cn.superiormc.mythictotem.MythicTotem;
import cn.superiormc.mythictotem.utils.CommonUtil;

import java.io.File;

public class InitManager {

    private boolean firstLoad = false;

    public InitManager() {
        File file = new File(MythicTotem.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            MythicTotem.instance.saveDefaultConfig();
            firstLoad = true;
        }
        init();
    }

    public void init() {
        resourceOutput("message.yml", true);
        resourceOutput("totems/2d-totem-example.yml", false);
        resourceOutput("totems/3d-totem-example.yml", false);
        resourceOutput("totems/3d-totem-with-ender-crystal-required-example.yml", false);
        resourceOutput("totems/3d-totem-with-key-required-example.yml", false);
    }
    private void resourceOutput(String fileName, boolean fix) {
        File tempVal1 = new File(MythicTotem.instance.getDataFolder(), fileName);
        if (!tempVal1.exists()) {
            if (!firstLoad && !fix) {
                return;
            }
            File tempVal2 = new File(fileName);
            if (tempVal2.getParentFile() != null && fix) {
                CommonUtil.mkDir(tempVal2.getParentFile());
            }
            MythicTotem.instance.saveResource(tempVal2.getPath(), false);
        }
    }
}
