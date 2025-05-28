package model.items.firearms.rifles;

import model.items.firearms.Gun;

public class AKM extends Gun {
    public AKM() {
        super("AKM", 3.6, 30,5,"АКМ (автомат Калашникова модернізований) — штурмова гвинтівка калібру 7,62×39 мм, розроблена Михайлом Калашниковим.");
    }

    @Override
    public String getCaliber() {
        return "7.62х39мм";
    }

    @Override
    public boolean IsAutomatic() {
        return true;
    }

    @Override
    public double getFireRate() {
        return 0.1;
    }

    @Override
    public double getReloadTime() {
        return 4.2;
    }

    @Override
    public double getSpread_Amount() {
        return 0.038;
    }
}
