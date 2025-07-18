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
import model.items.firearms.ammos.Ammo762mm;
import model.items.firearms.rifles.AKM;
import model.items.inventory.Inventory;
import model.objects.EntityType;
import model.objects.microobjects.behaviour.Task;
import utilies.ConsoleHelper;
import utilies.ImageLoader;
import utilies.RandomUtil;

import java.io.FileReader;
import java.io.FileWriter;

public class Recruit extends MicroObjectAbstract {//рекрут

    private int replicaCount = RandomUtil.getRandomInt(0,2);//я хотів, щоб репліка виводилась випадково
    private final int id;
    private static int idCounter = 0;

    public Recruit(String creatureName, int health, double armor, Inventory inventory, double experiencePoint, int x, int y,int speed) {//Справжній конструктор
        super(creatureName, health, armor, inventory, experiencePoint, x, y,speed, EntityType.MICROOBJECT);
        id=idCounter++;
    }

    public Recruit() {
        this("Recruit",100,5,Inventory.getInventory(50,new AKM()), RandomUtil.getRandomExperiencePoint(100),0,0,175);//Вимога задачі
        inventory.addItems(new Ammo762mm(),150);
    }

    public Recruit(int x, int y) {
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
        microObjectAbstract.getDamage(15);//просто виклик отримання шкоди в об'єкта параметра
    }

    @Override
    public void getDamage(int damage) {//Отримати шкоди, детальніше в Soldier
        int damageValue = (int) Math.floor(damage-damage/(100/getArmor()));
        ConsoleHelper.writeMessage("Recruit's damaged"+this.getCreatureName()+": "+damageValue+"Current health:"+this.getHealth());
        if(damageValue>0){
            setHealth(getHealth()-damageValue);
        }
    }


    public void getDamage(int damage,MicroObjectAbstract microObjectAbstract) {//Отримати шкоди, детальніше в Soldier
       getDamage(damage);
       addTask(Task.getDefenseTask(microObjectAbstract,Short.MAX_VALUE));
    }


    @Override
    public void talk() {//розмовляти
        switch (replicaCount){
            case 0-> ConsoleHelper.writeMessage("Argh.");
            case 1-> ConsoleHelper.writeMessage("I'll kill you!");
            case 2-> ConsoleHelper.writeMessage("To the glory of Seraphim!");
            case 3-> ConsoleHelper.writeMessage("Calling for support");
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
        animIdleUp= ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Recruit/idle/idleUp.png"),10,200,346,2.75);
        animIdleRight =ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Recruit/idle/idleRight.png"),10,140,355,2.75);
        animIdleDown=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Recruit/idle/idleDown.png"),10,150,370,2.75);
        animIdleLeft=ImageLoader.loadAnimationChannel(ImageLoader.loadSpriteSheet("src/main/resources/assets/textures/Recruit/idle/idleLeft.png"),10,155,370,2.75);

        mainTexture = new AnimatedTexture(animIdleLeft);
        mainTexture.loopAnimationChannel(animIdleLeft);

        mainTexture.setScaleX(0.25);
        mainTexture.setScaleY(0.25);

        mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+30);
        mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+237);
    }

    public void stop() {
        switch (getDirection()) {
            case RIGHT->{
                mainTexture.loopAnimationChannel(animIdleRight);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+33);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+212.5);

            }
            case LEFT->{
                mainTexture.loopAnimationChannel(animIdleLeft);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+30);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+237);
            }
            case DOWN -> {
                mainTexture.loopAnimationChannel(animIdleDown);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+33);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+227.5);
            }
            case UP -> {
                mainTexture.loopAnimationChannel(animIdleUp);
                mainTexture.setTranslateX(-mainTexture.getImage().getHeight() * 0.25+5);
                mainTexture.setTranslateY(-mainTexture.getImage().getWidth() * 0.25+365);}
        }
        stopPhysic();
    }

    @Override
    public void onAdded() {
        super.onAdded();
    }

    @Override
    public Object clone(){
        return new Recruit(getCreatureName(),getHealth(),getArmor(),Inventory.getInventory(getInventoryMax(),new AKM()),getExperiencePoint(),getX(),getY(),speed);
    }

    @Override
    public Entity getNewEntity() {
        MicroObjectAbstract recruit=this;

        Entity recruitE = FXGL.entityBuilder()
                .with(recruit) // додаємо Creature
                .type(EntityType.MICROOBJECT)
                .at(getX(),getY())
                .build();

        recruitE.getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(0, -4), // Зміщення хитбоксу (всередину спрайта)
                BoundingShape.box(31, 85) // Розмір хитбоксу
        ));

        enableLabelPrimitiveView(entity);
        entity=recruitE;
        return recruitE;
    }

    public double getInventoryMax(){
        return this.getInventory().getMaxWeight();
    }

    public void writeToXML(String filePath) throws Exception {
        XStream xstream = new XStream(new DomDriver());
        xstream.registerConverter(new RecruitConverter());
        xstream.alias("Recruit", Recruit.class);
        xstream.toXML(this, new FileWriter(filePath));
    }

    public static Recruit readFromXML(String filePath) throws Exception {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[]{Recruit.class});
        xstream.registerConverter(new RecruitConverter());
        xstream.alias("Recruit", Recruit.class);
        return (Recruit) xstream.fromXML(new FileReader(filePath));
    }

    static class RecruitConverter implements Converter {
        @Override
        public boolean canConvert(Class type) {
            return type.equals(Recruit.class);
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Recruit soldier = (Recruit) source;
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
            Recruit soldier = new Recruit();
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
