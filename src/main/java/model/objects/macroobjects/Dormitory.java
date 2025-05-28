package model.objects.macroobjects;

import utilies.ImageLoader;

public class Dormitory extends MacroObjectAbstract{

    public Dormitory(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean loadCreatures() {
        return false;
    }

    @Override
    public void loadTexture() {setTexture(ImageLoader.loadExternalTexture("src/main/resources/assets/textures/macroObjects/Dormitory.png"));}
}
