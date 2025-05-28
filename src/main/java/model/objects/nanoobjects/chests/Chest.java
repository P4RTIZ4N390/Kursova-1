package model.objects.nanoobjects.chests;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import model.Direction;
import model.TriggerComponent;
import model.items.inventory.Inventory;
import model.objects.EntityType;

public abstract class Chest extends Component {
    protected int x;
    protected int y;
    private final Inventory inventory;
    private final String name;

    protected EntityType type;
    protected AnimatedTexture texture;
    private final Direction direction;
    protected AnimationChannel rightView,leftView,downView,upView;

    public Chest(String name, int x, int y,int maxWeight,Direction direction) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.inventory=Inventory.getInventory(maxWeight);
        type=EntityType.CHEST;
        this.direction=direction;
        loadTextures();
        loadInventory();
    }

    protected abstract void loadTextures();
    protected abstract void loadInventory();

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public Direction getDirection() {
        return direction;
    }

    public AnimatedTexture getTexture() {
        return texture;
    }

    @Override
    public void onAdded() {
        super.onAdded();
        entity.addComponent(new TriggerComponent(50));
    }
}
