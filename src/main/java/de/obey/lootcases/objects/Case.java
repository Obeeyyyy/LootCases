package de.obey.lootcases.objects;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:16

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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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

    public ItemStack getRandomItem() {
        if(chanceList == null || chanceList.isEmpty())
            return null;

        return chanceList.get(new Random().nextInt(chanceList.size()));
    }

    public void giveReward(final Player player) {
        final ItemStack win = getRandomItem().clone();
        final ItemMeta meta = win.getItemMeta();

        final ArrayList<String> lore = (ArrayList<String>) meta.getLore();

        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);

        meta.setLore(lore);
        win.setItemMeta(meta);

        Util.giveItem(player, win);

    }

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

        setChanceItems();
    }

    public Inventory getInventory(final String name, final boolean prefix){
        return getInventory(name, prefix, false);
    }

    public Inventory getInventory(final String name, final boolean prefix, final boolean backItem){
        String add = this.caseName;

        if(prefix)
            add = ChatColor.translateAlternateColorCodes('&', this.casePrefix);

        final Inventory inv = Bukkit.createInventory(null, 9*6, name + add);
        final AtomicInteger slot = new AtomicInteger();

        if(!items.isEmpty())
            items.forEach(item -> inv.setItem(slot.getAndIncrement(), item));

        if(backItem)
            inv.setItem(inv.getSize() - 1, new ItemBuilder(Material.BARRIER, 1).setDisplayname("§c§oGo back").build());

        return inv;
    }


    public void setItems(final Inventory inventory) {
        final ArrayList<ItemStack> temp = new ArrayList<>();

        for (ItemStack item : inventory.getContents()) {
            if(item != null && item.getType() != Material.AIR) {
                setDisplayLoreForItem(item);
                temp.add(item);
            }
        }

        items = temp;

        setChanceItems();
        saveData();
    }

    public void setDisplayLoreForItem(final ItemStack item){
        final ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        if(meta.hasLore()) {
            lore = (ArrayList<String>) meta.getLore();

            if(!lore.get(lore.size() - 2).startsWith("§8┌ §7Chance§8:§f§o ")) {
                lore.add("");
                lore.add("§8┌ §7Chance§8:§f§o 0%");
                lore.add("§8└ §7Rarity§8:§f§o " + Init.getInstance().getCaseHandler().getRarity(0));

            } else {
                lore.remove(lore.size() - 1);
                lore.add("§8└ §7Rarity§8:§f§o " + Init.getInstance().getCaseHandler().getRarity(getChanceFromItem(item)));
            }

        } else {
            lore.add("");
            lore.add("§8┌ §7Chance§8:§f§o 0%");
            lore.add("§8└ §7Rarity§8:§f§o " + Init.getInstance().getCaseHandler().getRarity(0));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void balanceChances(final ArrayList<ItemStack> items) {
        final double[] currentSum = {0};

        this.items.forEach(item -> {
            if (!items.contains(item))
                currentSum[0] += getChanceFromItem(item);
        });

        if (currentSum[0] >= 100)
            return;

        if (items.isEmpty())
            return;

        final double chance = (100 - currentSum[0]) / items.size();

        DataHandler.executor.submit(() -> items.forEach(item -> {
            this.items.remove(item);
            final ItemMeta meta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            final DecimalFormat format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));

            if (!item.getItemMeta().hasLore()) {
                lore.add("");
                lore.add("§8┌ §7Chance§8:§f§o " + format.format(chance) + "%");
                lore.add("§8└ §7Rarity§8:§f§o " + Init.getInstance().getCaseHandler().getRarity(chance));

                meta.setLore(lore);
            } else {
                lore = (ArrayList<String>) meta.getLore();

                if (!lore.get(lore.size() - 2).startsWith("§8┌ §7Chance§8:§f§o ")) {
                    lore.add("");
                    lore.add("§8┌ §7Chance§8:§f§o " + format.format(chance) + "%");
                    lore.add("§8└ §7Rarity§8:§f§o " + Init.getInstance().getCaseHandler().getRarity(chance));

                } else {

                    lore.remove(lore.size() - 1);
                    lore.remove(lore.size() - 1);
                    lore.add("§8┌ §7Chance§8:§f§o " + format.format(chance) + "%");
                    lore.add("§8└ §7Rarity§8:§f§o " + Init.getInstance().getCaseHandler().getRarity(chance));
                }
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            this.items.add(item);
        }));

        saveData();

        new BukkitRunnable() {
            @Override
            public void run() {
                setChanceItems();
            }
        }.runTaskLater(Init.getInstance(), 20*2);
    }

    private double getChanceFromItem(final ItemStack item) {
        if(!item.hasItemMeta())
            return 0;

        if(!item.getItemMeta().hasLore())
            return 0;

        return Double.parseDouble(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 2).split(" ")[2].replace("%", ""));
    }

    public void setChanceItems() {
        if(items.isEmpty())
            return;

        if(chanceList != null) chanceList.clear();

        chanceList = new ArrayList<>();

        items.forEach(item -> {
            final double chance = getChanceFromItem(item);

            if (chance > 0) {
                for (int i = 0; i < chance * 10; i++) {
                    chanceList.add(item);
                }
            }
        });
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
