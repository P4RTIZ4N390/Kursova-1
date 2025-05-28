package model.items.firearms.ammos;

import model.items.Item;
import model.items.TypesOfItem;

public class CartridgeForWMUBF extends Item {
    public CartridgeForWMUBF() {
        super("Cartridge for WMUBF", 0.1,"", TypesOfItem.AMMO);
    }

    public static int getDamage() {
        return 50;
    }
}
