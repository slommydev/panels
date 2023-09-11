package net.slommy.slommypanels.GUIs.lootbags;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.CloseButton;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureStash {
    private Player player;
    private Map<TreasureBag, Integer> bags = new HashMap<>();
    private File file;

    public TreasureStash(Player player, File file) {
        this.player = player;
        this.file = file;
    }

    public void loadData() {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection bagsSection = cfg.getConfigurationSection("bags");
        if (bagsSection == null) {
            cfg.createSection("bags");
            try {
                cfg.save(file);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        bagsSection = cfg.getConfigurationSection("bags");
        for (TreasureBag bag : GlobalLootbagManagers.bags) {
            if (bagsSection.contains(bag.getName())) {
                bags.put(bag, bagsSection.getInt(bag.getName()));
            }
            else {
                bags.put(bag, 0);
            }
        }
    }

    public void save() {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection bagsSection = cfg.getConfigurationSection("bags");
        if (bagsSection == null) {
            cfg.createSection("bags");
        }
        bagsSection = cfg.getConfigurationSection("bags");
        for (TreasureBag bag : bags.keySet()) {
            bagsSection.set(bag.getName(), bags.get(bag));
        }
        try {
            cfg.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void giveBag(TreasureBag bag, int amount) {
        if (bags.containsKey(bag)) {
            bags.put(bag, bags.get(bag) + amount);
        }
        else {
            bags.put(bag, amount);
        }
    }

    public void useBag(TreasureBag bag) {
        if (bags.containsKey(bag)) {
            bags.put(bag, bags.get(bag) - 1);
            bag.openTreasureBag(player);
        }
    }

    public MenuHolder<SlommyPanels> getStashMenu(int page) {
        MenuHolder<SlommyPanels> panel;
        int pages = 1;
        for (int i = 0; i < bags.size(); i++) {
            if (i % 28 == 0 && i != 0) {
                pages++;
            }
        }
        if (page > pages) {
            page = pages;
        }
        if (page < 1) {
            page = 1;
        }
        if (pages > 1) {
            panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, "Treasure Stash - Page " + page + "/" + pages);
        }
        else {
            panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, "Treasure Stash");
        }

        List<Integer> takenSlots = new ArrayList<>();
        // make a border of empty panes around the menu
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                panel.setButton(i, new ItemButton<>(Items.items.get("gui/blank_pane").build(player)));
                takenSlots.add(i);
            }
        }
        panel.setButton(49, new CloseButton<>(Items.items.get("gui/close").build(player)));
        if (page != 1) {
            int finalPage = page;
            ItemButton previousPageButton = new ItemButton(Items.items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    MenuHolder<SlommyPanels> panel = getStashMenu(finalPage - 1);
                    player.openInventory(panel.getInventory());
                }
            };
            panel.setButton(45, previousPageButton);
        }
        if (pages > page) {
            int finalPage = page;
            ItemButton nextPageButton = new ItemButton(Items.items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    MenuHolder<SlommyPanels> panel = getStashMenu(finalPage + 1);
                    player.openInventory(panel.getInventory());
                }
            };
            panel.setButton(53, nextPageButton);
        }

        int i = 10;
        int totalBags = 0;
        for (TreasureBag bag : bags.keySet()) {
            totalBags += bags.get(bag);
        }
        if (totalBags < 1) {
            ItemButton noBagsButton = new ItemButton(Items.items.get("gui/no_bags").build(player));
            panel.setButton(22, noBagsButton);
            return panel;
        }
        for (TreasureBag bag : bags.keySet()) {
            while (takenSlots.contains(i)) {
                i++;
            }
            if (i > 44) {
                break;
            }
            if (bags.get(bag) > 0) {
                ItemStack bagItem = bag.getItem().build(player);
                bagItem.setAmount(bags.get(bag));
                ItemMeta meta = bagItem.getItemMeta();
                List<Component> lore;
                if (meta.lore() == null) {
                    lore = new ArrayList<>();
                }
                else {
                    lore = meta.lore();
                }
                lore.add(Msg.JSONcolor("&7You have: &e" + bags.get(bag)));
                meta.lore(lore);
                bagItem.setItemMeta(meta);
                ItemButton bagButton = new ItemButton(bagItem) {
                    @Override
                    public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                        useBag(bag);
                    }
                };
                panel.setButton(i, bagButton);
                takenSlots.add(i);
                i++;
            }
        }

        return panel;
    }
}
