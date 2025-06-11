package model.items.firearms;

import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import model.Direction;
import model.Player;
import model.items.inventory.Inventory;
import model.objects.microobjects.MicroObjectAbstract;

public interface Firearm {
    String getCaliber();
    void reload(Inventory inventory);
    void reloadForPlayer(Inventory inventory);
    void fire(MicroObjectAbstract shooter, Point2D target, Inventory inventory, Direction direction);
    void fireForPlayer(double shooterX, double shooterY, Point2D cursorLocation, Inventory inventory, Direction direction);
    boolean IsAutomatic();
    double getFireRate();
    double getReloadTime();
    double getSpread_Amount();
    AnimationChannel getGunTexture(Direction direction);
}
