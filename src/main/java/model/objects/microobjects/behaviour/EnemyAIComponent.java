package model.objects.microobjects.behaviour;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import jdk.jfr.Enabled;
import model.objects.EntityType;
import model.objects.microobjects.Creature;


@Enabled
public class EnemyAIComponent extends Component {

    private Entity player;
    private Creature creature;

    @Override
    public void onAdded() {
        player = FXGL.getGameWorld().getSingleton(EntityType.PLAYER);
        creature = entity.getComponents().stream()
                .filter(Creature.class::isInstance)
                .map(Creature.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null || creature == null){
            creature=entity.getComponents().stream()
                    .filter(Creature.class::isInstance)
                    .map(Creature.class::cast)
                    .findFirst()
                    .orElse(null);
            return;
        }

        // Наведення
        Point2D playerPos = player.getPosition();
        Point2D enemyPos = entity.getCenter();

        double angle = Math.toDegrees(Math.atan2(
                playerPos.getY() - enemyPos.getY(),
                playerPos.getX() - enemyPos.getX()
        ));

        entity.setRotation(angle);

        // Стрільба
        creature.fire(playerPos);
    }
}

