package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:11

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@UtilityClass
public final class Util {

    @Setter @Getter
    private String prefix = "§a§lLootCases§8 »";
    @Setter @Getter
    private String messageColor = "§7";
    @Setter @Getter
    private String highlightColor = "§a";

    public String transform(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replace("%c", messageColor).replace("%h", highlightColor));
    }

    public void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(transform(prefix + " " + message));
    }

    public boolean hasPermission(final CommandSender sender, final String permission, final boolean send) {

        if(!(sender instanceof Player))
            return true;

        if(!sender.hasPermission("lc." + permission)) {

            if(send)
                sendMessage(sender, "Du hast nicht die benötigten Rechte§8. §8(§c§o lc." + permission + " §8)");

            return false;
        }

        return true;
    }

    public void sendSyntax(final CommandSender sender, final String... lines) {
        sendMessage(sender, "Syntax§8:");

        for (String line : lines)
            sender.sendMessage("§8 - §7" + line);
    }

}
