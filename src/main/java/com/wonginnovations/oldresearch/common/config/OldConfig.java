package com.wonginnovations.oldresearch.common.config;

import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraftforge.common.config.Config;

@Config(modid = OldResearch.MODID)
public class OldConfig {
    public static double researchDifficultyMultiplier = 0.5D;
    public static int notificationDelay = 2000;
    public static int notificationMax = 10;
    public static int aspectTotalCap = 10000;
    public static boolean instantScans = false;
}
