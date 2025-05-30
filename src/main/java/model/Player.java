package model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.*;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.*;
import model.items.Item;
import model.items.firearms.Gun;
import model.items.firearms.ammos.*;
import model.items.firearms.pistols.SmithWessonMP57;
import model.items.firearms.rifles.AR15CrimsonOrderEdition;
import model.items.inventory.InventoryInteractionView;
import model.objects.EntityType;
import model.objects.microobjects.MicroObjectAbstract;
import com.almasb.fxgl.input.UserAction;
import model.items.inventory.Inventory;
import model.objects.nanoobjects.chests.BodyOfCrimsonGuardian;
import model.objects.nanoobjects.chests.Chest;
import model.objects.nanoobjects.bullets.Bullet;
import utilies.ConsoleHelper;
import utilies.ImageLoader;
import utilies.RandomUtil;

import java.util.*;

public class Player extends MicroObjectAbstract {//singleton pattern

    private static Player instance;
    private final InventoryInteractionView interactionView = InventoryInteractionView.getInstance(getInventory());
    private boolean inventoryOpen = false;
    private boolean interactionOpen = false;

    private final Gun wristMountedUnderBarrelFirearm=new Gun("WMUBF",0.1,1,30,"Не забути придумати") {

        @Override
        public String getCaliber() {
            return "Cartridge for WMUBF";
        }

        @Override
        public boolean IsAutomatic() {
            return false;
        }

        @Override
        public double getFireRate() {
            return 0;
        }

        @Override
        public double getReloadTime() {
            return 3;
        }

        @Override
        public double getSpread_Amount() {
            return 0;
        }

        @Override
        public AnimationChannel getGunTexture(Direction direction) {
            return null;
        }

        @Override
        public void fire(double shooterX, double shooterY, Point2D cursorLocation, Inventory inventory, Direction directionOfCreature) {
            if(leftAmmo > 0 ){
                Point2D startPos = getStartPos(shooterX, shooterY, directionOfCreature);
                Point2D direction = cursorLocation.subtract(startPos).normalize(); // Вектор напрямку
                direction=direction.add(RandomUtil.getRandomDoubleBetweenMinusAndPlus(getSpread_Amount()),RandomUtil.getRandomDoubleBetweenMinusAndPlus(getSpread_Amount())).normalize();//Обрахунок розкиду пострілу і траєкторії

                double angle = Math.toDegrees(Math.atan2(direction.getY(), direction.getX()));

                switch (directionOfCreature) {
                    case UP,DOWN->{
                        FXGL.getGameWorld().addEntity(getEntityBullet(direction,angle,new Point2D(startPos.getX()-1.5,startPos.getY()),EntityType.PLAYER_BULLET));
                        FXGL.getGameWorld().addEntity(getEntityBullet(direction,angle,new Point2D(startPos.getX()+1.5,startPos.getY()),EntityType.PLAYER_BULLET));
                    }
                    case LEFT,RIGHT->{
                        FXGL.getGameWorld().addEntity(getEntityBullet(direction,angle,new Point2D(startPos.getX(),startPos.getY()-1.5),EntityType.PLAYER_BULLET));
                        FXGL.getGameWorld().addEntity(getEntityBullet(direction,angle,new Point2D(startPos.getX(),startPos.getY()-1.5),EntityType.PLAYER_BULLET));
                    }
                }

                leftAmmo--;
            }else reloadForPlayer(getInventory());
        }

        @Override
        protected Entity getEntityBullet(Point2D direction, double angle, Point2D startPos, EntityType type) {
            Entity bullet = new Entity();
            bullet.setPosition(startPos);

            Bullet infoBullet = new Bullet(direction,50,40,1000) {
                @Override
                public Texture getBulletTexture() {
                    return ImageLoader.loadExternalTexture("D:\\java_projects\\Kursova2.1\\src\\main\\resources\\assets\\textures\\bullets\\45ACP.png");
                }
            };

            Texture texture = infoBullet.getBulletTexture();
            texture.setRotate(angle);
            bullet.getViewComponent().addChild(texture);
            bullet.getBoundingBoxComponent().addHitBox(new HitBox("BULLET_HITBOX",
                    BoundingShape.box(texture.getWidth(), texture.getHeight())));

            bullet.addComponent(infoBullet);
            bullet.setType(type);
            return bullet;
        }
    };//Ця зброя є тільки в гравця, тому вона створена анонімним класом

    public static Player getInstance() {
        if (instance == null) {
            Gun startGun=new SmithWessonMP57();
            instance = new Player("Unknown",200,10,Inventory.getInventoryForPlayer(50,startGun),0,0,0,200);
            instance.getInventory().addItems(new Ammo57mm(),66);
            instance.getInventory().addItems(new CartridgeForWMUBF(),2);
            instance.getInventory().addItem(new AR15CrimsonOrderEdition());
        }
        return instance;
    }

