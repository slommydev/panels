package net.slommy.slommypanels.GUIs.enrichment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.slommy.essentials.PlayerStats.PlayerTrackers;
import net.slommy.essentials.SlommyItems.*;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerTrackerUtil;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class EnrichmentMenuClickEvents implements Listener {
    @EventHandler
    public void onClickItemEnrichmentMenu(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof EnrichmentMenu) {
            Inventory inv = event.getInventory();
            ItemStack item = event.getCurrentItem();
            if (item == null) {
                return;
            }
            String itemID = GetSlommyItem.getFromItemTag(item);
            if (itemID.split("/")[0].equalsIgnoreCase("gui")) {
                event.setCancelled(true);
                if (itemID.split("/")[1].equalsIgnoreCase("add_enrichable_item")) {
                    SelectItemToEnrichMenu menu = new SelectItemToEnrichMenu();
                    menu.openEnrichableItemsMenu((Player) event.getWhoClicked());
                }
                if (itemID.split("/")[1].equalsIgnoreCase("locked_enrichment_slot")) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) {
                        return;
                    }
                    // Check which slot was clicked on by getting the number from the Display Name
                    int slot = 0;
                    try {
                        String displayNameString = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
                        slot = Integer.parseInt(displayNameString.split("#")[1]);
                    }
                    catch (Exception e) {
                        Msg.send((Player) event.getWhoClicked(), "&cError: &7" + e.getMessage());
                        return;
                    }
                    if (slot != PersistentDataUtils.getUnlockedEnrichmentSlots(Objects.requireNonNull(inv.getItem(13))) + 1) {
                        Msg.send((Player) event.getWhoClicked(), "&cYou must unlock the previous slot first!");
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0f);
                        return;
                    }
                    // Check if the player has enough money to unlock the slot
                    double cost = Math.pow(2, slot + 3);
                    PlayerTrackers trackers = PlayerTrackerUtil.getPlayerTracker((Player) event.getWhoClicked());
                    if (trackers.player_bank_balance < cost) {
                        Msg.send((Player) event.getWhoClicked(), "&cYou need &7" + cost + " &cto unlock this slot!");
                        return;
                    }
                    // Remove the money from the player's bank
                    trackers.player_bank_balance -= cost;
                    // Unlock the Slot
                    PersistentDataUtils.setUnlockedEnrichmentSlots(Objects.requireNonNull(inv.getItem(13)), slot);
                    ItemUpdateUtils.updateItemInContainer(((Player) event.getWhoClicked()), 13);
                    // Refresh the GUI
                    EnrichmentMenu menu = new EnrichmentMenu();
                    menu.openEnrichmentAnvil((Player) event.getWhoClicked(), Objects.requireNonNull(inv.getItem(13)));
                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.5f);
                }
                if (itemID.split("/")[1].equalsIgnoreCase("open_enrichment_slot")) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) {
                        return;
                    }
                    // Check which slot was clicked on by getting the number from the Display Name
                    int slot = 0;
                    try {
                        String displayNameString = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
                        slot = Integer.parseInt(displayNameString.split("#")[1]);
                    }
                    catch (Exception e) {
                        Msg.send((Player) event.getWhoClicked(), "&cError: &7" + e.getMessage());
                        return;
                    }
                    if (slot <= PersistentDataUtils.getUnlockedEnrichmentSlots(Objects.requireNonNull(inv.getItem(13)))) {
                        SelectEnrichmentMenu menu = new SelectEnrichmentMenu();
                        menu.openEnrichmentSelectMenu((Player) event.getWhoClicked(), Objects.requireNonNull(inv.getItem(13)), slot);
                    }
                }
                return;
            }
            if (event.getRawSlot() > 9*6 - 1) {
                event.setCancelled(true);
                return;
            }
            if (event.getSlot() == 13) {
                event.setCancelled(true);
                event.getWhoClicked().getInventory().addItem(item);
                inv.setItem(13, Items.items.get("gui/add_enrichable_item").build((Player) event.getWhoClicked()));
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.5f);
                EnrichmentMenu menu = new EnrichmentMenu();
                menu.openEnrichmentAnvil((Player) event.getWhoClicked(), null);
                return;
            }
            SlommyItem slommyItem = Items.items.get(itemID);
            if (slommyItem == null) {
                return;
            }
            if (!slommyItem.getType().equalsIgnoreCase("enrichment")) {
                return;
            }
            event.setCancelled(true);
            if (event.isLeftClick()) {
                PlayerTrackers trackers = PlayerTrackerUtil.getPlayerTracker((Player) event.getWhoClicked());
                if (trackers.player_bank_balance < Item.rarity.enrichmentRemoveCost(Item.findRarity(slommyItem.getRarity()))) {
                    Msg.send((Player) event.getWhoClicked(), "&cYou need &7" + Item.rarity.enrichmentRemoveCost(Item.findRarity(slommyItem.getRarity())) + " &cto remove this enrichment!");
                    return;
                } else {
                    trackers.player_bank_balance -= Item.rarity.enrichmentRemoveCost(Item.findRarity(slommyItem.getRarity()));
                    List<PersistentItemData> persistentItemData = GetSlommyItem.getPersistentDataValues(inv.getItem(13));
                    ItemMeta meta = item.getItemMeta();
                    int enrichmentSlot = meta.getPersistentDataContainer().get(new NamespacedKey(SlommyPanels.getInstance(), "enrichment-slot"), PersistentDataType.INTEGER);
                    for (PersistentItemData data : persistentItemData) {
                        if (data.getApplication() == PersistentItemData.Application.ENRICHMENT) {
                            if (data.getName().equalsIgnoreCase("slot" + enrichmentSlot)) {
                                ItemStack newItem = PersistentDataUtils.removePersistentDataTag(inv.getItem(13), data);
                                inv.setItem(13, newItem);
                                // update the Item
                                ItemUpdateUtils.updateItemInContainer(((Player) event.getWhoClicked()), 13);
                                break;
                            }
                        }
                    }
                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1.5f);
                    // Give the player the enrichment
                    ItemStack enrichment = Items.items.get(itemID).build((Player) event.getWhoClicked());
                    enrichment.setAmount(1);
                    event.getWhoClicked().getInventory().addItem(enrichment);
                }
                // Refresh the GUI
                EnrichmentMenu menu = new EnrichmentMenu();
                menu.openEnrichmentAnvil((Player) event.getWhoClicked(), Objects.requireNonNull(inv.getItem(13)));
                ItemUpdateUtils.updateItemInContainer(((Player) event.getWhoClicked()), 13);
            }
            if (event.isRightClick()) {
                // Remove the enrichment from the item without giving it to the player
                List<PersistentItemData> persistentItemData = GetSlommyItem.getPersistentDataValues(inv.getItem(13));
                ItemMeta meta = item.getItemMeta();
                int enrichmentSlot = meta.getPersistentDataContainer().get(new NamespacedKey(SlommyPanels.getInstance(), "enrichment-slot"), PersistentDataType.INTEGER);
                for (PersistentItemData data : persistentItemData) {
                    if (data.getApplication() == PersistentItemData.Application.ENRICHMENT) {
                        if (data.getName().equalsIgnoreCase("slot" + enrichmentSlot)) {
                            ItemStack newItem = PersistentDataUtils.removePersistentDataTag(inv.getItem(13), data);
                            inv.setItem(13, newItem);
                            // update the Item
                            ItemUpdateUtils.updateItemInContainer(((Player) event.getWhoClicked()), 13);
                            break;
                        }
                    }
                }
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2f);
                // Refresh the GUI
                EnrichmentMenu menu = new EnrichmentMenu();
                menu.openEnrichmentAnvil((Player) event.getWhoClicked(), Objects.requireNonNull(inv.getItem(13)));
                ItemUpdateUtils.updateItemInContainer(((Player) event.getWhoClicked()), 13);
            }
        }
    }

    @EventHandler
    public void onClickItemSelectItemMenu(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SelectItemToEnrichMenu) {
            Inventory inv = event.getInventory();
            ItemStack item = event.getCurrentItem();
            if (item == null) {
                return;
            }
            String itemID = GetSlommyItem.getFromItemTag(item);
            if (itemID.split("/")[0].equalsIgnoreCase("gui")) {
                event.setCancelled(true);
                return;
            }
            if (event.getRawSlot() > 9*6 - 1) {
                event.setCancelled(true);
                return;
            }
            else {
                EnrichmentMenu menu = new EnrichmentMenu();
                menu.openEnrichmentAnvil((Player) event.getWhoClicked(), item);
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1.5f);
            }
        }
    }

    @EventHandler
    public void onClickEnrichmentSelectMenu(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SelectEnrichmentMenu) {
            Inventory inv = event.getInventory();
            ItemStack item = event.getCurrentItem();
            if (item == null) {
                return;
            }
            String itemID = GetSlommyItem.getFromItemTag(item);
            if (itemID.split("/")[0].equalsIgnoreCase("gui")) {
                event.setCancelled(true);
                if (itemID.split("/")[1].equalsIgnoreCase("back")) {
                    EnrichmentMenu menu = new EnrichmentMenu();
                    menu.openEnrichmentAnvil((Player) event.getWhoClicked(), Objects.requireNonNull(inv.getItem(4)));
                }
                return;
            }
            SlommyItem slommyItem = Items.items.get(itemID);
            if (slommyItem == null) {
                return;
            }
            if (!slommyItem.getType().equalsIgnoreCase("enrichment")) {
                return;
            }
            ItemStack itemToEnrich = inv.getItem(4);
            String EnrichmentSlot = PlainTextComponentSerializer.plainText().serialize(inv.getItem(0).getItemMeta().displayName());
            PersistentItemData enrichment = new PersistentItemData("slot" + EnrichmentSlot, PersistentItemData.Application.ENRICHMENT, itemID);
            itemToEnrich = PersistentDataUtils.addPersistentDataTag(itemToEnrich, enrichment);
            inv.setItem(4, itemToEnrich);
            // take away one of the items with the ItemID in the player's inventory
            Inventory playerInventory = event.getWhoClicked().getInventory();
            for (int i = 0; i < playerInventory.getSize(); i++) {
                ItemStack playerItem = playerInventory.getItem(i);
                if (playerItem == null) {
                    continue;
                }
                String playerItemID = GetSlommyItem.getFromItemTag(playerItem);
                if (playerItemID == null) {
                    continue;
                }
                if (playerItemID.equalsIgnoreCase(itemID)) {
                    playerItem.setAmount(playerItem.getAmount() - 1);
                    playerInventory.setItem(i, playerItem);
                    break;
                }
            }
            ItemUpdateUtils.updateItemInContainer(((Player) event.getWhoClicked()), 4);
            EnrichmentMenu menu = new EnrichmentMenu();
            menu.openEnrichmentAnvil((Player) event.getWhoClicked(), itemToEnrich);
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1.5f);
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1);
            ItemUpdateUtils.updateItemInContainer(((Player) event.getWhoClicked()), 13);
        }
    }

    @EventHandler
    public void onEnrichmentMenuClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnrichmentMenu)) {
            return;
        }
        if (!(GetSlommyItem.getFromItemTag(Objects.requireNonNull(event.getInventory().getItem(13))).split("/")[0].equalsIgnoreCase("gui"))) {
            if (event.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) {
                return;
            }
            event.getPlayer().getInventory().addItem(Objects.requireNonNull(event.getInventory().getItem(13)));
        }
    }

    @EventHandler
    public void onEnrichmentSelectionClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof SelectEnrichmentMenu)) {
            return;
        }
        if (event.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) {
            return;
        }
        event.getPlayer().getInventory().addItem(Objects.requireNonNull(event.getInventory().getItem(4)));
    }

    @EventHandler
    public void onDragItem(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof EnrichmentMenu) {
            event.setCancelled(true);
        }
        if (event.getInventory().getHolder() instanceof SelectItemToEnrichMenu) {
            event.setCancelled(true);
        }
        if (event.getInventory().getHolder() instanceof SelectEnrichmentMenu) {
            event.setCancelled(true);
        }
    }
}
