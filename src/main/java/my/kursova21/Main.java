package my.kursova21;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import model.items.inventory.Inventory;
import model.objects.EntityType;
import model.objects.macroobjects.Cave;
import model.objects.macroobjects.Crypt;
import model.objects.microobjects.*;
import org.jetbrains.annotations.NotNull;
import utilies.ConsoleHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    protected static Cave cave=new Cave(1,1);
    protected static Crypt crypt=new Crypt(1,1);

    static Soldier soldier = new Soldier();
    protected Recruit recruit =new Recruit();

    private final static ArrayList<Creature> creatures = new ArrayList<>();

    public static void main(String[] args) {

        Main main = new Main();
        Cultist p=new Cultist();
        System.out.println("Початок роботи.");
        int command=0;
        creatures.add(soldier);
        creatures.add(p);
        creatures.add(new Soldier());
        creatures.add(main.recruit);
//        creatures.add(new Chest("chest"));
//        creatures.add(new Chest("Guardian"));
        cave.loadCreatures();
        crypt.loadCreatures();
        ConsoleHelper.writeSeparator();
        while (true) {
            switch (command){
                case 1->main.printObject1();
                case 2->main.changeObject1();
                case 3->main.printObject2();
                case 4->main.changeObject2();
                case 5-> p.print();
                case 6-> main.changeObject(p);
                case 7->{
                    ConsoleHelper.writeMessage("Оберіть об'єкт для взаємодій");
                    int object1=ConsoleHelper.readInt();
                    ConsoleHelper.writeMessage("Оберіть другий об'єкт для взаємодій");
                    int object2=ConsoleHelper.readInt();
                    Creature creature1=switch (object1){
                        case 1-> soldier;
                        case 2-> main.recruit;
                        case 3->p;
                        default -> null;
                    };
                    Creature creature2=switch (object2){
                        case 1-> soldier;
                        case 2-> main.recruit;
                        case 3->p;
                        default -> null;
                    };
                    if (creature1==null||creature2==null||creature1.equals(creature2)) {
                        ConsoleHelper.writeMessage("Це ті самі створіння,або якісь дані введено не коректно");
                        return;
                    }creature1.takeDamage(creature2);
                }
                case 8->main.printObjects(creatures);
                case 9->main.printCreatureFromArray();
                case 10->main.sortCreatures();
                case 11->main.copyCreatureToArray();
                case 12->main.binarySearchCreatures();
                case 13->main.commandTempObject48();
                case 14->main.commandDivideByParameter();
                case 15->main.addToArrays();
                case 16->main.printArrays();
                case 17 ->main.interactionOfTwoCreatures();
                case 18->main.interactionOfTwoMacrobjects();
                case 19->main.countingMicroobjects();
                case 20->main.removeFromArrays();
            }
            if (command==21){
                ConsoleHelper.writeMessage("Кінець роботи.");
                return;}
            printMenu();
            command=ConsoleHelper.readInt();
        }

    }

    public static void printMenu(){
        ConsoleHelper.writeMessage("""
                1 - вивести на екран Об'єкт 1;
                2 - змінити параметри Об'єкта 1;
                3 - вивести на екран Об'єкт 2;
                4 - змінити параметри Об'єкта 2;
                5 - вивести на екран Об'єкт 3;
                6 - змінити параметри Об'єкта 3;
                7 - обрати пару з трьох створених об’єктів і здійснити їх взаємодію;
                8 - вивести масив об'єктів
                9 - вивести i-й об’єкт поточного масиву
                10 - сортувати масив
                11 - копіювати i-й об’єкт поточного масиву
                12 - пошук об’єкта у поточному масиві з допомогою функції Arrays.binarySearch
                13 - атакувати тимчасовим створінням масив об'єктів
                14 - видалити певний тип створінь
                15 - додати до поточного масиву,чи одного з двох макрооб'єкта
                16 - вивести усі масиви
                17 - Взаємодія двох мікрообєктів
                18 - Взаємодія двох макрообєктів
                19 - порахувати істот за певним параметром
                20 - видалити з масиву створіння
                21 - завершення програми"""
                );
        ConsoleHelper.writeSeparator();
    }

    public void printObject1(){
        soldier.print();
    }

    public void changeObject1(){
        changeObject(soldier);
    }

    public  void printObject2(){
        recruit.print();
    }

    public void changeObject2(){
        changeObject(recruit);
    }

    public void changeObject(Creature object){
        ConsoleHelper.writeMessage("Введіть ім'я");
        object.setCreatureName(ConsoleHelper.readString());
        ConsoleHelper.writeMessage("Введіть ціле число здоров'я");
        object.setHealth(ConsoleHelper.readInt());
        ConsoleHelper.writeMessage("Введіть число броні");
        object.setArmor(ConsoleHelper.readDouble());
        ConsoleHelper.writeMessage("введіть число, максимальну вагу інвентарю");
        object.getInventory().setMaxWeight(ConsoleHelper.readDouble());
        ConsoleHelper.writeMessage("Введіть скільки досвіду випаде зі обєкта");
        object.setExperiencePoint(ConsoleHelper.readDouble());
        ConsoleHelper.writeMessage("Введіть координату х");
        object.setX(ConsoleHelper.readInt());
        ConsoleHelper.writeMessage("Введіть координату y");
        object.setY(ConsoleHelper.readInt());
        ConsoleHelper.writeSeparator();
    }

    public void printObjects(ArrayList<Creature> creatures){
        ConsoleHelper.writeMessage("Вміст масиву: ");
        creatures.forEach(c->ConsoleHelper.writeMessage(c.toString()));
        ConsoleHelper.writeSeparator();
    }

    public void printCreatureFromArray(){
        ConsoleHelper.writeMessage("Введіть номер обєкта");
        int index=ConsoleHelper.readInt();
        if (index>=creatures.size()||index<0){
            if (index>=creatures.size())
                ConsoleHelper.writeMessage("Некоректне число,воно більше за розміри масиву.");
            else ConsoleHelper.writeMessage("Некоректне число,воно від'ємне.");
            ConsoleHelper.writeSeparator();
            return;
        }
        ConsoleHelper.writeMessage(creatures.get(index).toString());
        ConsoleHelper.writeSeparator();
    }

    public Creature getCreatureFromArray(){
        ConsoleHelper.writeMessage("Введіть номер обєкта");
        int index=ConsoleHelper.readInt();
        if (index>=creatures.size()||index<0){
            if (index>=creatures.size())
                ConsoleHelper.writeMessage("Некоректне число,воно більше за розміри масиву.");
            else ConsoleHelper.writeMessage("Некоректне число,воно від'ємне.");
            ConsoleHelper.writeSeparator();
            return  null;
        }
        return creatures.get(index);
    }

    public void copyCreatureToArray(){
        Creature object1;
        try {
            object1 = (Creature) getCreatureFromArray().clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        object1.setCreatureName(object1.getCreatureName()+"-копія");
        creatures.add(object1);

    }

    public void sortCreatures(){
        creatures.sort(Creature::compareTo);
        ConsoleHelper.writeMessage("Масив відсортовано.");
    }

    public void binarySearchCreatures() {
        ConsoleHelper.writeMessage("Введіть параметр пошуку \n" +
                """
                0 - Ім'я
                1 - Здоров’я
                2 - Броня
                """);
        int parameter = ConsoleHelper.readInt();

        switch (parameter) {
            case 0 -> {
                ConsoleHelper.writeMessage("Уведіть ім'я для пошуку:");
                String name = ConsoleHelper.readString();

                // Сортуємо
                creatures.sort(Comparator.comparing(Creature::getCreatureName));

                // Створюємо тимчасовий об'єкт
                Creature temp = getTempCreature(name);

                // Пошук
                int result = Arrays.binarySearch(
                        creatures.toArray(new Creature[0]),
                        temp,
                        Comparator.comparing(Creature::getCreatureName)
                );

                if (result >= 0) {
                    ConsoleHelper.writeMessage("Знайдено на позиції: " + result);

                    // Факультативне: знайти всі збіги
                    int left = result;
                    while (left - 1 >= 0 && creatures.get(left - 1).getCreatureName().equals(name)) left--;

                    int right = result;
                    while (right + 1 < creatures.size() && creatures.get(right + 1).getCreatureName().equals(name)) right++;

                    ConsoleHelper.writeMessage("Усі збіги на позиціях:");
                    for (int i = left; i <= right; i++) {
                        ConsoleHelper.writeMessage("Індекс " + i + ": " + creatures.get(i));
                    }

                } else {
                    ConsoleHelper.writeMessage("Об’єкт не знайдено.");
                }
            }

            case 1 -> {
                ConsoleHelper.writeMessage("Уведіть значення здоров’я:");
                int health = ConsoleHelper.readInt();

                creatures.sort(Comparator.comparingInt(Creature::getHealth));

                Creature temp = getTempCreatureWithHealth(health);

                int result = Arrays.binarySearch(
                        creatures.toArray(new Creature[0]),
                        temp,
                        Comparator.comparingInt(Creature::getHealth)
                );

                if (result >= 0) {
                    ConsoleHelper.writeMessage("Знайдено на позиції: " + result);

                    int left = result;
                    while (left - 1 >= 0 && creatures.get(left - 1).getHealth() == health) left--;

                    int right = result;
                    while (right + 1 < creatures.size() && creatures.get(right + 1).getHealth() == health) right++;

                    ConsoleHelper.writeMessage("Усі збіги:");
                    for (int i = left; i <= right; i++) {
                        ConsoleHelper.writeMessage("Індекс " + i + ": " + creatures.get(i));
                    }
                } else {
                    ConsoleHelper.writeMessage("Об’єкт не знайдено.");
                }
            }

            case 2 -> {
                ConsoleHelper.writeMessage("Уведіть значення броні:");
                double armor = ConsoleHelper.readDouble();

                creatures.sort(Comparator.comparingDouble(Creature::getArmor));

                Creature temp = getTempCreatureWithArmor(armor);

                int result = Arrays.binarySearch(
                        creatures.toArray(new Creature[0]),
                        temp,
                        Comparator.comparingDouble(Creature::getArmor)
                );

                if (result >= 0) {
                    ConsoleHelper.writeMessage("Знайдено на позиції: " + result);

                    int left = result;
                    while (left - 1 >= 0 && creatures.get(left - 1).getArmor() == armor) left--;

                    int right = result;
                    while (right + 1 < creatures.size() && creatures.get(right + 1).getArmor() == armor) right++;

                    ConsoleHelper.writeMessage("Усі збіги:");
                    for (int i = left; i <= right; i++) {
                        ConsoleHelper.writeMessage("Індекс " + i + ": " + creatures.get(i));
                    }
                } else {
                    ConsoleHelper.writeMessage("Об’єкт не знайдено.");
                }
            }
        }
    }

    @NotNull
    private static Creature getTempCreatureWithArmor(double armor) {
        Creature temp = new Creature() {
            @Override public String toString() { return "Temp"; }
            @Override public void takeDamage(Creature creature) {}
            @Override public void getDamage(int damage) {}
            @Override public void talk() {}
            @Override public void print() {}
            @Override public void loadAnimatedTexture() {}

            @Override
            public void stop() {

            }

            @Override public Object clone() { return null; }

            @Override
            public Entity getNewEntity() {
                return null;
            }

        };
        temp.setArmor(armor);
        return temp;
    }

    @NotNull
    private static Creature getTempCreature(String name) {
        Creature temp = new Creature() {
            @Override
            public String toString() { return "Temp"; }
            @Override
            public void takeDamage(Creature creature) {}
            @Override
            public void getDamage(int damage) {}
            @Override
            public void talk() {}
            @Override
            public void print() {}
            @Override
            public void loadAnimatedTexture() {}

            @Override
            public void stop() {

            }

            @Override
            public Object clone() { return null; }

            @Override
            public Entity getNewEntity() {
                return null;
            }
        };
        temp.setCreatureName(name);
        return temp;
    }

    @NotNull
    private static Creature getTempCreatureWithHealth(int health) {
        Creature temp = new Creature() {
            @Override public String toString() { return "Temp"; }
            @Override public void takeDamage(Creature creature) {}
            @Override public void getDamage(int damage) {}
            @Override public void talk() {}
            @Override public void print() {}
            @Override public void loadAnimatedTexture() {}

            @Override
            public void stop() {

            }

            @Override public Object clone() { return null; }

            @Override
            public Entity getNewEntity() {
                return null;
            }
        };
        temp.setHealth(health);
        return temp;
    }

    public void commandTempObject48(){
        ConsoleHelper.writeMessage("Уведіть скільки шкоди завдати:");
        int damage = ConsoleHelper.readInt();

        Creature temp = new Creature() {
            @Override public String toString() { return "Temp"; }
            @Override public void takeDamage(Creature creature) {
                creature.getDamage(damage);
                ConsoleHelper.writeMessage("Завдано шкоди "+creature);
                ConsoleHelper.writeSeparator();
            }
            @Override public void getDamage(int damage) {}
            @Override public void talk() {}
            @Override public void print() {}
            @Override public void loadAnimatedTexture() {}

            @Override
            public void stop() {

            }

            @Override public Object clone() { return null; }

            @Override
            public Entity getNewEntity() {
                return null;
            }
        };

        creatures.forEach(temp::takeDamage);
    }

    public void commandDivideByParameter(){
        ConsoleHelper.writeMessage("Який тип створінь видалити");
        ConsoleHelper.writeMessage("""
                1 - Ворог
                2 - Скриня
                """);
        EntityType entityType;
        switch (ConsoleHelper.readInt()){
            case 1 -> entityType=EntityType.ENEMY;
            case 2 -> entityType=EntityType.CHEST;
            default -> {
                ConsoleHelper.writeMessage("Ви щось не то ввели.");
                return;
            }
        }
        creatures.removeIf(creature -> creature.getType() == entityType);
    }

    public void addToCave(){
        Creature creature = getNewCreature();
        if (creature==null) return;
        cave.getCreatures().add(creature);
    }

    public void addToCrypt(){
        Creature creature = getNewCreature();
        if (creature==null) return;
        crypt.getCreatures().add(creature);
    }

    public void addToCurrentArray(){
        Creature creature = getNewCreature();
        if (creature==null) return;
        creatures.add(creature);
    }

    public Creature getNewCreature(){
        ConsoleHelper.writeMessage("Введіть ,яке нове створіння ви хочете створити:");
        ConsoleHelper.writeMessage("""
                1 - Рекрут
                2 - Солдат
                3 - Окультист
                """);
        int choice = ConsoleHelper.readInt();
        ConsoleHelper.writeMessage("Введіть ім'я");
        String string=ConsoleHelper.readString();
        ConsoleHelper.writeMessage("Введіть ціле число здоров'я");
        int health=ConsoleHelper.readInt();
        ConsoleHelper.writeMessage("Введіть число броні");
        double armor=ConsoleHelper.readDouble();
        ConsoleHelper.writeMessage("введіть число, максимальну вагу інвентарю");
        double weight=ConsoleHelper.readDouble();
        ConsoleHelper.writeMessage("Введіть скільки досвіду випаде зі обєкта");
        double exp=ConsoleHelper.readDouble();
        ConsoleHelper.writeMessage("Введіть координату х");
        int x=(ConsoleHelper.readInt());
        ConsoleHelper.writeMessage("Введіть координату y");
        int y=ConsoleHelper.readInt();
        ConsoleHelper.writeMessage("Введіть швидкість:");
        int speed=ConsoleHelper.readInt();
        ConsoleHelper.writeSeparator();
        switch (choice){
            case 1 ->{return new Recruit(string,health,armor, Inventory.getInventory(weight),exp,x,y,speed);}
            case 2 ->{return new Soldier(string,health,armor, Inventory.getInventory(weight),exp,x,y,speed);}
            case 3 ->{return new Cultist(string,health,armor, Inventory.getInventory(weight),exp,x,y,speed);}
            default -> ConsoleHelper.writeMessage("Ви ввели ,щось не то ,буде повернуто null");
        }
        return null;
    }

    public void addToArrays(){
        ConsoleHelper.writeMessage("""
                Виберіть до якого масиву додати:
                1 - Поточний масив
                2 - До масиву макрооб'єкта Печера
                3 - До масиву макрооб'єкта Склеп
                """);
        int choice = ConsoleHelper.readInt();
        switch (choice){
            case 1 -> addToCurrentArray();
            case 2 -> addToCave();
            case 3 -> addToCrypt();
            default -> ConsoleHelper.writeMessage("Ви вибрали щось не то");
        }
    }

    public void printArrays(){
        ConsoleHelper.writeMessage("Поточний масив:");
        creatures.forEach(c->ConsoleHelper.writeMessage(c.toString()));
        ConsoleHelper.writeSeparator();
        ConsoleHelper.writeMessage("Печери:");
        cave.getCreatures().forEach(c->ConsoleHelper.writeMessage(c.toString()));
        ConsoleHelper.writeSeparator();
        ConsoleHelper.writeMessage("Склеп:");
        crypt.getCreatures().forEach(c->ConsoleHelper.writeMessage(c.toString()));
        ConsoleHelper.writeSeparator();
    }

    public Creature getCreatureFromArrays(){
        ConsoleHelper.writeMessage("""
                Виберіть з якого масиву взяти:
                1 - Поточний масив
                2 - З масиву макрооб'єкта Печера
                3 - З масиву макрооб'єкта Склеп
                """);
        ArrayList<Creature> creatures=new ArrayList<>();
        int choice = ConsoleHelper.readInt();
        switch (choice){
            case 1 ->creatures=Main.creatures;
            case 2 ->creatures=cave.getCreatures();
            case 3 ->creatures=crypt.getCreatures();
        }
        ConsoleHelper.writeSeparator();
        creatures.forEach(c->ConsoleHelper.writeMessage(c.toString()));
        ConsoleHelper.writeSeparator();
        ConsoleHelper.writeMessage("Введіть позицію створіння ,яке отримати:");
        try {
            return creatures.get(ConsoleHelper.readInt());
        }catch (IndexOutOfBoundsException e){
            ConsoleHelper.writeMessage("Ви ввели число більше за розміри масиву або менше 0 ,тому буде повернуто null.");
        }
        return null;
    }

    public void interactionOfTwoCreatures(){
        Creature creature1 = getCreatureFromArrays();
        Creature creature2 = getCreatureFromArrays();
        int health1=creature1.getHealth();
        int health2=creature2.getHealth();
        creature1.takeDamage(creature2);
        creature2.takeDamage(creature1);
        ConsoleHelper.writeMessage("Здоровя перед завданням шкоди першого : "+health1+",і після "+creature1.getHealth());
        ConsoleHelper.writeMessage("Здоровя перед завданням шкоди першого : "+health2+",і після "+creature2.getHealth());
        ConsoleHelper.writeSeparator();
    }

    public void interactionOfTwoMacrobjects(){
        ConsoleHelper.writeMessage("""
                Виберіть з якого масиву взяти створінь:
                1 - Поточний масив
                2 - З масиву макрооб'єкта Печера
                3 - З масиву макрооб'єкта Склеп
                """);
        ArrayList<Creature> creatures1;
        ArrayList<Creature> creatures2;
        ConsoleHelper.writeMessage("Виберіть перший масив:");
        int choice = ConsoleHelper.readInt();
        switch (choice){
            case 1 -> creatures1=Main.creatures;
            case 2 -> creatures1=cave.getCreatures();
            case 3 -> creatures1=crypt.getCreatures();
            default -> {
                ConsoleHelper.writeMessage("Ви вибрали щось не то");
                return;
            }
        }
        ConsoleHelper.writeSeparator();
        int choice2 = ConsoleHelper.readInt();
        ConsoleHelper.writeMessage("Виберіть другий масив(не можна вибрати однакові масиви):");
        if (choice2 == choice) return;
        switch (choice2){
            case 1 -> creatures2=Main.creatures;
            case 2 -> creatures2=cave.getCreatures();
            case 3 -> creatures2=crypt.getCreatures();
            default -> {
                ConsoleHelper.writeMessage("Ви вибрали щось не то");
                return;
            }
        }
        ConsoleHelper.writeSeparator();
        ConsoleHelper.writeMessage("До взаємодії :");
        printArrays();
        for (Creature creature : creatures1){
            for (Creature creature2 : creatures2){
                creature.takeDamage(creature2);
            }
        }
        for (Creature creature : creatures2){
            for (Creature creature1 : creatures1){
                creature.takeDamage(creature1);
            }
        }
        ConsoleHelper.writeSeparator();
        ConsoleHelper.writeMessage("Після взаємодії :");
        printArrays();
    }

    public void countingMicroobjects(){
        ConsoleHelper.writeMessage("""
                Оберіть параметр для підрахунку :
                1 - якщо здоров'я більше за введене
                2 - якщо броня більша за введене
                3 - якщо швидкість більша за введене
                """);
        int choice = ConsoleHelper.readInt();
        ConsoleHelper.writeMessage("Введіть попередньо вибраного параметра, значення параметра:");
        int parameter = ConsoleHelper.readInt();
        int count=0;
        switch (choice){
            case 1 -> {
                for (Creature creature : Main.creatures){if (creature.getHealth()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в поточному масиві ,здоров'я яких більше за "+parameter+":"+count);
                count=0;
                for (Creature creature : cave.getCreatures()){if (creature.getHealth()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в масиві печері ,здоров'я яких більше за "+parameter+":"+count);
                count=0;
                for (Creature creature : crypt.getCreatures()){if (creature.getHealth()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в масиві склеп ,здоров'я яких більше за "+parameter+":"+count);
            }
            case 2 -> {
                for (Creature creature : Main.creatures){if (creature.getArmor()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в поточному масиві ,броня в яких більше за "+parameter+":"+count);
                count=0;
                for (Creature creature : cave.getCreatures()){if (creature.getArmor()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в масиві печера ,броня в яких більше за "+parameter+":"+count);
                count=0;
                for (Creature creature : crypt.getCreatures()){if (creature.getArmor()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в масиві склеп ,броня в яких більше за "+parameter+":"+count);
            }
            case 3 ->{
                for (Creature creature : Main.creatures){if (creature.getSpeed()>parameter){count++;}}
                count=0;
                ConsoleHelper.writeMessage("Кількість створінь в масиві печер,швидкість в яких більше за "+parameter+":"+count);
                for (Creature creature : cave.getCreatures()){if (creature.getSpeed()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в масиві печер,швидкість в яких більше за "+parameter+":"+count);
                count=0;
                for (Creature creature : crypt.getCreatures()){if (creature.getSpeed()>parameter){count++;}}
                ConsoleHelper.writeMessage("Кількість створінь в масиві склеп,швидкість в яких більше за "+parameter+":"+count);
            }
            default -> ConsoleHelper.writeMessage("Ти щось не то увів");
        }
        ConsoleHelper.writeSeparator();
    }

    public void removeFromArrays(){
        ConsoleHelper.writeMessage("""
                Виберіть з якого масиву видалити :
                1 - З поточного масив
                2 - З масиву макрооб'єкта Печера
                3 - З масиву макрооб'єкта Склеп
                """);
        int choice = ConsoleHelper.readInt();
        switch (choice){
            case 1 -> {
                for (int i = 0; i < creatures.size(); i++) {
                    ConsoleHelper.writeMessage(i+"."+creatures.get(i).toString());
                }
                ConsoleHelper.writeMessage("Уведіть індекс створіння ,для видалення:");
                int indexToRemove=ConsoleHelper.readInt();
                try {
                    creatures.remove(indexToRemove);
                }catch (IndexOutOfBoundsException e){
                    ConsoleHelper.writeMessage("Ви ввели число більше за розміри масиву або менше 0.");
                }

            }
            case 2 -> {
                for (int i = 0; i < cave.getCreatures().size(); i++) {
                    ConsoleHelper.writeMessage(i+"."+cave.getCreatures().get(i).toString());
                }
                ConsoleHelper.writeMessage("Уведіть індекс створіння ,для видалення:");
                int indexToRemove=ConsoleHelper.readInt();
                try {
                    cave.getCreatures().remove(indexToRemove);
                }catch (IndexOutOfBoundsException e){
                    ConsoleHelper.writeMessage("Ви ввели число більше за розміри масиву або менше 0.");
                }
            }
            case 3 -> {
                for (int i = 0; i < crypt.getCreatures().size(); i++) {
                    ConsoleHelper.writeMessage(i+"."+crypt.getCreatures().get(i).toString());
                }
                ConsoleHelper.writeMessage("Уведіть індекс створіння ,для видалення:");
                int indexToRemove=ConsoleHelper.readInt();
                try {
                    crypt.getCreatures().remove(indexToRemove);
                }catch (IndexOutOfBoundsException e){
                    ConsoleHelper.writeMessage("Ви ввели число більше за розміри масиву або менше 0.");
                }
            }
        }
        ConsoleHelper.writeSeparator();
    }
}
