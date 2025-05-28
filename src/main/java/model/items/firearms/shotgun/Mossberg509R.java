package model.items.firearms.shotgun;

public class Mossberg509R extends Shotgun {


    public Mossberg509R() {
        super("Moss-berg509R",7, 7,4,"Дробовики 590R Rotary Safety Selector поєднують перевірену часом ефективність помпової дії з революційними новими функціями, такими як поворотний запобіжник, ергономічно розроблене ців'я та інтегроване поєднання ствола з теплозахисним кожухом.");
    }

    @Override
    public boolean IsAutomatic() {
        return false;
    }

    @Override
    public double getFireRate() {
        return 0;
    }

    @Override
    public double getReloadTime() {
        return 5.6;
    }

    @Override
    public double getSpread_Amount() {
        return 0.02;
    }
}
