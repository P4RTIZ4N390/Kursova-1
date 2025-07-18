package model.objects.nanoobjects.bullets;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import model.objects.microobjects.MicroObjectAbstract;

public abstract class  Bullet extends Component {
    private final Point2D velocity;
    private final int attackDamage;

    private final MicroObjectAbstract shooter;

    public Bullet(Point2D direction, int additionalDamage, int trueDamage,double bulletSpeed,MicroObjectAbstract shooter ) {
        this.velocity = direction.normalize().multiply(bulletSpeed*3);
        this.attackDamage= additionalDamage+trueDamage;
        this.shooter=shooter;
    }

    @Override
    public void onAdded() {
        entity.addComponent(new CollidableComponent(true));// Додаємо компонент колізії
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translate(velocity.multiply(tpf));
        // Якщо куля виходить за межі екрана, видаляємо її
        if (!(FXGL.getAppHeight()>entity.getY())||!(FXGL.getAppWidth()>entity.getX()) ) {
            entity.removeFromWorld();
        }
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public MicroObjectAbstract getShooter() {
        return shooter;
    }

    public void destroy() {
        entity.removeFromWorld();
    }

    public abstract Texture getBulletTexture();
}
