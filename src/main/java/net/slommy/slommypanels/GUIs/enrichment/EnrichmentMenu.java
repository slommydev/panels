package net.slommy.slommypanels.GUIs.enrichment;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyEssentials;
import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.IntegerCommaParser;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.SlommyItemExchange;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class EnrichmentMenu implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this,27, Msg.JSONcolor("Enrichment Anvil"));
    }

    public void openEnrichmentAnvil(Player player, ItemStack item) {
        Inventory inventory = SlommyPanels.getInstance().getServer().createInventory(this, 6*9, Msg.JSONcolor("Enrichment Anvil"));
        // Fill inventory with blank panes
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, Items.items.get("gui/blank_pane").build(player));
        }
        // Open up the slot for the item to be enriched
        if (item != null) {
            inventory.setItem(13, item);
            Inventory playerInventory = player.getInventory();
            // Remove the item from the player's inventory
            for (int i = 0; i < playerInventory.getSize(); i++) {
                if (playerInventory.getItem(i) != null && playerInventory.getItem(i).equals(item)) {
                    playerInventory.setItem(i, null);
                    break;
                }
            }
            SlommyItem recombobulatorItem = Items.items.get("items/gizmoco_enrichup_booster");
            if (SlommyItemExchange.checkInventory(player, recombobulatorItem) > 0) {
                if (!PersistentDataUtils.getHasRarityBoost(item) && Items.items.get(GetSlommyItem.getFromItemTag(item)).getRarityEnum() != Item.rarity.MYSTICAL && Items.items.get(GetSlommyItem.getFromItemTag(item)).getRarityEnum() != Item.rarity.MYTHIC) {
                    ItemStack recombobulator = recombobulatorItem.build(player);
                    ItemMeta meta = recombobulator.getItemMeta();
                    List<Component> lore = new ArrayList<>();
                    lore.add(Msg.JSONcolor("&7Click to consume a " + recombobulatorItem.getDisplayName()));
                    lore.add(Msg.JSONcolor("&7and upgrade your " + Items.items.get(GetSlommyItem.getFromItemTag(item)).getDisplayName() + "&7's rarity!"));
                    meta.lore(lore);
                    recombobulator.setItemMeta(meta);
                    inventory.setItem(53, recombobulator);
                }
            }
            // make a border around the item based on the item's rarity
            String itemID = GetSlommyItem.getFromItemTag(item);
            SlommyItem slommyItem = Items.items.get(itemID);
            if (slommyItem == null) {
                return;
            }
            Item.rarity rarity = Item.findRarity(slommyItem.getRarity(item));
            ItemStack borderItem = Items.items.get("gui/common_glass").build(player);
            int rarityValue = Item.rarity.value(rarity);
            List<Integer> EnrichmentInventorySlots = new ArrayList<>();
            switch (rarityValue) {
                case 1:
                    borderItem = Items.items.get("gui/common_glass").build(player);
                    EnrichmentInventorySlots.add(31);
                    break;
                case 2:
                    borderItem = Items.items.get("gui/uncommon_glass").build(player);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(32);
                    break;
                case 3:
                    borderItem = Items.items.get("gui/rare_glass").build(player);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(31);
                    EnrichmentInventorySlots.add(32);
                    break;
                case 4:
                    borderItem = Items.items.get("gui/epic_glass").build(player);
                    EnrichmentInventorySlots.add(29);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(32);
                    EnrichmentInventorySlots.add(33);
                    break;
                case 5:
                    borderItem = Items.items.get("gui/legendary_glass").build(player);
                    EnrichmentInventorySlots.add(29);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(31);
                    EnrichmentInventorySlots.add(32);
                    EnrichmentInventorySlots.add(33);
                    break;
                case 6:
                    borderItem = Items.items.get("gui/ultra_legendary_glass").build(player);
                    EnrichmentInventorySlots.add(28);
                    EnrichmentInventorySlots.add(29);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(32);
                    EnrichmentInventorySlots.add(33);
                    EnrichmentInventorySlots.add(34);
                    break;
                case 7:
                    borderItem = Items.items.get("gui/uber_legendary_glass").build(player);
                    EnrichmentInventorySlots.add(28);
                    EnrichmentInventorySlots.add(29);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(31);
                    EnrichmentInventorySlots.add(32);
                    EnrichmentInventorySlots.add(33);
                    EnrichmentInventorySlots.add(34);
                    break;
                case 8:
                    borderItem = Items.items.get("gui/mystical_glass").build(player);
                    EnrichmentInventorySlots.add(28);
                    EnrichmentInventorySlots.add(29);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(31);
                    EnrichmentInventorySlots.add(32);
                    EnrichmentInventorySlots.add(33);
                    EnrichmentInventorySlots.add(34);
                    EnrichmentInventorySlots.add(40);
                    break;
                case 9:
                    borderItem = Items.items.get("gui/mythic_glass").build(player);
                    EnrichmentInventorySlots.add(28);
                    EnrichmentInventorySlots.add(29);
                    EnrichmentInventorySlots.add(30);
                    EnrichmentInventorySlots.add(31);
                    EnrichmentInventorySlots.add(32);
                    EnrichmentInventorySlots.add(33);
                    EnrichmentInventorySlots.add(34);
                    EnrichmentInventorySlots.add(39);
                    EnrichmentInventorySlots.add(41);
                    break;
            }
            inventory.setItem(3, borderItem);
            inventory.setItem(4, borderItem);
            inventory.setItem(5, borderItem);
            inventory.setItem(12, borderItem);
            inventory.setItem(14, borderItem);
            inventory.setItem(21, borderItem);
            inventory.setItem(22, borderItem);
            inventory.setItem(23, borderItem);

            // Open up the slots for the enrichments
            for (Integer slot : EnrichmentInventorySlots) {
                inventory.setItem(slot, Items.items.get("gui/locked_enrichment_slot").build(player));
            }
            for (int i = 1; i < EnrichmentInventorySlots.size() + 1; i++) {
                if (PersistentDataUtils.getUnlockedEnrichmentSlots(item) > i - 1) {
                    inventory.setItem(EnrichmentInventorySlots.get(i - 1), Items.items.get(PersistentDataUtils.getEnrichmentInSlot(item, i)).build(player));
                }
                ItemStack slotItem = inventory.getItem(EnrichmentInventorySlots.get(i - 1));
                String slotItemID = GetSlommyItem.getFromItemTag(slotItem);
                if (slotItemID.split("/")[1].equalsIgnoreCase("locked_enrichment_slot")) {
                    ItemMeta meta = slotItem.getItemMeta();
                    meta.displayName(Msg.JSONcolor("&cLocked Enrichment Slot #" + i));
                    List<Component> lore = new ArrayList<>();
                    lore.add(Msg.JSONcolor("&7Cost to unlock:"));
                    lore.add(Msg.JSONcolor("&6" + IntegerCommaParser.parsedouble(Math.pow(2, i + 3)) + " \uD83C\uDD6D"));
                    lore.add(Msg.JSONcolor(""));
                    if (i == PersistentDataUtils.getUnlockedEnrichmentSlots(item) + 1) {
                        lore.add(Msg.JSONcolor("&eClick to unlock this slot!"));
                    }
                    else {
                        lore.add(Msg.JSONcolor("&cYou must unlock the previous slot!"));
                    }
                    meta.lore(lore);
                    slotItem.setItemMeta(meta);
                    inventory.setItem(EnrichmentInventorySlots.get(i - 1), slotItem);
                }
                if (slotItemID.split("/")[1].equalsIgnoreCase("open_enrichment_slot")) {
                    ItemMeta meta = slotItem.getItemMeta();
                    meta.displayName(Msg.JSONcolor("&aOpen Enrichment Slot #" + i));
                    List<Component> lore = new ArrayList<>();
                    lore.add(Msg.JSONcolor("&7Click to add an enrichment!"));
                    meta.lore(lore);
                    slotItem.setItemMeta(meta);
                    inventory.setItem(EnrichmentInventorySlots.get(i - 1), slotItem);
                } else {
                    SlommyItem slommySlotItem = Items.items.get(slotItemID);
                    if (slommySlotItem == null) {
                        continue;
                    }
                    if (slommySlotItem.getType().equalsIgnoreCase("ENRICHMENT")) {
                        ItemMeta meta = slotItem.getItemMeta();
                        List<Component> lore = meta.lore();
                        lore.add(Msg.JSONcolor(""));
                        lore.add(Msg.JSONcolor("&7Cost to remove:"));
                        lore.add(Msg.JSONcolor("&6" + IntegerCommaParser.parsedouble(Item.rarity.enrichmentRemoveCost(Item.findRarity(slommySlotItem.getRarity()))) + " \uD83C\uDD6D"));
                        lore.add(Msg.JSONcolor(""));
                        lore.add(Msg.JSONcolor("&eLeft Click to take off this enrichment!"));
                        lore.add(Msg.JSONcolor("&cRight Click to DELETE this enrichment!"));
                        meta.lore(lore);
                        // Get the Data Container
                        meta.getPersistentDataContainer().set(new NamespacedKey(SlommyPanels.getInstance(), "enrichment-slot"), PersistentDataType.INTEGER, i);
                        slotItem.setItemMeta(meta);
                        inventory.setItem(EnrichmentInventorySlots.get(i - 1), slotItem);
                    }
                }
            }
        } else {
            inventory.setItem(13, Items.items.get("gui/add_enrichable_item").build(player));
        }
        player.openInventory(inventory);
    }


}
