package de.obey.lootcases.listener;
/*

    Author - Obey -> LootCases
       22.02.2023 / 12:10

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.handler.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class LoadDataListener implements Listener {

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        DataHandler.get(event.getUniqueId());
    }

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        DataHandler.get(event.getPlayer()).saveData();
    }

}
