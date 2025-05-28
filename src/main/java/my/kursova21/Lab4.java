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

    private Creature buffer;
    private int amountOfActive = 0;

    private Label amountOfActiveMicroObjects, bufferLabel;
    private final List<Label> listOfActiveMicroObjects = new ArrayList<>();

    PhysicsWorld Physic;

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
        getGameWorld().addEntityFactory(new MicroObjectsFactory()); // Реєструємо фабрику
        getGameWorld().addEntityFactory(new NanoObjectsFactory());
        getGameWorld().addEntityFactory(new MacroObjectFactory());
        spawn("Crypt", 1000, 800);
        spawn("Dormitory", 1200, 800);
        spawn("Cave", WIDTH - 128, HEIGHT - 128);
        spawn("Recruit", 300, 500);
        spawn("Soldier", 500, 500);
        spawn("Cultist", 800, 300);
        setupControls();
        Physic = FXGL.getPhysicsWorld();
    }

    private void setupControls() {
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Active MicroObject or DeActivate MicroObject") {
            @Override
            protected void onActionBegin() {
                Point2D mousePos = FXGL.getInput().getMousePositionWorld();
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent())// Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    boolean mouseInside = Creature.isIsMouseInside(entity, mousePos);
                    if (mouseInside) {
                        Creature creature = entity.getComponents().stream()
                                .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                                .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                        ConsoleHelper.isNull(creature);
                        if (creature == null) {
                            break;
                        }
                        creature.setActive(!creature.isActive());
                        ConsoleHelper.writeMessageInLabelInRightCorner(creature + (creature.isActive() ? " тепер активний." : "тепер неактивний."), 7,WIDTH,HEIGHT);
                        amountOfActive = creature.isActive() ? ++amountOfActive : --amountOfActive;
                        setAmountOfActiveMicroObjects();
                        updateListOfActiveMicroObjects(creature);
                        break;
                    }
                }
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("CopyAction") {
            @Override
            protected void onActionBegin() {
                Point2D mousePos = FXGL.getInput().getMousePositionWorld();
                List<Entity> entityList = FXGL.getGameWorld().getEntities()
                        .stream()
                        .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                        .toList();
                for (Entity entity : entityList) {
                    boolean mouseInside = Creature.isIsMouseInside(entity, mousePos);
                    if (mouseInside) {
                        Creature creature = entity.getComponents().stream()
                                .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                                .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                        assert creature != null;
                        buffer = creature;
                        ConsoleHelper.writeMessageInLabelInRightCorner(creature + " скопійовано в буфер.", 8,WIDTH,HEIGHT);
                        setBufferLabel("Буфер: " + creature);
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

                Point2D mousePos = FXGL.getInput().getMousePositionWorld();

                getGameWorld().addEntity(buffer.createCopyAt(mousePos));
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
                    Creature creature = entity.getComponents().stream()
                            .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                            .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                    if (creature != null && creature.isActive()) {
                        creature.moveRight();
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
                    Creature creature = entity.getComponents().stream()
                            .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                            .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                    if (creature != null && creature.isActive()) {
                        creature.moveLeft();
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
                    Creature creature = entity.getComponents().stream()
                            .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                            .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                    if (creature != null && creature.isActive()) {
                        creature.moveUp();
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
                    Creature creature = entity.getComponents().stream()
                            .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                            .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                    if (creature != null && creature.isActive()) {
                        creature.moveDown();
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
                    Creature creature = entity.getComponents().stream()
                            .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                            .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                    if (creature != null && creature.isActive()) {
                        FXGL.getGameWorld().removeEntity(entity);
                    }
                }
                ConsoleHelper.writeMessageInLabelInRightCorner("Видалено усі активні мікрообєкти.", 8,WIDTH,HEIGHT);
                amountOfActive = 0;
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
                    Creature creature = entity.getComponents().stream()
                            .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                            .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                    if (creature != null && creature.isActive()) {
                        creature.setActive(false);
                    }
                }
                ConsoleHelper.writeMessageInLabelInRightCorner("Деактивовано усі активні мікрообєкти.", 8,WIDTH,HEIGHT);
                amountOfActive = 0;
                setAmountOfActiveMicroObjects();
                clearListOfActiveMicroObjects();
            }
        }, KeyCode.ESCAPE);

        input.addAction(new UserAction("insertNewMicroObject") {
            @Override
            protected void onActionBegin() {
                Creature creature = showCreateCreatureDialog();
                if (creature == null) return;
                ConsoleHelper.writeMessageInLabelInRightCorner("Insert New MicroObject ." + creature + ". Координатах x:" + creature.getX() + " y:" + creature.getY(), 8,WIDTH,HEIGHT);
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
                    boolean mouseInside = Creature.isIsMouseInside(entity, mousePos);
                    if (mouseInside) {
                        Creature creature = entity.getComponents().stream()
                                .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                                .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
                        if (creature == null) {
                            MacroObjectAbstract macroObjectAbstract = entity.getComponents().stream()
                                    .filter(MacroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є Creature
                                    .map(MacroObjectAbstract.class::cast).findFirst().orElse(null);
                            if (macroObjectAbstract == null || macroObjectAbstract.isEmpty()) {
                                ConsoleHelper.isNull(macroObjectAbstract);
                                return;
                            }
                            pullMicroObjectDialog(macroObjectAbstract);
                            break;
                        }
                        changeMicroObjectDialog(creature, entity);
                        break;
                    }
                }
            }
        }, MouseButton.SECONDARY);
    }

    public void setBufferLabel(String bufferLabelText) {
        try {
            FXGL.getGameScene().removeUINode(bufferLabel);
        } catch (Exception ignored) {
        }
        bufferLabel = new Label(bufferLabelText);
        bufferLabel.setFont(font);
        bufferLabel.setTextFill(Color.LIGHTGREEN);
        bufferLabel.setLayoutX(0);
        bufferLabel.setLayoutY(0);
        FXGL.getGameScene().addUINode(bufferLabel);
    }

    public void setAmountOfActiveMicroObjects() {
        try {
            FXGL.getGameScene().removeUINode(amountOfActiveMicroObjects);
        } catch (Exception ignored) {
        }
        amountOfActiveMicroObjects = new Label("Кількість активних мікрообєктів: " + amountOfActive + ".");
        amountOfActiveMicroObjects.setFont(font);
        amountOfActiveMicroObjects.setTextFill(Color.LIGHTGREEN);
        amountOfActiveMicroObjects.setLayoutX(0);
        amountOfActiveMicroObjects.setLayoutY(30);
        FXGL.getGameScene().addUINode(amountOfActiveMicroObjects);
    }

    public void updateListOfActiveMicroObjects(Creature creature) {
        listOfActiveMicroObjects.forEach(FXGL.getGameScene()::removeUINode);
        if (creature.isActive()) {
            int sizeOfList = listOfActiveMicroObjects.size();
            if (sizeOfList < 5) {
                switch (listOfActiveMicroObjects.size()) {
                    case 0 -> {
                        listOfActiveMicroObjects.add(new Label(creature.toString()));
                        Label l = listOfActiveMicroObjects.getFirst();
                        l.setLayoutY(50);
                    }
                    case 1 -> {
                        listOfActiveMicroObjects.add(new Label(creature.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(70);
                    }
                    case 2 -> {
                        listOfActiveMicroObjects.add(new Label(creature.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(90);
                    }
                    case 3 -> {
                        listOfActiveMicroObjects.add(new Label(creature.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(110);
                    }
                    case 4 -> {
                        listOfActiveMicroObjects.add(new Label(creature.toString()));
                        Label l = listOfActiveMicroObjects.get(sizeOfList);
                        l.setLayoutY(130);
                    }
                }
            } else {
                listOfActiveMicroObjects.removeLast();
                listOfActiveMicroObjects.addFirst(new Label(creature.toString()));
                Label l = listOfActiveMicroObjects.getFirst();
                l.setLayoutY(50);
                for (int i = 1; i < 5; i++) {
                    listOfActiveMicroObjects.get(i).setLayoutY(50 + 20 * i);
                }
            }
        } else {
            ArrayList<String> whatInsideInListOfActiveMicroObjects = new ArrayList<>();
            for (Label listOfActiveMicroObject : listOfActiveMicroObjects) {
                whatInsideInListOfActiveMicroObjects.add(listOfActiveMicroObject.getText());
            }
            int indexOfFound = whatInsideInListOfActiveMicroObjects.indexOf(creature.toString());
            if (indexOfFound != -1) {
                listOfActiveMicroObjects.remove(listOfActiveMicroObjects.get(indexOfFound));
            }
            if (!listOfActiveMicroObjects.isEmpty()) for (int i = 0; i < listOfActiveMicroObjects.size(); i++) {
                listOfActiveMicroObjects.get(i).setLayoutY(50 + 20 * i);
            }
        }
        listOfActiveMicroObjects.forEach(label -> {
            label.setLayoutX(0);
            label.setTextFill(Color.LIGHTGREEN);
            label.setFont(font);
            FXGL.getGameScene().addUINode(label);
        });
    }

    private void clearListOfActiveMicroObjects() {
        listOfActiveMicroObjects.forEach(FXGL.getGameScene()::removeUINode);
        listOfActiveMicroObjects.clear();
    }

    public void stopAllActiveMicroObjects() {
        List<Entity> entityList = FXGL.getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                .toList();
        for (Entity entity : entityList) {
            Creature creature = entity.getComponents().stream()
                    .filter(Creature.class::isInstance) // Перевіряємо, чи компонент є Creature
                    .map(Creature.class::cast).findFirst().orElse(null); // Перетворюємо компонент у Creature
            if (creature != null && creature.isActive()) {
                creature.stop();
            }
        }
    }

    private Creature showCreateCreatureDialog() {
        // Отримаємо місце знаходження миші
        //Point2D mouseLocation = FXGL.getInput().getMousePositionUI();

        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Створити новий мікрообєкт");
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
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

        // Приклад RadioButton ()
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
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            int healt = health.getValue();
            double arm = armor.getValue();
            double max = maxWeight.getValue();
            double exper = experience.getValue();
            int sped = speed.getValue();
            typeOfMicroObject type = typeChoice.getValue();
            boolean isActive = activeBox.isSelected();
            boolean patternYesSelected = patternYes.isSelected();

            Creature creature;

            switch (type) {
                case SOLDIER -> {
                    if (patternYesSelected) creature = new Soldier();
                    else
                        creature = new Soldier(name, healt, arm, Inventory.getInventory(max, new AKM()), exper, x, y, sped);
                }
                case CULTIST -> {
                    if (patternYesSelected) creature = new Cultist();
                    else
                        creature = new Cultist(name, healt, arm, Inventory.getInventory(max, new AKM()), exper, x, y, sped);
                }

                default -> {
                    if (patternYesSelected) creature = new Recruit();
                    else
                        creature = new Recruit(name, healt, arm, Inventory.getInventory(max, new AKM()), exper, x, y, sped);
                }
            }

            // Створюємо нову сутність через new Entity(...)
            Entity newEntity = FXGL.entityBuilder()
                    .at(new Point2D(x, y))
                    .with(creature) // Додаємо клонований компонент
                    .build();
            newEntity.getBoundingBoxComponent().addHitBox(new HitBox(
                    new Point2D(0, -4), // Зміщення хитбоксу (всередину спрайта)
                    BoundingShape.box(31, 85) // Розмір хитбоксу
            ));

            // Наприклад, додаємо властивості через компонент чи проперти (необов’язково)
            creature.setActive(isActive);  // встановлюємо активність

            // Додаємо істоту у світ гри
            FXGL.getGameWorld().addEntity(newEntity);
            return creature;
        }
        return null;
    }

    private void changeMicroObjectDialog(Creature creature, Entity entity) {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Змінити мікроОбєкт " + creature);
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // Назва істоти
        TextField nameField = new TextField();
        nameField.setPromptText(creature.getCreatureName());
        grid.add(new Label("Ім'я:"), 0, 0);
        grid.add(nameField, 1, 0);

        //Поля координат
        Spinner<Integer> xField = new Spinner<>(0, WIDTH, (int) entity.getX());
        Spinner<Integer> yField = new Spinner<>(0, HEIGHT, (int) entity.getY());
        grid.add(new Label("X:"), 0, 1);
        grid.add(xField, 1, 1);
        grid.add(new Label("Y:"), 0, 2);
        grid.add(yField, 1, 2);

        // Кількість здоров'я
        Spinner<Integer> health = new Spinner<>(0, 10000, creature.getHealth());
        grid.add(new Label("Health:"), 0, 3);
        grid.add(health, 1, 3);

        // Кількість броні
        Spinner<Double> armor = new Spinner<>(0, 1000, creature.getArmor(), 0.1);
        grid.add(new Label("Armor:"), 0, 4);
        grid.add(armor, 1, 4);


        // Кількість досвіду об'єкта
        Spinner<Double> experience = new Spinner<>(0, 1000, creature.getExperiencePoint(), 0.1);
        grid.add(new Label("experience:"), 0, 5);
        grid.add(experience, 1, 5);

        // Активність (CheckBox)
        CheckBox activeBox = new CheckBox("Активний");
        activeBox.setSelected(creature.isActive());
        grid.add(activeBox, 1, 6);

        //Встановлюємо положення на положення миші, діалогу
        Point position = getNormalPositionForDialog(300, 400);
        dialog.setX(position.getX());
        dialog.setY(position.getY());


        dialog.getDialogPane().setContent(grid);

        // Обробка результату діалогу

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().isEmpty() ? creature.getCreatureName() : nameField.getText().trim();
            int newX = xField.getValue();
            int newY = yField.getValue();
            int healt = health.getValue();
            double arm = armor.getValue();
            double exper = experience.getValue();
            boolean isActive = activeBox.isSelected();

            boolean isHaveChanged = false;

            StringBuilder changes = new StringBuilder("Зміни в " + creature + ": \n ");
            if (!creature.getCreatureName().equals(name)) {
                changes.append("Імя змінено на ").append(name).append(" \n ");
                isHaveChanged = true;
            }
            if (!(creature.getX() == newX)) {
                changes.append("Переміщено на x: ").append(newX).append(" \n ");
                isHaveChanged = true;
            }
            if (!(creature.getY() == newY)) {
                changes.append("Переміщено на y: ").append(newY).append(" \n ");
                isHaveChanged = true;
            }
            if (!(creature.getHealth() == healt)) {
                changes.append("Кількість здоровя ").append("змінено на ").append(healt).append(" \n ");
                isHaveChanged = true;
            }
            if (!(creature.getArmor() == arm)) {
                changes.append("Кількість броні змінено на ").append(arm).append(" \n ");
                isHaveChanged = true;
            }
            if (!(creature.getExperiencePoint() == exper)) {
                changes.append("Кількість досвіду змінено на ").append(exper).append(" \n ");
                isHaveChanged = true;
            }
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            physics.overwritePosition(new Point2D(newX, newY));

            if (isHaveChanged) ConsoleHelper.writeMessageInLabelInRightCorner(changes.toString(), 10,WIDTH,HEIGHT);
            creature.setCreatureName(name);
            creature.setHealth(healt);
            creature.setArmor(arm);
            creature.setExperiencePoint(exper);
            creature.setActive(isActive);  // встановлюємо активність
        }
    }

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
        ObservableList<Creature> observableCreatures = FXCollections.observableArrayList(macroObjectAbstract.getCreatures());
        ListView<Creature> listView = getListCreatureView(observableCreatures);

        grid.add(listView, 1, 0);

        listView.setMaxSize(150, 200);

        //Встановлюємо положення на положення миші, діалогу

        Point position = getNormalPositionForDialog(300, 375);
        dialog.setX(position.getX());
        dialog.setY(position.getY());

        dialog.getDialogPane().setContent(grid);

        // Обробка результату діалогу
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int selectedCreature = listView.getSelectionModel().getSelectedIndex();
            if (selectedCreature < 0) return;
            macroObjectAbstract.pullCreature(selectedCreature);
        }
    }

    @NotNull
    private static ListView<Creature> getListCreatureView(ObservableList<Creature> observableCreatures) {
        ListView<Creature> listView = new ListView<>();
        listView.setItems(observableCreatures);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Creature creature, boolean empty) {
                super.updateItem(creature, empty);

                // Якщо рядок порожній або об’єкт null, нічого не показуємо
                if (empty || creature == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Створюємо контейнер-рядок
                    HBox row = new HBox(10); // 10px відстань між елементами
                    row.setAlignment(Pos.CENTER_LEFT);

                    // Назва істоти
                    Text nameText = new Text(creature.toString());
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
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.MACROOBJECT, EntityType.ENEMY) {

            @Override
            protected void onCollision(Entity macroObject, Entity microObject) {
                Creature microObjectComponent = microObject.getComponents().stream()
                        .filter(Creature.class::isInstance)
                        .map(Creature.class::cast)
                        .findFirst()
                        .orElse(null);
                MacroObjectAbstract macroObjectComponent = macroObject.getComponents().stream()
                        .filter(MacroObjectAbstract.class::isInstance)
                        .map(MacroObjectAbstract.class::cast)
                        .findFirst()
                        .orElse(null);
                assert macroObjectComponent != null;
                assert microObjectComponent != null;
                macroObjectComponent.addCreature(microObjectComponent);
                microObject.removeFromWorld();
                ConsoleHelper.writeMessageInLabelInRightCorner(microObjectComponent + " увійшов в " + macroObjectComponent, 8,WIDTH,HEIGHT);
            }
        });
    }

    /**
     * @param width ширина вікна діалогу
     * @param height висота вікна діалогу
     */
    private static Point getNormalPositionForDialog(int width, int height) {
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
