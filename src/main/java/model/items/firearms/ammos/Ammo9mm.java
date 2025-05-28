package model.items.firearms.ammos;

import model.items.Item;
import model.items.TypesOfItem;

public class Ammo9mm extends Item {

    public Ammo9mm() {
        super("9mm", 0.007,"", TypesOfItem.AMMO);
    }

    public static int getDamage() {
        return 7;
    }
}
