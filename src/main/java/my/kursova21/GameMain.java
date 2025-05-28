package my.kursova21;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.geometry.BoundingBox;
import javafx.util.Duration;
import model.objects.EntityType;
import model.Player;
import model.objects.microobjects.Creature;
import model.objects.microobjects.MicroObjectsFactory;
import model.objects.nanoobjects.NanoObjectsFactory;
import model.objects.nanoobjects.bullets.Bullet;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static model.objects.microobjects.Creature.MAX_HEIGHT;
import static model.objects.microobjects.Creature.MAX_WIDTH;

public class GameMain extends GameApplication {
    private final static String GAME_TITLE = "Game_Main";
    private final static String GAME_VERSION = "0.0.1";
    public final static int WIDTH=Lab4.WIDTH;
    public final static int HEIGHT=Lab4.HEIGHT;

    PhysicsWorld physicsWorld;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle(GAME_TITLE);
        gameSettings.setVersion(GAME_VERSION);
        gameSettings.setWidth(WIDTH);
        gameSettings.setHeight(HEIGHT);
        gameSettings.setAppIcon("");
        gameSettings.setDeveloperMenuEnabled(true);

    }

    @Override
    protected void initGame() {
        createBorders();
        getGameWorld().addEntityFactory(new MicroObjectsFactory()); // Реєструємо фабрику
        getGameWorld().addEntityFactory(new NanoObjectsFactory());
        Creature player = spawn("Player", 200, 200).getComponent(Player.class);
        spawn("Recruit",300, 500);
        spawn("Soldier", 500, 500);
//        spawn("Chest",500,500);
        spawn("BodyOfCrimsonGuardian",100,400);
        spawn("Cultist",800,300);
        FXGL.getGameScene().addUINode(player.getInventory().getInventoryController().getInventoryView());
        initPhysics();
    }

    @Override
    protected void initInput() {
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {


                Bullet bulletComponent = bullet.getComponents().stream()
                        .filter(Bullet.class::isInstance)
                        .map(Bullet.class::cast)
                        .findFirst()
                        .orElse(null);
                Creature enemyComponent = enemy.getComponents().stream()
                        .filter(Creature.class::isInstance)
                        .map(Creature.class::cast)
                        .findFirst()
                        .orElse(null);

                assert enemyComponent != null;
                assert bulletComponent != null;
                enemyComponent.getDamage(bulletComponent.getAttackDamage()); // Наносимо шкоду
                bullet.removeFromWorld(); // Видаляємо кулю
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BULLET, EntityType.PLAYER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {


                Bullet bulletComponent = bullet.getComponents().stream()
                        .filter(Bullet.class::isInstance)
                        .map(Bullet.class::cast)
                        .findFirst()
                        .orElse(null);
                Creature enemyComponent = enemy.getComponents().stream()
                        .filter(Creature.class::isInstance)
                        .map(Creature.class::cast)
                        .findFirst()
                        .orElse(null);

                assert bulletComponent != null;
                assert enemyComponent != null;
                enemyComponent.getDamage(bulletComponent.getAttackDamage()); // Наносимо шкоду
                bullet.removeFromWorld(); // Видаляємо кулю
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                Creature playerComponent = player.getComponents().stream()
                        .filter(Creature.class::isInstance)
                        .map(Creature.class::cast)
                        .findFirst()
                        .orElse(null);
                assert playerComponent != null;
                playerComponent.stop();
            }
        });
    }

    private void createBorders() {
        // Верхня стіна
        FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(0, 0)
                .with(new CollidableComponent(true))
                .bbox(new HitBox("TOP", BoundingShape.box(MAX_WIDTH, 5)))
                .buildAndAttach();


        // Нижня стіна
        FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(0, MAX_HEIGHT)
                .with(new CollidableComponent(true))
                .bbox(new HitBox("BOTTOM", BoundingShape.box(MAX_WIDTH, 5)))
                .buildAndAttach();

        // Ліва стіна
        FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(0, 0)
                .with(new CollidableComponent(true))
                .bbox(new HitBox("LEFT", BoundingShape.box(5, MAX_HEIGHT)))
                .buildAndAttach();

        // Права стіна
        FXGL.entityBuilder()
                .type(EntityType.WALL)
                .at(MAX_WIDTH, 0)
                .with(new CollidableComponent(true))
                .bbox(new HitBox("RIGHT", BoundingShape.box(5, MAX_HEIGHT)))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
