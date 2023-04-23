package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       28.02.2023 / 17:18

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.utils.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public final class EditCaseListener implements Listener {

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if(event.getInventory().getTitle() == null)
            return;

        final Inventory inventory = event.getInventory();

        if(event.getInventory().getTitle().startsWith("§eEdit ")) {
            final String caseName = inventory.getTitle().split(" ")[1];

            Init.getInstance().getCaseHandler().getCases().get(caseName).setItems(inventory);
            Util.sendMessage(event.getPlayer(), "Items for " + caseName + " set§8.");
            return;
        }
    }

}
