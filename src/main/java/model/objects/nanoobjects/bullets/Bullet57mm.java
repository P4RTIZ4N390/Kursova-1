package model.objects.nanoobjects.bullets;

import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import model.items.firearms.ammos.Ammo57mm;
import model.objects.microobjects.MicroObjectAbstract;
import utilies.ImageLoader;

public class Bullet57mm extends Bullet {

    private static final double bulletSpeed = 720; // Швидкість кулі, у метрах/секундах
    private static final int trueDamage= Ammo57mm.getDamage();

    public Bullet57mm(Point2D direction, int additionalDamage, MicroObjectAbstract shooter) {
        super(direction, additionalDamage, trueDamage, bulletSpeed, shooter);
    }

    @Override
    public Texture getBulletTexture() {
        return ImageLoader.loadExternalTexture("src/main/resources/assets/textures/bullets/57mm.png");
    }
}
