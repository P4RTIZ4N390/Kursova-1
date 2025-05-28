package model.objects.macroobjects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import model.objects.EntityType;

import javax.naming.spi.ObjectFactory;

import static model.objects.microobjects.MicroObjectsFactory.enableHitboxView;

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

        //enableHitboxView(e);

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

        //enableHitboxView(e);

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
        //enableHitboxView(e);

        return e;
    }
}
