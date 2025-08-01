package model.objects.microobjects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.geometry.Point2D;
import model.items.firearms.ammos.Ammo57mm;
import model.items.firearms.smg.FNP90;
import model.items.inventory.Inventory;
import model.objects.EntityType;
import utilies.ConsoleHelper;
import utilies.ImageLoader;
import utilies.RandomUtil;

import java.io.FileReader;
import java.io.FileWriter;


public class Cultist extends Soldier {//Окультист

    private int replicaCount=RandomUtil.getRandomInt(0,2);//я хотів, щоб репліка виводилась випадково

    private final int id;
    private static int idCounter = 0;

    private int mana;

    public Cultist(String creatureName, int health, double armor, Inventory inventory, double experiencePoint, int x, int y,int speed,int mana) {//Справжній конструктор
        super(creatureName, health, armor, inventory, experiencePoint, x, y, speed);
        getInventory().clear();
        getInventory().addItem(new FNP90());
        setCurrentItem("FN P90");
        getInventory().addItems(new Ammo57mm(),200);
        id = idCounter++;
        this.mana = mana;
    }

    public Cultist() {
        this("Cultist",200,55,Inventory.getInventory(100,new FNP90()), RandomUtil.getRandomExperiencePoint(150),0,0,200,1500);//Вимога задачі
    }

    public Cultist(int x, int y) {
        this();
        setX(x);
        setY(y);
    }

    @Override
    public String toString() {
        return this.getCreatureName()+id+"("+this.getHealth()+")";
    }

    @Override
    public void takeDamage(MicroObjectAbstract microObjectAbstract) {//Завдати шкоди
        microObjectAbstract.getDamage(40);//Просто виклик отримання шкоди в об'єкта параметра
    }

    @Override
    public void getDamage(int damage) {//Отримати шкоди, детальніше в StrangeRat
        int damageValue = (int) Math.floor(damage-damage/(100/getArmor()));
        if(damageValue>0){
            setHealth(getHealth()-damageValue);
        }
    }

    @Override
    public void talk() {//розмовляти
        switch (replicaCount){
            case 0-> ConsoleHelper.writeMessage("I'm "+this.getCreatureName()+"."+Thread.currentThread().getName());
            case 1-> ConsoleHelper.writeMessage("Here sleeps his saint");
            case 2-> ConsoleHelper.writeMessage("To the glory of Seraphim!");
            case 3-> ConsoleHelper.writeMessage("Get out of here, pagan!");
            default -> {
                replicaCount =0;
                this.talk();
            }
        }
        replicaCount++;
    }

    @Override
    public void print() {//виведення даних про істоту
        ConsoleHelper.writeMessage(String.format("%s,HP:%d,Armor:%.1f;Xp:%.1f;x:%d,y:%d",getCreatureName(),getHealth(),getArmor(),getExperiencePoint(),getX(),getY()));
        this.getInventory().showInventory();
    }

    @Override
    public void loadAnimatedTexture() {
        animIdleRight =ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Cultist/idle/idleRight.png"),12,145,340,2.75);
        animIdleDown=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Cultist/idle/idleDown.png"),12,150,360,2.75);
        animIdleUp=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Cultist/idle/idleUp.png"),12,150,340,2.75);
        animIdleLeft=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Cultist/idle/idleLeft.png"),12,150,350,2.75);

        mainTexture = new AnimatedTexture(animIdleRight);
        mainTexture.loopAnimationChannel(animIdleRight);

        mainTexture.setScaleX(0.25);
        mainTexture.setScaleY(0.25);

        mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+30);
        mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+305);
    }

    @Override
    public void stop() {
        switch (getDirection()) {
            case RIGHT->{
                mainTexture.loopAnimationChannel(animIdleRight);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+30);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+305);

            }
            case LEFT->{
                mainTexture.loopAnimationChannel(animIdleLeft);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+30);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+315);
            }
            case DOWN -> {
                mainTexture.loopAnimationChannel(animIdleDown);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+30);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+308);
            }
            case UP -> {
                mainTexture.loopAnimationChannel(animIdleUp);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+27);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+320);}
        }
        stopPhysic();
    }



    @Override
    public Object clone() {
        return new Cultist(getCreatureName(),getHealth(),getArmor(),Inventory.getInventory(this.getInventoryMax()),getExperiencePoint(),getX(),getY(),speed,mana);
    }

    @Override
    public Entity getNewEntity() {
        MicroObjectAbstract cultist=this;
        Entity cultistE = FXGL.entityBuilder()
                .with(cultist)
                .type(EntityType.MICROOBJECT)
                .at(getX(),getY())
                .build();


        cultistE.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(0, 0), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(32, 82) // Розмір хитбоксу
        ));

        entity = cultistE;

        enableLabelPrimitiveView(entity);

        return cultistE;
    }

    public void writeToXML(String filePath) throws Exception {
        XStream xstream = new XStream(new DomDriver());
        xstream.registerConverter(new CultistConverter());
        xstream.alias("Cultist", Cultist.class);
        xstream.toXML(this, new FileWriter(filePath));
    }

    public static Cultist readFromXML(String filePath) throws Exception {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[]{Cultist.class});
        xstream.registerConverter(new Cultist.CultistConverter());
        xstream.alias("Cultist", Cultist.class);
        return (Cultist) xstream.fromXML(new FileReader(filePath));
    }

    static class CultistConverter implements Converter {
        @Override
        public boolean canConvert(Class type) {
            return type.equals(Recruit.class);
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Cultist soldier = (Cultist) source;
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
            Cultist soldier = new Cultist();
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
