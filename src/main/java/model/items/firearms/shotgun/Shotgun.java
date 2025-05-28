package model.items.firearms.shotgun;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import model.Direction;
import model.objects.EntityType;
import model.items.firearms.Gun;
import model.items.inventory.Inventory;
import model.objects.nanoobjects.bullets.Buckshot;
import model.objects.nanoobjects.bullets.Bullet;
import utilies.ConsoleHelper;

public abstract class Shotgun extends Gun{


    public Shotgun(String name, double weight, int amountOfAmmo, int additionalDamage, String description) {
        super(name, weight, amountOfAmmo, additionalDamage, description);
    }

    @Override
    public void fire(double shooterX, double shooterY, Point2D target, Inventory inventory, Direction currentDirection) {
        if (leftAmmo > 0 && !isReloading()) {
            for (int i = -2; i <= 2; i++) {
                Point2D startPos = new Point2D(shooterX + 20, shooterY + 10); // Початкова позиція кулі
                Point2D direction = target.subtract(startPos).normalize(); // Вектор напрямку

                direction = direction.add(i * 0.1, i * 0.1).normalize();

                Entity bullet = new Entity();
                bullet.setPosition(startPos);

                double angle = Math.toDegrees(Math.atan2(direction.getY(), direction.getX()));

                Bullet infoBullet = new Buckshot(direction, additionalDamage);

                Texture texture = infoBullet.getBulletTexture();
                texture.setRotate(angle);
                bullet.getViewComponent().addChild(texture);
                bullet.getBoundingBoxComponent().addHitBox(new HitBox("BULLET_HITBOX",
                        BoundingShape.box(texture.getWidth(), texture.getHeight())));

                bullet.addComponent(infoBullet);
                bullet.setType(EntityType.ENEMY_BULLET);

                FXGL.getGameWorld().addEntity(bullet);
                ConsoleHelper.writeMessage(String.valueOf(leftAmmo));

            }
            leftAmmo--;
        } else reload(inventory);
    }

    @Override
    public String getCaliber() {
        return "12mm";
    }
}
