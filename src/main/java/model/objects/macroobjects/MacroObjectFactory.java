package model.objects.macroobjects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.objects.EntityType;
import model.objects.microobjects.MicroObjectsFactory;

public class MacroObjectFactory implements EntityFactory {
    @Spawns("Cave")
    public Entity spawnCave(SpawnData data) {
        MacroObjectAbstract cave =new Cave((int) data.getX(),(int) data.getY());

        Entity e = FXGL.entityBuilder()
                .at(data.getX(),data.getY())
                .viewWithBBox(cave.getTexture())
                .collidable()
                .type(EntityType.MACROOBJECT)
                .with(cave)
                .build();
        MicroObjectsFactory.enableLabelPrimitiveView(e,"в)");
        enableCirclePrimitiveView(e,Color.YELLOW);
        MicroObjectsFactory.enableHitboxView(e);

        return e;
    }

    @Spawns("Crypt")
    public Entity spawnCrypt(SpawnData data) {
        MacroObjectAbstract crypt =new Crypt((int) data.getX(),(int) data.getY());

        Entity e = FXGL.entityBuilder(data)
                .with(crypt)
                .viewWithBBox(crypt.getTexture())
                .type(EntityType.MACROOBJECT)
                .build();

        enableCirclePrimitiveView(e,Color.CYAN);
        MicroObjectsFactory.enableHitboxView(e);
        MicroObjectsFactory.enableLabelPrimitiveView(e,"а)");
        return e;
    }

    @Spawns("Dormitory")
    public Entity spawn(SpawnData data) {
        MacroObjectAbstract dormitory =new Dormitory((int) data.getX(),(int) data.getY());

        Entity e = FXGL.entityBuilder(data)
                .with(dormitory)
                .viewWithBBox(dormitory.getTexture())
                .type(EntityType.MACROOBJECT)
                .build();

        enableCirclePrimitiveView(e,Color.CRIMSON);
        MicroObjectsFactory.enableHitboxView(e);
        MicroObjectsFactory.enableLabelPrimitiveView(e,"б)");

        return e;
    }

    public static void enableCirclePrimitiveView(Entity entity, Color circleColor) {
        HitBox hitBox =entity.getBoundingBoxComponent().hitBoxesProperty().getFirst();
        Circle circle = new Circle(hitBox.getMaxX()-8, hitBox.getMinY()+5, 5, circleColor);
        entity.getViewComponent().addChild(circle);
    }
}
