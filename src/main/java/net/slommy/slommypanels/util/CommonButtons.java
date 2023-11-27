package net.slommy.slommypanels.util;

import net.slommy.essentials.SlommyItems.Items;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.entity.Player;
import xyz.janboerman.guilib.api.menu.ItemButton;

public class CommonButtons {
    public static ItemButton blankButton(Player player) {
        return new ItemButton<>(Items.get("gui/blank_pane").build(player));
    }
}
