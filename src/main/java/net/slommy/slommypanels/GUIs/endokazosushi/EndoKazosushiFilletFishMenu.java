package net.slommy.slommypanels.GUIs.endokazosushi;

import net.kyori.adventure.text.Component;
import net.slommy.essentials.SlommyItems.Item;
import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.SlommyItemExchange;
import net.slommy.slommypanels.SlommyPanels;
import net.slommy.slommypanels.util.CommonButtons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.*;

public class EndoKazosushiFilletFishMenu {
    public static void openMenu(Player player) {
        Map<SlommyItem, Integer> fishInInventory = SlommyItemExchange.findItemsInCategory(player, "fish");
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 54, "Fillet Fish");
        for (int i = 0; i < 54; i++) {
            while (i >= 19 && i <= 43 && i != 26 && i != 27 && i != 35 && i != 36) i++;
            panel.setButton(i, CommonButtons.blankButton(player));
        }
        Map<Item.rarity, Integer> fishRarity = new HashMap<>();
        for (SlommyItem fish : fishInInventory.keySet()) {
            fishRarity.put(fish.getRarityEnum(), fishRarity.getOrDefault(fish.getRarityEnum(), 0) + fishInInventory.get(fish));
        }
        ItemStack filletItem = Items.get("gui/endo_fillet").build(player);
        ItemMeta filletMeta = filletItem.getItemMeta();
        filletMeta.displayName(Msg.JSONcolor("&aFillet Fish"));
        List<Component> lore = new ArrayList<>();
        // Organize the rarities in the map by the rarity Value
        List<Item.rarity> sortedRarities = new ArrayList<>(fishRarity.keySet());
        sortedRarities.sort(Comparator.comparingInt(rarity -> rarity.value()));
        // Reverse the list so that the rarities are in descending order
        Collections.reverse(sortedRarities);
        lore.add(Msg.JSONcolor("&7You are filleting:"));
        for (Item.rarity rarity : sortedRarities) {
            lore.add(Msg.JSONcolor("&7- &e" + fishRarity.get(rarity) + " " + rarity.color() + decapitalizeRarityName(rarity.getName()) + " Fish"));
        }
        lore.add(Msg.JSONcolor(""));
        lore.add(Msg.JSONcolor("&eClick to fillet!"));
        filletMeta.lore(lore);
        filletItem.setItemMeta(filletMeta);
        panel.setButton(13, new ItemButton(filletItem));

        player.openInventory(panel.getInventory());
    }

    public static String decapitalizeRarityName(String rarityName) {
        // The rarity strings are in all caps, so we need to decapitalize them
        return rarityName.substring(0, 1) + rarityName.substring(1).toLowerCase();
    }
}
