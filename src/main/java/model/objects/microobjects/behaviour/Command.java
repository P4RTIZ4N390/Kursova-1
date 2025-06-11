package model.objects.microobjects.behaviour;

import javafx.geometry.Point2D;
import model.objects.macroobjects.MacroObjectAbstract;
import model.objects.microobjects.MicroObjectAbstract;

public record Command(Commands commandName,
               MacroObjectAbstract macroObjectAbstract,
               MicroObjectAbstract microObjectAbstract ,
               Point2D toMove) {
    public static Command getAttackCommand(MicroObjectAbstract microObjectAbstract) {
        return new Command(Commands.ATTACK,null,microObjectAbstract,null);
    }

    public static Command getDefenseCommand(MicroObjectAbstract microObjectAbstract) {
        return new Command(Commands.DEFENSE,null,microObjectAbstract,null);
    }

    public static Command getMoveToMacroObjectCommand(MacroObjectAbstract macroObjectAbstract) {
        return new Command(Commands.MOVE,macroObjectAbstract,null,null);
    }

    public static Command getMoveToMicroObjectCommand(MicroObjectAbstract microObjectAbstract) {
        return new Command(Commands.MOVE,null,microObjectAbstract,null);
    }

    public static Command getMoveToCommand(Point2D point) {
        return new Command(Commands.MOVE,null,null,point);
    }


}
