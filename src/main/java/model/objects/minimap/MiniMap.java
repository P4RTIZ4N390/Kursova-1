package model.objects.minimap;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import model.objects.EntityType;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.microobjects.MicroObjectAbstract;

import static my.kursova21.Lab6.MAP_WIDTH;
import static my.kursova21.Lab6.MAP_HEIGHT;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppHeight;

public class MiniMap extends Pane {

    private static final double MINI_W = 200;
    private static final double MINI_H = 200;
    private static final double PADDING = 10;
    private static final double RADIUS_MICRO = 2;

    private final Group dotsGroup = new Group();
    private final Rectangle viewRect = new Rectangle();
    private final double scaleX = MINI_W  / MAP_WIDTH;
    private final double scaleY = MINI_H  / MAP_HEIGHT;

    public MiniMap() {
        // Розмір і стиль панелі
        setPrefSize(MINI_W, MINI_H);
        setStyle("-fx-border-color: black; -fx-background-color: beige;");
        setTranslateX(getAppWidth() - MINI_W - PADDING);
        setTranslateY(PADDING);

        // Накладання групи з крапками та прямокутником
        getChildren().addAll(dotsGroup, viewRect);

        // Налаштування прямокутника видимої зони
        viewRect.setStroke(Color.RED);
        viewRect.setFill(null);
        viewRect.setStrokeWidth(1);

        // Клік по мінікарті
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::onClick);
    }

    private void onClick(MouseEvent e) {
        double worldX = e.getX() / scaleX;
        double worldY = e.getY() / scaleY;
        Viewport vp = FXGL.getGameScene().getViewport();
        double halfW = getAppWidth()  / 2.0;
        double halfH = getAppHeight() / 2.0;

        double nx = clamp(worldX - halfW, 0, MAP_WIDTH  - getAppWidth());
        double ny = clamp(worldY - halfH, 0, MAP_HEIGHT - getAppHeight());

        vp.setX(nx);
        vp.setY(ny);
    }

    /** Викликати з onUpdate(double tpf) головного класу гри */
    public void update() {
        dotsGroup.getChildren().clear();

        // Додаємо макрооб'єкти
        for (Entity e : FXGL.getGameWorld().getEntitiesByType(EntityType.MACROOBJECT)) {
            Point2D p = e.getPosition();
            Color color;

            MacroObjectAbstract microObjectAbstract = e.getComponents().stream()
                    .filter(MacroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                    .map(MacroObjectAbstract.class::cast).findFirst().orElse(null);
            switch (microObjectAbstract.getClass().getSimpleName()) {
                case "Crypt"-> color = Color.CYAN;
                case "Dormitory"-> color = Color.CRIMSON;
                default -> color = Color.YELLOW;
            }
            Rectangle c = new Rectangle(p.getX() * scaleX, p.getY() * scaleY, 250*scaleX, 250*scaleY);
            c.setFill(color);
            dotsGroup.getChildren().add(c);
        }

        // Додаємо мікрооб'єкти
        for (Entity e : FXGL.getGameWorld().getEntitiesByType(EntityType.MICROOBJECT)) {
            Point2D p = e.getPosition();
            Color color;
            MicroObjectAbstract microObjectAbstract = e.getComponents().stream()
                    .filter(MicroObjectAbstract.class::isInstance) // Перевіряємо, чи компонент є MicroObjectAbstract
                    .map(MicroObjectAbstract.class::cast).findFirst().orElse(null);
            switch (microObjectAbstract.getClass().getSimpleName()) {
                case "Recruit"-> color = Color.GREEN;
                case "Soldier"-> color = Color.YELLOW;
                default -> color = Color.RED;
            }
            Circle c = new Circle(p.getX() * scaleX, p.getY() * scaleY, RADIUS_MICRO, color);
            dotsGroup.getChildren().add(c);
        }

        // Оновлюємо прямокутник видимої області
        Viewport vp = FXGL.getGameScene().getViewport();
        viewRect.setX(vp.getX() * scaleX);
        viewRect.setY(vp.getY() * scaleY);
        viewRect.setWidth(getAppWidth() * scaleX);
        viewRect.setHeight(getAppHeight() * scaleY);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(v, max));
    }
}
