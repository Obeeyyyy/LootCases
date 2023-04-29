package de.obey.lootcases.objects;
/*

    Author - Obey -> LootCases
       22.02.2023 / 11:40

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.handler.DataHandler;
import de.obey.lootcases.utils.ItemBuilder;
import de.obey.lootcases.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Getter @Setter
public final class UserCases {

    private final UUID uuid;

    private final File file;
    private final YamlConfiguration data;

    private long lastSeen = System.currentTimeMillis();

    private Inventory caseInventory = Bukkit.createInventory(null, 9*5, Util.transform("%cDeine %hCases"));

    public UserCases(final UUID uuid) {
        this.uuid = uuid;

        file = new File(Init.getInstance().getDataFolder().getPath() + "/playerFiles/" + uuid.toString() + ".yml");
        data = YamlConfiguration.loadConfiguration(file);

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        updatedCaseInventory();
    }

    public Inventory updatedCaseInventory() {

        if(!caseInventory.getTitle().equalsIgnoreCase(Util.transform("%cYour %hCases")))
            caseInventory = Bukkit.createInventory(null, 9*5, Util.transform("%cYour %hCases"));

        DataHandler.executor.submit(() -> {
            for (final Case currentCase : Init.getInstance().getCaseHandler().getCases().values()) {
                if(currentCase.getDisplayItem()== null) {
                    // skull textur benutzen
                    caseInventory.setItem(currentCase.getDisplaySlot(), new ItemBuilder(Material.SKULL_ITEM,1, (byte) 3).setTextur(currentCase.getSkullTexture(), currentCase.getCaseUUID()).build());
                } else {
                    // itemstack benutzen

                    final ItemStack item = currentCase.getDisplayItem().clone();
                    final ItemMeta meta = item.getItemMeta();

                    meta.setDisplayName(Util.transform(currentCase.getCasePrefix()));

                    final ArrayList<String> lore = new ArrayList<>();

                    lore.add("");
                    lore.add(Util.transform("§8➥ %cDu hast %h" + getAmountOfCase(currentCase.getCaseName()) + " " + currentCase.getCasePrefix() + "%c Cases§8."));
                    lore.add("");
                    lore.add(Util.transform("§8➥ %cRechtsklick §8- %cÖffne die Preview§8."));
                    lore.add(Util.transform("§8➥ %cLinksklick §8- %cÖffne eine Case§8."));
                    lore.add("");

                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    caseInventory.setItem(currentCase.getDisplaySlot(), item);
                }
            }
        });

        return caseInventory;
    }

    public int getAmountOfCase(final String caseName) {
        return data.getInt("cases." + caseName);
    }

    public void saveData() {
        DataHandler.executor.submit(() -> {
            try {
                data.save(file);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

}
