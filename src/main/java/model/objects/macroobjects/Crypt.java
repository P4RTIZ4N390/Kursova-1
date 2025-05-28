package model.objects.macroobjects;

import model.items.firearms.ammos.Ammo57mm;
import model.items.firearms.smg.FNP90;
import model.objects.microobjects.Creature;
import model.objects.microobjects.Cultist;
import model.objects.microobjects.Soldier;
import utilies.ImageLoader;


public class Crypt extends MacroObjectAbstract{
    public Crypt(int x, int y) {
        super(x, y);
        loadCreatures();
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
        Creature creature=getCreatures().get(index);
        creature.getInventory().clear();
        creature.getInventory().addItem(new FNP90());
        creature.getInventory().addItems(new Ammo57mm(),200);
        creature.setCurrentItem("FN P90");
        creature.getInventory().showInventory();
        super.pullCreature(index);
    }
}
