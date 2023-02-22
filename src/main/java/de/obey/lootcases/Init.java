package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:11

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public final class Init extends JavaPlugin {

    private CaseHandler caseHandler;

    @Override
    public void onDisable() {
        caseHandler.getEffectRunnable().stop();
        caseHandler.getEffectThread().stop();
        saveFiles();
    }

    @Override
    public void onEnable() {

        caseHandler = new CaseHandler();

        loadFolderAndFiles();
        loadListener();
        loadCommands();

        caseHandler.loadCaseBlocks();
        caseHandler.loadCases();
        caseHandler.startEffects();

        DataHandler.startSavingRunnable();
    }

    public void loadFolderAndFiles() {

        if(!getDataFolder().exists())
            getDataFolder().mkdir();

        File temp = new File(getDataFolder().getPath() + "/cases");

        if(!temp.exists())
            temp.mkdir();

        temp = new File(getDataFolder().getPath() + "/playerFiles");

        if(!temp.exists())
            temp.mkdir();

        temp = new File(getDataFolder().getPath() + "/data.yml");

        if(!temp.exists()) {
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        caseHandler.setData(YamlConfiguration.loadConfiguration(temp));

        if(caseHandler.getData().contains("prefix")) {
            Util.setPrefix(caseHandler.getData().getString("prefix"));
        } else {
            caseHandler.getData().set("prefix", "&a&lLootCases &8 »");
        }

        if(caseHandler.getData().contains("messagecolor")) {
            Util.setMessageColor(caseHandler.getData().getString("messagecolor"));
        } else {
            caseHandler.getData().set("messagecolor", "&7");
        }

        if(caseHandler.getData().contains("highlightcolor")) {
            Util.setHighlightColor(caseHandler.getData().getString("highlightcolor"));
        } else {
            caseHandler.getData().set("highlightcolor", "&a");
        }
    }

    private void loadListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new AccessBlockListener(), this);
        pluginManager.registerEvents(new LoadDataListener(), this);
        pluginManager.registerEvents(new CasesInventoryListener(), this);
    }

    private void loadCommands() {
        getCommand("lootcases").setExecutor(new LootCasesCommand());
    }

    public void saveFiles() {
        try {
            caseHandler.getData().save(new File(getDataFolder() + "/data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Init getInstance() {
        return getPlugin(Init.class);
    }
}
