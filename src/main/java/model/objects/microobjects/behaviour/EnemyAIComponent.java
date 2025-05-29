package model.objects.microobjects.behaviour;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import jdk.jfr.Enabled;
import model.objects.EntityType;
import model.objects.microobjects.MicroObjectAbstract;


@Enabled
public class EnemyAIComponent extends Component {

    private Entity player;
    private MicroObjectAbstract microObjectAbstract;

    @Override
    public void onAdded() {
        try {
            player = FXGL.getGameWorld().getSingleton(EntityType.PLAYER);
        } catch (Exception e) {
            player = null;
        }
        microObjectAbstract = entity.getComponents().stream()
                .filter(MicroObjectAbstract.class::isInstance)
                .map(MicroObjectAbstract.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null || microObjectAbstract == null){
            microObjectAbstract =entity.getComponents().stream()
                    .filter(MicroObjectAbstract.class::isInstance)
                    .map(MicroObjectAbstract.class::cast)
                    .findFirst()
                    .orElse(null);
            return;
        }
        if (microObjectAbstract.isDead()){
            return;
        }

        // Наведення
        Point2D playerPos = player.getPosition();
        Point2D enemyPos = entity.getCenter();

//        double angle = Math.toDegrees(Math.atan2(
//                playerPos.getY() - enemyPos.getY(),
//                playerPos.getX() - enemyPos.getX()
//        ));

        // Стрільба
        microObjectAbstract.fire(playerPos);
    }
}

