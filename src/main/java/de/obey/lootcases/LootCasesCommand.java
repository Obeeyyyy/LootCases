package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 19:10

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

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

        final CaseHandler caseHandler = Init.getInstance().getCaseHandler();

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("spawnblock")) {

                if(!(sender instanceof Player))
                    return false;

                final Block block = ((Player) sender).getLocation().getBlock();

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
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("create")) {

                final String caseName = args[1].toLowerCase();

                if(caseHandler.exist(caseName)) {
                    Util.sendMessage(sender, "A case named §8'§f "+ caseName +" §8'§7 already exists§8.");
                    return false;
                }

                caseHandler.createNewCase(caseName);
                Util.sendMessage(sender, "The §8'§f" + caseName + "§8'§7 case was created§8,§7 edit it using the /lc command§8.");

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("setslot")) {

                final String caseName = args[1].toLowerCase();

                if(!caseHandler.exist(caseName)) {
                    Util.sendMessage(sender, "There is no case named §8'§f "+ caseName +" §8'%c§8.");
                    return false;
                }

                try {
                    final int slot = Integer.parseInt(args[2]);

                    if(slot < 0) {
                        Util.sendMessage(sender, "Please use a number greater than 0§8.");
                        return false;
                    }

                    caseHandler.getCases().get(caseName).setDisplaySlot(slot);
                    Util.sendMessage(sender, "§8'§f" + caseName + "§8' %cwill now be displayed in slot " + slot + "§8.");

                }catch (final NumberFormatException exception) {
                    Util.sendMessage(sender, "Please use a number§8.");
                }

                return false;
            }
        }

        Util.sendSyntax(sender,
                "/lc reload (Reloads the cached data)",
                "/lc spawnblock (Create a access block)",
                "/lc create <casename> (Create a new case)",
                "/lc setslot <casename> <slot> (Set a cases displayslot)",
                "/lc settexture <casename> <texture> (Set a cases skull texture)",
                "/lc setslot <casename> <slot> (Set a cases displayslot)",
                "/lc setslot <casename> <slot> (Set a cases displayslot)"
        );

        return false;
    }
}
