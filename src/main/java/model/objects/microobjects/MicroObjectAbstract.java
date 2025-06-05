package model.objects.microobjects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.*;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import model.Direction;
import model.TriggerComponent;
import model.items.Item;
import model.items.firearms.Gun;
import model.items.inventory.Inventory;
import model.objects.EntityType;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.macroobjects.typeOfMacroObject;
import my.kursova21.Lab4;
import org.jetbrains.annotations.NotNull;
import utilies.ImageLoader;

import java.util.Objects;

public abstract class MicroObjectAbstract extends Component implements Comparable<MicroObjectAbstract>,Cloneable {

    public static int MAX_HEIGHT= Lab4.HEIGHT;
    public static int MAX_WIDTH=Lab4.WIDTH;
    public static double RADIUS = 50;
    // Статичний блок ініціалізації
    static {
        System.out.println("Виконано статичний блок ініціалізації");
    }
    // нестатичний блок ініціалізації
    {
        System.out.println("Виконано нестатичний блок ініціалізації");
    }

    private String creatureName;
    private int health;//point of Health, when this field<=0 creature die
    private double armor;//Math.floor(damage-damage/(100/getArmor) damage it's received damage
    protected Inventory inventory;//it's a place where keeping items
    private double experiencePoint;//Amount of xp, what player received
    protected PhysicsComponent physics=new PhysicsComponent();
    protected final int speed;
    private final EntityType type;
    private MacroObjectAbstract macroObjectAbstract;

    private boolean active = false;

    private int x;
    private int y;

    protected AnimatedTexture mainTexture;
    protected AnimatedTexture weaponTexture;
    protected AnimationChannel animIdleRight,animIdleLeft,animIdleDown,animIdleUp,animWalkRight, animWalkLeft, animWalkUp, animWalkDown;

    protected Direction direction=Direction.RIGHT;

    public MicroObjectAbstract(String creatureName, int health, double armor, Inventory inventory, double experiencePoint, int x, int y, int speed, EntityType type) {//true constructor
        super();
        this.creatureName = creatureName;
        this.health = health;
        this.armor = armor;
        this.inventory = inventory;
        this.experiencePoint = experiencePoint;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.type = type;
    }

    public MicroObjectAbstract() {//task condition
        this("Creature",100,5,Inventory.getInventory(50),150,0,0,1,EntityType.ENEMY);
    }

    public MicroObjectAbstract(int x, int y) {
        this("Creature",100,5,Inventory.getInventory(50),150,x,y,1,EntityType.ENEMY);
    }

    private boolean wasActive = true;

    @Override
    public void onUpdate(double tpf) {
            this.x = (int) entity.getX();
            this.y = (int) entity.getY();

        if (active && !wasActive) {
            mainTexture.loop();
            wasActive = true;
        } else if (!active && wasActive) {
            mainTexture.stop();
            wasActive = false;
        }

        super.onUpdate(tpf);
    }

    public String getCreatureName() {
        return creatureName;
    }

