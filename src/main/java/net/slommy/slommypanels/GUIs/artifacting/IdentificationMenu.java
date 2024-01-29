package net.slommy.slommypanels.GUIs.artifacting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.slommy.essentials.SlommyItems.*;
import net.slommy.essentials.SlommyItems.Artifacting.ArtifactSeries;
import net.slommy.essentials.util.Msg;
import net.slommy.essentials.util.PlayerDataUtil;
import net.slommy.essentials.util.SoundLibrary;
import net.slommy.slommypanels.SlommyPanels;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class IdentificationMenu implements InventoryHolder {
    public void open(Player player, SlommyItem slommyItem, ArtifactSeries series) {
        ItemStack itemStack = slommyItem.build(player);
        ItemMeta returnMeta = itemStack.getItemMeta();
        List<Component> lore2 = returnMeta.lore();
        for (int i = 0; i < lore2.size(); i++) {
            // Make the lore line obfuscated
            lore2.set(i, lore2.get(i).asComponent().decoration(TextDecoration.OBFUSCATED, true));
        }
        // remove the last 2 lore lines
        lore2.remove(lore2.size() - 1);
        lore2.remove(lore2.size() - 1);
        // remove any lore lines that are not obfuscated
        for (int i = 0; i < lore2.size(); i++) {
            if (!lore2.get(i).asComponent().hasDecoration(TextDecoration.OBFUSCATED)) {
                lore2.remove(i);
                i--;
            }
        }
        returnMeta.lore(lore2);
        itemStack.setItemMeta(returnMeta);
        Inventory inv = SlommyPanels.getInstance().getServer().createInventory(this, 27, "Identifying Artifact...");
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, Items.get("gui/blank_pane").build(player));
        }
        Map<String, Double> itemStats = slommyItem.getCustomStats();
        double artifactFortuity = PlayerDataUtil.getPlayerData(player).getStatValue("ArtifactFortuity", player);
        for (String stat : itemStats.keySet()) {
            double roll1 = Math.random() * 100;
            if (roll1 == 0) {
                roll1 = 1;
            }
            int statPercentageValue = (int) roll1;
            statPercentageValue = (int) (statPercentageValue + Math.pow(statPercentageValue, (artifactFortuity / 100)));
            if (statPercentageValue >= 100) {
                statPercentageValue = 100;
            }
            PersistentItemData artifactStatModifier = new PersistentItemData(stat, PersistentItemData.Application.ARTIFACT_STATMOD, String.valueOf(statPercentageValue));
            PersistentDataUtils.addPersistentDataTag(itemStack, artifactStatModifier);
        }
        DecimalFormat df = new DecimalFormat("#.##");
        PersistentItemData artifactFidelity = new PersistentItemData("artifact_fidelity", PersistentItemData.Application.PLACEHOLDER, df.format(PersistentDataUtils.getArtifactFidelity(itemStack)));
        PersistentDataUtils.addPersistentDataTag(itemStack, artifactFidelity);
        PersistentItemData is_artifact = new PersistentItemData("is_artifact", PersistentItemData.Application.IS_IDENTIFIED_ARTIFACT, "true");
        PersistentDataUtils.addPersistentDataTag(itemStack, is_artifact);
        PersistentItemData artifactSeries = new PersistentItemData("artifact_series", PersistentItemData.Application.ARTIFACT_FROM_SERIES, series.toString());
        PersistentDataUtils.addPersistentDataTag(itemStack, artifactSeries);
        int unusualRoll = (int) (Math.random() * 16);
        if (unusualRoll == 0) {
            PersistentItemData unusual = new PersistentItemData("unusual", PersistentItemData.Application.ARTIFACT_IS_UNUSUAL, "true");
            PersistentDataUtils.addPersistentDataTag(itemStack, unusual);
            // TODO: decide what kind of unusual
        }
        inv.setItem(13, itemStack);
        player.openInventory(inv);
        // wait 10 ticks
        Bukkit.getScheduler().runTaskLater(SlommyPanels.getInstance(), () -> {
            ItemUpdateUtils.updateItemInContainer(player, 13);
            if (PersistentDataUtils.getArtifactFidelity(itemStack) == 100) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                Msg.broadcast(Msg.PAPI(player, "&6%luckperms_prefix%%player_name%%luckperms_suffix%" + " &eidentified a &b&l100% &eFidelity " + slommyItem.getDisplayName() + "&e!"));
            }
            else if (PersistentDataUtils.getArtifactFidelity(itemStack) == 0 || PersistentDataUtils.getArtifactFidelity(itemStack) == 1) {
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 0);
                Msg.broadcast(Msg.PAPI(player, "%luckperms_prefix%%player_name%%luckperms_suffix%" + " &7identified a &4&l" + PersistentDataUtils.getArtifactFidelity(itemStack) + "% &7Fidelity " + slommyItem.getDisplayName() + "&7... &4lol..."));
            }
        }, SoundLibrary.artifactDrumRoll(player));
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
