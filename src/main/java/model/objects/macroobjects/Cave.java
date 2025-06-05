package model.objects.macroobjects;

import model.items.firearms.ammos.Ammo45ACP;
import model.items.firearms.ammos.Ammo762mm;
import model.items.firearms.pistols.PM;
import model.items.firearms.rifles.AKM;
import model.items.firearms.smg.SMG45;
import model.objects.microobjects.MicroObjectAbstract;
import model.objects.microobjects.Recruit;
import model.objects.microobjects.Soldier;
import utilies.ImageLoader;
import utilies.RandomUtil;

public class Cave extends MacroObjectAbstract{

    public Cave(int x, int y) {
        super(x, y);
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
        MicroObjectAbstract microObjectAbstract =getCreatures().get(index);
        microObjectAbstract.getInventory().clear();
        if (RandomUtil.getRandomInt(0, 1) == 0) {
            microObjectAbstract.getInventory().addItem(new AKM());
            microObjectAbstract.getInventory().addItems(new Ammo762mm(), 200);
            microObjectAbstract.setCurrentItem("AKM");
            microObjectAbstract.getInventory().addItem(new PM());
        } else {
            microObjectAbstract.getInventory().addItem(new SMG45());
            microObjectAbstract.getInventory().addItems(new Ammo45ACP(), 200);
            microObjectAbstract.setCurrentItem("SMG 45");
        }
        microObjectAbstract.getInventory().showInventory();
        super.pullCreature(index);
    }
}
