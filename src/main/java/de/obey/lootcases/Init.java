package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:11

*/

import org.bukkit.plugin.java.JavaPlugin;

public final class Init extends JavaPlugin {

    @Override
    public void onEnable() {

        if(!getDataFolder().exists())
            getDataFolder().mkdir();

    }

    public static Init getInstance() {
        return getPlugin(Init.class);
    }
}
