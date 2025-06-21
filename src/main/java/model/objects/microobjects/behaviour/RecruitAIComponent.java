package model.objects.microobjects.behaviour;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarCell;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarPathfinder;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jdk.jfr.Enabled;
import model.TriggerComponent;
import model.objects.EntityType;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.microobjects.MicroObjectAbstract;
import my.kursova21.Lab4;
import my.kursova21.Lab5;
import utilies.ConsoleHelper;
import utilies.PathFinder;


import java.util.ArrayList;
import java.util.List;


@Enabled
public class RecruitAIComponent extends Component {


    public static AStarGrid aStarGrid ;
        private static  AStarPathfinder<AStarCell> pf;

    private final double threshold=16;
    private final static int CELL_SIZE=16;

    private final MicroObjectAbstract microObjectAbstract;
    private boolean moving = false;
    private boolean firing = false;


    private Point2D moveTarget;
    private Point2D lastMoveTarget;
    private List<AStarCell> currentPath = new ArrayList<>();
    private int pathIndex = 0;


    private MicroObjectAbstract attackTarget;

    private MacroObjectAbstract macroTarget;
    private boolean macroIsTarget = false;

    private final List<Task> taskList = new ArrayList<>();

    private Task currentTask;

    public RecruitAIComponent(MicroObjectAbstract microObjectAbstract) {
        this.microObjectAbstract = microObjectAbstract;
    }

    @Override
    public void onAdded() {
        if (aStarGrid == null) {
            aStarGrid= AStarGrid.fromWorld(FXGL.getGameWorld(),Lab4.WIDTH/CELL_SIZE, Lab5.HEIGHT/CELL_SIZE,CELL_SIZE,CELL_SIZE, type -> {
                if (type == EntityType.MACROOBJECT) {
                    return CellState.NOT_WALKABLE;
                }
                return  CellState.WALKABLE;});
            List<MacroObjectAbstract> allMacroObjects = FXGL.getGameWorld().getEntities()
                    .stream()
                    .filter(e -> e.getComponentOptional(TriggerComponent.class).isPresent()) // Шукаємо тригерні об'єкти
                    .flatMap(e -> e.getComponents().stream())                                // Розгортаємо всі компоненти кожної сутності
                    .filter(MacroObjectAbstract.class::isInstance)                            // Фільтруємо лише ті, що є MacroObjectAbstract
                    .map(MacroObjectAbstract.class::cast)
                    .toList();
            for (MacroObjectAbstract macroObjectAbstract : allMacroObjects) {
                BoundingBoxComponent boundingBox=macroObjectAbstract.getEntity().getBoundingBoxComponent();
                PathFinder.blockInflatedZone(aStarGrid, (int) boundingBox.getMinXWorld(), (int) boundingBox.getMinYWorld(), (int) (boundingBox.getMaxXWorld()-boundingBox.getMinXWorld()), (int) (boundingBox.getMaxYWorld()-boundingBox.getMinYWorld()),2,CELL_SIZE);
            }
            pf = new AStarPathfinder<>(aStarGrid);
        }
    }

    public MicroObjectAbstract getMicroObjectAbstract() {
        return microObjectAbstract;
    }

    @Override
    public void onUpdate(double tpf) {
        if (microObjectAbstract == null) {
            onAdded();
        }
        if (microObjectAbstract.isDead()) {
            return;
        }
        think();
        super.onUpdate(tpf);
    }

    /**
     * Задати нову ціль, до якої агент має дійти.
     *
     * @param target абсолютні координати на сцені
     */
    public void setTargetToMove(Point2D target) {
        this.moveTarget = toCellCenter(target);
    }

    public void think() {
        if (microObjectAbstract.isDead()) return;
        if (currentTask != null) {
            doSomething(currentTask);
        }
        if (taskList.isEmpty()) {
            return;
        }
        taskList.sort(Task::compareTo);
        currentTask = taskList.getFirst();
    }

    private void doSomething(Task task) {
        if (microObjectAbstract.isDead()) return;
        switch (task.typeOfTask()) {
            case MOVE -> {
                if (task.macroObjectAbstract() != null) {
                    macroTarget = task.macroObjectAbstract();
                    macroIsTarget = true;
                    moveToMacroTarget();
                } else {
                    moveTarget = moveTarget != null ? toCellCenter(moveTarget) : task.toMove();
                    moveToTarget();
                }
            }
            case ATTACK -> {
                if (task.microObjectAbstract() == null) {
                    taskList.remove(task);
                    return;
                }
                attackTarget = task.microObjectAbstract();
                moveTarget = closestPointInRadius(microObjectAbstract.getPosition(), toCellCenter(attackTarget.getPosition()), 250);
                moveToAttackTarget();
                attackTarget();
            }
            case DEFENSE -> {
                if (task.microObjectAbstract() == null) {
                    taskList.remove(task);
                    return;
                }
                attackTarget = task.microObjectAbstract();
                moveTarget = closestPointInRadius(microObjectAbstract.getPosition(),toCellCenter(attackTarget.getPosition()), 350);
                moveToTarget();
                attackTarget();
            }

            default -> ConsoleHelper.writeMessageInLabelInRightCorner("Невідома команда", 5, 1920, 1080);
        }
    }

