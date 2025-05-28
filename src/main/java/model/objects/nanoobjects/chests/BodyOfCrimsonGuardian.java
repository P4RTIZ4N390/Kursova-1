package model.objects.nanoobjects.chests;

import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.geometry.Point2D;
import model.Direction;
import model.items.firearms.ammos.Ammo556mm;
import model.items.firearms.rifles.AR15CrimsonOrderEdition;
import utilies.ImageLoader;

public class BodyOfCrimsonGuardian extends Chest{

    public BodyOfCrimsonGuardian(String name, int x, int y, Direction direction) {
        super(name, x, y, 50, direction);
    }

    @Override
    protected void loadTextures() {
        switch (getDirection()) {
            case LEFT -> texture=new AnimatedTexture(ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/bodyOfGuardian/left.png"),1,310,170,1));
            case RIGHT -> texture=new AnimatedTexture(ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/bodyOfGuardian/right.png"),1,290,170,1));
            case UP -> texture=new AnimatedTexture(ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/bodyOfGuardian/up.png"),1,130,220,1));
            case DOWN -> texture=new AnimatedTexture(ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/bodyOfGuardian/down.png"),1,130,195,1));
        }

        texture.setScaleX(0.25);
        texture.setScaleY(0.25);

        switch (getDirection()) {
            case LEFT -> {
                texture.setTranslateX(-texture.getImage().getWidth() * 0.25-38);
                texture.setTranslateY(-texture.getImage().getHeight() * 0.25-21);
            }
            case RIGHT -> {
                texture.setTranslateX(-texture.getImage().getWidth() * 0.25-39);
                texture.setTranslateY(-texture.getImage().getHeight() * 0.25-21);
            }
            case UP -> {
                texture.setTranslateX(-texture.getImage().getWidth() * 0.25-15);
                texture.setTranslateY(-texture.getImage().getHeight() * 0.25-27);
            }
            case DOWN -> {
                texture.setTranslateX(-texture.getImage().getWidth() * 0.25-15);
                texture.setTranslateY(-texture.getImage().getHeight() * 0.25-25);
            }
        }
    }

    @Override
    protected void loadInventory() {
        getInventory().addItem(new AR15CrimsonOrderEdition());
        getInventory().addItems(new Ammo556mm(), 40);
    }

    @Override
    public void onAdded() {
        super.onAdded();
        entity.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(0, 0), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(texture.getWidth()*0.25, texture.getHeight()*0.25) // Розмір хитбоксу
        ));
    }
}
