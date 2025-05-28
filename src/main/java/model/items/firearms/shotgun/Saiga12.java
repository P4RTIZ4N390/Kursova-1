package model.items.firearms.shotgun;

public class Saiga12 extends Shotgun{
    public Saiga12() {
        super("Saiga 12",3.6,10,3,"Сайга-12 — самозарядна рушниця, розроблена на Іжевському машинобудівному заводі на базі автомата Калашникова і призначена для промислового й аматорського полювання на дрібного, середнього звіра та птахів в районах з будь-якими кліматичними умовами. Сімейство зброї під назвою «Сайга» було розроблено під час розпаду СРСР на Іжевському Машинобудівному Заводі.");
    }

    @Override
    public boolean IsAutomatic() {
        return true;
    }

    @Override
    public double getFireRate() {
        return 1.33;
    }

    @Override
    public double getReloadTime() {
        return 5.6;
    }

    @Override
    public double getSpread_Amount() {
        return 0.035;
    }
}