    private void moveToMacroTarget() {
        if (microObjectAbstract.isInMacroObject() && microObjectAbstract.getMacroObjectAbstract().equals(this.macroTarget)) {
            // Якщо вже біля цілі — припиняємо рух
            microObjectAbstract.stop();
            macroTarget = null;
            taskList.remove(currentTask);
            macroIsTarget = false;
            currentTask = null;
            moving = false;
            think();
            return;
        }

        if (microObjectAbstract.isInMacroObject() && !microObjectAbstract.getMacroObjectAbstract().equals(this.macroTarget) && macroIsTarget) {
            microObjectAbstract.getMacroObjectAbstract().pullCreature(microObjectAbstract);
            return;
        }

        if (microObjectAbstract.isDead() || entity == null) return;
        if (macroTarget != null) {

            moving = true;
            double x = entity.getX();
            double y = entity.getY();
            double tx = macroTarget.getX();
            double ty = macroTarget.getY();

            double dx = tx - x;
            double dy = ty - y;

            // Зупиняємо поточний рух, щоб не було зміщення по двох осях
            microObjectAbstract.stop();

            if (Math.abs(dx) > threshold) {
                if (dx > 0) {
                    microObjectAbstract.moveRight();
                } else {
                    microObjectAbstract.moveLeft();
                }
            } else if (Math.abs(dy) > threshold) {
                if (dy > 0) {
                    microObjectAbstract.moveDown();
                } else {
                    microObjectAbstract.moveUp();
                }
            }

            // Запланувати перевірку через деякий час
            FXGL.runOnce(() -> {
                moving = false;
                moveToMacroTarget();
            }, Duration.seconds(0.15));
        }
    }

