package model.objects.nanoobjects.bullets;

import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import model.items.firearms.ammos.Ammo45ACP;
import model.objects.microobjects.MicroObjectAbstract;
import utilies.ImageLoader;

public class Bullet45ACP extends Bullet {

    private static final double bulletSpeed = 350; // Швидкість кулі, у метрах/секундах
    private static final int trueDamage= Ammo45ACP.getDamage();

    public Bullet45ACP(Point2D direction, int additionalDamage, MicroObjectAbstract shooter) {
        super(direction, additionalDamage, trueDamage, bulletSpeed, shooter);
    }


    @Override
    public Texture getBulletTexture() {
        return ImageLoader.loadExternalTexture("src/main/resources/assets/textures/bullets/45ACP.png");
    }
}
