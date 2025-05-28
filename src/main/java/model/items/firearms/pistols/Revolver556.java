package model.items.firearms.pistols;

import model.items.firearms.Gun;

public class Revolver556 extends Gun {
    public Revolver556() {
        super("Revolver 5.56",1.4,6,3,"Revolver 5.56 (Револьвер 5.56) - розробка ордена ,як простий ,легко доступний й потужний пістолет ,для знешкодження товстошкурих звірів і броньованих цілей.Завдяки доступності патронів цього калібру, отримав добре сприйнятя в ближніх вимірах,використовуються стражами,розвідкою і як запасна зброя в деяких відділеннях.Застарілий");
    }
    @Override
    public String getCaliber() {//Калібр
        return "5.56мм";
    }

    public boolean IsAutomatic() {//Чи автоматична зброя
        return false;
    }

    @Override
    public double getFireRate() {//Швидкість стрільби
        return 0;
    }

    @Override
    public double getReloadTime() {//Час перезарядки
        return 7.5;
    }

    @Override
    public double getSpread_Amount() {//Число розкиду
        return 0.02;
    }
}
