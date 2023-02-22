package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 18:14

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

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

    private Thread effectThread;
    private final EffectRunnable effectRunnable = new EffectRunnable();

    public CaseHandler() {}

    public void startEffects() {
        effectThread = new Thread(effectRunnable);
        effectThread.start();
    }

    public void loadCaseBlocks() {
        if(data.contains("caseblocks")) {
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
    }

    public void loadCases() {
        final File folder = new File(Init.getInstance().getDataFolder() + "/cases");

        for(final File caseFile : Objects.requireNonNull(folder.listFiles())){
            loadCaseFromFile(caseFile);
        }
    }

    private void loadCaseFromFile(final File file){
        final String caseName = file.getName().split(".yml")[0].toLowerCase();

        Bukkit.getConsoleSender().sendMessage("§8-> §a§oloaded case§7: §f" + caseName);

        cases.put(caseName, new Case(caseName));
    }

    public void addAccessBlock(final Block block) {
        caseBlocks.add(block);

        final List<String> locations = data.getStringList("caseblocks");

        locations.add(block.getWorld().getName() +  "#" + block.getLocation().getX() + "#" + block.getLocation().getY() + "#" + block.getLocation().getZ() + "#" + block.getLocation().getYaw() + "#" + block.getLocation().getPitch());
        data.set("caseblocks", locations);
        block.setType(Material.ENDER_CHEST);

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

    public boolean exist(final String caseName) {
        return cases.containsKey(caseName.toLowerCase());
    }
}