    public void setCreatureName(String creatureName) {
        this.creatureName = creatureName;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getArmor() {
        return armor;
    }

    public void setArmor(double armor) {
        this.armor = armor;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public double getExperiencePoint() {
        return experiencePoint;
    }

    public void setExperiencePoint(double experiencePoint) {
        this.experiencePoint = experiencePoint;
    }

    public void setX(int x) {
        this.x = x;
       // ConsoleHelper.writeMessage("Змінено x "+this+x);
    }
    public void setY(int y) {
        this.y = y;
        //ConsoleHelper.writeMessage("Змінено у "+this+y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setMainTexture(AnimatedTexture mainTexture) {
        this.mainTexture = mainTexture;
    }

    public Texture getMainTexture() {
        return mainTexture;
    }

    public EntityType getType() {
        return type;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MicroObjectAbstract microObjectAbstract = (MicroObjectAbstract) o;
        return health == microObjectAbstract.health && Double.compare(experiencePoint, microObjectAbstract.experiencePoint) == 0 && x == microObjectAbstract.x && y == microObjectAbstract.y && Objects.equals(creatureName, microObjectAbstract.creatureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creatureName, health, experiencePoint, x, y);
    }

    @Override
    public void onAdded() {
        super.onAdded();
        physics.setBodyType(BodyType.KINEMATIC);
        entity.addComponent(new TriggerComponent(RADIUS));
        entity.addComponent(new CollidableComponent(true));
        entity.addComponent(physics);
        entity.getViewComponent().clearChildren();
        loadAnimatedTexture();
        entity.getViewComponent().addChild(mainTexture); // або твоя текстура істоти
        updateItem();
        //entity.getViewComponent().addChild(weaponTexture);

    }

    public abstract String toString();
    public abstract void takeDamage(MicroObjectAbstract microObjectAbstract);
    public abstract void getDamage(int damage);
    public abstract void talk();
    public abstract void print();
    public abstract void loadAnimatedTexture();
    public abstract void stop();


    public void stopPhysic(){
        physics.setVelocityX(0);
        physics.setVelocityY(0);
    }

    public void moveRight() {
        if (x>=MAX_WIDTH-10){
            stopPhysic();
            return;
        }
        direction=Direction.RIGHT;
        updateItem();
        physics.setVelocityX(speed);
    }

    public void moveLeft() {
        if (x<=10){
            stopPhysic();
            return;
        }
        direction=Direction.LEFT;
        updateItem();
        physics.setVelocityX(-speed);
    }

    public void moveUp() {
        if (y<=5){
            stopPhysic();
            return;
        }
        direction=Direction.UP;
        updateItem();
        physics.setVelocityY(-speed);
    }

    public void moveDown() {
        if (y>=MAX_HEIGHT-50){
            stopPhysic();
            return;
        }
        direction=Direction.DOWN;
        updateItem();
        physics.setVelocityY(speed);
    }

    public void fire(Point2D target){
        boolean firing;
        Item item = inventory.getCurrentGun();
        if (item instanceof Gun gun) {
            if(gun.isReloading()){
                return;
            }
            firing= gun.IsAutomatic();

            gun.fire(getX(), getY(),target, getInventory(),direction);
            if (gun.IsAutomatic()) {
                FXGL.run(() -> {
                    if (firing) {
                        gun.fire(getX(), getY(), target, getInventory(),direction);
                    }
                }, Duration.seconds(gun.getFireRate())); // Час між пострілами (0.1 сек = 10 пострілів за секунду)
            }
        }
    }

    public void setCurrentItem(String itemName){
        inventory.setCurrentGun(itemName);
        Gun gun= (Gun) inventory.checkItemAndReturn(itemName);
        weaponTexture=new AnimatedTexture(gun.getGunTexture(direction));
    }

    private void updateItem() {
        if (weaponTexture==null) {
            weaponTexture=new AnimatedTexture(ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Box.png"),1,13,15,1));
        }
        weaponTexture.loopAnimationChannel(inventory.getCurrentGun().getGunTexture(direction));
    }


    @Override
    public int compareTo(@NotNull MicroObjectAbstract o) {
        return Double.compare(o.armor, armor)+Double.compare(o.speed,speed)+inventory.compareTo(o.inventory);
    }

    public static int compare(MicroObjectAbstract o1, MicroObjectAbstract o2) {
        return o1.compareTo(o2);
    }

    public int compareToName(@NotNull MicroObjectAbstract o) {
        return creatureName.compareTo(o.creatureName);
    }

    public int compareToHealth(@NotNull MicroObjectAbstract o) {
        return Integer.compare(o.health, health);
    }

    public int compareToArmor(@NotNull MicroObjectAbstract o) {
        return Double.compare(o.armor, armor);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public abstract Object clone() throws CloneNotSupportedException;

    public static boolean isIsMouseInside(Entity entity, Point2D mousePos) {
        BoundingBoxComponent bbox = entity.getBoundingBoxComponent();//Визначаємо розміри хартбоксу знайденого створіння
        double minX = bbox.getMinXWorld();
        double maxX = bbox.getMaxXWorld();
        double minY = bbox.getMinYWorld();
        double maxY = bbox.getMaxYWorld();
        return mousePos.getX() >= minX && mousePos.getX() <= maxX &&
                mousePos.getY() >= minY && mousePos.getY() <= maxY;//Перевіряємо чи є усереднені створіння курсор
    }

    public Entity createCopyAt(Point2D pos){
        int x=(int) pos.getX();
        int y=(int) pos.getY();

        // Клонуємо компонент, щоб уникнути спільного стану
        MicroObjectAbstract clone;
        try {
            clone = (MicroObjectAbstract) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }

        clone.setX(x);
        clone.setY(y);

        Entity newEntity = FXGL.entityBuilder()
                .at(new Point2D(x, y))
                .type(clone.getType()) // Якщо маєш тип
                .with(clone) // Додаємо клонований компонент
                .build();
        newEntity.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(0, -4), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(31, 85) // Розмір хитбоксу
        ));
        return newEntity;
    }

    public abstract Entity getNewEntity();

    public PhysicsComponent getPhysics() {
        return physics;
    }

    public MacroObjectAbstract getMacroObjectAbstract() {
        return macroObjectAbstract;
    }

    public void setMacroObjectAbstract(MacroObjectAbstract macroObjectAbstract) {
        this.macroObjectAbstract = macroObjectAbstract;
    }
}

