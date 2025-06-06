package my.kursova21;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.TriggerComponent;
import model.items.firearms.rifles.AKM;
import model.items.inventory.Inventory;
import model.objects.EntityType;
import model.objects.microobjects.*;
import model.objects.macroobjects.*;
import model.objects.nanoobjects.NanoObjectsFactory;
import org.jetbrains.annotations.NotNull;
import utilies.ConsoleHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static utilies.ConsoleHelper.font;
import static utilies.ConsoleHelper.smallFont;

public class Lab4 extends GameApplication {

    private final static String GAME_TITLE = "Lab4";
    private final static String LAB_VERSION = "0.0.1";
    public final static int WIDTH = 1920;
    public final static int HEIGHT = 1080;

    private MicroObjectAbstract buffer;// Буфер для зберігання, мікроОбєктів для подальшого копіювання
    private int amountOfActive = 0;// Кількість активних мікроОбєктів

    private Label amountOfActiveMicroObjects, bufferLabel;// Label активних мікроОбєктів і ще один для показу мікроОбєкт, що є в буфері
    private final List<Label> listOfActiveMicroObjects = new ArrayList<>();// Label активних мікроОбєктів

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle(GAME_TITLE);
        gameSettings.setVersion(LAB_VERSION);
        gameSettings.setWidth(WIDTH);
        gameSettings.setHeight(HEIGHT);
        gameSettings.setAppIcon("");
        gameSettings.setDeveloperMenuEnabled(true);
        gameSettings.setManualResizeEnabled(true);
    }

    @Override
    protected void initGame() {
        initPhysics();
        FXGL.getGameScene().setCursor(Cursor.DEFAULT);
        getGameWorld().addEntityFactory(new MicroObjectsFactory());// Реєструємо фабрику МікроОбєктів
        getGameWorld().addEntityFactory(new NanoObjectsFactory());// Реєструємо фабрику наноОбєктів
        getGameWorld().addEntityFactory(new MacroObjectFactory());// Реєструємо фабрику МакроОбєктів
        spawn("Crypt", 1000, 800);
        spawn("Dormitory", 1200, 800);
        spawn("Cave", 1400,800 );
        spawn("Recruit", 300, 500);
        spawn("Soldier", 500, 500);
        spawn("Cultist", 800, 300);
        setupControls();
    }

    //Встановлення контролю
    protected void setupControls() {
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Active MicroObject or DeActivate MicroObject") {
            @Override
            protected void onActionBegin() {
                Point2D mousePos = FXGL.getInput().getMousePositionWorld(); // Визначаємо позицію миші на екрані
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent())// Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    boolean mouseInside = MicroObjectAbstract.isIsMouseInside(entity, mousePos);// Перевірка чи курсор всередині об'єкта
                    if (mouseInside) {
                        MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                                .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                                .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                        if (microObjectAbstract == null) {break;}
                        //Робимо об'єкт активним чи не активним
                        microObjectAbstract.setActive(!microObjectAbstract.isActive());
                        //Виводимо повідомлення, щоб стан об'єкта змінився і змінюємо кількість активних мікроОбєктів
                        ConsoleHelper.writeMessageInLabelInRightCorner(microObjectAbstract + (microObjectAbstract.isActive() ? " тепер активний." : "тепер неактивний."), 7,WIDTH,HEIGHT);
                        amountOfActive = microObjectAbstract.isActive() ? ++amountOfActive : --amountOfActive;
                        //Оновлюємо
                        setAmountOfActiveMicroObjects();
                        updateListOfActiveMicroObjects(microObjectAbstract);
                        break;
                    }
                }
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("CopyAction") {
            @Override
            protected void onActionBegin() {
                Point2D mousePos = FXGL.getInput().getMousePositionWorld(); // Визначаємо позицію миші на екрані
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent())// Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    boolean mouseInside = MicroObjectAbstract.isIsMouseInside(entity, mousePos);
                    if (mouseInside) {
                        MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                                .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                                .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                        if (microObjectAbstract == null) {break;}
                        buffer = microObjectAbstract;
                        //Виводимо повідомлення
                        ConsoleHelper.writeMessageInLabelInRightCorner(microObjectAbstract + " скопійовано в буфер.", 8,WIDTH,HEIGHT);
                        //Змінюємо вміст буфера
                        setBufferLabel("Буфер: " + microObjectAbstract);
                        break;
                    }
                }
            }
        }, KeyCode.C, InputModifier.CTRL);

        input.addAction(new UserAction("insertFromBuffer") {
            @Override
            protected void onActionBegin() {
                if (buffer == null) {
                    ConsoleHelper.writeMessageInLabelInRightCorner("Буфер пустий!", 7,WIDTH,HEIGHT);
                    return;
                }

                Point2D mousePos = FXGL.getInput().getMousePositionWorld();// Визначаємо позицію миші на екрані

                getGameWorld().addEntity(buffer.createCopyAt(mousePos)); // Створюємо клон, додає до світу на місці курсора
                ConsoleHelper.writeMessageInLabelInRightCorner("Скопійовано з буфера на місце курсора із" + buffer + ".", 7,WIDTH,HEIGHT);
            }
        }, KeyCode.V, InputModifier.CTRL);

        input.addAction(new UserAction("moveOnArrowRight") {
            @Override
            protected void onActionBegin() {
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                            .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                            .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                    if (microObjectAbstract != null && microObjectAbstract.isActive()) {
                        microObjectAbstract.moveRight();//Переміщуємо вправо, якщо об'єкт активний
                    }
                }
            }

            @Override
            protected void onActionEnd() {
                stopAllActiveMicroObjects();
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("moveOnArrowLeft") {
            @Override
            protected void onActionBegin() {
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                            .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                            .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                    if (microObjectAbstract != null && microObjectAbstract.isActive()) {
                        microObjectAbstract.moveLeft();//Переміщуємо вліво, якщо об'єкт активний
                    }
                }
            }

            @Override
            protected void onActionEnd() {
                stopAllActiveMicroObjects();
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("moveOnArrowUp") {
            @Override
            protected void onActionBegin() {
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                            .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                            .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                    if (microObjectAbstract != null && microObjectAbstract.isActive()) {
                        microObjectAbstract.moveUp();//Переміщуємо верх, якщо об'єкт активний
                    }
                }
            }

            @Override
            protected void onActionEnd() {
                stopAllActiveMicroObjects();
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("moveOnArrowDown") {
            @Override
            protected void onActionBegin() {
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                            .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                            .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                    if (microObjectAbstract != null && microObjectAbstract.isActive()) {
                        microObjectAbstract.moveDown();//Переміщуємо вниз, якщо об'єкт активний
                    }
                }
            }

            @Override
            protected void onActionEnd() {
                stopAllActiveMicroObjects();
            }
        }, KeyCode.DOWN);

        input.addAction(new UserAction("deleteAllActiveMicroObjects") {
            @Override
            protected void onActionBegin() {
                if (amountOfActive == 0) {
                    return;
                }
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent())// Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                            .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                            .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                    if (microObjectAbstract != null && microObjectAbstract.isActive()) {
                        FXGL.getGameWorld().removeEntity(entity);//Видаляємо всі активні мікроОбєкти
                    }
                }
                ConsoleHelper.writeMessageInLabelInRightCorner("Видалено усі активні мікрообєкти.", 8,WIDTH,HEIGHT);
                amountOfActive = 0;
                //Оновлюємо статистику
                setAmountOfActiveMicroObjects();
                clearListOfActiveMicroObjects();
            }
        }, KeyCode.DELETE);

        input.addAction(new UserAction("deActiveAllActiveMicroObjects") {
            @Override
            protected void onActionBegin() {
                if (amountOfActive == 0) {
                    return;
                }
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent())// Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                            .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є Creature
                            .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                    if (microObjectAbstract != null && microObjectAbstract.isActive()) {
                        microObjectAbstract.setActive(false);//Деактивуємо всі активні мікоОбєкти
                    }
                }
                ConsoleHelper.writeMessageInLabelInRightCorner("Деактивовано усі активні мікрообєкти.", 8,WIDTH,HEIGHT);
                //Оновлюємо статистику
                amountOfActive = 0;
                setAmountOfActiveMicroObjects();
                clearListOfActiveMicroObjects();
            }
        }, KeyCode.ESCAPE);

        input.addAction(new UserAction("insertNewMicroObject") {
            @Override
            protected void onActionBegin() {
                MicroObjectAbstract microObjectAbstract = showCreateCreatureDialog();//Отримуємо дані про новий мікроОбєкт
                if (microObjectAbstract == null) return;
                //Виводимо повідомлення про створення нового мікроОбєкта
                ConsoleHelper.writeMessageInLabelInRightCorner("Insert New MicroObject ." + microObjectAbstract
                        + ". Координатах x:" + microObjectAbstract.getX()
                        + " y:" + microObjectAbstract.getY(), 8,WIDTH,HEIGHT);
            }
        }, KeyCode.INSERT);

        input.addAction(new UserAction("editObject") {
            @Override
            protected void onActionBegin() {
                Point2D mousePos = FXGL.getInput().getMousePositionWorld();
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent())// Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    boolean mouseInside = MicroObjectAbstract.isIsMouseInside(entity, mousePos);
                    if (mouseInside) {
                        MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                                .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                                .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
                        if (microObjectAbstract == null) {//Якщо не знайшло запускаємо перевірку на макроОбєкт
                            MacroObjectAbstract macroObjectAbstract = entity.getComponents().stream()// Перевіряємо, чи компонент є MacroObjectAbstract
                                    .filter(MacroObjectAbstract.class::isInstance) // Перетворюємо компонент у MacroObjectAbstract
                                    .map(MacroObjectAbstract.class::cast).findFirst().orElse(null);
                            //Якщо не знайшли, або макроОбєкт пустий
                            if (macroObjectAbstract == null || macroObjectAbstract.isEmpty()) {
                                ConsoleHelper.isNull(macroObjectAbstract);
                                return;
                            }
                            //Запускаємо діалог для вибору мікроОбєкта і подальшого витягування
                            pullMicroObjectDialog(macroObjectAbstract);
                            break;
                        }
                        //Запускаємо діалог для внесення змін в мікроОбєкті
                        changeMicroObjectDialog(microObjectAbstract, entity);
                        break;
                    }
                }
            }
        }, MouseButton.SECONDARY);
    }

    /**
     * @param bufferLabelText текст рядка буфера
     */
    public void setBufferLabel(String bufferLabelText) {
        //Створення й оновлення рядка буфера
        try {//Спроба видалити рядок буфера
            FXGL.getGameScene().removeUINode(bufferLabel);
        } catch (Exception ignored) {}
        //Створення нового рядка буфера
        bufferLabel = new Label(bufferLabelText);
        //Встановлення стилю
        bufferLabel.setFont(font);
        bufferLabel.setTextFill(Color.LIGHTGREEN);
        //Розміщення
        bufferLabel.setLayoutX(0);
        bufferLabel.setLayoutY(0);
        //Додавання на сцену рядка буфера
        FXGL.getGameScene().addUINode(bufferLabel);
    }

    public void setAmountOfActiveMicroObjects() {
        try {//Спроба видалити рядок буфера
            FXGL.getGameScene().removeUINode(amountOfActiveMicroObjects);
        } catch (Exception ignored) {
        }
        // Створення нового рядку кількості мікроОбєктів
        amountOfActiveMicroObjects = new Label("Кількість активних мікрообєктів: " + amountOfActive + ".");
        //Встановлення стилю
        amountOfActiveMicroObjects.setFont(font);
        amountOfActiveMicroObjects.setTextFill(Color.LIGHTGREEN);
        //Розміщення
        amountOfActiveMicroObjects.setLayoutX(0);
        amountOfActiveMicroObjects.setLayoutY(30);
        //Додавання на сцену рядка кількості мікроОбєктів
        FXGL.getGameScene().addUINode(amountOfActiveMicroObjects);
    }

    /**Оновлення списку активних мікроОбєктів
     * @param microObjectAbstract мікроОбєкт який буде додано до списку
     */
    public void updateListOfActiveMicroObjects(MicroObjectAbstract microObjectAbstract) {
        listOfActiveMicroObjects.forEach(FXGL.getGameScene()::removeUINode);//Видаляємо елементи списку
        if (microObjectAbstract.isActive()) {
            int sizeOfList = listOfActiveMicroObjects.size();
            if (sizeOfList < 5) {//Додавання нових елементів списку
                switch (listOfActiveMicroObjects.size()) {
                    case 0 -> {
                        listOfActiveMicroObjects.add(new Label(microObjectAbstract.toString()));
                        Label l = listOfActiveMicroObjects.getFirst();
                        l.setLayoutY(50);
                    }
                    case 1 -> {
                        listOfActiveMicroObjects.add(new Label(microObjectAbstract.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(70);
                    }
                    case 2 -> {
                        listOfActiveMicroObjects.add(new Label(microObjectAbstract.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(90);
                    }
                    case 3 -> {
                        listOfActiveMicroObjects.add(new Label(microObjectAbstract.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(110);
                    }
                    case 4 -> {
                        listOfActiveMicroObjects.add(new Label(microObjectAbstract.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(130);
                    }
                }
            } else {// Інакше видалити останній
                listOfActiveMicroObjects.removeLast();
                listOfActiveMicroObjects.addFirst(new Label(microObjectAbstract.toString()));//Додати на початок списку новий рядок
                Label l = listOfActiveMicroObjects.getFirst();
                // Розмісти його, замість видаленого
                l.setLayoutY(50);
                //Всі інші перемістити ніби на один нижче
                for (int i = 1; i < 5; i++) {
                    listOfActiveMicroObjects.get(i).setLayoutY(50 + 20 * i);
                }
            }
        } else {
            //Створюємо список вмісту рядків, за допомогою лямбди
            List<String> whatInsideInListOfActiveMicroObjects = listOfActiveMicroObjects.stream().
                    map(Label::getText).
                    toList();
            int indexOfFound = whatInsideInListOfActiveMicroObjects.indexOf(microObjectAbstract.toString());
            if (indexOfFound != -1) {
                listOfActiveMicroObjects.remove(listOfActiveMicroObjects.get(indexOfFound));
            }
            if (!listOfActiveMicroObjects.isEmpty()) // Переміщуєм ,якщо не список не пустий
                for (int i = 0; i < listOfActiveMicroObjects.size(); i++) {
                    listOfActiveMicroObjects.get(i).setLayoutY(50 + 20 * i);
                }
        }
        //Додавання рядків до сцени
        listOfActiveMicroObjects.forEach(label -> {
            label.setLayoutX(0);
            //Встановлення стилю
            label.setTextFill(Color.LIGHTGREEN);
            label.setFont(font);
            FXGL.getGameScene().addUINode(label);
        });
    }

    // Очищення списку рядків активних мікроОбєктів
    private void clearListOfActiveMicroObjects() {
        listOfActiveMicroObjects.forEach(FXGL.getGameScene()::removeUINode);
        listOfActiveMicroObjects.clear();
    }

    // Зупиняє всі активні мікроОбєкти
    public void stopAllActiveMicroObjects() {
        List<Entity> entityList = FXGL.getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                .toList();
        for (Entity entity : entityList) {
            MicroObjectAbstract microObjectAbstract = entity.getComponents().stream()
                    .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                    .map(MicroObjectAbstract.class::cast).findFirst().orElse(null); // Перетворюємо компонент у MicroObjectAbstract
            if (microObjectAbstract != null && microObjectAbstract.isActive()) {
                microObjectAbstract.stop();
            }
        }
    }

    // Діалог створення нового мікроОбєкта
    private MicroObjectAbstract showCreateCreatureDialog() {

        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Створити новий мікрообєкт");
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        //Відступи між елементами
        grid.setPadding(new Insets(20));

        // Назва істоти
        TextField nameField = new TextField();
        nameField.setPromptText("Ім'я");
        grid.add(new Label("Ім'я:"), 0, 0);
        grid.add(nameField, 1, 0);

        // Позиція X, Y
        TextField xField = new TextField("100");
        TextField yField = new TextField("100");
        grid.add(new Label("X:"), 0, 1);
        grid.add(xField, 1, 1);
        grid.add(new Label("Y:"), 0, 2);
        grid.add(yField, 1, 2);

        // Тип істоти (ChoiceBox з enum)
        ChoiceBox<typeOfMicroObject> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll(typeOfMicroObject.RECRUIT, typeOfMicroObject.SOLDIER, typeOfMicroObject.CULTIST);
        typeChoice.setValue(typeOfMicroObject.RECRUIT);
        grid.add(new Label("Тип:"), 0, 3);
        grid.add(typeChoice, 1, 3);

        // Активність (CheckBox)
        CheckBox activeBox = new CheckBox("Активний");
        activeBox.setSelected(false);
        grid.add(activeBox, 1, 4);

        // Чи створити за шаблоном (RadioButton)
        ToggleGroup patternChoice = new ToggleGroup();
        RadioButton patternYes = new RadioButton("Створити за шаблоном:Так");
        RadioButton patternNo = new RadioButton("Створити за шаблоном:Ні");
        patternYes.setToggleGroup(patternChoice);
        patternNo.setToggleGroup(patternChoice);
        patternNo.setSelected(true);
        grid.add(patternYes, 1, 5);
        grid.add(patternNo, 1, 6);

        // Кількість здоров'я
        Spinner<Integer> health = new Spinner<>(0, 10000, 1);
        grid.add(new Label("Health:"), 0, 7);
        grid.add(health, 1, 7);

        // Кількість броні
        Spinner<Double> armor = new Spinner<>(0, 1000, 0.1, 0.1);
        grid.add(new Label("Armor:"), 0, 8);
        grid.add(armor, 1, 8);

        // Максимальна вага інвентарю
        Spinner<Integer> maxWeight = new Spinner<>(5, 500, 1);
        grid.add(new Label("maxWeight:"), 0, 9);
        grid.add(maxWeight, 1, 9);

        // Кількість досвіду об'єкта
        Spinner<Double> experience = new Spinner<>(0, 1000, 0, 0.1);
        grid.add(new Label("experience:"), 0, 10);
        grid.add(experience, 1, 10);

        //Швидкість об'єкта
        Spinner<Integer> speed = new Spinner<>(0, 10000, 1);
        grid.add(new Label("speed:"), 0, 11);
        grid.add(speed, 1, 11);

        dialog.getDialogPane().setContent(grid);

        // Обробка результату діалогу

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().trim();
            int x ;
            int y ;
            // Перевірка координат
            try{
                x = Integer.parseInt(xField.getText());
                y = Integer.parseInt(yField.getText());
                if (x<0 || y<0){
                    ConsoleHelper.writeMessageInLabelInRightCorner("Ви ввели відємне число.",8,WIDTH,HEIGHT);
                    return null;
                }
            } catch (NumberFormatException e) {
                ConsoleHelper.writeMessageInLabelInRightCorner("Ви ввели не число.",8,WIDTH,HEIGHT);
                return null;
            }
            int healt = health.getValue();
            double arm = armor.getValue();
            double max = maxWeight.getValue();
            double exper = experience.getValue();
            int sped = speed.getValue();
            typeOfMicroObject type = typeChoice.getValue();
            boolean isActive = activeBox.isSelected();
            boolean patternYesSelected = patternYes.isSelected();

            // Перевірка ім'я
            if (name.isEmpty() && !patternYesSelected) {
                ConsoleHelper.writeMessageInLabelInRightCorner("Ви ввели пусте ім'я.",8,WIDTH,HEIGHT);
                return null;
            }

            MicroObjectAbstract microObjectAbstract;

            // Створюємо мікроОбєкт, в залежно від вибору
            switch (type) {
                case SOLDIER -> {
                    if (patternYesSelected) microObjectAbstract = new Soldier(x,y);
                    else
                        microObjectAbstract = new Soldier(name, healt, arm, Inventory.getInventory(max, new AKM()), exper, x, y, sped);
                }
                case CULTIST -> {
                    if (patternYesSelected) microObjectAbstract = new Cultist(x,y);
                    else
                        microObjectAbstract = new Cultist(name, healt, arm, Inventory.getInventory(max, new AKM()), exper, x, y, sped);
                }
                default -> {
                    if (patternYesSelected) microObjectAbstract = new Recruit(x,y);
                    else
                        microObjectAbstract = new Recruit(name, healt, arm, Inventory.getInventory(max, new AKM()), exper, x, y, sped);
                }
            }

            Entity newEntity = microObjectAbstract.getNewEntity();

            microObjectAbstract.setActive(isActive);  // Встановлюємо активність

            // Додаємо істоту у світ гри
            FXGL.getGameWorld().addEntity(newEntity);
            return microObjectAbstract;
        }
        return null;
    }

    /**
     * Діалог зміни мікроОбєкта
     * @param microObjectAbstract об'єкт який буде змінюватись
     * @param entity об'єкта який буде змінюватись
     */
    protected void changeMicroObjectDialog(MicroObjectAbstract microObjectAbstract, Entity entity) {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Змінити мікроОбєкт " + microObjectAbstract);
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // Назва істоти
        TextField nameField = new TextField();
        nameField.setPromptText(microObjectAbstract.getCreatureName());
        grid.add(new Label("Ім'я:"), 0, 0);
        grid.add(nameField, 1, 0);

        //Поля координат
        Spinner<Integer> xField = new Spinner<>(0, WIDTH, microObjectAbstract.getX());
        Spinner<Integer> yField = new Spinner<>(0, HEIGHT, microObjectAbstract.getY());
        grid.add(new Label("X:"), 0, 1);
        grid.add(xField, 1, 1);
        grid.add(new Label("Y:"), 0, 2);
        grid.add(yField, 1, 2);

        // Кількість здоров'я
        Spinner<Integer> health = new Spinner<>(0, 10000, microObjectAbstract.getHealth());
        grid.add(new Label("Health:"), 0, 3);
        grid.add(health, 1, 3);

        // Кількість броні
        Spinner<Double> armor = new Spinner<>(0, 1000, microObjectAbstract.getArmor(), 0.1);
        grid.add(new Label("Armor:"), 0, 4);
        grid.add(armor, 1, 4);


        // Кількість досвіду об'єкта
        Spinner<Double> experience = new Spinner<>(0, 1000, microObjectAbstract.getExperiencePoint(), 0.1);
        grid.add(new Label("experience:"), 0, 5);
        grid.add(experience, 1, 5);

        // Активність (CheckBox)
        CheckBox activeBox = new CheckBox("Активний");
        activeBox.setSelected(microObjectAbstract.isActive());
        grid.add(activeBox, 1, 6);

        //Встановлюємо положення на положення миші, діалогу
        Point position = getNormalPositionForDialog(300, 400);
        dialog.setX(position.getX());
        dialog.setY(position.getY());

        // Встановлюємо макет
        dialog.getDialogPane().setContent(grid);

        // Обробка результату діалогу

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().isEmpty() ? microObjectAbstract.getCreatureName() : nameField.getText().trim();
            int newX = xField.getValue();
            int newY = yField.getValue();
            int healt = health.getValue();
            double arm = armor.getValue();
            double exper = experience.getValue();
            boolean isActive = activeBox.isSelected();

            boolean isHaveChanged = false;

            //Вивід повідомлення про зміни
            StringBuilder changes = new StringBuilder("Зміни в " + microObjectAbstract + ": \n ");
            if (!microObjectAbstract.getCreatureName().equals(name)) {
                changes.append("Імя змінено на ").append(name).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getX() == newX)) {
                changes.append("Переміщено на x: ").append(newX).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getY() == newY)) {
                changes.append("Переміщено на y: ").append(newY).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getHealth() == healt)) {
                changes.append("Кількість здоровя ").append("змінено на ").append(healt).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getArmor() == arm)) {
                changes.append("Кількість броні змінено на ").append(arm).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getExperiencePoint() == exper)) {
                changes.append("Кількість досвіду змінено на ").append(exper).append(" \n ");
                isHaveChanged = true;
            }
            if (entity != null) {
                PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
                physics.overwritePosition(new Point2D(newX, newY));
            }

            // Вносимо зміни, якщо вони були
            if (isHaveChanged) ConsoleHelper.writeMessageInLabelInRightCorner(changes.toString(), 10,WIDTH,HEIGHT);
            microObjectAbstract.setCreatureName(name);
            microObjectAbstract.setHealth(healt);
            microObjectAbstract.setArmor(arm);
            microObjectAbstract.setExperiencePoint(exper);
            microObjectAbstract.setActive(isActive);  // встановлюємо активність
        }
    }

    /**
     * Діалог зміни мікроОбєкта
     * @param microObjectAbstract об'єкт який буде змінюватись
     * @param entity об'єкта який буде змінюватись
     * @param position координати на які буде встановлено діалог
     */
    protected void changeMicroObjectDialog(MicroObjectAbstract microObjectAbstract, Entity entity,Point position) {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Змінити мікроОбєкт " + microObjectAbstract);
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // Назва істоти
        TextField nameField = new TextField();
        nameField.setPromptText(microObjectAbstract.getCreatureName());
        grid.add(new Label("Ім'я:"), 0, 0);
        grid.add(nameField, 1, 0);

        //Поля координат
        Spinner<Integer> xField = new Spinner<>(0, WIDTH, microObjectAbstract.getX());
        Spinner<Integer> yField = new Spinner<>(0, HEIGHT, microObjectAbstract.getY());
        grid.add(new Label("X:"), 0, 1);
        grid.add(xField, 1, 1);
        grid.add(new Label("Y:"), 0, 2);
        grid.add(yField, 1, 2);

        // Кількість здоров'я
        Spinner<Integer> health = new Spinner<>(0, 10000, microObjectAbstract.getHealth());
        grid.add(new Label("Health:"), 0, 3);
        grid.add(health, 1, 3);

        // Кількість броні
        Spinner<Double> armor = new Spinner<>(0, 1000, microObjectAbstract.getArmor(), 0.1);
        grid.add(new Label("Armor:"), 0, 4);
        grid.add(armor, 1, 4);


        // Кількість досвіду об'єкта
        Spinner<Double> experience = new Spinner<>(0, 1000, microObjectAbstract.getExperiencePoint(), 0.1);
        grid.add(new Label("experience:"), 0, 5);
        grid.add(experience, 1, 5);

        // Активність (CheckBox)
        CheckBox activeBox = new CheckBox("Активний");
        activeBox.setSelected(microObjectAbstract.isActive());
        grid.add(activeBox, 1, 6);

        if (position != null) {
            dialog.setX(position.getX());
            dialog.setY(position.getY());
        }

        // Встановлюємо макет
        dialog.getDialogPane().setContent(grid);

        // Обробка результату діалогу

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().isEmpty() ? microObjectAbstract.getCreatureName() : nameField.getText().trim();
            int newX = xField.getValue();
            int newY = yField.getValue();
            int healt = health.getValue();
            double arm = armor.getValue();
            double exper = experience.getValue();
            boolean isActive = activeBox.isSelected();

            boolean isHaveChanged = false;

            //Вивід повідомлення про зміни
            StringBuilder changes = new StringBuilder("Зміни в " + microObjectAbstract + ": \n ");
            if (!microObjectAbstract.getCreatureName().equals(name)) {
                changes.append("Імя змінено на ").append(name).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getX() == newX)) {
                changes.append("Переміщено на x: ").append(newX).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getY() == newY)) {
                changes.append("Переміщено на y: ").append(newY).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getHealth() == healt)) {
                changes.append("Кількість здоровя ").append("змінено на ").append(healt).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getArmor() == arm)) {
                changes.append("Кількість броні змінено на ").append(arm).append(" \n ");
                isHaveChanged = true;
            }
            if (!(microObjectAbstract.getExperiencePoint() == exper)) {
                changes.append("Кількість досвіду змінено на ").append(exper).append(" \n ");
                isHaveChanged = true;
            }
            if (entity != null) {
                PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
                physics.overwritePosition(new Point2D(newX, newY));
            }

            // Вносимо зміни, якщо вони були
            if (isHaveChanged) ConsoleHelper.writeMessageInLabelInRightCorner(changes.toString(), 10,WIDTH,HEIGHT);
            microObjectAbstract.setCreatureName(name);
            microObjectAbstract.setHealth(healt);
            microObjectAbstract.setArmor(arm);
            microObjectAbstract.setExperiencePoint(exper);
            microObjectAbstract.setActive(isActive);  // встановлюємо активність
        }
    }

    /**
     * @param macroObjectAbstract з якого буде витягнуто макроОбєкт
     */
    private void pullMicroObjectDialog(MacroObjectAbstract macroObjectAbstract) {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Витягнути мікроОбєкт з макроОбєкта " + macroObjectAbstract.getClass().getSimpleName());
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // Обгортаємо звичайний список в ObservableList
        ObservableList<MicroObjectAbstract> observableMicroObjectAbstracts = FXCollections.observableArrayList(macroObjectAbstract.getCreatures());
        // Створюємо візуальний список
        ListView<MicroObjectAbstract> listView = getListMicroObjectView(observableMicroObjectAbstracts);

        grid.add(listView, 1, 0);

        // Встановлюємо максимальні розміри вікна списку
        listView.setMaxSize(150, 200);

        // Встановлюємо положення на положення миші, діалогу
        Point position = getNormalPositionForDialog(300, 375);
        dialog.setX(position.getX());
        dialog.setY(position.getY());

        //Встановлюємо макет в діалог
        dialog.getDialogPane().setContent(grid);

        // Обробка результату діалогу
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int selectedCreature = listView.getSelectionModel().getSelectedIndex();
            if (selectedCreature < 0) return;
            macroObjectAbstract.pullCreature(selectedCreature);
        }
    }

    /**
     * @param observableMicroObjectAbstracts  список мікроОбєктів
     * @return список перегляду мікроОбєктів
     */
    protected static ListView<MicroObjectAbstract> getListMicroObjectView(@NotNull ObservableList<MicroObjectAbstract> observableMicroObjectAbstracts) {

        ListView<MicroObjectAbstract> listView = new ListView<>();
        listView.setItems(observableMicroObjectAbstracts);

        // Переробляємо фабрику контейнерів
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MicroObjectAbstract microObjectAbstract, boolean empty) {
                super.updateItem(microObjectAbstract, empty);

                // Якщо рядок порожній або об’єкт null, нічого не показуємо
                if (empty || microObjectAbstract == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Створюємо контейнер-рядок
                    HBox row = new HBox(10); // 10px відстань між елементами
                    row.setAlignment(Pos.CENTER_LEFT);

                    // Назва істоти
                    Text nameText = new Text(microObjectAbstract.toString());
                    nameText.setFont(smallFont);
                    nameText.setFill(Color.LIGHTGREEN);

                    // Додаємо все в HBox
                    row.getChildren().addAll(nameText);

                    // Встановлюємо графіку замість простого тексту
                    setGraphic(row);
                }
            }
        });
        return listView;
    }


    @Override
    // Ініціалізація фізики
    protected void initPhysics() {
        //Додавання колізій між макроОбєктом і мікроОбєктом
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.MACROOBJECT, EntityType.ENEMY) {

            @Override
            protected void onCollision(Entity macroObject, Entity microObject) {
                MicroObjectAbstract microObjectComponent = microObject.getComponents().stream()
                        .filter(MicroObjectAbstract.class::isInstance)
                        .map(MicroObjectAbstract.class::cast)
                        .findFirst()
                        .orElse(null);// Отримуємо мікроОбєкт з entity
                MacroObjectAbstract macroObjectComponent = macroObject.getComponents().stream()
                        .filter(MacroObjectAbstract.class::isInstance)
                        .map(MacroObjectAbstract.class::cast)
                        .findFirst()
                        .orElse(null);// Отримуємо макроОбєкт з entity
                // Перевірка на null
                if (microObjectComponent == null || macroObjectComponent == null) return;
                // Додавання мікрОбєкт до макроОбєкта
                macroObjectComponent.addCreature(microObjectComponent);
                // Видаляємо entity зі світу (ніби він зайшов в макроОбєкт)
                microObject.removeFromWorld();
                // Вивід повідомлення, про додавання в макроОбєкт
                ConsoleHelper.writeMessageInLabelInRightCorner(microObjectComponent + " увійшов в " + macroObjectComponent, 8,WIDTH,HEIGHT);
            }
        });
    }

    /**
     * @param width ширина вікна діалогу
     * @param height висота вікна діалогу
     */
    protected static Point getNormalPositionForDialog(int width, int height) {
        // Отримуємо поточну позицію курсора (Screen Coordinates)
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        int x = mousePos.x;
        int y = mousePos.y;

        // Отримуємо розміри всього екрану
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Обчислюємо, чи «приплили» за межі правої або нижньої сторони
        if (x + width > screenWidth) {
            // Якщо край вікна виходить за праву межу екрана,
            // підправляємо x так, щоб права грань була під екран
            x = screenWidth - width;
        }

        // Аналогічно для Y (нижній край вікна)
        if (y + height > screenHeight) {
            y = screenHeight - height;
        }

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        return new Point(x, y);
    }
}