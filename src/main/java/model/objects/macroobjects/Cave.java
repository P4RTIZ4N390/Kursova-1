package model.objects.macroobjects;

import model.items.firearms.ammos.Ammo45ACP;
import model.items.firearms.ammos.Ammo57mm;
import model.items.firearms.ammos.Ammo762mm;
import model.items.firearms.pistols.PM;
import model.items.firearms.rifles.AKM;
import model.items.firearms.smg.FNP90;
import model.items.firearms.smg.SMG45;
import model.objects.microobjects.Creature;
import model.objects.microobjects.Recruit;
import model.objects.microobjects.Soldier;
import utilies.ImageLoader;
import utilies.RandomUtil;

import java.util.ArrayList;

public class Cave extends MacroObjectAbstract{

    public Cave(int x, int y) {
        super(x, y);
        loadCreatures();
    }

    @Override
    public boolean loadCreatures() {
        addCreature(new Soldier());
        addCreature(new Recruit());
        addCreature(new Recruit());
        addCreature(new Soldier());
        addCreature(new Soldier());
        return false;
    }

    @Override
    public void loadTexture() {
        setTexture(ImageLoader.loadExternalTexture("src/main/resources/assets/textures/macroObjects/Cave.png"));
    }

    @Override
    public void pullCreature(int index) {
        Creature creature=getCreatures().get(index);
        creature.getInventory().clear();
        if (RandomUtil.getRandomInt(0, 1) == 0) {
            creature.getInventory().addItem(new AKM());
            creature.getInventory().addItems(new Ammo762mm(), 200);
            creature.setCurrentItem("AKM");
            creature.getInventory().addItem(new PM());
        } else {
            creature.getInventory().addItem(new SMG45());
            creature.getInventory().addItems(new Ammo45ACP(), 200);
            creature.setCurrentItem("SMG 45");
        }
        creature.getInventory().showInventory();
        super.pullCreature(index);
    }
}
