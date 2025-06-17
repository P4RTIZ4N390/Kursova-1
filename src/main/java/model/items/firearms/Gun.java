package model.items.firearms;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import model.Direction;
import model.Player;
import model.objects.EntityType;
import model.items.Item;
import model.items.TypesOfItem;
import model.items.inventory.Inventory;
import model.objects.microobjects.MicroObjectAbstract;
import model.objects.nanoobjects.bullets.*;
import org.jetbrains.annotations.NotNull;
import utilies.ConsoleHelper;
import utilies.ImageLoader;
import utilies.RandomUtil;

public abstract class Gun extends Item implements Firearm {

    protected final int amountOfAmmo;//Кількість патронів в обоймі
    protected int additionalDamage;//Додаткова шкода, до шкоди кулі
    protected int leftAmmo;//Скільки патронів залишилося

    private boolean reloading=false;

    @Override
    public AnimationChannel getGunTexture(Direction direction) {
        return ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Box.png"),1,13,15,1);
    }

    public Gun(String name, double weight, int amountOfAmmo, int additionalDamage , String description) {
        super(name, weight,description, TypesOfItem.FIREARM_WEAPON);
        this.amountOfAmmo = amountOfAmmo;
        leftAmmo = amountOfAmmo;
        this.additionalDamage = additionalDamage;
    }

    @Override
    public abstract String getCaliber();//Повернути тип патронів

    @Override
    public void reload(Inventory inventory) {//Перезаряджання
        if (reloading) return; // Якщо вже перезаряджається, не запускаємо повторно
        reloading = true;
        if (inventory.checkItemAndReturnAmount(getCaliber(), amountOfAmmo) && leftAmmo==0) {
            leftAmmo = amountOfAmmo;
            inventory.removeItem(getCaliber(), amountOfAmmo);
        } else {
            int amountOfAmmoInInventory = inventory.checkItemAndReturnAmount(getCaliber());
            if (amountOfAmmoInInventory > 0) {
                if (leftAmmo==0) {
                    leftAmmo = amountOfAmmoInInventory;
                    inventory.removeItem(getCaliber(), amountOfAmmoInInventory);
                }else {
                    int neededAmmo = amountOfAmmo-leftAmmo;
                    if (neededAmmo>=amountOfAmmoInInventory) {
                        leftAmmo =leftAmmo+ amountOfAmmoInInventory;
                        inventory.removeItem(getCaliber(), amountOfAmmoInInventory);
                    }else {
                        leftAmmo = amountOfAmmo;
                        inventory.removeItem(getCaliber(), neededAmmo);
                    }
                }
            }else {
                reloading = false;
                return;
            }
        }

        FXGL.runOnce(() -> reloading = false, Duration.seconds(getReloadTime()));
    }

    @Override
    public void reloadForPlayer(Inventory inventory) {//Перезаряджання
        if (reloading) return; // Якщо вже перезаряджається, не запускаємо повторно
        reloading = true;
        ConsoleHelper.writeMessage("Перезаряджання...");

        if (inventory.checkItemAndReturnAmount(getCaliber(), amountOfAmmo) && leftAmmo==0) {
            leftAmmo = amountOfAmmo;
            inventory.removeItem(getCaliber(), amountOfAmmo);
        } else {
            int amountOfAmmoInInventory = inventory.checkItemAndReturnAmount(getCaliber());
            if (amountOfAmmoInInventory > 0) {
                if (leftAmmo==0) {
                    leftAmmo = amountOfAmmoInInventory;
                    inventory.removeItem(getCaliber(), amountOfAmmoInInventory);
                }else {
                    int neededAmmo = amountOfAmmo-leftAmmo;
                    if (neededAmmo>=amountOfAmmoInInventory) {
                        leftAmmo =leftAmmo+ amountOfAmmoInInventory;
                        inventory.removeItem(getCaliber(), amountOfAmmoInInventory);
                    }else {
                        leftAmmo = amountOfAmmo;
                        inventory.removeItem(getCaliber(), neededAmmo);
                    }
                }
            } else {
                ConsoleHelper.writeMessage("В тебе недостатньо патронів цього типу \""+getCaliber()+'\"');
                reloading = false;
                return;
            }
        }

        FXGL.runOnce(() -> {
            reloading = false;
            ConsoleHelper.writeMessage("Перезаряджання завершене!");
        }, Duration.seconds(getReloadTime()));
    }

