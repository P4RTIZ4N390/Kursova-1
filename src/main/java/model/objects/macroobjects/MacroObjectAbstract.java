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
import model.objects.microobjects.MicroObjectAbstract;
import utilies.ConsoleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static my.kursova21.Lab4.HEIGHT;
import static my.kursova21.Lab4.WIDTH;
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
        loadCreatures();
    }

    private final List<MicroObjectAbstract> microObjectAbstracts =new CopyOnWriteArrayList<>();

    public List<MicroObjectAbstract> getCreatures() {
        return microObjectAbstracts;
    }

    public abstract boolean loadCreatures();
    public abstract void loadTexture();

    public void addCreature(MicroObjectAbstract microObjectAbstract) {
        if (!microObjectAbstracts.isEmpty()){
            FXGL.getGameScene().removeUINode(sizeOfCreatures);
        }
        microObjectAbstracts.add(microObjectAbstract);
        sizeOfCreatures.setText(String.valueOf(microObjectAbstracts.size()));
        microObjectAbstract.setMacroObjectAbstract(this);
        FXGL.getGameScene().addUINode(sizeOfCreatures);
    }

    public void pullCreature(int index) {
        MicroObjectAbstract microObjectAbstract = microObjectAbstracts.get(index);
        FXGL.getGameScene().removeUINode(sizeOfCreatures);
        sizeOfCreatures.setText(String.valueOf(microObjectAbstracts.size()));
        if (!microObjectAbstracts.isEmpty())  FXGL.getGameScene().addUINode(sizeOfCreatures);
        FXGL.getGameWorld().addEntity(microObjectAbstract.getNewEntity());
        microObjectAbstract.getPhysics().overwritePosition(new Point2D(x+64, y-89));
        microObjectAbstracts.remove(index);
    }

    public void pullCreature(MicroObjectAbstract microObjectAbstract) {
        int index = microObjectAbstracts.indexOf(microObjectAbstract);
        if (index == -1) {
            ConsoleHelper.writeMessageInLabelInRightCorner("МікроОб'єкт зараз за межами макроОб'єкта.",7,WIDTH,HEIGHT);
            return;
        }
        pullCreature(index);
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
    public void onUpdate(double tpf) {
        microObjectAbstracts.forEach(microObject -> microObject.onUpdate(tpf));
        super.onUpdate(tpf);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public boolean isEmpty() {
        return microObjectAbstracts.isEmpty();
    }

    public Point2D getPosition() {
        return new Point2D(x, y);
    }
}
