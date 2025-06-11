package model.objects.microobjects;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.objects.EntityType;
import model.Player;
import model.objects.microobjects.behaviour.RecruitAIComponent;

import static utilies.ConsoleHelper.font;
import static utilies.ConsoleHelper.smallFont;

public class MicroObjectsFactory implements EntityFactory {
    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        Player player = Player.getInstance();
        player.setX((int) data.getX());
        player.setY((int) data.getY());

        Entity playerE =FXGL.entityBuilder(data)
                .with(player)
                .type(EntityType.PLAYER)
                .build();
        playerE.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(-6, -4), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(33, 73) // Розмір хитбоксу
        ));

        enableHitboxView(playerE);

        return playerE;
    }

    @Spawns("Cultist")
    public Entity spawnCultist(SpawnData data) {
        MicroObjectAbstract cultist=new Cultist();
        cultist.setX((int) data.getX());
        cultist.setY((int) data.getY());
        Entity cultistE =FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .with(cultist) // додаємо Creature
                .type(EntityType.ENEMY)
                .build();

        cultistE.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(0, 0), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(32, 82) // Розмір хитбоксу
        ));

//        enableHitboxView(cultistE);
        cultist.enableLabelPrimitiveView(cultistE);
        return cultistE;
    }
    @Spawns("Recruit")
    public Entity spawnRecruit(SpawnData data) {
        MicroObjectAbstract recruit=new Recruit();
        recruit.setX((int) data.getX());
        recruit.setY((int) data.getY());

        Entity recruitE =FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .with(recruit) // додаємо Creature
                .type(EntityType.ENEMY)
                .build();

        recruitE.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(0, -5), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(32, 86) // Розмір хитбоксу
        ));
//        enableLabelPrimitiveView(recruitE,"а)");
        recruit.enableLabelPrimitiveView(recruitE);
//        enableHitboxView(recruitE);
        return recruitE;
    }

    @Spawns("Soldier")
    public Entity spawnSoldier(SpawnData data) {
        MicroObjectAbstract soldier=new Soldier();
        soldier.setX((int) data.getX());
        soldier.setY((int) data.getY());

        Entity soldierE=FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .with(soldier)
                .type(EntityType.ENEMY)
                .build();
        soldierE.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(0, -5), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(32, 86) // Розмір хитбоксу
        ));
        soldier.enableLabelPrimitiveView(soldierE);
//        enableLabelPrimitiveView(soldierE,"б)");
//        enableHitboxView(soldierE);
        return soldierE;
    }

    public static void enableHitboxView(Entity entity) {
        for (HitBox box : entity.getBoundingBoxComponent().hitBoxesProperty()) {
            Rectangle rect = new Rectangle(box.getWidth()+3, box.getHeight()+2, Color.color(1, 1, 1, 0.1));
            rect.setStroke(Color.RED);
            rect.setStrokeWidth(1.5);
            rect.setTranslateX(box.getMinX());
            rect.setTranslateY(box.getMinY());

            entity.getViewComponent().addChild(rect);
        }
    }

    public static void enableLabelPrimitiveView(Entity entity,String string) {
        HitBox hitBox =entity.getBoundingBoxComponent().hitBoxesProperty().getFirst();
        Label label = new Label(string);
        label.setFont(smallFont);
        label.setLayoutY(hitBox.getMaxY()+10);
        label.setLayoutX(hitBox.getMaxX()/2);
        entity.getViewComponent().addChild(label);
    }

}
