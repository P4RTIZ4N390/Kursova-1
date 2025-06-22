package model.objects.microobjects;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import model.Direction;
import model.items.firearms.ammos.Ammo57mm;
import model.items.firearms.smg.FNP90;
import model.items.inventory.Inventory;
import model.objects.EntityType;
import utilies.ConsoleHelper;
import utilies.ImageLoader;
import utilies.RandomUtil;

import java.io.FileReader;
import java.io.FileWriter;


public class Soldier extends Recruit {

    private int replicaCount = RandomUtil.getRandomInt(0, 2);//я хотів, щоб репліка виводилась випадково

    private final int id;
    private static int idCounter = 0;

    public Soldier() {
        this("Soldier", 150, 45, Inventory.getInventory(50), RandomUtil.getRandomExperiencePoint(100), 0, 0, 180);
    }

    public Soldier(String creatureName, int health, double armor, Inventory inventory, double experiencePoint, int x, int y, int speed) {
        super(creatureName, health, armor, inventory, experiencePoint, x, y, speed);
        getInventory().clear();
        getInventory().addItem(new FNP90());
        setCurrentItem("FN P90");
        getInventory().addItems(new Ammo57mm(), 200);
        id = idCounter++;
    }

    public Soldier(int x, int y) {
        this();
        setX(x);
        setY(y);
    }

    @Override
    public String toString() {
        return this.getCreatureName() + id + "(" + this.getHealth() + ")";
    }

    @Override
    public void getDamage(int damage) {
        int damageValue = (int) Math.floor(damage - damage / (100 / getArmor()));
        if (damageValue > 0) {
            setHealth(getHealth() - damageValue);
        }
    }

    @Override
    public void talk() {
        switch (replicaCount) {
            case 0 -> ConsoleHelper.writeMessage("Argh.");
            case 1 -> ConsoleHelper.writeMessage("Fight honestly Crimson Warrior.!");
            case 2 -> ConsoleHelper.writeMessage("To the glory of Seraphim!");
            case 3 -> ConsoleHelper.writeMessage("Calling for support");
            case 4 -> ConsoleHelper.writeMessage("In these eyes, I see the truth.");
            default -> {
                replicaCount = 0;
                this.talk();
            }
        }
        replicaCount++;
    }

    @Override
    public void print() {

    }

    @Override
    public void loadAnimatedTexture() {
        animIdleLeft = ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Soldier/Idle/idleLeft.png"), 12, 125, 360, 1.95);
        animIdleUp = ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Soldier/Idle/idleUp.png"), 12, 135, 353, 1.95);
        animIdleRight = ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Soldier/Idle/idleRight.png"), 12, 125, 365, 1.95);
        animIdleDown = ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Soldier/Idle/idleDown.png"), 12, 145, 365, 1.95);


        mainTexture = new AnimatedTexture(animIdleRight);
        mainTexture.loopAnimationChannel(animIdleRight);

        mainTexture.setScaleX(0.25);
        mainTexture.setScaleY(0.25);

        mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25 + 45);
        mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25 + 232);
    }

    @Override
    public void stop() {
        switch (getDirection()) {
            case RIGHT -> {
                mainTexture.loopAnimationChannel(animIdleRight);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25 + 45);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25 + 232);

            }
            case LEFT -> {
                mainTexture.loopAnimationChannel(animIdleLeft);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25 + 42);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25 + 231);
            }
            case DOWN -> {
                mainTexture.loopAnimationChannel(animIdleDown);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25 + 36);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25 + 288);
            }
            case UP -> {
                mainTexture.loopAnimationChannel(animIdleUp);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25 + 37);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25 + 265);
            }
        }
        stopPhysic();
    }

    @Override
    public Object clone() {
        return new Soldier(getCreatureName(), getHealth(), getArmor(), Inventory.getInventory(this.getInventoryMax()), getExperiencePoint(), getX(), getY(), speed);
    }

    public void writeToXML(String filePath) throws Exception {
        XStream xstream = new XStream(new DomDriver());
        xstream.registerConverter(new SoldierConverter());
        xstream.alias("Soldier", Soldier.class);
        xstream.toXML(this, new FileWriter(filePath));
    }

    public static Soldier readFromXML(String filePath) throws Exception {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[]{Soldier.class});
        xstream.registerConverter(new SoldierConverter());
        xstream.alias("Soldier", Soldier.class);
        return (Soldier) xstream.fromXML(new FileReader(filePath));
    }

    static class SoldierConverter implements Converter {
        @Override
        public boolean canConvert(Class type) {
            return type.equals(Soldier.class);
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Soldier soldier = (Soldier) source;
            addNode(writer, "creatureName", soldier.getCreatureName());
            addNode(writer, "health", String.valueOf(soldier.getHealth()));
            addNode(writer, "armor", String.valueOf(soldier.getArmor()));
            addNode(writer, "experiencePoint", String.valueOf(soldier.getExperiencePoint()));
            addNode(writer, "x", String.valueOf(soldier.getX()));
            addNode(writer, "y", String.valueOf(soldier.getY()));
            addNode(writer, "active", String.valueOf(soldier.isActive()));
        }

        private void addNode(HierarchicalStreamWriter writer, String name, String value) {
            if (value != null) {
                writer.startNode(name);
                writer.setValue(value);
                writer.endNode();
            }
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            Soldier soldier = new Soldier();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                String value = reader.getValue();
                switch (nodeName) {
                    case "creatureName" -> soldier.setCreatureName(value);
                    case "health" -> soldier.setHealth(Integer.parseInt(value));
                    case "armor" -> soldier.setArmor(Double.parseDouble(value));
                    case "experiencePoint" -> soldier.setExperiencePoint(Double.parseDouble(value));
                    case "x" -> soldier.setX(Integer.parseInt(value));
                    case "y" -> soldier.setY(Integer.parseInt(value));
                    case "active" -> soldier.setActive(Boolean.parseBoolean(value));
                }
                reader.moveUp();
            }
            return soldier;
        }
    }
}
