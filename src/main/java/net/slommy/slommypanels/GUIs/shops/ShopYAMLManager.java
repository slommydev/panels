package net.slommy.slommypanels.GUIs.shops;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopYAMLManager {
    public static Shop load(File file) {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String name = cfg.getString("name");
        String title = cfg.getString("title");
        List<ShopItem> items = getShopItems(file);
        return new Shop(items, name, title);
    }

    public static List<ShopItem> getShopItems(File file) {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection items = cfg.getConfigurationSection("items");
        List<ShopItem> shopItems = new ArrayList<>();
        for (String key : items.getKeys(false)) {
            ConfigurationSection item = items.getConfigurationSection(key);
            String itemKey = item.getString("item");
            if (itemKey == null) {
                itemKey = "gui/null_item_field";
            }
            List<ShopExchange> exchanges = new ArrayList<>();
            ConfigurationSection exchangeSection = item.getConfigurationSection("exchanges");
            for (String exchange : exchangeSection.getKeys(false)) {
                ConfigurationSection exchangeCfg = exchangeSection.getConfigurationSection(exchange);
                ShopExchange.ExchangeType type = ShopExchange.ExchangeType.valueOf(exchangeCfg.getString("type"));
                int amount = exchangeCfg.getInt("amount");
                if (type == ShopExchange.ExchangeType.TRADE) {
                    String tradeItemKey = exchangeCfg.getString("item");
                    SlommyItem tradeItem = Items.items.get(tradeItemKey);
                    exchanges.add(new ShopExchange(ShopExchange.ExchangeType.TRADE, amount, tradeItem));
                } else {
                    exchanges.add(new ShopExchange(type, amount));
                }
            }
            SlommyItem slommyItem = Items.items.get(itemKey);
            ShopItem shopItem = new ShopItem(slommyItem, exchanges);
            shopItem.setRequirement("none");
            shopItem.setRequirementMessage("none");
            if (item.contains("requirement")) {
                shopItem.setRequirement(item.getString("requirement"));
            }
            if (item.contains("requirement-message")) {
                shopItem.setRequirementMessage(item.getString("requirement-message"));
            }
            if (item.contains("amount")) {
                shopItem.setAmount(item.getInt("amount"));
            }
            if (item.contains("display-item")) {
                shopItem.setDisplayItem(Items.items.get(item.getString("display-item")));
            }
            shopItems.add(shopItem);
        }
        return shopItems;
    }
}
