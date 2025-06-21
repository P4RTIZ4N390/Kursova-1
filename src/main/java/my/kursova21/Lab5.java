package my.kursova21;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.TriggerComponent;
import model.objects.EntityType;
import model.objects.macroobjects.*;
import model.objects.microobjects.*;
import model.objects.microobjects.behaviour.Task;
import model.objects.microobjects.behaviour.TypeOfTask;
import model.objects.microobjects.behaviour.RecruitAIComponent;
import model.objects.nanoobjects.bullets.Bullet;
import org.jetbrains.annotations.NotNull;
import utilies.ConsoleHelper;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static utilies.ConsoleHelper.smallFont;

public class Lab5 extends Lab4 {
    @Override
    protected void initSettings(GameSettings gameSettings) {
        super.initSettings(gameSettings);
        gameSettings.setTitle("Lab5");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initGame() {
        super.initGame();

        Recruit recruit = new Recruit(0, 0);
        Entity entity = recruit.getNewEntity();
        //   FXGL.getGameWorld().addEntity(entity);
        Soldier soldier = new Soldier(800, 20);
        Entity entity1 = soldier.getNewEntity();
        FXGL.getGameWorld().addEntity(entity1);
        Cultist cultist = new Cultist(0, 0);
        Entity entity2 = cultist.getNewEntity();
        FXGL.getGameWorld().addEntity(entity2);
        //recruit.addCommand(Command.getAttackTask(soldier, (short) 5));
        drawDebugGrid(RecruitAIComponent.aStarGrid,16);
    }

    @Override
    protected void setupControls() {
        super.setupControls();
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Search with parameters") {
            @Override
            protected void onActionBegin() {
                ListView<MicroObjectAbstract> list = getListMicroObjectLongNameView(FXCollections.observableList(searchDialogWithParametersAndResult()));
                if (list.getItems().isEmpty()) {
                    return;
                }
                workWithSearchResult(list);
                super.onActionBegin();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Search with macroObject") {
            @Override
            protected void onActionBegin() {
                ListView<MicroObjectAbstract> list = getListMicroObjectLongNameView(FXCollections.observableList(searchDialogWithMacroObjectAndResult()));
                if (list.getItems().isEmpty()) {
                    return;
                }
                workWithSearchResult(list);
                super.onActionBegin();
            }
        }, KeyCode.S, InputModifier.CTRL);

        input.addAction(new UserAction("Work with AllMicroObjects") {
            @Override
            protected void onActionBegin() {
                workWithSearchResult(getListMicroObjectLongNameView(FXCollections.observableList(getAllMicroObjectsToWork())));
                super.onActionBegin();
            }
        },KeyCode.A);
    }

    private List<MicroObjectAbstract> searchDialogWithParametersAndResult() {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Пошук мікроОбєктів");
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Пошук", ButtonBar.ButtonData.YES), new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE));

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(10);
        //Відступи між елементами
        grid.setPadding(new Insets(15));

        TextField nameField = new TextField();
        nameField.setPromptText("Ім'я шуканого мікроОбєкта :");
        grid.add(nameField, 0, 0);

        nameField.setPrefSize(170, 25);

        // Тип шуканого мікроОбєкт (ChoiceBox з enum)
        ChoiceBox<typeOfMicroObject> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll(typeOfMicroObject.MICRO_OBJECT, typeOfMicroObject.RECRUIT, typeOfMicroObject.SOLDIER, typeOfMicroObject.CULTIST);
        typeChoice.setValue(typeOfMicroObject.MICRO_OBJECT);
        grid.add(new Label("Тип шуканого мікроОбєкта :"), 0, 2);
        grid.add(typeChoice, 1, 2);

        // Активність шуканого мікроОбєкт (CheckBox)
        CheckBox activeBox = new CheckBox("Активний");
        activeBox.setSelected(false);
        grid.add(activeBox, 0, 3);

        dialog.getDialogPane().setContent(grid);

        // Встановлення розміру вікна діалогу
        dialog.getDialogPane().setPrefSize(330, 165);

        // Обробка результату діалогу

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.get().getButtonData().equals(ButtonBar.ButtonData.CANCEL_CLOSE)) {
            return new ArrayList<>();
        }

        List<MicroObjectAbstract> allMicroObjects = new ArrayList<>(FXGL.getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                .flatMap(e -> e.getComponents().stream())                                // Розгортаємо всі компоненти кожної сутності
                .filter(MicroObjectAbstract.class::isInstance)                            // Фільтруємо лише ті, що є MicroObjectAbstract
                .map(MicroObjectAbstract.class::cast)
                .toList());

