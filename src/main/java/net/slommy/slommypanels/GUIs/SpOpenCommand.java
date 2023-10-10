package net.slommy.slommypanels.GUIs;

import net.kyori.adventure.text.TextComponent;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.GUIs.enrichment.EnrichmentMenu;
import net.slommy.slommypanels.GUIs.lootbags.GlobalLootbagManagers;
import net.slommy.slommypanels.GUIs.lootbags.LootTable;
import net.slommy.slommypanels.GUIs.skills.AllSkillsPanel;
import net.slommy.slommypanels.GUIs.skills.IndividualSkillPanel;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;

public class SpOpenCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof  Player)) {
            if (args.length < 2) {
                sender.sendMessage("You must specify a player to open the GUI for.");
                return true;
            }
        }
        Player player;
        if (sender instanceof Player && args.length < 2) {
            player = (Player) sender;
        } else {
            player = sender.getServer().getPlayer(args[1]);
        }
        if (player == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        if (args[0].equalsIgnoreCase("skills")) {
            SlommyPanels plugin = (SlommyPanels) sender.getServer().getPluginManager().getPlugin("SlommyPanels");
            MenuHolder<SlommyPanels> panel = new AllSkillsPanel(plugin, player).getPanel();
            player.openInventory(panel.getInventory());
        }

        if (args[0].equalsIgnoreCase("skilltest")) {
            MenuHolder<SlommyPanels> panel = IndividualSkillPanel.open(player, "Fishing");
            player.openInventory(panel.getInventory());
        }

        if (args[0].equalsIgnoreCase("testbag")) {
            GlobalLootbagManagers.getBag("testbag").openTreasureBag(player);
        }

        if (args[0].equalsIgnoreCase("enrich")) {
            EnrichmentMenu menu = new EnrichmentMenu();
            menu.openEnrichmentAnvil(player, null);
        }


        return true;
    }
}
