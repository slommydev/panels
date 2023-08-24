package net.slommy.slommypanels;


import com.comphenix.protocol.ProtocolLib;
import net.slommy.slommypanels.GUIs.SpOpenCommand;
import net.slommy.slommypanels.cmds.shop;
import net.slommy.slommypanels.cmds.skills;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.janboerman.guilib.GuiLibrary;

public final class SlommyPanels extends JavaPlugin {
    GuiLibrary guiLibrary;
    static SlommyPanels instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        GuiLibrary guiLibrary = (GuiLibrary) getServer().getPluginManager().getPlugin("GuiLib");
        ProtocolLib protocolLib = (ProtocolLib) getServer().getPluginManager().getPlugin("ProtocolLib");
        if (guiLibrary == null) {
            getLogger().severe("GuiLib not found. Disabling SlommyPanels.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (protocolLib == null) {
            getLogger().severe("ProtocolLib not found. Disabling SlommyPanels.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("spopen").setExecutor(new SpOpenCommand());
        getCommand("skills").setExecutor(new skills());
        getCommand("shop").setExecutor(new shop());
        this.guiLibrary = guiLibrary;
        this.instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public GuiLibrary getGuiLibrary() {
        return guiLibrary;
    }

    public static SlommyPanels getInstance() {
        return instance;
    }

}
