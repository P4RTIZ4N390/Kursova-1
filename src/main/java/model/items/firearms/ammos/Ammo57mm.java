package model.items.firearms.ammos;

import model.items.Item;
import model.items.TypesOfItem;

public class Ammo57mm extends Item {

    public Ammo57mm() {
        super("5.7×28мм", 0.02, "", TypesOfItem.AMMO);
    }

    public static int getDamage() {
        return 10;
    }
}
