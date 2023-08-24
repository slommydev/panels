package net.slommy.slommypanels.GUIs.shops;

import org.bukkit.Bukkit;

import java.io.File;
import java.util.List;

public class Shop {
    private List<ShopItem> items;
    private String name;
    private String title;
    private int pages = 1;

    public Shop(List<ShopItem> items, String name, String title) {
        this.items = items;
        this.name = name;
        this.title = title;
        for (int i = 0; i < items.size(); i++) {
            // split the items into pages of 28 items each
            if (i % 28 == 0 && i != 0) {
                pages++;
            }
        }
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public List<ShopItem> getItems(int page) {
        List<ShopItem> items = this.items;
        if (page != 1) {
            items = items.subList((page - 1) * 28, items.size());
        }
        if (items.size() > 28) {
            items = items.subList(0, 28);
        }
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return 54;
    }

    public static Shop getShop(String filePath) {
        // check if the file exists
        File file = new File(filePath);
        if (!file.exists()) {
            Bukkit.getLogger().warning("Shop file " + filePath + " does not exist");
            return null;
        }
        return ShopYAMLManager.load(file);
    }

    public static String getShopPath() {
        return Bukkit.getPluginsFolder() + "/SlommyPanels/shops/";
    }

    public static String getShopPath(String name) {
        return getShopPath() + name + ".yml";
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
