package model.objects.macroobjects;

import model.objects.microobjects.Cultist;
import utilies.ImageLoader;

public class Dormitory extends MacroObjectAbstract{

    public Dormitory(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean loadCreatures() {
        addCreature(new Cultist());
        addCreature(new Cultist());
        addCreature(new Cultist());
        return false;
    }

    @Override
    public void loadTexture() {setTexture(ImageLoader.loadExternalTexture("src/main/resources/assets/textures/macroObjects/Dormitory.png"));}
}
