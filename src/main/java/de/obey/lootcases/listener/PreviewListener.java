package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       28.02.2023 / 18:04

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.handler.DataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class PreviewListener implements Listener {

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if(!event.getInventory().getTitle().startsWith("§7Preview "))
            return;

        event.setCancelled(true);

        final Player player = (Player) event.getWhoClicked();

        if(event.getSlot() == event.getInventory().getSize() - 1)
            player.openInventory(DataHandler.get(player).updatedCaseInventory());

    }
}
