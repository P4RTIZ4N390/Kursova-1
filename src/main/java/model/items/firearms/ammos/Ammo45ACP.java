package model.items.firearms.ammos;

import model.items.Item;
import model.items.TypesOfItem;

public class Ammo45ACP extends Item {

    public Ammo45ACP() {
        super("45ACP", 0.02,"", TypesOfItem.AMMO);
    }

    public static int getDamage() {
        return 9;
    }
}
