package net.slommy.slommypanels.GUIs.enchant;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.SlommyItems.PersistentDataUtils;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.SlommyItems.enchantments.SlommyEnchantment;
import net.slommy.essentials.util.GetSlommyItem;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentSlot {
    private ItemStack item;
    private SlommyItem slommyItem;
    private boolean isEnchantmentTome;
    private Map<SlommyEnchantment, Integer> currentlyAppliedEnchantments;
    private boolean isEmpty;

    public EnchantmentSlot(ItemStack item) {
        setItem(item);
    }

    public void setItem(ItemStack item) {
        this.item = item;
        slommyItem = Items.get(GetSlommyItem.getFromItemTag(item));
        isEnchantmentTome = slommyItem.getItemID().equals("items/enchantment_tome");
        currentlyAppliedEnchantments = PersistentDataUtils.getAppliedEnchantments(item);
        isEmpty = slommyItem.getItemID().equals("gui/add_enchant_item");
    }

    public boolean comparison(EnchantmentSlot otherSlot) {
        if (otherSlot == null || isEmpty || otherSlot.isEmpty) return false;

        if (isEnchantmentTome && otherSlot.isEnchantmentTome) {
            // Tome combination logic
            if (currentlyAppliedEnchantments.size() != 1|| otherSlot.currentlyAppliedEnchantments.size() != 1) return false; // validity check


            Map.Entry<SlommyEnchantment, Integer> enchantmentA = currentlyAppliedEnchantments.entrySet().iterator().next();
            Map.Entry<SlommyEnchantment, Integer> enchantmentB = otherSlot.currentlyAppliedEnchantments.entrySet().iterator().next();

            // only allow tomes with same enchant & same level to be combined
            return enchantmentA.getKey().equals(enchantmentB.getKey()) && enchantmentA.getValue().equals(enchantmentB.getValue());
        }


        return compareItems(otherSlot);
    }

    private boolean compareItems(EnchantmentSlot otherSlot) {
        // Checks if the item is identical to the second item, or if one is an Enchantment Tome.
        boolean value = true;
        if (isEnchantmentTome || otherSlot.isEnchantmentTome) return true;

        return slommyItem.getItemID().equalsIgnoreCase(otherSlot.getSlommyItem().getItemID());
    }

    private Map<SlommyEnchantment, Integer> mergeEnchantments(EnchantmentSlot otherSlot) {
        Map<SlommyEnchantment, Integer> merged = new HashMap<>();

        merged.putAll(currentlyAppliedEnchantments);

        for (Map.Entry<SlommyEnchantment, Integer> entry : otherSlot.currentlyAppliedEnchantments.entrySet()) {
            SlommyEnchantment enchantment = entry.getKey();
            int incomingLevel = entry.getValue();
            Integer existingLevel = merged.get(enchantment);
            if (existingLevel == null) {
                merged.put(enchantment, incomingLevel);
            } else {
                merged.put(enchantment, combineLevels(enchantment, existingLevel, incomingLevel));
            }
        }
        return merged;
    }

    private int combineLevels(SlommyEnchantment enchantment, int levelA, int levelB) {
        int maxLevel = enchantment.MaxLevel();

        if (levelA == levelB && levelA < maxLevel) {
            return levelA + 1;
        }

        return Math.max(levelA, levelB);
    }

    public ItemStack getCombinedResult(EnchantmentSlot otherSlot) {
        if (!comparison(otherSlot)) return null;

        ItemStack result;

        if (isEnchantmentTome && otherSlot.isEnchantmentTome) {
            result = item.clone();
        } else if (isEnchantmentTome) {
            result = otherSlot.item.clone();
        } else {
            result = item.clone();
        }
        Map<SlommyEnchantment, Integer> mergedEnchantments = mergeEnchantments(otherSlot);
        PersistentDataUtils.setAppliedEnchantments(mergedEnchantments, result);
        return result;
    }




    public ItemStack getItem() {
        return item;
    }

    public SlommyItem getSlommyItem() {
        return slommyItem;
    }

    public boolean isEnchantmentTome() {
        return isEnchantmentTome;
    }

    public Map<SlommyEnchantment, Integer> getCurrentlyAppliedEnchantments() {
        return currentlyAppliedEnchantments;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
