package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       22.02.2023 / 16:35

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.handler.CaseHandler;
import de.obey.lootcases.handler.DataHandler;
import de.obey.lootcases.objects.Case;
import de.obey.lootcases.objects.UserCases;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.xml.crypto.Data;

public final class CasesInventoryListener implements Listener {

    private CaseHandler caseHandler;

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if(!ChatColor.stripColor(event.getInventory().getTitle()).startsWith("Deine"))
            return;

        event.setCancelled(true);

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        if(caseHandler == null)
            caseHandler = Init.getInstance().getCaseHandler();

        final Case caze = caseHandler.getCaseFromDisplayItem(event.getCurrentItem());

        if (caze == null)
            return;

        if(event.isRightClick()) {
            event.getWhoClicked().openInventory(caze.getInventory("§7Preview ", true, true));
            return;
        }

        final Player player = (Player) event.getWhoClicked();

        if(event.isLeftClick())
            caseHandler.openCase(player, caze, event.isShiftClick());
    }


}
