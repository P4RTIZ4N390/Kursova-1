package model.items.firearms.smg;

import model.items.firearms.Gun;

public class FNP90 extends Gun {
    public FNP90() {
        super("FN P90",3.5,50,4,"FN P90 — бельгійський пістолет-кулемет (персональна зброя самооборони), розроблений в 1986–1987 роках фірмою FN Herstal. Був розроблений, в першу чергу, для танкістів і водіїв бойової броньованої техніки. Має прогресивний і зручний ергономічний дизайн. Спеціально для P90 був розроблений патрон типу 5,7 × 28 мм SS190, що має високу пробивну потужність і низький ступень рикошету. Куля даного патрона розвиває дулову швидкість до 715 м/с і здатна пробити титан/кевларовий бронежилет, що відповідає вимогам НАТО CRISAT з 200 метрів.");
    }


    @Override
    public String getCaliber() {
        return "5.7×28мм";
    }

    public boolean IsAutomatic() {
        return true;
    }

    @Override
    public double getFireRate() {
        return 0.067;
    }

    @Override
    public double getReloadTime() {
        return 4.8;
    }

    @Override
    public double getSpread_Amount() {
        return 0.029;
    }
}