    @Override
    public void fireForPlayer(double shooterX, double shooterY, Point2D cursorLocation, Inventory inventory, Direction directionOfCreature){
        if (leftAmmo > 0 && !isReloading()) {
            Point2D startPos = getStartPos(shooterX, shooterY, directionOfCreature);
            Point2D direction = cursorLocation.subtract(startPos).normalize(); // Вектор напрямку
            direction=direction.add(RandomUtil.getRandomDoubleBetweenMinusAndPlus(getSpread_Amount()),RandomUtil.getRandomDoubleBetweenMinusAndPlus(getSpread_Amount())).normalize();//Обрахунок розкиду пострілу і траєкторії

            double angle = Math.toDegrees(Math.atan2(direction.getY(), direction.getX()));

            ConsoleHelper.writeMessage(String.valueOf(normalizeAngle(angle)));
            ConsoleHelper.writeMessage(String.valueOf(leftAmmo));



            FXGL.getGameWorld().addEntity(getEntityBullet(direction,angle,startPos,EntityType.PLAYER_BULLET, Player.getInstance()));
            leftAmmo--;
            if (leftAmmo==3) {
                ConsoleHelper.writeMessage("Залишилося три кулі");
            }
        } else reloadForPlayer(inventory);
    }

    @Override
    public void fire(MicroObjectAbstract shooter, Point2D target, Inventory inventory, Direction directionOfCreature) {
        if (leftAmmo > 0 && !isReloading()) {
            Point2D startPos = getStartPos(shooter.getX(), shooter.getY(), directionOfCreature);
            Point2D direction = target.subtract(startPos).normalize(); // Вектор напрямку
            direction=direction.add(RandomUtil.getRandomDoubleBetweenMinusAndPlus(getSpread_Amount()),RandomUtil.getRandomDoubleBetweenMinusAndPlus(getSpread_Amount())).normalize();
            double angle = Math.toDegrees(Math.atan2(direction.getY(), direction.getX()));


            FXGL.getGameWorld().addEntity(getEntityBullet(direction,angle,startPos,EntityType.ENEMY_BULLET,shooter));
            leftAmmo--;
        } else reload(inventory);
    }

    @NotNull
    public static Point2D getStartPos(double shooterX, double shooterY, Direction directionOfCreature) {
        Point2D startPos;
        switch (directionOfCreature) {// Початкова позиція кулі
            case RIGHT -> startPos = new Point2D(shooterX + 25, shooterY + 9);
            case LEFT -> startPos = new Point2D(shooterX - 25, shooterY + 9);
            case UP -> startPos = new Point2D(shooterX +8, shooterY -8);
            case DOWN -> startPos = new Point2D(shooterX +6.5, shooterY +9.5);
            default -> startPos = new Point2D(shooterX + 20, shooterY + 10);
        }
        return startPos;
    }

    @NotNull
    private Bullet getInfoBullet(Point2D direction,MicroObjectAbstract shooter) {
        Bullet infoBullet;

        switch (getCaliber()){
            case "5.7×28мм"->infoBullet=new Bullet57mm(direction,additionalDamage,shooter);
            case "45ACP"->infoBullet=new Bullet45ACP(direction,additionalDamage,shooter);
            case "5.56мм"->infoBullet=new Bullet556mm(direction,additionalDamage,shooter);
            case "7.62х39мм"->infoBullet=new Bullet762mm(direction,additionalDamage,shooter);
            default ->  infoBullet=new Bullet9mm(direction,additionalDamage,shooter);
        }
        return infoBullet;
    }

    @Override
    public String getDescription() {//повернути опис зброї
        return super.getDescription() + '\n' + "Калібр: " + getCaliber()+'\n' + "Ємність магазину: " + amountOfAmmo+'\n'+"Додаткова шкода з пострілу: "+ additionalDamage;
    }

    public boolean isReloading() {
        return reloading;
    }

    public static Direction getWhereFireInDirection(double angle) {
        angle = normalizeAngle(angle);

        if ((angle >= -45 && angle <= 45)) {
            return Direction.RIGHT;
        } else if ((angle >= 45 && angle <= 135)) {
            return Direction.DOWN;
        } else if ((angle >= -135 && angle <= -45)) {
            return Direction.UP;
        } else {
            return Direction.LEFT;
        }
    }

    private static double normalizeAngle(double angle) {
        // Метод для нормалізації кута до діапазону [-180, 180]
        // Бо метод який я використав визначення повертає значення від -180 до 180
        angle = angle % 360;
        if (angle > 180) angle -= 360;
        if (angle < -180) angle += 360;
        return angle;
    }
    protected Entity getEntityBullet(Point2D direction, double angle,Point2D startPos,EntityType type,MicroObjectAbstract shooter) {
        Entity bullet = new Entity();
        bullet.setPosition(startPos);

        Bullet infoBullet = getInfoBullet(direction,shooter);

        Texture texture = infoBullet.getBulletTexture();
        texture.setRotate(angle);
        bullet.getViewComponent().addChild(texture);
        bullet.getBoundingBoxComponent().addHitBox(new HitBox("BULLET_HITBOX",
                BoundingShape.box(texture.getWidth(), texture.getHeight())));

        bullet.addComponent(infoBullet);
        bullet.setType(type);
        return bullet;
    }

    public int getAdditionalDamage() {
        return additionalDamage;
    }
}
