package model.items.firearms.pistols;

import com.almasb.fxgl.texture.AnimationChannel;
import model.Direction;
import model.items.firearms.Gun;

public class Glock17 extends Gun {

    public Glock17() {
        super("Glock17", 1.0,17,1,"Glock 17 (Глок 17) — австрійський пістолет, розроблений фірмою Glock Ges.m.b.H. для потреб австрійської армії. Він став першим зразком озброєння, розробленим цією фірмою. Зразок виявився досить вдалим і зручним для застосування, завдяки чому пізніше його прийнято на озброєння армії Австрії під позначенням Р80.");
    }



    @Override
    public boolean IsAutomatic() {//Чи автоматична зброя
        return true;
    }

    @Override
    public double getFireRate() {//Швидкість стрільби
        return 0.1;
    }

    @Override
    public String getCaliber() {//Калібр
        return "9mm";
    }

    @Override
    public double getReloadTime() {//Час перезарядки
        return 3.5;
    }

    @Override
    public double getSpread_Amount() {//Число розкиду
        return 0.03;
    }

}
