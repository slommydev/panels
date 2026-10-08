package net.slommy.slommypanels.GUIs.lootbags;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.slommy.essentials.SlommyItems.ItemUpdateUtils;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.SlommyItems.enchantments.SlommyEnchantment;
import net.slommy.essentials.SlommyItems.enchantments.util.EnchantmentRegister;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.RomanNumeral;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureBag implements InventoryHolder {
    private SlommyItem item;
    private String name;
    private String displayName;
    private LootTable lootTable;
    private LockPicker lockPicker;
    public Map<String, Boolean> lockpickAllowed = new HashMap<>();

    public TreasureBag(SlommyItem item, String name, String displayName, LootTable lootTable) {
        this.item = item;
        this.name = name;
        this.displayName = displayName;
        this.lootTable = lootTable;
    }

    public SlommyItem getItem() {
        return item;
    }

    public void setItem(SlommyItem item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    public void openTreasureBag(Player player) {
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this,36, Msg.JSONcolor(displayName));
        this.lockpickAllowed.put(player.getName(), true);

        // Fill inventory with items from loot table
        List<LootItem> drops = lootTable.getListOfDrops(player);
        List<Integer> takenSlots = new ArrayList<>();
        for (LootItem drop : drops) {
            int slot = (int) (Math.random() * 27);
            while (takenSlots.contains(slot)) {
                slot = (int) (Math.random() * 27);
            }
            if (LootTable.Rarity.getRarityByChance(lootTable.getChance(drop)) == LootTable.Rarity.EXTREMELY_RARE || LootTable.Rarity.getRarityByChance(lootTable.getChance(drop)) == LootTable.Rarity.CRAZY_RARE || LootTable.Rarity.getRarityByChance(lootTable.getChance(drop)) == LootTable.Rarity.INDUBITABLY_RARE) {
                // broadcast to server
                LootTable.Rarity rarity = LootTable.Rarity.getRarityByChance(lootTable.getChance(drop));
                List<Player> onlinePlayers = new ArrayList<>(SlommyPanels.getInstance().getServer().getOnlinePlayers());
                if (EnchantmentRegister.getEnchantmentIDs().contains(drop.getEnchantmentID()) && drop.getSlommyItem().getItemID().equalsIgnoreCase("items/enchantment_tome")) {
                    SlommyEnchantment ench = EnchantmentRegister.getEnchant(drop.getEnchantmentID());
                    String simulatedItemName = ench.Rarity().color() + "Enchantment Tome &7(" + ench.Name() + "&7)";
                    for (Player onlinePlayer : onlinePlayers) {
                        Msg.sendJSON(onlinePlayer, LootTable.Rarity.getRarityString(rarity) + "! &b" + player.getName() + " &ejust got a &d" + simulatedItemName + "&e from a " + item.getDisplayName() + "&e!");
                    }
                } else {
                    for (Player onlinePlayer : onlinePlayers) {
                        Msg.sendJSON(onlinePlayer, LootTable.Rarity.getRarityString(rarity) + "! &b" + player.getName() + " &ejust got a &d" + drop.getSlommyItem().getDisplayName() + "&e from a " + item.getDisplayName() + "&e!");
                    }
                }
            }
            try {
                ItemStack itemStack = drop.getSlommyItem().build(player);
                if (!drop.getEnchantmentID().equalsIgnoreCase("")) {
                    if (EnchantmentRegister.getEnchantmentIDs().contains(drop.getEnchantmentID())) {
                        SlommyEnchantment enchantment = EnchantmentRegister.getEnchant(drop.getEnchantmentID());
                        enchantment.apply(itemStack, drop.getEnchantmentLevel(), false);
                    } else {
                        Msg.debugBroadcast(drop.getEnchantmentID() + " is not found in enchantment registry");
                    }
                }
                itemStack.setAmount(drop.getAmount());
                inventory.setItem(slot, itemStack);
                takenSlots.add(slot);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("Could not add item " + drop.getId());
                Msg.send(player, "&c&lERROR! &7Could not add item &e" + drop.getId() + "&7 to your treasure bag. :( At least you could marvel at what could've been...");
            }

        }
        Msg.send(player, "&e&lTREASURE! &7You opened " + item.getDisplayName() + ":");
        List<String> sentDrops = new ArrayList<>();
        for (LootItem drop : drops) {
            // group all the drops of the same item together, and then send a message
            int amount = 0;
            for (LootItem drop2 : drops) {
                if (drop2.getId().equals(drop.getId())) {
                    amount += drop2.getAmount();
                }
            }
            if (sentDrops.contains(drop.getId())) {
                continue;
            }
            sentDrops.add(drop.getId());
            Msg.send(player, "&7- &e" + amount + "x " + drop.getSlommyItem().getDisplayName());
        }

        for (int i = 27; i < 36; i++) {
            inventory.setItem(i, Items.items.get("gui/blank_pane").build(player));
        }
        inventory.setItem(31, Items.items.get("gui/close").build(player));
        ItemStack treasureStash = Items.items.get("gui/treasure_stash").build(player);
        ItemMeta meta = treasureStash.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Msg.JSONcolor("&7Click to return to your &6stash&7."));
        meta.lore(lore);
        treasureStash.setItemMeta(meta);
        inventory.setItem(32, treasureStash);
        ItemStack bag = item.build(player);
        TreasureStash stash = TreasureStashUtil.getTreasureStash(player);
        if (stash.getBagAmount(this.name) > 0) {
            ItemMeta meta2 = bag.getItemMeta();
            List<Component> lore2 = new ArrayList<>();
            lore2.add(Msg.JSONcolor("&7You have &e" + stash.getBagAmount(this.name) + " &7left."));
            lore2.add(Msg.JSONcolor("&eClick to open another!"));
            meta2.lore(lore2);
            bag.setItemMeta(meta2);
            inventory.setItem(33, bag);
        }
        if (this.lockPicker != null) {
            if (stash.getLockpickAmount(this.lockPicker.getName()) > 0) {
                ItemStack lockpick = this.lockPicker.getItem().build(player);
                ItemMeta meta2 = lockpick.getItemMeta();
                List<Component> lore2 = new ArrayList<>();
                lore2.add(Msg.JSONcolor("&7You have &e" + stash.getLockpickAmount(this.lockPicker.getName()) + " &7left."));
                lore2.add(Msg.JSONcolor("&eClick to use a lockpick!"));
                meta2.lore(lore2);
                lockpick.setItemMeta(meta2);
                inventory.setItem(30, lockpick);
            }
        }
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 2f);
        player.openInventory(inventory);
        for (int i = 0; i < 27; i++) {
            ItemUpdateUtils.updateItemInContainer(player, i);
        }
    }

    public LockPicker getLockPicker() {
        return lockPicker;
    }

    public void setLockPicker(LockPicker lockPicker) {
        this.lockPicker = lockPicker;
    }

    public List<LootItem> getAllDrops() {
        return lootTable.getAllDropItems();
    }

    public MenuHolder<SlommyPanels> getLootDropViewPanel(Player player, int page) {
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, Msg.color(displayName + " Loot"));
        List<Integer> takenSlots = new ArrayList<>();
        // make a border of empty panes around the menu
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                panel.setButton(i, new ItemButton<>(Items.items.get("gui/blank_pane").build(player)));
                takenSlots.add(i);
            }
        }

        ItemStack bagItem = item.build(player);
        ItemMeta bagItemMeta = bagItem.getItemMeta();
        List<Component> bagItemLore = new ArrayList<>();
        bagItemLore.add(Msg.JSONcolor("&7Main Drop Rolls: &e" + lootTable.getMainDropAmount() + " " + lootTable.getBossLuckStringForMainDrops(player)));
        bagItemLore.add(Msg.JSONcolor("&7Secondary Drop Rolls: &e" + lootTable.getSecondaryDropAmount() + " " + lootTable.getBossLuckStringForSecondaryDrops(player)));
        bagItemMeta.lore(bagItemLore);
        bagItem.setItemMeta(bagItemMeta);
        panel.setButton(4, new ItemButton<>(bagItem));


        List<LootItem> drops = getAllDrops();
        // sort drops by whether they are a main drop or a secondary drop, then by drop chance (rarest to least rare), then alphabetically by item category, then alphabetically by item name
        drops.sort((o1, o2) -> {
            if (lootTable.getMainDrops().containsKey(o1) && !lootTable.getMainDrops().containsKey(o2)) {
                return -1;
            }
            if (!lootTable.getMainDrops().containsKey(o1) && lootTable.getMainDrops().containsKey(o2)) {
                return 1;
            }
            if (lootTable.getMainDrops().containsKey(o1) && lootTable.getMainDrops().containsKey(o2)) {
                if (lootTable.getChance(o1) < lootTable.getChance(o2)) {
                    return -1;
                }
                if (lootTable.getChance(o1) > lootTable.getChance(o2)) {
                    return 1;
                }
            }
            if (lootTable.getSecondaryDrops().containsKey(o1) && !lootTable.getSecondaryDrops().containsKey(o2)) {
                return -1;
            }
            if (!lootTable.getSecondaryDrops().containsKey(o1) && lootTable.getSecondaryDrops().containsKey(o2)) {
                return 1;
            }
            if (lootTable.getSecondaryDrops().containsKey(o1) && lootTable.getSecondaryDrops().containsKey(o2)) {
                if (lootTable.getChance(o1) < lootTable.getChance(o2)) {
                    return -1;
                }
                if (lootTable.getChance(o1) > lootTable.getChance(o2)) {
                    return 1;
                }
            }
            if (o1.getSlommyItem().getItemCategory().compareTo(o2.getSlommyItem().getItemCategory()) < 0) {
                return -1;
            }
            if (o1.getSlommyItem().getItemCategory().compareTo(o2.getSlommyItem().getItemCategory()) > 0) {
                return 1;
            }
            if (o1.getSlommyItem().getItemID().compareTo(o2.getSlommyItem().getItemID()) < 0) {
                return -1;
            }
            if (o1.getSlommyItem().getItemID().compareTo(o2.getSlommyItem().getItemID()) > 0) {
                return 1;
            }
            return 0;
        });

        int pages = (int) Math.ceil(drops.size() / 27.0);
        if (page != 1) {
            ItemButton previousPageButton = new ItemButton(Items.items.get("gui/prev_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    MenuHolder<SlommyPanels> panel = getLootDropViewPanel(player, page - 1);
                    player.openInventory(panel.getInventory());
                }
            };
            panel.setButton(45, previousPageButton);
        }
        if (page != pages) {
            ItemButton nextPageButton = new ItemButton(Items.items.get("gui/next_page").build(player)) {
                @Override
                public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                    MenuHolder<SlommyPanels> panel = getLootDropViewPanel(player, page + 1);
                    player.openInventory(panel.getInventory());
                }
            };
            panel.setButton(53, nextPageButton);
        }

        List<LootItem> itemsOnPage = new ArrayList<>();
        for (int i = 0; i < 28; i++) {
            if (drops.size() > i + (page - 1) * 27) {
                itemsOnPage.add(drops.get(i + (page - 1) * 27));
            }
        }
        for (int i = 0; i < itemsOnPage.size(); i++) {
            LootItem item = itemsOnPage.get(i);
            int slot = i;
            while (takenSlots.contains(slot)) {
                slot++;
            }
            ItemStack itemStack = item.getSlommyItem().build(player);
            ItemMeta meta = itemStack.getItemMeta();
            List<Component> lore = new ArrayList<>();
            if (!item.getEnchantmentID().equalsIgnoreCase("")) {
                if (EnchantmentRegister.getEnchantmentIDs().contains(item.getEnchantmentID())) {
                    lore.add(Msg.JSONcolor("&9" + EnchantmentRegister.getEnchant(item.getEnchantmentID()).Name() + " " +  RomanNumeral.convert(item.getEnchantmentLevel())));
                    if (item.getSlommyItem().getItemID().equalsIgnoreCase("enchantment_tome")) {
                        String itemNamePlainText = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
                        meta.displayName(Msg.JSONcolor(EnchantmentRegister.getEnchant(item.getEnchantmentID()).Rarity().color() + itemNamePlainText));
                    }
                }
            }
            if (lootTable.getMainDrops().containsKey(item)) {
                lore.add(Msg.JSONcolor("&7Main Drop"));
            } else {
                lore.add(Msg.JSONcolor("&7Secondary Drop"));
            }
            double chance = lootTable.getChance(item);
            DecimalFormat df = new DecimalFormat("#.##");
            LootTable.Rarity rarity = LootTable.Rarity.getRarityByChance(chance);
            lore.add(Msg.JSONcolor(LootTable.Rarity.getRarityString(rarity)));
            if (player.hasPermission("dev.true")) {
                lore.add(Msg.JSONcolor("&8(Admin Only) Drop Chance: " + df.format(chance * 100) + "%"));
            }
            meta.lore(lore);
            itemStack.setItemMeta(meta);
            itemStack.setAmount(item.getAmount());
            panel.setButton(slot, new ItemButton<>(itemStack));
            takenSlots.add(slot);
        }

        panel.setButton(49, new ItemButton(Items.items.get("gui/back").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                player.closeInventory();
                Bukkit.dispatchCommand(player, "stash");
            }
        });

        return panel;
    }

    public void viewLootDrops(Player player) {
        MenuHolder<SlommyPanels> panel = getLootDropViewPanel(player, 1);
        player.openInventory(panel.getInventory());
    }

    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this,27, Msg.JSONcolor(displayName));
    }
}
