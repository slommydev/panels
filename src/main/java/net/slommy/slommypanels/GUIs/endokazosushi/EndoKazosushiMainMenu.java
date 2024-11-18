package net.slommy.slommypanels.GUIs.endokazosushi;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.essentials.requirements.RequirementManager;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

public class EndoKazosushiMainMenu {
    public static void openMainMenu(Player player) {
        MenuHolder<SlommyPanels> panel = new MenuHolder<>(SlommyPanels.getInstance(), 27, "Endo Kazosushi");
        for(int i = 0; i < 27; i++) {
            panel.setButton(i, new ItemButton<>(Items.get("gui/blank_magenta").build(player)));
        }

        panel.setButton(11, new ItemButton(Items.get("gui/endo_fillet").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                EndoKazosushiFilletFishMenu.openMenu(player);
            }
        });
        panel.setButton(12, new ItemButton(Items.get("gui/endo_upgrade_fillet").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                Msg.send(player, "&cThis does not exist yet");
            }
        });
        panel.setButton(13, new ItemButton(Items.get("gui/endo_sell_fillet").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                Msg.send(player, "&cThis does not exist yet");
            }
        });
        panel.setButton(14, new ItemButton(Items.get("gui/endo_kazosushis_requests_board").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                if (!RequirementManager.getRequirement("endo_request_board").check(player)) {
                    Msg.send(player, "&c" + RequirementManager.getRequirement("endo_request_board").getLockedMessage());
                    return;
                }
                EndoKazosushiRequestsMenu.openRequestsMenu(player);
            }
        });
        panel.setButton(15, new ItemButton(Items.get("gui/endo_shop").build(player)) {
            @Override
            public void onClick(MenuHolder menuHolder, InventoryClickEvent event) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shop " + player.getName() + " fish_fillet");
            }
        });

        player.openInventory(panel.getInventory());
    }
}
