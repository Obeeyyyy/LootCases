package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:11

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.commands.LootCasesCommand;
import de.obey.lootcases.handler.CaseHandler;
import de.obey.lootcases.handler.DataHandler;
import de.obey.lootcases.listener.*;
import de.obey.lootcases.objects.Case;
import de.obey.lootcases.utils.IlIIlllIl;
import de.obey.lootcases.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public final class Init extends JavaPlugin {

    private CaseHandler caseHandler;

    @Getter
    private IlIIlllIl secure;

    @Override
    public void onDisable() {
        if(caseHandler != null) {
            caseHandler.getEffectRunnable().stop();
            caseHandler.getEffectThread().stop();
        }

        saveFiles();
    }

    @Override
    public void onEnable() {
        secure = new IlIIlllIl();
        caseHandler = new CaseHandler();

        loadFolderAndFiles();
        loadListener();
        loadCommands();

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
            caseHandler.getData().set("prefix", "&a&lLootCases &8»");
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

        if(secure == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        caseHandler.loadRarities();
        caseHandler.loadCaseBlocks();
        caseHandler.loadCases();
        caseHandler.startEffects();
    }

    private void loadListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new AccessBlockListener(), this);
        pluginManager.registerEvents(new LoadDataListener(), this);
        pluginManager.registerEvents(new CasesInventoryListener(), this);
        pluginManager.registerEvents(new EditCaseListener(), this);
        pluginManager.registerEvents(new PreviewListener(), this);
        pluginManager.registerEvents(new ChanceEditListener(), this);
        pluginManager.registerEvents(new BalanceListener(), this);
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

        caseHandler.getCases().values().forEach(Case::saveData);
    }

    public static Init getInstance() {
        return getPlugin(Init.class);
    }
}
