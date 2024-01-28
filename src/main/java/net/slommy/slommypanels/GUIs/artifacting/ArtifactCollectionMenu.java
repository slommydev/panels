package net.slommy.slommypanels.GUIs.artifacting;

import net.slommy.essentials.SlommyItems.Artifacting.ArtifactSeries;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

public class ArtifactCollectionMenu {
    public static void open(Player player, ArtifactSeries series) {
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 27, series.getName() + " Artifacting");
        for (int i = 0; i < 27; i++) {
            panel.setButton(i, new ItemButton(series.getBackgroundGlass().build(player)));
        }

        panel.setButton(10, new ItemButton(series.getIcon().build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                ArtifactMenu.openArtifactMenu(player, series, 1);
            }
        });
        panel.setButton(16, new ItemButton(series.getUpgradeIcon().build(player)));

        player.openInventory(panel.getInventory());

    }
}