        List<MacroObjectAbstract> allMacroObjects = FXGL.getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                .flatMap(e -> e.getComponents().stream())                                // Розгортаємо всі компоненти кожної сутності
                .filter(MacroObjectAbstract.class::isInstance)                            // Фільтруємо лише ті, що є MacroObjectAbstract
                .map(MacroObjectAbstract.class::cast)
                .toList();

        List<MicroObjectAbstract> microObjectAbstractsInMacroObjects = new ArrayList<>();
        allMacroObjects.forEach(macroObjectAbstract -> microObjectAbstractsInMacroObjects.addAll(macroObjectAbstract.getCreatures()));

        allMicroObjects.addAll(microObjectAbstractsInMacroObjects);

        String name = nameField.getText();

        if (!name.isEmpty())
            allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getCreatureName().equalsIgnoreCase(name))
                    .toList();

        switch (typeChoice.getValue()) {
            case RECRUIT -> allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getClass().equals(Recruit.class))
                    .toList();
            case SOLDIER -> allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getClass().equals(Soldier.class))
                    .toList();
            case CULTIST -> allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getClass().equals(Cultist.class))
                    .toList();
        }
        if (activeBox.isSelected()) {
            allMicroObjects = allMicroObjects.stream()
                    .filter(MicroObjectAbstract::isActive)
                    .toList();
        }
        return allMicroObjects;
    }

    private List<MicroObjectAbstract> searchDialogWithMacroObjectAndResult() {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Пошук мікроОбєктів,за макрооб'єктом");
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Пошук", ButtonBar.ButtonData.YES), new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE));

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(10);
        //Відступи між елементами
        grid.setPadding(new Insets(15));

        // Належність до макроОбєкта шуканого мікроОбєкт (ChoiceBox з enum)
        ChoiceBox<typeOfMacroObject> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll(typeOfMacroObject.CAVE, typeOfMacroObject.CRYPT, typeOfMacroObject.DORMITORY, typeOfMacroObject.UNIVERSAL);
        typeChoice.setValue(typeOfMacroObject.UNIVERSAL);
        grid.add(new Label("Належність до макрооб'єкта :"), 0, 1);
        grid.add(typeChoice, 1, 1);

        // Активність шуканого мікроОбєкт (CheckBox)
        CheckBox activeBox = new CheckBox("Активний");
        activeBox.setSelected(false);
        grid.add(activeBox, 0, 2);

        dialog.getDialogPane().setContent(grid);

        // Встановлення розміру вікна діалогу
        dialog.getDialogPane().setPrefSize(330, 165);

        // Обробка результату діалогу

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.get() == ButtonType.CANCEL) {
            return new ArrayList<>();
        }

        List<MicroObjectAbstract> allMicroObjects = getAllMicroObjectsToWork();

        switch (typeChoice.getValue()) {
            case CAVE -> allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getMacroObjectAbstract() instanceof Cave)
                    .toList();
            case CRYPT -> allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getMacroObjectAbstract() instanceof Crypt)
                    .toList();
            case DORMITORY -> allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getMacroObjectAbstract() instanceof Dormitory)
                    .toList();
            default -> allMicroObjects = allMicroObjects.stream()
                    .filter(m -> m.getMacroObjectAbstract() == null)
                    .toList();
        }
        if (activeBox.isSelected()) {
            allMicroObjects = allMicroObjects.stream()
                    .filter(MicroObjectAbstract::isActive)
                    .toList();
        }
        return allMicroObjects;
    }

    protected static List<MicroObjectAbstract> getAllMicroObjectsToWork() {

        List<MicroObjectAbstract> allMicroObjects = new ArrayList<>(FXGL.getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                .flatMap(e -> e.getComponents().stream())                                // Розгортаємо всі компоненти кожної сутності
                .filter(MicroObjectAbstract.class::isInstance)                            // Фільтруємо лише ті, що є MicroObjectAbstract
                .map(MicroObjectAbstract.class::cast)
                .toList());

        List<MacroObjectAbstract> allMacroObjects = FXGL.getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                .flatMap(e -> e.getComponents().stream())                                // Розгортаємо всі компоненти кожної сутності
                .filter(MacroObjectAbstract.class::isInstance)                            // Фільтруємо лише ті, що є MacroObjectAbstract
                .map(MacroObjectAbstract.class::cast)
                .toList();

        List<MicroObjectAbstract> microObjectAbstractsInMacroObjects = new ArrayList<>();
        allMacroObjects.forEach(macroObjectAbstract -> microObjectAbstractsInMacroObjects.addAll(macroObjectAbstract.getCreatures()));

        allMicroObjects.addAll(microObjectAbstractsInMacroObjects);

        return allMicroObjects;
    }



    private void workWithSearchResult(ListView<MicroObjectAbstract> microObjectListView) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Результати пошуку");

        // Основні кнопки
        ButtonType pullBtn = new ButtonType("Витягнути з макроОб'єкта", ButtonBar.ButtonData.OTHER);
        ButtonType editBtn = new ButtonType("Змінити", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(pullBtn, editBtn, ButtonType.CANCEL);

        // Список
        microObjectListView.setPrefSize(450, 200);

        // Основна сітка
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(microObjectListView, 0, 0, 2, 1); // На весь рядок

        // Нижній рядок кнопок
        Button sortBtn = new Button("Сортувати за критеріям");
        sortBtn.setOnAction(e -> sortByCriteriaDialog(microObjectListView));

        Button addTaskBtn = callDialogForAddTask(microObjectListView); // вже готова кнопка

        HBox bottomButtons = new HBox(10, sortBtn, addTaskBtn);
        bottomButtons.setAlignment(Pos.CENTER_RIGHT);
        grid.add(bottomButtons, 0, 1, 2, 1); // на всю ширину

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;

        MicroObjectAbstract selected = microObjectListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (result.get() == pullBtn) {
            MacroObjectAbstract macro = selected.getMacroObjectAbstract();
            if (macro == null) {
                ConsoleHelper.writeMessageInLabelInRightCorner("МікроОб'єкт не належить макроОб'єкту.", 7, WIDTH, HEIGHT);
                return;
            }
            macro.pullCreature(selected);
        }

        if (result.get() == editBtn) {
            changeMicroObjectDialog(selected, selected.getEntity(), null);
        }
    }


    private void sortByCriteriaDialog(ListView<MicroObjectAbstract> microObjectAbstracts) {
        List<MicroObjectAbstract> result = new ArrayList<>(microObjectAbstracts.getItems());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Сортувати за ");
        ButtonType sortBtn = new ButtonType("Сортувати", ButtonBar.ButtonData.OTHER);
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(sortBtn, new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE));

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(10);
        //Відступи між елементами
        grid.setPadding(new Insets(15));

        List<String> criteria = new ArrayList<>();
        Collections.addAll(criteria, "За здоров'ям", "За бронею", "За назвою");

        ChoiceBox<String> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll(criteria);
        typeChoice.setValue(criteria.getFirst());
        grid.add(new Label("Критерій :"), 0, 1);
        grid.add(typeChoice, 1, 1);

        dialog.getDialogPane().setContent(grid);
        // Обробка результату діалогу

        Optional<ButtonType> resultOfDialog = dialog.showAndWait();

        if (resultOfDialog.get() == ButtonType.CANCEL) {
            workWithSearchResult(microObjectAbstracts);
            return;
        }

        int selectedCriteria = criteria.indexOf(typeChoice.getValue());

        switch (selectedCriteria) {
            case 0: {
                result.sort(MicroObjectAbstract::compareToHealth);
                break;
            }
            case 1: {
                result.sort(MicroObjectAbstract::compareToArmor);
                break;
            }
            case 2: {
                result.sort(MicroObjectAbstract::compareToName);
                break;
            }
        }

        microObjectAbstracts.setItems(FXCollections.observableList(result));
    }

    private static ListView<MacroObjectAbstract> getListViewMacroObjects(ListView<MicroObjectAbstract> microObjectListView) {
        List<MacroObjectAbstract> macroObjects = new ArrayList<>(microObjectListView.getItems()
                .stream()
                .map(MicroObjectAbstract::getMacroObjectAbstract)
                .toList());
        return getListMacroObjectView(FXCollections.observableList(macroObjects));
    }

    protected static ListView<MacroObjectAbstract> getListMacroObjectView(@NotNull ObservableList<MacroObjectAbstract> observableMacroObjectAbstracts) {

        ListView<MacroObjectAbstract> listView = new ListView<>();
        listView.setItems(observableMacroObjectAbstracts);

        // Переробляємо фабрику контейнерів
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MacroObjectAbstract macroObjectAbstract, boolean empty) {
                super.updateItem(macroObjectAbstract, empty);

                if (empty) return;

                // Назва істоти
                Text nameText = new Text(macroObjectAbstract == null ? "Не належить" : macroObjectAbstract.toString());
                nameText.setFont(smallFont);
                nameText.setFill(Color.LIGHTGREEN);


                // Створюємо контейнер-рядок
                HBox row = new HBox(10); // 10px відстань між елементами
                row.setAlignment(Pos.CENTER_LEFT);

                // Додаємо все в HBox
                row.getChildren().addAll(nameText);

                // Встановлюємо графіку замість простого тексту
                setGraphic(row);

            }
        });
        return listView;
    }

    /**
     * @param observableMicroObjectAbstracts список мікроОбєктів
     * @return список перегляду мікроОбєктів
     */
    protected static ListView<MicroObjectAbstract> getListMicroObjectLongNameView(@NotNull ObservableList<MicroObjectAbstract> observableMicroObjectAbstracts) {

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
                    Text nameText = new Text(microObjectAbstract + "     " + microObjectAbstract.getWhereMicroObject() + "            " + microObjectAbstract.getMacroObjectAbstract());
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

    /**
     * @param observableMicroObjectAbstracts список мікроОбєктів
     * @return список перегляду мікроОбєктів
     */
    protected static ListView<MicroObjectAbstract> getListMicroObjectShortNameView(@NotNull ObservableList<MicroObjectAbstract> observableMicroObjectAbstracts) {

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
                    Text nameText = new Text(microObjectAbstract + "            " + microObjectAbstract.getWhereMicroObject());
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
        FXGL.getPhysicsWorld().setGravity(0,0);
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BULLET, EntityType.MICROOBJECT) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {


                Bullet bulletComponent = bullet.getComponents().stream()
                        .filter(Bullet.class::isInstance)
                        .map(Bullet.class::cast)
                        .findFirst()
                        .orElse(null);
                Recruit enemyComponent = enemy.getComponents().stream()
                        .filter(Recruit.class::isInstance)
                        .map(Recruit.class::cast)
                        .findFirst()
                        .orElse(null);

                assert bulletComponent != null;
                assert enemyComponent != null;
                if (bulletComponent.getShooter().equals(enemyComponent)) return;
                enemyComponent.getDamage(bulletComponent.getAttackDamage(), bulletComponent.getShooter()); // Наносимо шкоду
                bullet.removeFromWorld(); // Видаляємо кулю
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.MICROOBJECT, EntityType.MICROOBJECT) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                Recruit recruit1 = bullet.getComponents().stream()
                        .filter(Recruit.class::isInstance)
                        .map(Recruit.class::cast)
                        .findFirst()
                        .orElse(null);
                Recruit recruit2 = enemy.getComponents().stream()
                        .filter(Recruit.class::isInstance)
                        .map(Recruit.class::cast)
                        .findFirst()
                        .orElse(null);

                assert recruit1 != null;
                assert recruit2 != null;

                FXGL.runOnce(()->{
                    recruit2.stop();
                    recruit1.stop();
                }, Duration.seconds(0.08));
            }

            @Override
            protected void onCollisionEnd(Entity bullet, Entity enemy) {
                Recruit recruit1 = bullet.getComponents().stream()
                        .filter(Recruit.class::isInstance)
                        .map(Recruit.class::cast)
                        .findFirst()
                        .orElse(null);
                Recruit recruit2 = enemy.getComponents().stream()
                        .filter(Recruit.class::isInstance)
                        .map(Recruit.class::cast)
                        .findFirst()
                        .orElse(null);

                assert recruit1 != null;
                assert recruit2 != null;

                FXGL.runOnce(()->{
                    recruit2.stop();
                    recruit1.stop();
                }, Duration.seconds(0.08));
            }
        });
        super.initPhysics();
    }

    protected Button callDialogForAddTask(ListView<MicroObjectAbstract> microObjectListView) {
        Button addButton = new Button("Додати завдання");
        addButton.setOnAction(e -> {
            try {
                createAddTaskDialog(microObjectListView.getSelectionModel().getSelectedItem(), microObjectListView);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        return addButton;
    }

    protected static void createAddTaskDialog(@NotNull MicroObjectAbstract microObjectAbstract, ListView<MicroObjectAbstract> microObjectListView) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Додати завдання для " + microObjectAbstract);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        ChoiceBox<TypeOfTask> choice = new ChoiceBox<>(FXCollections.observableArrayList(TypeOfTask.values()));
        choice.setValue(TypeOfTask.MOVE);
        grid.add(new Label("Тип завдання:"), 0, 0);
        grid.add(choice, 1, 0);

        Spinner<Integer> priority = new Spinner<>(0, 10000, 1);
        grid.add(new Label("Пріоритет :"), 0, 1);
        grid.add(priority, 1, 1);

        VBox dynamicBox = new VBox(8);
        grid.add(dynamicBox, 0, 2, 2, 1);

        ObservableList<MicroObjectAbstract> targets = FXCollections.observableArrayList(
                microObjectListView.getItems().stream()
                        .filter(m -> !m.isInMacroObject() && m != microObjectAbstract)
                        .toList()
        );
        ListView<MicroObjectAbstract> attackLv = getListMicroObjectShortNameView(targets);

        attackLv.setPrefSize(400, 250);

        ListView<MacroObjectAbstract> macroObjectAbstractListView = getListMacroObjectView(FXCollections.observableArrayList(
                FXGL.getGameWorld().getEntities().stream()
                        .flatMap(entity -> entity.getComponents()
                                .stream().filter(MacroObjectAbstract.class::isInstance)
                                .map(MacroObjectAbstract.class::cast)).toList()
        ));

        AtomicInteger xPos = new AtomicInteger(microObjectAbstract.getX());
        AtomicInteger yPos = new AtomicInteger(microObjectAbstract.getY());

        grid.setPrefSize(400, 300);

        Spinner<Integer> xSpinner = new Spinner<>(0, WIDTH, xPos.get());
        Spinner<Integer> ySpinner = new Spinner<>(0, HEIGHT, yPos.get());
        xSpinner.setEditable(true);
        ySpinner.setEditable(true);
        // Функція, яка малює в dynamicBox потрібні контролери
        Runnable rebuild = () -> {
            xPos.set(xSpinner.getValue());
            yPos.set(ySpinner.getValue());
            dynamicBox.getChildren().clear();
            switch (choice.getValue()) {
                case MOVE -> dynamicBox.getChildren().addAll(
                        new HBox(5, new Label("X:"), xSpinner),
                        new HBox(5, new Label("Y:"), ySpinner)
                );

                case ATTACK,DEFENSE -> dynamicBox.getChildren().addAll(new Label("Ціль:"), attackLv);


                case MOVE_TO_MACROOBJECT ->{
                    xPos.set(xSpinner.getValue());
                    yPos.set(ySpinner.getValue());
                    dynamicBox.getChildren().addAll(new Label("Ціль:"), macroObjectAbstractListView);
                }
            }

        };

        // Відмалювати початкові поля
        rebuild.run();

        // Слухач на зміну типу
        choice.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> rebuild.run());

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> resultOfDialog = dialog.showAndWait();

        if (resultOfDialog.get() == ButtonType.CANCEL) return;

        short priorityValue =Short.parseShort(String.valueOf(priority.getValue()));

        switch (choice.getValue()) {
            case MOVE -> microObjectAbstract.addTask(Task.getMoveToTask(new Point2D(xSpinner.getValue(),ySpinner.getValue()),priorityValue));

            case ATTACK -> microObjectAbstract.addTask(Task.getAttackTask(attackLv.getSelectionModel().getSelectedItem(), priorityValue));

            case DEFENSE -> microObjectAbstract.addTask(Task.getDefenseTask(attackLv.getSelectionModel().getSelectedItem(),priorityValue));

            case MOVE_TO_MACROOBJECT -> microObjectAbstract.addTask(Task.getMoveToMacroObjectTask(macroObjectAbstractListView.getSelectionModel().getSelectedItem(),priorityValue));
        }
    }

    /**
     * Накладає візуалізацію сітки на екран.
     *
     * @param grid     – твоя AStarGrid
     * @param cellSize – розмір клітинки в пікселях
     */
    public void drawDebugGrid(AStarGrid grid, int cellSize) {
        int cols = grid.getWidth();   // кількість клітин по X
        int rows = grid.getHeight();  // кількість клітин по Y

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                CellState state = grid.get(x, y).getState();

                Rectangle rect = new Rectangle(cellSize, cellSize);
                // рамка
                rect.setStroke(Color.color(1, 1, 1, 0.2));
                rect.setStrokeWidth(1);

                // заповнення залежно від стану
                if (state == CellState.WALKABLE) {
                    rect.setFill(Color.color(0, 1, 0, 0.1));  // прозорий зелений
                } else {
                    rect.setFill(Color.color(1, 0, 0, 0.1));  // прозорий червоний
                }

                // ставимо в правильну позицію
                rect.setTranslateX(x * cellSize);
                rect.setTranslateY(y * cellSize);

                // додаємо у UI шар сцени
                FXGL.getGameScene().addUINode(rect);
            }
        }
    }
}
