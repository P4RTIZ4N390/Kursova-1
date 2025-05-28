package model.items.firearms.rifles;

import model.items.firearms.Gun;

public class AR15 extends Gun {

    public AR15() {
        super("AR15", 3.2,30,4,"AR-15 — самозарядна (напівавтоматична) гвинтівка на основі відведення газів,з перевідником вогню, повітряним охолодженням і живленням від магазина, яка вироблялася в Сполучених Штатах між 1959 і 1964 роками.");
    }

    @Override
    public String getCaliber() {
        return "5.56мм";
    }

    @Override
    public boolean IsAutomatic() {
        return true;
    }

    @Override
    public double getFireRate() {
        return 0.075;
    }

    @Override
    public double getReloadTime() {
        return 4;
    }

    @Override
    public double getSpread_Amount() {
        return 0.02;
    }


}
