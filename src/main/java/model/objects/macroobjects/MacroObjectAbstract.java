package model.objects.macroobjects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import model.TriggerComponent;
import model.objects.microobjects.Creature;

import java.util.ArrayList;

import static utilies.ConsoleHelper.font;

public abstract class MacroObjectAbstract extends Component {
    private final int x;
    private final int y;
    private final Label sizeOfCreatures=new Label();

    private Texture texture;

    private final PhysicsComponent physicsComponent= new PhysicsComponent();

    public MacroObjectAbstract(int x, int y) {
        this.x = x;
        this.y = y;
        loadTexture();
    }

    private final ArrayList<Creature> creatures=new ArrayList<>();

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    public abstract boolean loadCreatures();
    public abstract void loadTexture();

    public void addCreature(Creature creature) {
        if (!creatures.isEmpty()){
            FXGL.getGameScene().removeUINode(sizeOfCreatures);
        }
        creatures.add(creature);
        sizeOfCreatures.setText(String.valueOf(creatures.size()));
        FXGL.getGameScene().addUINode(sizeOfCreatures);
    }

    public void pullCreature(int index) {
        Creature creature = creatures.get(index);
        creatures.remove(index);
        FXGL.getGameScene().removeUINode(sizeOfCreatures);
        sizeOfCreatures.setText(String.valueOf(creatures.size()));
        if (!creatures.isEmpty())  FXGL.getGameScene().addUINode(sizeOfCreatures);
        FXGL.getGameWorld().addEntity(creature.getNewEntity());
        creature.getPhysics().overwritePosition(new Point2D(x+64, y-89));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void onAdded() {
        super.onAdded();
        sizeOfCreatures.setTextFill(Color.LIGHTGREEN);
        sizeOfCreatures.setFont(font);
        sizeOfCreatures.setLayoutY(y);
        sizeOfCreatures.setLayoutX(x);
        physicsComponent.setBodyType(BodyType.STATIC);
        entity.addComponent(new CollidableComponent(true));
        entity.addComponent(physicsComponent);
        entity.addComponent(new TriggerComponent(50));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public boolean isEmpty() {
        return creatures.isEmpty();
    }
}