    private Player(String creatureName, int health, double armor, Inventory inventory, double experiencePoint, int x, int y,int speed) {
        super(creatureName, health, armor, inventory, experiencePoint, x, y, speed, EntityType.PLAYER);
        setupControls();
    }

    @Override
    public String toString() {
        return getCreatureName()+"("+getHealth()+")";
    }

    @Override
    public void takeDamage(MicroObjectAbstract microObjectAbstract) {
    }

    @Override
    public void getDamage(int damage) {//Отримати шкоди, детальніше в StrangeRat

        int damageValue = (int) Math.floor(damage-damage/(100/getArmor()));
        if(damageValue>0){
            setHealth(getHealth()-damageValue);
        }
    }


    @Override
    public void talk() {}

    @Override
    public void print() {}

    @Override
    public void loadAnimatedTexture() {
        animIdleRight =ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/idle/idleRight.png"),4,160,280,2);
        animIdleLeft=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/idle/idleLeft.png"),4,160,280,2);
        animIdleDown=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/idle/idleUp.png"),4,116,288,2.5);
        animIdleUp=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/idle/idleUp.png"),4,116,300,2.5);

        animWalkUp=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/walk/walkUp.png"),10,92,316,1.25);
        animWalkDown=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/walk/walkDown.png"),12,96,316,1.25);
        animWalkLeft=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/walk/walkLeft.png"),10,255,280,1.25);
        animWalkRight=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/player animations/walk/walkRight.png"),10,255,280,1.25);


        mainTexture = new AnimatedTexture(animIdleRight);
        mainTexture.loopAnimationChannel(animIdleRight);

        mainTexture.setScaleX(0.25);
        mainTexture.setScaleY(0.25);

        mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25);
        mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+50);
    }


    public void stop() {
        switch (direction) {
            case RIGHT->{
                mainTexture.loopAnimationChannel(animIdleRight);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+50);

            }
            case LEFT->{
                mainTexture.loopAnimationChannel(animIdleLeft);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+50);
            }
            case DOWN -> {
                mainTexture.loopAnimationChannel(animIdleDown);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+20);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+5);
            }
            case UP -> {
                mainTexture.loopAnimationChannel(animIdleUp);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+32);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25);}
        }
        stopPhysic();
    }

    @Override
    public void moveRight() {
        if (mainTexture.getAnimationChannel() != animWalkRight) {
            mainTexture.loopAnimationChannel(animWalkRight);
            mainTexture.setTranslateX(-mainTexture.getImage().getWidth() * 0.25+516);
            mainTexture.setTranslateY(-mainTexture.getImage().getHeight() * 0.25-42);}
        super.moveRight();
    }

    @Override
    public void moveLeft() {
        if (mainTexture.getAnimationChannel() != animWalkLeft) {
            mainTexture.loopAnimationChannel(animWalkLeft);
            mainTexture.setTranslateX(-mainTexture.getImage().getWidth() * 0.25+518);
            mainTexture.setTranslateY(-mainTexture.getImage().getHeight() * 0.25-42);
        }
        super.moveLeft();
    }

    @Override
    public void moveUp() {
        if (mainTexture.getAnimationChannel() != animWalkUp){ mainTexture.loopAnimationChannel(animWalkUp);
            mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+45);
            mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+108);
        }
        super.moveUp();
    }

    @Override
    public void moveDown() {
        if (mainTexture.getAnimationChannel() != animWalkDown){
            mainTexture.loopAnimationChannel(animWalkDown);
            mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+38);
            mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+152);
        }
        super.moveDown();
    }

    private void interactWithClickedEntity() {
        Point2D mousePos = FXGL.getInput().getMousePositionWorld(); // Отримуємо позицію миші у світових координатах
        List<Entity> entityList = FXGL.getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                .filter(e -> e.getComponentOptional(Player.class).isEmpty()) // Виключаємо гравця
                .toList();

        entityList.stream()
                .filter(entity -> {
                    boolean isMouseInside = isIsMouseInside(entity, mousePos);

                    // Перевіряємо дистанцію між гравцем і ціллю
                    Point2D playerPos = Player.getInstance().getEntity().getPosition();
                    double distance = playerPos.distance(entity.getPosition());

                    return isMouseInside && distance <= RADIUS;
                })
                .findFirst().flatMap(entity -> entity.getComponents().stream()
                        .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є Creature
                        .map(MicroObjectAbstract.class::cast) // Перетворюємо компонент у Creature
                        .findFirst()).ifPresent(creature -> {
                    if (!creature.isDead()) {
                        return;
                    }
                    interactionView.setNewInteraction(creature.getInventory(),creature.getCreatureName());
                    System.out.println("Невідома істота: " + creature.getCreatureName());

                    interactionOpen = true;
                    inventoryOpen = true;
                });
        entityList.stream().filter(entity -> {
                    boolean isMouseInside = isIsMouseInside(entity, mousePos);

                    // Перевіряємо дистанцію між гравцем і ціллю
                    Point2D playerPos = Player.getInstance().getEntity().getPosition();
                    double distance = playerPos.distance(entity.getPosition());

                    return isMouseInside && distance <= RADIUS;
                })
                .findFirst().flatMap(entity -> entity.getComponents().stream()
                        .filter(Chest.class::isInstance) // Перевіряємо, чи компонент є Chest
                        .map(Chest.class::cast) // Перетворюємо компонент у Chest
                        .findFirst()).ifPresent(chest -> {
                            if (chest instanceof BodyOfCrimsonGuardian bodyOfCrimsonGuardian){
                                interactionView.setNewInteraction(bodyOfCrimsonGuardian.getInventory(),"Тіло Стража Багрового Id"+chest.getName());
                            }
                            else {interactionView.setNewInteraction(chest.getInventory(),chest.getName());}
                    System.out.println("Невідома скриня: " + chest.getName());

                    interactionOpen = true;
                    inventoryOpen = true;
                });
    }

    public void setupControls() {

        Input input = FXGL.getInput();

        input.addAction(new UserAction("Shoot from WMUBF") {
            @Override
            protected void onActionBegin() {
                if (!inventoryOpen) {
                    Point2D mousePos = FXGL.getInput().getMousePositionWorld().subtract(getX(),getY());

                    Direction directionWhereFiring=Gun.getWhereFireInDirection(Math.toDegrees(Math.atan2(mousePos.getY(), mousePos.getX())));

                    if (direction!=directionWhereFiring){
                        direction=directionWhereFiring;
                        stop();
                    }

                    wristMountedUnderBarrelFirearm.fire(getX(),getY(),FXGL.getInput().getMousePositionUI(),getInventory(),direction);
                }
            }
        },KeyCode.E);

        input.addAction(new UserAction("Shoot") {
            private boolean firing = false;

            @Override
            protected void onActionBegin() {
                if (!inventoryOpen) {
                    Item item = inventory.getCurrentGun();
                    if (item instanceof Gun gun) {
                        if(gun.isReloading()){
                            return;
                        }
                        firing= gun.IsAutomatic();

                        Point2D mousePos = FXGL.getInput().getMousePositionWorld().subtract(getX(),getY());

                        Direction directionWhereFiring=Gun.getWhereFireInDirection(Math.toDegrees(Math.atan2(mousePos.getY(), mousePos.getX())));

                        if (direction!=directionWhereFiring){
                            direction=directionWhereFiring;
                            stop();
                        }

                        gun.fireForPlayer(getX(), getY(), FXGL.getInput().getMousePositionWorld(), getInventory(),direction);

                        if (gun.IsAutomatic()) {
                            FXGL.run(() -> {
                                if (firing) {
                                    gun.fireForPlayer(getX(), getY(), FXGL.getInput().getMousePositionWorld(), getInventory(),direction);
                                }
                            }, Duration.seconds(gun.getFireRate())); // Час між пострілами (0.1 сек = 10 пострілів за секунду)
                        }
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                firing = false;
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Interact with Entity") {
            @Override
            protected void onActionBegin() {
                interactWithClickedEntity();
            }
        }, MouseButton.SECONDARY); // ПКМ

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                moveRight();
            }

            @Override
            protected void onActionEnd() {
                stop();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                moveLeft();
            }

            @Override
            protected void onActionEnd() {
                stop();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onActionBegin() {
                moveUp();
            }

            @Override
            protected void onActionEnd() {
                stop();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onActionBegin() {
                moveDown();
            }

            @Override
            protected void onActionEnd() {
                stop();
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Toggle Inventory") {//Відкрити та закрити інвентар
            @Override
            protected void onActionBegin() {
                if (inventoryOpen && interactionOpen){
                    interactionView.closeInventory();
                    inventoryOpen=!inventoryOpen;
                    return;
                }
                inventory.showInventoryView();
                inventoryOpen=!inventoryOpen;
            }
        }, KeyCode.I);//І - інвентар

        FXGL.getInput().addAction(new UserAction("Reload weapon") {//Перезарядка
            @Override
            protected void onActionBegin() {
                Gun gun = inventory.getCurrentGun();
                wristMountedUnderBarrelFirearm.reloadForPlayer(inventory);
                if (gun.isReloading()) {
                    ConsoleHelper.writeMessage("Зброя,вже перезаряджається.");
                } else {
                    gun.reloadForPlayer(inventory);
                }
            }
        }, KeyCode.R);//І - інвентар
    }

    @Override
    public void onAdded() {
        super.onAdded();
    }

    @Override
    public Object clone() {
        return instance;//Він сінгелтон, він може існувати тільки один
    }

    @Override
    public Entity getNewEntity() {
        return getEntity();
    }


    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }
}
