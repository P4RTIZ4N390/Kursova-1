package my.kursova21;

import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import model.objects.microobjects.behaviour.RecruitAIComponent;
import model.objects.minimap.MiniMap;
import utilies.ImageLoader;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class Lab6 extends Lab5 {
    private static final double EDGE_THRESHOLD = 30;  // відстань до краю екрану в пікселях
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

        // Куллінг до [0 .. MAP_WIDTH - screenW], [0 .. MAP_HEIGHT - screenH]
        newX = Math.max(0, Math.min(newX, MAP_WIDTH  - screenW));
        newY = Math.max(0, Math.min(newY, MAP_HEIGHT - screenH));

        viewport.setX(newX);
        viewport.setY(newY);
        miniMap.update();
    }



    @Override
    protected void setupControls() {
        super.setupControls();
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
}
