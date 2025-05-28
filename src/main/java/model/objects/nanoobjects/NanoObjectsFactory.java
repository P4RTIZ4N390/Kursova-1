package model.objects.nanoobjects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Direction;
import model.objects.nanoobjects.chests.BodyOfCrimsonGuardian;

public class NanoObjectsFactory  implements EntityFactory {

//    @Spawns("Chest")
//    public Entity spawnChest(SpawnData data) {
//        Chest chest = new Chest((int) data.getX(),(int) data.getY(),"Chest");
//        return FXGL.entityBuilder(data)
//                .with(chest)
//                .with(new CollidableComponent())
//                .viewWithBBox(chest.getMainTexture())
//                .build();
//    }

    @Spawns("BodyOfCrimsonGuardian")
    public Entity spawnBodyOfCrimsonGuardian(SpawnData data) {
        BodyOfCrimsonGuardian bodyOfCrimsonGuardian=new BodyOfCrimsonGuardian("id540",(int) data.getX(),(int) data.getY(), Direction.DOWN);

        Entity bodyOfCrimsonGuardianE=FXGL.entityBuilder(data)
                .with(bodyOfCrimsonGuardian)
                .with(new CollidableComponent())
                .view(bodyOfCrimsonGuardian.getTexture())
                .build();

        enableHitboxView(bodyOfCrimsonGuardianE);

        return bodyOfCrimsonGuardianE;
    }

    public static void enableHitboxView(Entity entity) {
        for (HitBox box : entity.getBoundingBoxComponent().hitBoxesProperty()) {
            Rectangle rect = new Rectangle(box.getWidth(), box.getHeight(), Color.color(1, 0, 0, 0.3));
            rect.setStroke(Color.RED);
            rect.setStrokeWidth(1.5);
            rect.setTranslateX(box.getMinX());
            rect.setTranslateY(box.getMinY());

            entity.getViewComponent().addChild(rect);
        }
    }
}
