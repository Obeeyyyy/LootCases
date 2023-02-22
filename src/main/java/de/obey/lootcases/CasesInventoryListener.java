package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       22.02.2023 / 16:35

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class CasesInventoryListener implements Listener {

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if(!ChatColor.stripColor(event.getInventory().getTitle()).startsWith("Your"))
            return;

        event.setCancelled(true);


    }


}
