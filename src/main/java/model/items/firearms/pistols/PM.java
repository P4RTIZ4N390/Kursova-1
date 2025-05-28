package model.items.firearms.pistols;

import model.items.firearms.Gun;

public class PM extends Gun {
    public PM() {
        super("Pistolet Makarova",1.0,9,-2,"9-мм пістоле́т Мака́рова — радянський самозарядний пістолет, прийнятий на озброєння Збройних сил та правоохоронних структур СРСР у 1951 році.");
    }
    @Override
    public String getCaliber() {
        return "9mm";
    }//Калібр

    @Override
    public boolean IsAutomatic() {//Чи автоматична зброя
        return false;
    }

    @Override
    public double getFireRate() {//Швидкість стрільби
        return 0;
    }

    @Override
    public double getReloadTime() {//Час перезарядки
        return 3;
    }

    @Override
    public double getSpread_Amount() {//Число розкиду
        return 0.04;
    }


}
