package model.items.firearms.ammos;

import model.items.Item;
import model.items.TypesOfItem;

public class Ammo12gauge extends Item {
    public Ammo12gauge() {
        super("12mm", 0.004, "", TypesOfItem.AMMO);
    }

    public static int getDamage() {
        return 4;
    }
}
