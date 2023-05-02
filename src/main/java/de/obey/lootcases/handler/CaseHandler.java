package de.obey.lootcases.handler;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:14

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import de.obey.lootcases.objects.Case;
import de.obey.lootcases.objects.UserCases;
import de.obey.lootcases.utils.Animation;
import de.obey.lootcases.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
public final class CaseHandler {

    private final Init init = Init.getInstance();

    @Setter
    private YamlConfiguration data;

    private final HashMap<String, Case> cases = new HashMap<>();
    private ArrayList<Block> caseBlocks = new ArrayList<>();
    private final HashMap<Player, Animation> animations = new HashMap<>();
    public final HashMap<Double, String> rarites = new HashMap<>();

    private Thread effectThread;
    private final EffectRunnable effectRunnable = new EffectRunnable();

    public CaseHandler() {}

    public void openCase(final Player player, final Case caze, final boolean instant){
        if(animations.containsKey(player))
            return;

        final UserCases userCases = DataHandler.get(player);

        if(userCases.getAmountOfCase(caze.getCaseName()) <= 0) {
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 0.1f);
            return;
        }

        userCases.removeCase(caze.getCaseName(), 1);
        userCases.updatedCaseInventory();

        new Animation(player, caze, instant);
    }

    public void setRarityForAllItems() {
        if(cases.isEmpty())
            return;

        for (Case value : cases.values()) {
            if(!value.getItems().isEmpty()) {
                for (ItemStack item : value.getItems()) {
                    value.setDisplayLoreForItem(item);
                }
            }
        }
    }

    public void startEffects() {
        effectThread = new Thread(effectRunnable);
        effectThread.start();
    }

    public void loadRarities() {
        rarites.clear();

        final YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(Init.getInstance().getDataFolder().getPath() + "/data.yml"));

        if(data.contains("rarities")) {
            data.getConfigurationSection("rarities").getKeys(false).forEach(chance -> {
                rarites.put(Double.parseDouble(chance), data.getString("rarities." + chance));
            });
        } else {
            data.set("rarities." + 10, "&cSelten");
        }
    }

    public String getRarity(final double chance) {
        String found = "§fCommon";
        double currentMin = 1000;

        if(!rarites.isEmpty()) {
            for (final double min : rarites.keySet()) {
                if(chance <= min) {
                    if (currentMin >= min) {
                        found = rarites.get(min);
                        currentMin = min;
                    }
                }
            }
        }

        return ChatColor.translateAlternateColorCodes('&', found);
    }

    public void loadCaseBlocks() {
        if(data.contains("caseblocks")) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    final List<String> locations = data.getStringList("caseblocks");

                    if(locations == null)
                        return;

                    locations.forEach(crypt -> {
                        final String[] cryptedData = crypt.split("#");
                        // So siehts in der file aus: weltname#x#y#z#yaw#pitch -> world#1#100#1#0#0
                        final Location location = new Location(Bukkit.getWorld(cryptedData[0]),
                                Double.parseDouble(cryptedData[1]),
                                Double.parseDouble(cryptedData[2]),
                                Double.parseDouble(cryptedData[3]),
                                Float.parseFloat(cryptedData[4]),
                                Float.parseFloat(cryptedData[5]));

                        if(location.getWorld() == null)
                            return;

                        caseBlocks.add(location.getBlock());
                    });
                }
            }.runTaskLater(Init.getInstance(), 40);
        }
    }

    public void loadCases() {
        final File folder = new File(Init.getInstance().getDataFolder() + "/cases");

        for(final File caseFile : Objects.requireNonNull(folder.listFiles())){
            loadCaseFromFile(caseFile);
        }
    }

    private void loadCaseFromFile(final File file){
        final String caseName = file.getName().split(".yml")[0].toLowerCase();

        Bukkit.getConsoleSender().sendMessage("§8-> §a§oloaded Case§7: §f" + caseName);

        cases.put(caseName, new Case(caseName));
    }

    public void addAccessBlock(final Block block) {
        caseBlocks.add(block);
        block.setType(Material.ENDER_PORTAL_FRAME);

        final List<String> locations = data.getStringList("caseblocks");

        locations.add(block.getWorld().getName() +  "#" + block.getLocation().getX() + "#" + block.getLocation().getY() + "#" + block.getLocation().getZ() + "#" + block.getLocation().getYaw() + "#" + block.getLocation().getPitch());
        data.set("caseblocks", locations);

        final ArmorStand holo = block.getWorld().spawn(block.getLocation().clone().add(0.5, -0.5, 0.5), ArmorStand.class);
        holo.setVisible(false); holo.setGravity(false); holo.setCustomName(Util.transform("%h§k--%c§l LootCases %h§k--")); holo.setCustomNameVisible(true);

        Init.getInstance().saveFiles();
    }

    public void removeAccessBlock(final Block block) {
        caseBlocks.remove(block);

        final List<String> locations = new ArrayList<>();

        caseBlocks.forEach(current -> locations.add(current.getWorld().getName() +  "#" + current.getLocation().getX() + "#" + current.getLocation().getY() + "#" + current.getLocation().getZ() + "#" + current.getLocation().getYaw() + "#" + current.getLocation().getPitch()));
        data.set("caseblocks", locations);

        block.setType(Material.AIR);

        for (Entity nearbyEntity : block.getWorld().getNearbyEntities(block.getLocation(), 1, 1, 1)) {
            if(nearbyEntity instanceof ArmorStand)
                nearbyEntity.remove();
        }

        Init.getInstance().saveFiles();
    }

    public void createNewCase(final String caseName) {
        if(exist(caseName))
            return;

        cases.put(caseName, new Case(caseName));
    }

    public void deleteCase(final String caseName) {
        if(!exist(caseName))
            return;

        cases.get(caseName).delete();
        cases.remove(caseName);
    }

    public Case getCaseFromDisplayItem(final ItemStack item) {
        if(!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return null;

        for (Case value : cases.values()) {
            if(value.getCasePrefix().equalsIgnoreCase(item.getItemMeta().getDisplayName()))
                return value;
        }

        return null;
    }

    public boolean exist(final CommandSender sender, final String caseName) {
        if(!exist(caseName)) {
            Util.sendMessage(sender, "There is no case named §8'§f "+ caseName +" §8'%c§8.");
            return false;
        }

        return true;
    }

    public boolean exist(final String caseName) {
        return cases.containsKey(caseName.toLowerCase());
    }
}
