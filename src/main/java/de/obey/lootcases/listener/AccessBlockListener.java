package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       21.02.2023 / 19:44

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.handler.DataHandler;
import de.obey.lootcases.Init;
import de.obey.lootcases.utils.Util;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class AccessBlockListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Init.getInstance().getSecure().autoCheck();
    }

    @EventHandler
    public void on(final BlockBreakEvent event) {
        final Player player = event.getPlayer();

        if(!Init.getInstance().getCaseHandler().getCaseBlocks().contains(event.getBlock()))
            return;

        if(!Util.hasPermission(player, "admin", true) || player.getGameMode() != GameMode.CREATIVE)
            return;

        if(!player.isSneaking()) {
            Util.sendMessage(player, "Sneake um diesen Block zu zerstören§8.");
            event.setCancelled(true);
            return;
        }

        Init.getInstance().getCaseHandler().removeAccessBlock(event.getBlock());
        Util.sendMessage(player, "Access Block wurde zerstört§8.");
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(event.getClickedBlock() == null || !Init.getInstance().getCaseHandler().getCaseBlocks().contains(event.getClickedBlock()))
            return;

        event.setCancelled(true);

        final Player player = event.getPlayer();

        player.openInventory(DataHandler.get(player).updatedCaseInventory());
    }
}
