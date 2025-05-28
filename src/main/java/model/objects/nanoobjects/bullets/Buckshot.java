package model.objects.nanoobjects.bullets;

import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import model.items.firearms.ammos.Ammo12gauge;
import utilies.ImageLoader;

public class Buckshot extends Bullet{

    private static final double bulletSpeed = 420; // Швидкість кулі, у метрах/секундах
    private static final int trueDamage= Ammo12gauge.getDamage();

    public Buckshot(Point2D direction, int additionalDamage) {
        super(direction, additionalDamage, trueDamage, bulletSpeed);
    }

    @Override
    public Texture getBulletTexture() {
        return ImageLoader.loadExternalTexture("src/main/resources/assets/textures/bullets/Buckshot.png");
    }
}
