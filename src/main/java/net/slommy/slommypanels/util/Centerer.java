package net.slommy.slommypanels.util;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Centerer {
    public static List<Integer> placeItemsEvenly(int containerSize, int itemAmount) {
        List<Integer> slots = new ArrayList<>();
        // split the itemAmount into groups of 9 for each row.
        // for each group, start from the center of the row, and place items to the left and right of the center.
        // if the amount of items in the group is even, the center slot should be empty.
        // if the amount of items in the group is odd, the center slot should be filled.
        Map<Integer, Integer> groups = splitIntoGroups(itemAmount);
        //Bukkit.getLogger().info("Groups: " + groups.size());
        int containerRows = containerSize / 9;
        int groupCount = groups.size();
        for (int i = 0; i < groupCount; i++) {
            int groupSize = groups.get(i);
            int centerRow = (containerRows / 2) + i;
            int centerSlot = (centerRow * 9) + 4;
            /* Bukkit.getLogger().info("Group " + i);
            Bukkit.getLogger().info("Group size: " + groupSize);
            Bukkit.getLogger().info("Center row: " + centerRow);
            Bukkit.getLogger().info("Center slot: " + centerSlot); */
            int leftSlot = centerSlot - 1;
            int rightSlot = centerSlot + 1;
            if (groupSize % 2 == 0) {
                // even
                int leftItems = groupSize / 2;
                int rightItems = groupSize / 2;
                for (int j = 0; j < leftItems; j++) {
                    slots.add(leftSlot);
                    leftSlot--;
                }
                for (int j = 0; j < rightItems; j++) {
                    slots.add(rightSlot);
                    rightSlot++;
                }
            } else {
                // odd
                int leftItems = groupSize / 2;
                int rightItems = (groupSize / 2) + 1;
                for (int j = 0; j < leftItems; j++) {
                    slots.add(leftSlot);
                    leftSlot--;
                }
                slots.add(centerSlot);
                for (int j = 0; j < rightItems; j++) {
                    slots.add(rightSlot);
                    rightSlot++;
                }
            }
        }
        return slots;
    }

    public static Map<Integer, Integer> splitIntoGroups(int itemAmount) {
        Map<Integer, Integer> groups = new HashMap<>();
        int groupCount = itemAmount / 9;
        int remainder = itemAmount % 9;
        for (int i = 0; i < groupCount; i++) {
            groups.put(i, 9);
        }
        if (remainder != 0) {
            groups.put(groupCount, remainder);
        }
        return groups;
    }
}
