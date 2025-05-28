package model.items.firearms.smg;

import model.items.firearms.Gun;

public class SMG45 extends Gun {
    public SMG45() {
        super("SMG 45",2.9,26,3,"Пістолет-кулемет SMG-45 розроблений американською компанією LWRC International (LWRCI) і випускається у двох базових варіантах – автоматичному (для застосування в поліції та збройних силах) та лише самозарядному, для цивільного ринку.");
    }


    @Override
    public String getCaliber() {
        return "45ACP";
    }

    public boolean IsAutomatic() {
        return true;
    }

    @Override
    public double getFireRate() {
        return (double) 1/((double) 800 /60);
    }

    @Override
    public double getReloadTime() {
        return 2;
    }

    @Override
    public double getSpread_Amount() {
        return 0.32;
    }


}
