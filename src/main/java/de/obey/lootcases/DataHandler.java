package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       22.02.2023 / 11:41

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public final class DataHandler {

    public final HashMap<UUID, UserCases> userData = new HashMap<>();
    public final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserCases get(final Player player) {
        return get(player.getUniqueId());
    }

    public UserCases get(final UUID uuid) {
        if(userData.containsKey(uuid))
            return userData.get(uuid);

        final UserCases data = new UserCases(uuid);

        userData.put(uuid, data);
        return data;
    }

    public void startSavingRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {

                userData.values().forEach(data -> {
                    data.saveData();

                    if (data.getOfflinePlayer().isOnline())
                        return;

                    // if the user was not online for 30 minutes the user will be removed from the cache
                    if (System.currentTimeMillis() - data.getLastSeen() <= 1000 * 60 * 30)
                        return;

                    userData.remove(data.getUuid());
                });

            }
        }.runTaskTimer(Init.getInstance(), 20*30, 20*30);
    }

}
