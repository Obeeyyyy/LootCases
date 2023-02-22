package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       22.02.2023 / 12:10

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public final class LoadDataListener implements Listener {

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        DataHandler.get(event.getUniqueId());
    }

}
