package model.objects.microobjects.behaviour;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import jdk.jfr.Enabled;
import model.objects.microobjects.MicroObjectAbstract;
import utilies.ConsoleHelper;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;


@Enabled
public class RecruitAIComponent extends Component {

    private MicroObjectAbstract microObjectAbstract;
    private boolean moving = false;
    private boolean firing = false;

    private Point2D moveTarget;
    private MicroObjectAbstract attackTarget;

    private final Queue<Command> commandQueue =new ArrayDeque<>();

    private Command currentCommand;

    @Override
    public void onAdded() {
        microObjectAbstract = entity.getComponents().stream()
                .filter(MicroObjectAbstract.class::isInstance)
                .map(MicroObjectAbstract.class::cast)
                .findFirst()
                .orElse(null);
    }

    public MicroObjectAbstract getMicroObjectAbstract() {
        return microObjectAbstract;
    }

    @Override
    public void onUpdate(double tpf) {
        if (microObjectAbstract == null) {
            onAdded();
        }
        if ( microObjectAbstract.isDead() || microObjectAbstract.isInMacroObject()) {
            return;
        }
        think();
        super.onUpdate(tpf);
    }

    /**
     * Задати нову ціль, до якої агент має дійти.
     * @param target абсолютні координати на сцені
     */
    public void setTargetToMove(Point2D target) {
        this.moveTarget = target;
    }

    public void moveToTarget(){
        if (microObjectAbstract.isInMacroObject() && !microObjectAbstract.getMacroObjectAbstract().equals(currentCommand.macroObjectAbstract())){
            microObjectAbstract.getMacroObjectAbstract().pullCreature(microObjectAbstract);
        }
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

            double threshold = 10.0;

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
                microObjectAbstract.stopPhysic();
                moveTarget = null;
                currentCommand = null;
                moving = false;
                think();
                return;
            }

            // Запланувати перевірку через деякий час
            FXGL.runOnce(() -> {
                moving = false;
                moveToTarget();
            }, Duration.seconds(0.15));
        }
    }

    public void think(){
        if (currentCommand != null) {
            doSomething();
            return;
        }
        if (commandQueue.isEmpty()) {
            return;
        }
        currentCommand = commandQueue.poll();
    }

    private void doSomething(){
        switch (currentCommand.commandName()){
            case MOVE ->{
                moveTarget=currentCommand.macroObjectAbstract()!=null?currentCommand.macroObjectAbstract().getPosition():null;
                moveTarget=moveTarget!=null?moveTarget:currentCommand.toMove();
                moveToTarget();
            }
            case ATTACK -> {
                attackTarget=currentCommand.microObjectAbstract();
                moveTarget=closestPointInRadius(microObjectAbstract.getPosition(),attackTarget.getPosition(),250);
                moveToAttackTarget();
                attackTarget();
            }
            default -> ConsoleHelper.writeMessageInLabelInRightCorner("Невідома команда",5,1920,1080);
        }
    }

    public void addCommand(Command command){
        commandQueue.add(command);
    }

    public Queue<Command> getCommandQueue() {
        return commandQueue;
    }

    public static Point2D closestPointInRadius(Point2D a, Point2D b, double radius) {
        Point2D dir = a.subtract(b);
        double distance = dir.magnitude();

        if (distance <= radius) {
            return a; // А вже в межах радіуса
        }

        // нормалізуємо і множимо на R
        return b.add(dir.normalize().multiply(radius));
    }

    public void moveToAttackTarget(){
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

            double threshold = 15.0;

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
                microObjectAbstract.stopPhysic();
                moveTarget = null;
                moving = false;
                return;
            }

            // Запланувати перевірку через деякий час
            FXGL.runOnce(() -> {
                moving = false;
                moveToAttackTarget();
            }, Duration.seconds(0.15));
        }
    }


    public void attackTarget(){
        if (attackTarget==null) return;
        if (firing) return;
        microObjectAbstract.fire(attackTarget.getPosition());
        firing = true;
        FXGL.runOnce(()->firing=false,Duration.seconds(1.2));
    }
}
