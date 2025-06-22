package my.kursova21;

import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.TriggerComponent;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.microobjects.MicroObjectAbstract;
import model.objects.microobjects.behaviour.RecruitAIComponent;
import model.objects.minimap.MiniMap;
import org.jetbrains.annotations.NotNull;
import utilies.ConsoleHelper;
import utilies.ImageLoader;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class Lab6 extends Lab5 {
    private static final double EDGE_THRESHOLD = 30;  // відстань до краю екрана в пікселях
    private static final double CAM_SPEED = 400;      // швидкість переміщення камери (пікселів за сек)
    public static final int MAP_WIDTH  = 8000;
    public static final int MAP_HEIGHT = 8000;

    @Override
    protected void onUpdate(double tpf) {
        Viewport viewport = FXGL.getGameScene().getViewport();
        double mouseX  = FXGL.getInput().getMouseXUI();
        double mouseY  = FXGL.getInput().getMouseYUI();
        double screenW = getAppWidth();
        double screenH = getAppHeight();

        // Обчислюємо зсув
        double dx = 0, dy = 0;

        if (mouseX < EDGE_THRESHOLD) {
            dx = -CAM_SPEED * tpf;
        } else if (mouseX > screenW - EDGE_THRESHOLD) {
            dx = CAM_SPEED * tpf;
        }

        if (mouseY < EDGE_THRESHOLD) {
            dy = -CAM_SPEED * tpf;
        } else if (mouseY > screenH - EDGE_THRESHOLD) {
            dy = CAM_SPEED * tpf;
        }

        // Нові координати камери
        double newX = viewport.getX() + dx;
        double newY = viewport.getY() + dy;

        // Пониження до [0 .. MAP_WIDTH - screenW], [0 .. MAP_HEIGHT - screenH]
        newX = Math.max(0, Math.min(newX, MAP_WIDTH  - screenW));
        newY = Math.max(0, Math.min(newY, MAP_HEIGHT - screenH));

        viewport.setX(newX);
        viewport.setY(newY);
        miniMap.update();
    }



    @Override
    protected void setupControls() {
        super.setupControls();
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Save object") {
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
                        if (microObjectAbstract == null) {
                            MacroObjectAbstract macroObjectAbstract = entity.getComponents().stream()// Перевіряємо, чи компонент є MacroObjectAbstract
                                    .filter(MacroObjectAbstract.class::isInstance) // Перетворюємо компонент у MacroObjectAbstract
                                    .map(MacroObjectAbstract.class::cast).findFirst().orElse(null);
                        }
                        saveMicroObject(microObjectAbstract);
                        break;
                    }
                }
                super.onActionBegin();
            }
        }, KeyCode.L);
        input.addAction(new UserAction("Object on screen") {
            @Override
            protected void onActionBegin() {
                countObjectsOnScreen();
                super.onActionBegin();
            }
        }, KeyCode.U);

    }

    @Override
    protected void initGame() {
        super.initGame();
        //drawDebugGrid(RecruitAIComponent.aStarGrid,32);
        FXGL.entityBuilder()
                // створюємо Rectangle розміром levelWidth x levelHeight
                .view(ImageLoader.loadExternalTexture("src/main/resources/assets/textures/uni.png"))
                .with(new IrremovableComponent())
                .zIndex(-100).buildAndAttach();
    }

    private MiniMap miniMap;

    @Override
    protected void initUI() {
        super.initUI();

        // Створюємо та додаємо мінікарту
        miniMap = new MiniMap();
        FXGL.getGameScene().addUINode(miniMap);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void saveMicroObject(MicroObjectAbstract microObject) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Зберегти обєкт");

        // Додаємо кнопки "Пошук" і "Скасувати"
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Пошук", ButtonBar.ButtonData.YES),
                new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE)
        );

        // Створюємо вміст діалогу
        VBox content = new VBox(10);
        Label macroLabel = new Label("Назва макрооб’єкта:");
        TextField macroField = new TextField();
        Label fileLabel = new Label("Обраний файл:");
        TextField filePathField = new TextField();
        filePathField.setEditable(false); // Поле лише для відображення
        Button chooseFileButton = getButton(filePathField);

        content.getChildren().addAll(macroLabel, macroField, fileLabel, filePathField, chooseFileButton);
        dialog.getDialogPane().setContent(content);

        // Прив’язуємо діалог до Stage гри
        dialog.initOwner(FXGL.getPrimaryStage());

        Optional<ButtonType> result = dialog.showAndWait();
    }

    @NotNull
    private static Button getButton(TextField filePathField) {
        Button chooseFileButton = new Button("Вибрати файл");

        // Обробник для кнопки вибору файлу
        chooseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Виберіть файл");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Текстові файли", "*.txt")
            );
            File selectedFile = fileChooser.showOpenDialog(FXGL.getPrimaryStage());
            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });
        return chooseFileButton;
    }

    public void countObjectsOnScreen() {
        int microAmount=0;
        int macroAmount=0;
        Viewport viewport = FXGL.getGameScene().getViewport();
        List<Entity> entities=FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(viewport.getX(), viewport.getY(), viewport.getWidth(), viewport.getHeight()));
        for (Entity entity : entities) {
            if (entity.getComponents().stream().anyMatch(MicroObjectAbstract.class::isInstance)) {
                microAmount++;
            }
            if (entity.getComponents().stream().anyMatch(MacroObjectAbstract.class::isInstance)) {
                macroAmount++;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (macroAmount>0){
            stringBuilder.append("Кількість макроОбєктів на екрані:").append(macroAmount).append('.').append('\n');
        }
        if (macroAmount>0){
            stringBuilder.append("Кількість мікроОбєктів на екрані:").append(microAmount).append('.');
        }
        ConsoleHelper.writeMessageInLabelInRightCorner(stringBuilder.toString(),10,1920,1080);
    }
}