    public void addTask(Task task) {
        if (taskList.contains(task)) return;
        taskList.add(task);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public static Point2D closestPointInRadius(Point2D a, Point2D b, double radius) {
        Point2D dir = a.subtract(b);
        double distance = dir.magnitude();

        if (distance <= radius) {
            return null; // А вже в межах радіуса
        }

        // нормалізуємо і множимо на R
        return toCellCenter(b.add(dir.normalize().multiply(radius)));
    }

    public void moveToTarget() {
        Point2D entityPos=PathFinder.getHitboxCenter(entity);
        if (microObjectAbstract.isInMacroObject()) {
            microObjectAbstract.getMacroObjectAbstract().pullCreature(microObjectAbstract);
            return;
        }

        if (entity == null) return;

        if (moveTarget != null && !microObjectAbstract.isDead()) {
            moving = true;
            if (lastMoveTarget == null || !lastMoveTarget.equals(moveTarget)) {
                double dx = moveTarget.getX() - entityPos.getX();
                double dy = moveTarget.getY() - entityPos.getY();
                if (Math.abs(dx) <= threshold && Math.abs(dy) <= threshold) {
                    taskList.remove(currentTask);
                    resetMovement();
                    return;
                }
                lastMoveTarget = new Point2D(moveTarget.getX(), moveTarget.getY());
                computePathToTarget(moveTarget);
            }
        }
    }

    private void resetMovement() {
        moveTarget = null;
        moving = false;
    }

    public void moveToAttackTarget() {
        if (microObjectAbstract.isInMacroObject()) {
            microObjectAbstract.getMacroObjectAbstract().pullCreature(microObjectAbstract);
            return;
        }
        if (microObjectAbstract.isDead()) return;
        if (moveTarget != null) {

            moving = true;
            double x = entity.getX();
            double y = entity.getY();
            double tx = moveTarget.getX();
            double ty = moveTarget.getY();

            double dx = tx - x;
            double dy = ty - y;

            // Зупиняємо поточний рух, щоб не було зміщення по двох осях
            microObjectAbstract.stopPhysic();

            double threshold = 25.0;

            if (Math.abs(dx) > threshold) {
                if (dx > 0) {
                    microObjectAbstract.moveRight();
                } else {
                    microObjectAbstract.moveLeft();
                }
            } else if (Math.abs(dy) > threshold) {
                if (dy > 0) {
                    microObjectAbstract.moveDown();
                } else {
                    microObjectAbstract.moveUp();
                }
            } else {
                // Якщо вже біля цілі — припиняємо рух
                microObjectAbstract.stop();
                resetMovement();
                return;
            }

            // Запланувати перевірку через деякий час
            FXGL.runOnce(() -> {
                moving = false;
                moveToAttackTarget();
            }, Duration.seconds(0.15));
        }
    }

    public void attackTarget() {
        if (microObjectAbstract.isDead()) return;
        if (attackTarget == null) return;
        if (checkForDeadAndDo()) return;
        if (firing) return;
        microObjectAbstract.fire(attackTarget.getPosition());
        firing = true;
        FXGL.runOnce(() -> firing = false, Duration.seconds(1.2));
        checkForDeadAndDo();
    }

    private boolean checkForDeadAndDo() {
        if (attackTarget.isDead() || !microObjectAbstract.checkForAmmo()) {
            taskList.remove(currentTask);
            currentTask = null;
            attackTarget = null;
            moveTarget = null;
            macroIsTarget = false;
            return true;
        }
        return false;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public MacroObjectAbstract getMacroTarget() {
        return macroTarget;
    }

    public void setMacroTarget(MacroObjectAbstract macroTarget) {
        this.macroTarget = macroTarget;
    }

    public boolean isMacroIsTarget() {
        return macroIsTarget;
    }

    public void setMacroIsTarget(boolean macroIsTarget) {
        this.macroIsTarget = macroIsTarget;
    }

    public Point2D getMoveTarget() {
        return moveTarget;
    }

    public void setMoveTarget(Point2D moveTarget) {
        this.moveTarget = moveTarget;
    }


    private void followPath() {
        if (currentPath == null || pathIndex >= currentPath.size() || entity==null) {
            microObjectAbstract.stopPhysic();
            resetMovement();
            think();
            return;
        }

        Point2D entityPos=PathFinder.getHitboxCenter(entity);

        AStarCell cell = currentPath.get(pathIndex);
        double cellCenterX = (cell.getX() + 0.5) * CELL_SIZE;
        double cellCenterY = (cell.getY() + 0.5) * CELL_SIZE;

        double dx = cellCenterX - entityPos.getX();
        double dy = cellCenterY - entityPos.getY();

        // Якщо по X вже близько — переходь по Y
        if (Math.abs(dx) <= threshold) {
            // Якщо по Y теж близько — ця клітинка пройдена
            if (Math.abs(dy) <= threshold) {
                pathIndex++;
            } else {
                // рухаємося по Y
                microObjectAbstract.stop();
                if (dy > 0) microObjectAbstract.moveDown();
                else microObjectAbstract.moveUp();
            }
        } else {
            // рухаємося по X
            microObjectAbstract.stop();
            if (dx > 0) microObjectAbstract.moveRight();
            else microObjectAbstract.moveLeft();
        }

        // Наступний крок

        FXGL.runOnce(()->{
            followPath();
            think();
        }, Duration.seconds(0.05));
    }


    private void computePathToTarget(Point2D target) {
        target=toCellCenter(target);
        Point2D entityPos=PathFinder.getHitboxCenter(entity);
        try {
            currentPath = pf.findPath((int) entityPos.getX()/CELL_SIZE, (int) entityPos.getY()/CELL_SIZE, (int) target.getX()/CELL_SIZE, (int) target.getY()/CELL_SIZE);
        }catch (Exception e){
            if (e instanceof ArrayIndexOutOfBoundsException){
                currentPath=null;
            }
            else throw (RuntimeException) e;
        }
        pathIndex = 0;
        if (currentPath == null || currentPath.isEmpty()) {
            ConsoleHelper.writeMessageInLabelInRightCorner("A*: шлях не знайдено або він порожній",8,Lab5.WIDTH,Lab5.HEIGHT);
            resetMovement();
            taskList.remove(currentTask);
            currentTask = null;
            think();
            return;
        }

        drawDebugPath(currentPath);

        followPath();
    }

    private void computePathToTargetWithOutDeleteTask(Point2D target) {
        target=toCellCenter(target);
        Point2D entityPos=PathFinder.getHitboxCenter(entity);
        try {
            currentPath = pf.findPath((int) entityPos.getX()/CELL_SIZE, (int) entityPos.getY()/CELL_SIZE, (int) target.getX()/CELL_SIZE, (int) target.getY()/CELL_SIZE);
        }catch (Exception e){
            if (e instanceof ArrayIndexOutOfBoundsException){
                currentPath=null;
            }
            else throw (RuntimeException) e;
        }
        pathIndex = 0;
        if (currentPath == null || currentPath.isEmpty()) {
            think();
            return;
        }
        followPath();
    }

    private static void drawDebugPath(List<AStarCell> cells) {
        for (AStarCell cell :cells) {
            Rectangle r = new Rectangle(CELL_SIZE, CELL_SIZE, Color.color(0, 0, 1, 0.3));
            r.setTranslateX(cell.getX() * CELL_SIZE);
            r.setTranslateY(cell.getY() * CELL_SIZE);
            FXGL.getGameScene().addUINode(r);
        }

    }

    public static Point2D toCellCenter(Point2D point) {
        int x = (int) (point.getX() / CELL_SIZE);
        int y = (int) (point.getY() / CELL_SIZE);
        return new Point2D((x + 0.5) * CELL_SIZE, (y + 0.5) * CELL_SIZE);
    }
}
