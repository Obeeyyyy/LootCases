package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:16

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Getter @Setter
public final class Case {

    private final File caseFile;
    private final YamlConfiguration data;

    private UUID caseUUID;
    private String caseName, casePrefix, skullTexture;
    private ItemStack displayItem;
    private int displaySlot;
    private ArrayList<ItemStack> items, chanceList;

    private final String path;

    public Case(final String caseName) {
        this.caseName = caseName.toLowerCase();

        caseFile = new File(Init.getInstance().getDataFolder().getPath() + "/cases/" + caseName.toLowerCase() + ".yml");
        data = YamlConfiguration.loadConfiguration(caseFile);

        path = "case." + caseName + ".";

        if(!caseFile.exists()) {
            try {
                caseFile.createNewFile();

                data.set(path + "uuid", UUID.randomUUID().toString());
                data.set(path + "prefix", "&5&l" + caseName);
                data.set(path + "skulltexture", "none");
                data.set(path + "displayitem", new ItemStack(Material.DRAGON_EGG));
                data.set(path + "displayslot", 13);
                data.set(path + "items", new ArrayList<>());

                data.save(caseFile);

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        loadFromFile();
    }

    public void loadFromFile() {
        caseUUID = UUID.fromString(data.getString(path + "uuid"));
        casePrefix = ChatColor.translateAlternateColorCodes('&', data.getString(path + "prefix"));
        skullTexture = data.getString(path + "skulltexture");
        displayItem = data.getItemStack(path + "displayitem");
        displaySlot = data.getInt(path + "displayslot");
        items = (ArrayList<ItemStack>) data.getList(path + "items");
    }

    public void delete() {
        caseFile.delete();
    }

    public void saveData() {

        data.set(path + "uuid", caseUUID.toString());
        data.set(path + "prefix", casePrefix.replace("§", "&"));
        data.set(path + "skulltexture", skullTexture);
        data.set(path + "displayitem", displayItem);
        data.set(path + "displayslot", displaySlot);
        data.set(path + "items", items);

        DataHandler.executor.submit(() -> {
            try {
                data.save(caseFile);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }

}
