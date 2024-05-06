package net.slommy.slommypanels.cmds;

import net.slommy.essentials.util.Msg;
import net.slommy.slommypanels.GUIs.profileviewer.ProfileViewer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class profile implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Msg.send(sender, "&cYou must be a player to use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            ProfileViewer.openProfileViewer(player, player);
            return true;
        }
        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            Msg.send(sender, "&cThat player is not online or could not be found");
            return true;
        }
        // open the profile GUI for the target player
        ProfileViewer.openProfileViewer(player, target);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
