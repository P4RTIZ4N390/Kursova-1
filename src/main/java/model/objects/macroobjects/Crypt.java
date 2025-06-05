package model.objects.macroobjects;

import model.items.firearms.ammos.Ammo57mm;
import model.items.firearms.smg.FNP90;
import model.objects.microobjects.MicroObjectAbstract;
import model.objects.microobjects.Cultist;
import model.objects.microobjects.Soldier;
import utilies.ImageLoader;


public class Crypt extends MacroObjectAbstract{
    public Crypt(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean loadCreatures() {
        addCreature(new Cultist());
        addCreature(new Soldier());
        return true;
    }

    @Override
    public void loadTexture() {
        setTexture(ImageLoader.loadExternalTexture("src/main/resources/assets/textures/macroObjects/Crypt.png"));
    }

    @Override
    public void pullCreature(int index) {
        MicroObjectAbstract microObjectAbstract =getCreatures().get(index);
        microObjectAbstract.getInventory().clear();
        microObjectAbstract.getInventory().addItem(new FNP90());
        microObjectAbstract.getInventory().addItems(new Ammo57mm(),200);
        microObjectAbstract.setCurrentItem("FN P90");
        microObjectAbstract.getInventory().showInventory();
        super.pullCreature(index);
    }
}
