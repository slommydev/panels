package net.slommy.slommypanels.GUIs.imbuing;

import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.player.PersistentPlayerData;
import net.slommy.essentials.player.PersistentPlayerDataUtil;
import net.slommy.essentials.util.GetSlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SelectCharmToImbueMenu implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return SlommyPanels.getInstance().getServer().createInventory(this, 27, "Select Charm to Imbue");
    }

    public void openSelectCharmToImbueMenu(Player player) {
        Inventory inv = SlommyPanels.getInstance().getServer().createInventory(this, 6*9, Msg.JSONcolor("Select Charm to Imbue"));

        List<Integer> takenSlots = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, Items.items.get("gui/blank_pane").build(player));
                takenSlots.add(i);
            }
        }

        // Get a list of charms from the player
        Inventory playerInventory = player.getInventory();
        List<ItemStack> charms = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = playerInventory.getItem(i);
            if (item == null) {
                continue;
            }
            String itemID = GetSlommyItem.getFromItemTag(item);
            if (itemID == null) {
                continue;
            }
            PersistentPlayerData data = PersistentPlayerDataUtil.getPersistentPlayerData(player);
            List<String> imbued_charm_ids = data.getImbuedCharms();
            if (imbued_charm_ids.contains(itemID)) {
                continue;
            }
            SlommyItem slommyItem = Items.get(itemID);
            if (slommyItem == null) {
                continue;
            }
            if (slommyItem.getTypeEnum() == Item.type.CHARM) {
                charms.add(item);
            }
        }

        // Populate the empty spaces with charms
        int slot = 10;
        for (ItemStack charm : charms) {
            while(takenSlots.contains(slot)) {
                slot++;
            }
            inv.setItem(slot, charm);
            slot++;
        }

        inv.setItem(49, Items.items.get("gui/back").build(player));

        player.openInventory(inv);
    }
}
