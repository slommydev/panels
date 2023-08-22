package net.slommy.slommypanels.cmds;

import net.slommy.slommypanels.GUIs.skills.AllSkillsPanel;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.janboerman.guilib.api.menu.MenuHolder;

public class skills implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            SlommyPanels plugin = (SlommyPanels) sender.getServer().getPluginManager().getPlugin("SlommyPanels");
            MenuHolder<SlommyPanels> panel = new AllSkillsPanel(plugin, player).getPanel();
            player.openInventory(panel.getInventory());
        }
        return true;
    }
}
