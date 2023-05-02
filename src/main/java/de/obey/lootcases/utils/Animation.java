package de.obey.lootcases.utils;
/*

    Author - Obey -> Crate-v6
       13.08.2022 / 00:30

*/

import de.obey.lootcases.Init;
import de.obey.lootcases.handler.CaseHandler;
import de.obey.lootcases.handler.DataHandler;
import de.obey.lootcases.objects.Case;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

public final class Animation {

    private CaseHandler caseHandler;

    private final Player player;
    private final Case caze;
    private Inventory inventory;
    private BukkitTask runnable;
    private final ArrayList<ItemStack> items;

    public Inventory getInventory() {
        return inventory;
    }

    public Animation(final Player player, final Case caze, final boolean instant) {
        this.player = player;
        this.caze = caze;

        items = caze.getChanceList();

        if(instant) {
            giveWin(player, getRandomItem());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            return;
        }

        caseHandler = Init.getInstance().getCaseHandler();
        caseHandler.getAnimations().put(player, this);

        inventory = Bukkit.createInventory(null, 9*3, "§7Öffne§8: " + caze.getCasePrefix());

        for(int slot = 0; slot < 27; slot++) {
            inventory.setItem(slot, getRandomPane());
        }

        final ItemStack gewinn = new ItemStack(Material.IRON_FENCE, 1);
        final ItemMeta meta = gewinn.getItemMeta();

        meta.setDisplayName("§eGewinn");
        gewinn.setItemMeta(meta);

        inventory.setItem(4, gewinn);
        inventory.setItem(22, gewinn);

        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
        player.openInventory(inventory);

        startAnimation();
    }

    public void startAnimation(){
        DataHandler.executor.execute(() -> runnable = new BukkitRunnable() {

            int ticks, delay, delayState, state = 0;

            @Override
            public void run() {

                if(state == 0) {
                    if (ticks == 5) {
                        for (int i = 9; i < 17; i++) {
                            inventory.setItem(i, getRandomItem());
                        }

                        state = 1;
                        ticks = 0;
                    }
                }

                if(state == 1){
                    switch (ticks) {
                        case 40 : delay = 3; break;
                        case 80 : delay = 7; break;
                        case 120 : delay = 15; break;
                        case 160 : delay = 22; break;
                        case 200 : end(); break;
                    }

                    if(delayState == delay) {
                        delayState = 0;
                        spin();
                    } else {
                        delayState++;
                    }
                }

                spinPanes();
                ticks++;
            }
        }.runTaskTimer(Init.getInstance(), 0, 1));
    }

    private void end() {
        runnable.cancel();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

        ItemStack win = inventory.getItem(13);

        for(ItemStack result : caze.getItems()) {
            if(result.equals(win)) {
                win = result;
                break;
            }
        }

        Init.getInstance().getCaseHandler().getAnimations().remove(player);
        giveWin(player, win);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(Init.getInstance(), 20);
    }

    public void forceEnd() {
        runnable.cancel();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

        Init.getInstance().getCaseHandler().getAnimations().remove(player);
        giveWin(player, getRandomItem());
    }

    private void giveWin(final Player player, final ItemStack win){
        final ItemStack item = win.clone();
        final ItemMeta meta = item.getItemMeta();

        final ArrayList<String> lore = (ArrayList<String>) meta.getLore();

        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);

        meta.setLore(lore);
        item.setItemMeta(meta);

        if(player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            return;
        }

        player.getInventory().addItem(item);
    }

    private void spin() {
      DataHandler.executor.execute(() -> {
            inventory.setItem(17, inventory.getItem(16));
            inventory.setItem(16, inventory.getItem(15));
            inventory.setItem(15, inventory.getItem(14));
            inventory.setItem(14, inventory.getItem(13));
            inventory.setItem(13, inventory.getItem(12));
            inventory.setItem(12, inventory.getItem(11));
            inventory.setItem(11, inventory.getItem(10));
            inventory.setItem(10, inventory.getItem(9));
            inventory.setItem(9, getRandomItem());
        });

        player.playSound(player.getLocation(), Sound.CLICK, 0.4f, 0.4f);
    }

    private void spinPanes(){
        for(int i = 0; i < 9; i++){
            if(i != 4)
                inventory.setItem(i, getRandomPane());
        }

        for(int i = 18; i < 27; i++){
            if(i != 22)
                inventory.setItem(i, getRandomPane());
        }
    }

    private ItemStack getRandomPane(){
        final Random random = new Random();

        int number = 0;

        while (number == 0 || number == 8 || number == 7 || number == 12 || number == 15){
            number = random.nextInt(16);
        }

        final ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) number);
        final ItemMeta meta = pane.getItemMeta();

        meta.setDisplayName("§7-§8/§7-");
        pane.setItemMeta(meta);

        return pane;
    }

    private ItemStack getRandomItem(){
        return items.get(new Random().nextInt(items.size()));
    }

}
