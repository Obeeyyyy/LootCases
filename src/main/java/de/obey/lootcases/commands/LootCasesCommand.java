package de.obey.lootcases.commands;
/*

    Author - Obey -> LootCases
       21.02.2023 / 19:10

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.handler.CaseHandler;
import de.obey.lootcases.handler.DataHandler;
import de.obey.lootcases.objects.UserCases;
import de.obey.lootcases.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LootCasesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            if (!Util.hasPermission(sender, "admin", false)) {
                Util.sendMessage(sender, "This plugin was made by Obey³#9051");
                return false;
            }
        }

        Init.getInstance().getSecure().autoCheck();

        final CaseHandler caseHandler = Init.getInstance().getCaseHandler();

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("spawnblock")) {

                if(!(sender instanceof Player))
                    return false;

                final Player player = (Player) sender;
                final Block block = player.getLocation().getBlock();

                if(caseHandler.getCaseBlocks().contains(block)) {
                    Util.sendMessage(sender, "Hier steht bereits ein access Block§8.");
                    return false;
                }


                caseHandler.addAccessBlock(block);

                Util.sendMessage(sender, "Du hast einen neuen access Block erstellt§.");

                return false;
            }

            if(args[0].equalsIgnoreCase("reload")) {

                Init.getInstance().loadFolderAndFiles();
                Util.sendMessage(sender, "Cached data was reloaded§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("rlr")) {

                Init.getInstance().getCaseHandler().loadRarities();
                Init.getInstance().getCaseHandler().setRarityForAllItems();
                Util.sendMessage(sender, "Rarities where reloaded§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("list")) {

                if(caseHandler.getCases().isEmpty()) {
                    Util.sendMessage(sender, "§c§oThere are no cases§8.");
                    return false;
                }

                Util.sendMessage(sender, "Alle cases§8:");

                caseHandler.getCases().values().forEach(caze -> {
                    sender.sendMessage("§8 - §7" + caze.getCaseName() + "§8 (" +  caze.getCasePrefix() + "§8)");
                });

                return false;
            }
        }

        if(args.length == 2) {
            final String caseName = args[1].toLowerCase();

            if(args[0].equalsIgnoreCase("create")) {


                if(caseHandler.exist(caseName)) {
                    Util.sendMessage(sender, "A case named §8'§f "+ caseName +" §8'§7 already exists§8.");
                    return false;
                }

                caseHandler.createNewCase(caseName);
                Util.sendMessage(sender, "The §8'§f" + caseName + "§8'§7 case was created§8,§7 edit it using the /lc command§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {

                if(!caseHandler.exist(sender, caseName))
                    return false;

                caseHandler.deleteCase(caseName);
                Util.sendMessage(sender, "The §8'§f" + caseName + "§8'§7 case has been deleted§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("setitem")) {

                if(!caseHandler.exist(sender, caseName))
                    return false;

                if(!(sender instanceof Player))
                    return false;

                final Player player = (Player) sender;

                if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                    Util.sendMessage(sender, "You need to hold an item in your hand§8.");
                    return false;
                }

                caseHandler.getCases().get(caseName).setDisplayItem(player.getItemInHand());
                caseHandler.getCases().get(caseName).saveData();

                Util.sendMessage(sender, "Displayitem for " + caseName + " set§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("edit")) {

                if(!caseHandler.exist(sender, caseName))
                    return false;

                if(!(sender instanceof Player))
                    return false;

                final Player player = (Player) sender;

                player.openInventory(caseHandler.getCases().get(caseName).getInventory("§9Edit ", false));

                return false;
            }

            if(args[0].equalsIgnoreCase("chance")) {

                if(!caseHandler.exist(sender, caseName))
                    return false;

                if(!(sender instanceof Player))
                    return false;

                final Player player = (Player) sender;

                player.openInventory(caseHandler.getCases().get(caseName).getInventory("§9Chance ", false));

                return false;
            }

            if(args[0].equalsIgnoreCase("balance")) {

                if(!caseHandler.exist(sender, caseName))
                    return false;

                if(!(sender instanceof Player))
                    return false;

                final Player player = (Player) sender;

                player.openInventory(caseHandler.getCases().get(caseName).getInventory("§9Balance ", false));

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("setslot")) {

                final String caseName = args[1].toLowerCase();

                if(!caseHandler.exist(sender, caseName))
                    return false;

                try {
                    final int slot = Integer.parseInt(args[2]);

                    if(slot < 0) {
                        Util.sendMessage(sender, "Please use a number greater than 0§8.");
                        return false;
                    }

                    caseHandler.getCases().get(caseName).setDisplaySlot(slot);
                    caseHandler.getCases().get(caseName).saveData();
                    Util.sendMessage(sender, "§8'§f" + caseName + "§8' %cwill now be displayed in slot " + slot + "§8.");

                }catch (final NumberFormatException exception) {
                    Util.sendMessage(sender, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        if(args.length == 4) {
            if (args[0].equalsIgnoreCase("add")) {
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                if (offlinePlayer == null) {
                    Util.sendMessage(sender, args[1] + " war noch nie auf dem Server§8.");
                    return false;
                }

                final String caseName = args[2].toLowerCase();

                if (!caseHandler.exist(sender, caseName))
                    return false;

                final UserCases userCases = DataHandler.get(offlinePlayer.getUniqueId());

                try {
                    final int amount = Integer.parseInt(args[3]);
                    userCases.addCase(caseName, amount);
                    Util.sendMessage(sender, offlinePlayer.getName() + " hat %h" + amount + "%c " + caseName + " Cases bekommen§8.");
                } catch (final NumberFormatException exception) {
                    Util.sendMessage(sender, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        if(args.length >= 3) {
            if(args[0].equalsIgnoreCase("setprefix")) {

                final String caseName = args[1].toLowerCase();

                if(!caseHandler.exist(sender, caseName))
                    return false;

                String prefix = args[2];

                if(args.length > 3) {
                    for (int i = 3; i < args.length; i++)
                        prefix = prefix + " " + args[i];
                }

                caseHandler.getCases().get(caseName).setCasePrefix(prefix);
                caseHandler.getCases().get(caseName).saveData();
                Util.sendMessage(sender, "Prefix für §8'§f" + caseName + "§8'%c zu §r" + prefix + "%c gesetzt§8.");

                return false;
            }
        }

        Util.sendSyntax(sender,
                "/lc reload (Reloade die cached data)", "" ,
                "/lc rlr (Reloade Raritäten in lore für ALLE Items)", "" ,
                "/lc list (List all Cases)", "" ,
                "/lc spawnblock (Spawne einen AccessBlock)", "" ,
                "/lc create <casename> (Erstelle eine neue Crate)", "" ,
                "/lc delete <casename> (Weg damit)", "" ,
                "/lc setitem <casename> (Setze den Casedisplaynamen)", "" ,
                "/lc edit <casename> (Setze die Caseitems)", "" ,
                "/lc chance <casename> (Setze die item Chancen)", "" ,
                "/lc balance <casename> (Balance die Chancen der Items in einer Case)", "" ,
                "/lc setslot <casename> <slot> (Setze den Displayslot einer Case)", "" ,
                "/lc setprefix <casename> <prefix> (Setze den Case Prefix)", "" ,
                "/lc add <spieler> <casename> <amount> (Gebe einem Spieler Cases)"
        );

        return false;
    }
}
