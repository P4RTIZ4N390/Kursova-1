package model.objects.microobjects.behaviour;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import jdk.jfr.Enabled;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.microobjects.MicroObjectAbstract;
import utilies.ConsoleHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;


@Enabled
public class RecruitAIComponent extends Component {

    private final MicroObjectAbstract microObjectAbstract;
    private boolean moving = false;
    private boolean firing = false;

    private Point2D moveTarget;
    private MicroObjectAbstract attackTarget;

    private MacroObjectAbstract macroTarget;
    private boolean macroIsTarget;

    private final List<Command> commandsList =new ArrayList<>();

    private Command currentCommand;

    public RecruitAIComponent(MicroObjectAbstract microObjectAbstract) {
        this.microObjectAbstract = microObjectAbstract;
    }

    @Override
    public void onAdded() {
    }

    public MicroObjectAbstract getMicroObjectAbstract() {
        return microObjectAbstract;
    }

    @Override
    public void onUpdate(double tpf) {
        if (microObjectAbstract == null) {
            onAdded();
        }
        if ( microObjectAbstract.isDead()) {
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
        if (microObjectAbstract.isInMacroObject()){
            microObjectAbstract.getMacroObjectAbstract().pullCreature(microObjectAbstract);
            return;
        }

        if (microObjectAbstract.isDead() ) return;
        if (moveTarget != null) {

            moving = true;
            double x = entity.getX();
            double y = entity.getY();
            double tx = moveTarget.getX();
            double ty = moveTarget.getY();

            double dx = tx - x;
            double dy = ty - y;

            // Зупиняємо поточний рух, щоб не було зміщення по двох осях
            microObjectAbstract.stop();

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
                commandsList.remove(currentCommand);
                currentCommand = null;
                moving = false;
                think();
                return;
            }

            // Запланувати перевірку через деякий час
            FXGL.runOnce(() -> {
                moving = false;
                moveToTarget();
            }, Duration.seconds(0.25));
        }
    }

    public void think(){
        if (microObjectAbstract.isDead()) return;
        if (currentCommand != null) {
            doSomething(currentCommand);
        }
        if (commandsList.isEmpty()) {
            return;
        }
        commandsList.sort(Command::compareTo);
        currentCommand = commandsList.getFirst();
    }

    private void doSomething(Command command) {
        if (microObjectAbstract.isDead()) return;
        switch (command.commandName()){
            case MOVE ->{
                if (command.macroObjectAbstract()!=null){
                    macroTarget = command.macroObjectAbstract();
                    macroIsTarget = true;
                    moveToMacroTarget();
                }else {
                    moveTarget = moveTarget != null ? moveTarget : command.toMove();
                    moveToTarget();
                }
            }
            case ATTACK -> {
                if (command.microObjectAbstract()==null){
                    commandsList.remove(command);
                    return;
                }
                attackTarget=command.microObjectAbstract();
                moveTarget=closestPointInRadius(microObjectAbstract.getPosition(),attackTarget.getPosition(),250);
                moveToAttackTarget();
                attackTarget();
            }
            case DEFENSE ->{
                if (command.microObjectAbstract()==null){
                    commandsList.remove(command);
                    return;
                }
                attackTarget=command.microObjectAbstract();
                moveTarget=closestPointInRadius(microObjectAbstract.getPosition(),attackTarget.getPosition(),250);
                moveToTarget();
                attackTarget();
            }

            default -> ConsoleHelper.writeMessageInLabelInRightCorner("Невідома команда",5,1920,1080);
        }
    }

    private void moveToMacroTarget() {
        if (microObjectAbstract.isInMacroObject() && microObjectAbstract.getMacroObjectAbstract().equals(this.macroTarget)){
            // Якщо вже біля цілі — припиняємо рух
            microObjectAbstract.stop();
            macroTarget = null;
            commandsList.remove(currentCommand);
            macroIsTarget = false;
            currentCommand = null;
            moving = false;
            think();
            return;
        }

        if (microObjectAbstract.isInMacroObject() && !microObjectAbstract.getMacroObjectAbstract().equals(this.macroTarget) && macroIsTarget) {
            microObjectAbstract.getMacroObjectAbstract().pullCreature(microObjectAbstract);
            return;
        }

        if (microObjectAbstract.isDead() || entity==null) return;
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

            double threshold = 5.0;

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

    public void addCommand(Command command){
        if (commandsList.contains(command)) return;
        commandsList.add(command);
    }

    public List<Command> getCommandsLits() {
        return commandsList;
    }

    public static Point2D closestPointInRadius(Point2D a, Point2D b, double radius) {
        Point2D dir = a.subtract(b);
        double distance = dir.magnitude();

        if (distance <= radius) {
            return null; // А вже в межах радіуса
        }

        // нормалізуємо і множимо на R
        return b.add(dir.normalize().multiply(radius));
    }

    public void moveToAttackTarget(){
        if (microObjectAbstract.isInMacroObject()){
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
        if (microObjectAbstract.isDead()) return;
        if (attackTarget==null) return;
        if (checkForDeadAndDo()) return;
        if (firing) return;
        microObjectAbstract.fire(attackTarget.getPosition());
        firing = true;
        FXGL.runOnce(()->firing=false,Duration.seconds(1.2));
        checkForDeadAndDo();
    }

    private boolean checkForDeadAndDo(){
        if (attackTarget.isDead() || !microObjectAbstract.checkForAmmo()){
            commandsList.remove(currentCommand);
            currentCommand = null;
            attackTarget=null;
            moveTarget=null;
            macroIsTarget=false;
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
}
