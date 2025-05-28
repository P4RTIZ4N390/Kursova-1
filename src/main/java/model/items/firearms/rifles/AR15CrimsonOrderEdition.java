package model.items.firearms.rifles;

import model.items.firearms.Gun;

public class AR15CrimsonOrderEdition extends Gun {
    public AR15CrimsonOrderEdition() {
        super("AR15CrimsonOrderEdition", 3.5,35,5,"AR-15 — Модифікація AR15 Багровим орденом,покращенні відвід газів і дуло зі сплавів магічного підвиду  сталі зробили більш влучнішою." +
                "Додано рукоятку переведення вогню,екран для показу кількості патроні,ММГ для його робити і збільшений стандартний магазин().Також у пізніших версіях,змінено форму дула для зменшення видимості ,без значних втрат у стійкості і влучності.");}

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
        return 0.072;
    }

    @Override
    public double getReloadTime() {
        return 3;
    }

    @Override
    public double getSpread_Amount() {
        return 0.015;
    }
}
