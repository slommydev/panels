package net.slommy.slommypanels.events;

import net.slommy.slommypanels.util.LoadTreasureStash;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoadStuffOnJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        LoadTreasureStash.load(event.getPlayer());
        Bukkit.getLogger().info("Loaded Treasure stash for player " + event.getPlayer().getName());
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        LoadTreasureStash.save(event.getPlayer());
        Bukkit.getLogger().info("Saved Treasure stash for player " + event.getPlayer().getName());
    }
}
