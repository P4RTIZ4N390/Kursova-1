package model.objects.microobjects.behaviour;

import javafx.geometry.Point2D;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.microobjects.MicroObjectAbstract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Task(TypeOfTask typeOfTask,
                   MacroObjectAbstract macroObjectAbstract,
                   MicroObjectAbstract microObjectAbstract ,
                   Point2D toMove, short priority) implements Comparable<Task> {
    public static Task getAttackTask(MicroObjectAbstract microObjectAbstract, short priority) {
        return new Task(TypeOfTask.ATTACK,null,microObjectAbstract,null,priority);
    }

    public static Task getDefenseTask(MicroObjectAbstract microObjectAbstract, short priority) {
        return new Task(TypeOfTask.DEFENSE,null,microObjectAbstract,null,priority);
    }

    public static Task getMoveToMacroObjectTask(MacroObjectAbstract macroObjectAbstract, short priority) {
        return new Task(TypeOfTask.MOVE,macroObjectAbstract,null,null,priority);
    }

    public static Task getMoveToTask(Point2D point, short priority) {
        return new Task(TypeOfTask.MOVE,null,null,point,priority);
    }


    @Override
    public int compareTo(@NotNull Task o) {
        return Short.compare(o.priority, priority);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return priority() == task.priority() && Objects.equals(toMove(), task.toMove()) && typeOfTask() == task.typeOfTask() && Objects.equals(macroObjectAbstract(), task.macroObjectAbstract()) && Objects.equals(microObjectAbstract(), task.microObjectAbstract());
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeOfTask(), macroObjectAbstract(), microObjectAbstract(), toMove(), priority());
    }
}
