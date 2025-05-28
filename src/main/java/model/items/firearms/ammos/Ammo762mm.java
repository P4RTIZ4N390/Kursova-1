package model.items.firearms.ammos;

import model.items.Item;
import model.items.TypesOfItem;

public class Ammo762mm extends Item {

    public Ammo762mm() {
        super("7.62х39мм", 0.05, "", TypesOfItem.AMMO);
    }

    public static int getDamage() {
        return 10;
    }
}
