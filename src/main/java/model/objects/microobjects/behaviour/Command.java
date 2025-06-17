package model.objects.microobjects.behaviour;

import javafx.geometry.Point2D;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.microobjects.MicroObjectAbstract;
import org.jetbrains.annotations.NotNull;

public record Command(Commands commandName,
               MacroObjectAbstract macroObjectAbstract,
               MicroObjectAbstract microObjectAbstract ,
               Point2D toMove,short priority) implements Comparable<Command> {
    public static Command getAttackCommand(MicroObjectAbstract microObjectAbstract,short priority) {
        return new Command(Commands.ATTACK,null,microObjectAbstract,null,priority);
    }

    public static Command getDefenseCommand(MicroObjectAbstract microObjectAbstract,short priority) {
        return new Command(Commands.DEFENSE,null,microObjectAbstract,null,priority);
    }

    public static Command getMoveToMacroObjectCommand(MacroObjectAbstract macroObjectAbstract,short priority) {
        return new Command(Commands.MOVE,macroObjectAbstract,null,null,priority);
    }

    public static Command getMoveToMicroObjectCommand(MicroObjectAbstract microObjectAbstract,short priority) {
        return new Command(Commands.MOVE,null,microObjectAbstract,null,priority);
    }

    public static Command getMoveToCommand(Point2D point,short priority) {
        return new Command(Commands.MOVE,null,null,point,priority);
    }


    @Override
    public int compareTo(@NotNull Command o) {
        return Short.compare(priority, o.priority);
    }
}
