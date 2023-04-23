package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       07.03.2023 / 14:09

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.objects.Case;
import de.obey.lootcases.utils.Util;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public final class ChanceEditListener implements Listener {

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if (event.getInventory().getTitle() == null)
            return;

        final Inventory inventory = event.getInventory();

        if(event.getInventory().getTitle().startsWith("§eChance ")) {
            final String caseName = inventory.getTitle().split(" ")[1];
            final Case caze = Init.getInstance().getCaseHandler().getCases().get(caseName);

            caze.setItems(inventory);

            Util.sendMessage(event.getPlayer(),"You have changed the chances§8.");
        }
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;

        if (event.getClickedInventory().getTitle() == null)
            return;

        if (event.getClickedInventory() != event.getInventory())
            return;

        if (!event.getClickedInventory().getTitle().startsWith("§eChance "))
            return;

        event.setCancelled(true);

        final Case caze = Init.getInstance().getCaseHandler().getCases().get(event.getInventory().getTitle().split(" ")[1]);
        final ItemStack item = event.getCurrentItem();

        ArrayList<String> lore = new ArrayList<>();

        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            lore = (ArrayList<String>) item.getItemMeta().getLore();
        }

        final Player player = (Player) event.getWhoClicked();
        final DecimalFormat format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
        final double chance = Double.parseDouble(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 2).split(" ")[2].replace("%", ""));

        // Chance hoch
        if(event.getClick().isLeftClick()){
            if(event.getClick().isShiftClick()){ // + 0.1
                lore.set(lore.size() - 2, "§8┌ §7Chance§8:§f§o " + format.format((chance + 0.1)) + "%");
            } else { //  // + 1
                lore.set(lore.size() - 2, "§8┌ §7Chance§8:§f§o " + format.format((chance + 1)) + "%");
            }

            final ItemMeta meta = item.getItemMeta();

            meta.setLore(lore);
            item.setItemMeta(meta);
            player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        }

        // Chance runter
        if(event.getClick().isRightClick()){
            if(event.getClick().isShiftClick()){ // - 0.1
                lore.set(lore.size() - 2, "§8┌ §7Chance§8:§f§o " + format.format((chance - 0.1)) + "%");
            } else { //  - 1
                lore.set(lore.size() - 2, "§8┌ §7Chance§8:§f§o " + format.format((chance - 1)) + "%");
            }

            final ItemMeta meta = item.getItemMeta();

            meta.setLore(lore);
            item.setItemMeta(meta);
            player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        }

        caze.setDisplayLoreForItem(item);

    }
}
