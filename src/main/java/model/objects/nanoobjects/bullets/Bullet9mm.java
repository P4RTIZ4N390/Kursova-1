package model.objects.nanoobjects.bullets;

import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import model.items.firearms.ammos.Ammo9mm;
import utilies.ImageLoader;

public class Bullet9mm extends Bullet {

    private static final double bulletSpeed = 420; // Швидкість кулі, у метрах/секундах
    private static final int trueDamage= Ammo9mm.getDamage();

    public Bullet9mm(Point2D direction, int additionalDamage) {
        super(direction, trueDamage,additionalDamage,bulletSpeed);
    }

    @Override
    public Texture getBulletTexture() {
        return ImageLoader.loadExternalTexture("src/main/resources/assets/textures/bullets/9mm.png");
    }
}
