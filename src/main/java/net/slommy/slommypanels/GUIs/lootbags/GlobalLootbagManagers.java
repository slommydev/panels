package net.slommy.slommypanels.GUIs.lootbags;

import java.util.ArrayList;
import java.util.List;

public class GlobalLootbagManagers {
    public static List<LootTable> tables = new ArrayList<>();
    public static List<TreasureBag> bags = new ArrayList<>();

    public static LootTable getTable(String name) {
        for (LootTable table : tables) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }

    public static TreasureBag getBag(String name) {
        for (TreasureBag bag : bags) {
            if (bag.getName().equalsIgnoreCase(name)) {
                return bag;
            }
        }
        return null;
    }
}
