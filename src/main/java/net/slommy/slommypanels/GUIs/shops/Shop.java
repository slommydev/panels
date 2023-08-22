package net.slommy.slommypanels.GUIs.shops;

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
            if (i % 28 == 0) {
                pages++;
            }
        }
    }

    public List<ShopItem> getItems() {
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
}
