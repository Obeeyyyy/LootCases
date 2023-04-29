package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       30.04.2023 / 00:03

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.objects.Case;
import de.obey.lootcases.utils.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public final class BalanceListener implements Listener {

    private final HashMap<Player, ArrayList<ItemStack>> balanceItems = new HashMap<Player, ArrayList<ItemStack>>();

    @EventHandler
    public void on(final InventoryClickEvent event){
        if(event.getClickedInventory() == null)
            return;

        if(event.getClickedInventory().getTitle() == null)
            return;

        if(event.getClickedInventory() != event.getInventory())
            return;

        if(event.getClickedInventory().getTitle().startsWith("§9Balance ")){

            event.setCancelled(true);

            final Player player = (Player) event.getWhoClicked();

            if(!balanceItems.containsKey(player))
                balanceItems.put(player, new ArrayList<>());

            final ItemStack clicked = event.getCurrentItem();

            if(clicked == null || clicked.getType() == Material.AIR)
                return;

            if(balanceItems.get(player).contains(clicked))
                return;

            balanceItems.get(player).add(clicked);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        }
    }

    @EventHandler
    public void on(final InventoryCloseEvent event){
        if(event.getInventory().getTitle() == null)
            return;

        final Player player = (Player) event.getPlayer();

        if(player == null)
            return;

        if(event.getInventory().getTitle().startsWith("§9Balance ")) {
            if(!balanceItems.containsKey(player)){
                Util.sendMessage(player, "§c§oEin Fehler ist aufgetrete, bitte versuche es erneut§8.");
                return;
            }

            final String caseName = event.getInventory().getTitle().split(" ")[1];
            final Case crate = Init.getInstance().getCaseHandler().getCases().get(caseName);

            crate.balanceChances(balanceItems.get(player));
            Util.sendMessage(player, "Die gewählten Items wurden gebalanced§8.");
            return;
        }
    }

}
