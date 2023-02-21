package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:11

*/

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@UtilityClass
public final class Util {

    public void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage("§a§lLootCases§8 » §7" + ChatColor.translateAlternateColorCodes('&', message));
    }

    public boolean hasPermission(final CommandSender sender, final String permission, final boolean send) {
        return false;
    }

}
