package net.slommy.slommypanels.GUIs.shops;

import net.kyori.adventure.text.TextComponent;
import net.slommy.essentials.SlommyItems.SlommyItem;
import net.slommy.essentials.util.IntegerCommaParser;
import net.slommy.essentials.util.Msg;
import org.w3c.dom.Text;

import java.awt.*;

public class ShopExchange {
    public enum ExchangeType {
        BUY,
        TRADE,
    }
    SlommyItem tradeItem;
    ExchangeType type;
    int exchangeAmount;

    public ShopExchange(ExchangeType type, int exchangeAmount) {
        this.type = type;
        this.exchangeAmount = exchangeAmount;
    }

    public ShopExchange(ExchangeType type, int exchangeAmount, SlommyItem tradeItem) {
        this.type = type;
        this.exchangeAmount = exchangeAmount;
        this.tradeItem = tradeItem;
    }

    public TextComponent getDisplayLine() {
        if (type == ExchangeType.TRADE) {
            return Msg.JSONcolor("&7- " + IntegerCommaParser.parse(exchangeAmount) + "x " + tradeItem.getDisplayName());
        }
        if (type == ExchangeType.BUY) {
            return Msg.JSONcolor("&7- " + "&6" + IntegerCommaParser.parse(exchangeAmount) + " \uD83C\uDD6D");
        }
        return null;
    }
}
