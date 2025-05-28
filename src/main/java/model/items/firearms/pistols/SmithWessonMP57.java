package model.items.firearms.pistols;

import model.items.firearms.Gun;

public class SmithWessonMP57 extends Gun {

    public SmithWessonMP57() {
        super("Smith & Wesson M&P 5.7",1.0,22,5,"Smith & Wesson M&P 5.7 – це напівавтоматичний пістолет, розроблений американською компанією Smith & Wesson. Він використовує малокаліберний високошвидкісний набій 5.7×28 мм, який відомий своєю високою пробивною здатністю та малою віддачею.");
    }
    @Override
    public String getCaliber() {
        return "5.7×28мм";
    }//Калібр

    public boolean IsAutomatic() {//Чи автоматична зброя
        return false;
    }

    @Override
    public double getFireRate() {//Швидкість стрільби
        return 0;
    }

    @Override
    public double getReloadTime() {//Час перезарядки
        return 2.8;
    }

    @Override
    public double getSpread_Amount() {//Число розкиду
        return 0.015;
    }
}
