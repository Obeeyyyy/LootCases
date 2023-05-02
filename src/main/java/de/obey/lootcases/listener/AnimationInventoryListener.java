package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       02.05.2023 / 13:24

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.handler.CaseHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class AnimationInventoryListener implements Listener {

    private CaseHandler caseHandler;

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if (event.getInventory() == null)
            return;

        if (event.getInventory().getTitle() == null)
            return;

        if (!event.getInventory().getTitle().startsWith("§7Öffne§8: "))
            return;

        if (caseHandler == null)
            caseHandler = Init.getInstance().getCaseHandler();

        if(caseHandler.getAnimations().containsKey(event.getPlayer())) {
            caseHandler.getAnimations().get(event.getPlayer()).forceEnd();
        }
    }

    @EventHandler
    public void on(final InventoryClickEvent event){
        if (event.getClickedInventory() == null)
            return;

        if (event.getClickedInventory().getTitle() == null)
            return;

        if (event.getInventory().getTitle().startsWith("§7Öffne§8: ")) {
            event.setCancelled(true);
        }
    }

}
