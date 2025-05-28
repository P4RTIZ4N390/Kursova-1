package model.items.firearms.ammos;

import model.items.Item;
import model.items.TypesOfItem;

public class Ammo556mm extends Item {

    public Ammo556mm() {
        super("5.56мм", 0.04, "", TypesOfItem.AMMO);
    }

    public static int getDamage() {
        return 12;
    }
}
