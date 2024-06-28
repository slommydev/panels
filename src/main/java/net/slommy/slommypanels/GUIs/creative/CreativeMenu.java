package net.slommy.slommypanels.GUIs.creative;

import net.slommy.essentials.SlommyItems.ConfigReaders.ItemCategories;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.*;

public class CreativeMenu {
    public static void openCategoryMenu(Player player, int page) {
        Map<String, SlommyItem> itemsMap = Items.getItems();
        Map<String, SlommyItem> itemsMap2 = new HashMap<>();
        // Remove the minecraft category
        for (String key : itemsMap.keySet()) {
            if (!key.startsWith("minecraft/")) {
                itemsMap2.put(key, itemsMap.get(key));
            }
        }
        int items = itemsMap2.size();
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 6*9, "Creative Menu (" + items + " items)");
        List<Integer> takenSlots = new ArrayList<>();

        // make a border of empty panes around the shop
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                panel.setButton(i, new ItemButton<>(Items.items.get("gui/blank_pane").build(player)));
                takenSlots.add(i);
            }
        }
        List<String> categories = ItemCategories.getItemCategories();
        categories.sort(String::compareTo);
        // Remove the minecraft category
        categories.remove("minecraft");
        // remove any categories that are not in the range of the page
        int pageIncrement = 28 * (page - 1);
        categories = categories.subList(pageIncrement, Math.min(pageIncrement + 28, categories.size()));

        for (String category : categories) {
            int slot = getNextAvailableSlot(takenSlots);
            ItemStack categoryItem = new ItemStack(Material.CHEST);
            ItemMeta meta = categoryItem.getItemMeta();
            meta.displayName(Msg.JSONcolor("&a" + category));
            categoryItem.setItemMeta(meta);
            panel.setButton(slot, new ItemButton<>(categoryItem) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    CreativeMenu.openCategory(player, category, 1);
                }
            });
            takenSlots.add(slot);
        }

        player.openInventory(panel.getInventory());
    }

    public static void openCategory(Player player, String category, int page) {
        int itemsSize = Items.getItemsInCategory(category).size();
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 6*9, category + " (" + itemsSize + " items) - Page " + page + "/" + (int) Math.ceil((double) itemsSize / 28));
        List<Integer> takenSlots = new ArrayList<>();

        // make a border of empty panes around the shop
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                panel.setButton(i, new ItemButton<>(Items.items.get("gui/blank_pane").build(player)));
                takenSlots.add(i);
            }
        }

        List<SlommyItem> items = Items.getItemsInCategory(category);
        // Organize the list of items by alphabetical order
        items.sort(Comparator.comparing(SlommyItem::getItemID));
        int pageIncrement = 28 * (page - 1);
        for (SlommyItem item : items.subList(pageIncrement, Math.min(pageIncrement + 28, items.size()))) {
            int slot = getNextAvailableSlot(takenSlots);
            panel.setButton(slot, new ItemButton<>(item.build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    Map<Integer, ItemStack> droppedItems = player.getInventory().addItem(item.build(player));
                    for (ItemStack droppedItem : droppedItems.values()) {
                        player.getWorld().dropItem(player.getLocation(), droppedItem);
                    }
                }
            });
            takenSlots.add(slot);
        }

        if (page != 1) {
            ItemButton previousPageButton = new ItemButton(Items.items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    CreativeMenu.openCategory(player, category, page - 1);
                }
            };
            panel.setButton(45, previousPageButton);
        }

        if (items.size() > pageIncrement + 28) {
            ItemButton nextPageButton = new ItemButton(Items.items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    CreativeMenu.openCategory(player, category, page + 1);
                }
            };
            panel.setButton(53, nextPageButton);
        }

        ItemButton backButton = new ItemButton(Items.items.get("gui/back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                CreativeMenu.openCategoryMenu(player, 1);
            }
        };

        panel.setButton(49, backButton);

        player.openInventory(panel.getInventory());
    }

    public static int getNextAvailableSlot(List<Integer> takenSlots) {
        for (int i = 0; i < 54; i++) {
            if (!takenSlots.contains(i)) {
                return i;
            }
        }
        return 0;
    }
}
