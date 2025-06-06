package my.kursova21;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.UserAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.TriggerComponent;
import model.objects.macroobjects.*;
import model.objects.microobjects.*;
import org.jetbrains.annotations.NotNull;
import utilies.ConsoleHelper;

import java.awt.*;
import java.util.*;
import java.util.List;

import static utilies.ConsoleHelper.smallFont;

public class Lab5 extends Lab4{
    @Override
    protected void initSettings(GameSettings gameSettings) {
        super.initSettings(gameSettings);
        gameSettings.setTitle("Lab5");
    }

    public static void main(String[] args) {launch(args);}

    @Override
    protected void setupControls() {
        super.setupControls();
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Search with parameters") {
            @Override
            protected void onActionBegin() {
                ListView<MicroObjectAbstract> list = getListMicroObjectView(FXCollections.observableList(searchDialogWithParametersAndResult()));
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
                ListView<MicroObjectAbstract> list = getListMicroObjectView(FXCollections.observableList(searchDialogWithMacroObjectAndResult()));
                if (list.getItems().isEmpty()) {
                    return;
                }
                workWithSearchResult(list);
                super.onActionBegin();
            }
        }, KeyCode.S,InputModifier.CTRL );
    }

    private List<MicroObjectAbstract> searchDialogWithParametersAndResult() {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Пошук мікроОбєктів");
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Пошук", ButtonBar.ButtonData.YES),new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE) );

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(10);
        //Відступи між елементами
        grid.setPadding(new Insets(15));

        TextField nameField = new TextField();
        nameField.setPromptText("Ім'я шуканого мікроОбєкта :");
        grid.add(nameField, 0, 0);

        nameField.setPrefSize(170,25);

        // Тип шуканого мікроОбєкт (ChoiceBox з enum)
        ChoiceBox<typeOfMicroObject> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll(typeOfMicroObject.MICRO_OBJECT,typeOfMicroObject.RECRUIT, typeOfMicroObject.SOLDIER, typeOfMicroObject.CULTIST);
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

        if (result.get() == ButtonType.CANCEL) {
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
            allMicroObjects=allMicroObjects.stream()
                .filter(m->m.getCreatureName().equalsIgnoreCase(name))
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
        dialog.setTitle("Пошук мікроОбєктів");
        // Кнопки OK та Cancel
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Пошук", ButtonBar.ButtonData.YES),new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE) );

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(10);
        //Відступи між елементами
        grid.setPadding(new Insets(15));

        // Належність до макроОбєкта шуканого мікроОбєкт (ChoiceBox з enum)
        ChoiceBox<typeOfMacroObject> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll(typeOfMacroObject.CAVE,typeOfMacroObject.CRYPT,typeOfMacroObject.DORMITORY,typeOfMacroObject.UNIVERSAL);
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

    private void workWithSearchResult(ListView<MicroObjectAbstract> microObjectListView) {
        // Створюємо діалог
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Результати пошуку");
        // Кнопки OK та Cancel
        ButtonType pullBtn = new ButtonType("Витягнути з макроОб'єкта", ButtonBar.ButtonData.OTHER);
        ButtonType editBtn = new ButtonType("Змінити", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(pullBtn,editBtn,ButtonType.CANCEL);

        // Макет з полями введення
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(microObjectListView, 1, 0);

        // Встановлюємо максимальні розміри вікна списку
        microObjectListView.setPrefSize(450, 200);

        //Встановлюємо макет в діалог
        dialog.getDialogPane().setContent(grid);

        // Обробка результату діалогу
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.get() == ButtonType.CANCEL) {
            return;
        }

        MicroObjectAbstract selectedMicroObjectAbstract = microObjectListView.getSelectionModel().getSelectedItem();

        if (selectedMicroObjectAbstract == null) {
            return;
        }

        if (result.get() == pullBtn) {
            MacroObjectAbstract macroObjectAbstract = selectedMicroObjectAbstract.getMacroObjectAbstract();
            if (macroObjectAbstract == null){
                ConsoleHelper.writeMessageInLabelInRightCorner("МікроОб'єкт не належить макроОб'єкту.",7,WIDTH,HEIGHT);
                return;
            }
            macroObjectAbstract.pullCreature(selectedMicroObjectAbstract);
        }

        if (result.get() == editBtn){
            changeMicroObjectDialog(selectedMicroObjectAbstract,selectedMicroObjectAbstract.getEntity(),null);
        }

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
                Text nameText = new Text(macroObjectAbstract==null?"Не належить":macroObjectAbstract.toString());
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
                    Text nameText = new Text(microObjectAbstract+"            "+microObjectAbstract.getWhereMicroObject()+"            "+microObjectAbstract.getMacroObjectAbstract());
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


}
