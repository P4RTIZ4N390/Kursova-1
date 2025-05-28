package model.items.inventory;

import model.items.*;
import model.items.firearms.Gun;


public class InventoryController {//від контролера, тут тільки назва, з'єднує інвентар, та перегляд
    private final Inventory inventory;//інвентар за який відповідає контролер
    private final InventoryView inventoryView;//перегляд інвентарю

    private InventoryController(Inventory inventory) {
        this.inventory = inventory;
        inventoryView = new InventoryView(this);
    }

    public static InventoryController getInventoryControllerForEntity(Inventory inventory) {//отримати контролер для істот
        return new InventoryController(inventory);
    }

    public static InventoryController getInventoryControllerForPlayer(Inventory inventory) {//отримати контролер для гравця
        return new InventoryController(inventory);
    }

    public void updateInventoryView(){//оновити перегляд інвентарю
        inventoryView.clearItems();//очисти перегляд інвентарю
        for (Item item : inventory.getItems().keySet()) {//Проходимося keySet, інвентарю
            /*
                Перший аргумент, це назва і кількість предмета в дужках
                Другий аргумент, це вага предмета
                Третій аргумент, це опис
                Четвертий, не пам'ятаю, і не знаю чи знадобиться на далі
             */
            InventoryItemView newItem = new InventoryItemView(item.getName()+"("+inventory.getItems().get(item)+")", item.getWeight()*inventory.getItems().get(item),item.getDescription(),0);
            inventoryView.addItem(newItem);//додавання предмета до перегляду
        }
    }

    public InventoryView getInventoryView() {
        return inventoryView;
    }

    public void setCurrentGun(String itemName) {
        String trueItemName = itemName.split("\\(")[0];
        inventory.setCurrentGun(trueItemName);
    }

    public  boolean IsGun(String itemName) {
        String trueItemName = itemName.split("\\(")[0];
        return inventory.checkGunAndReturn(trueItemName)!=null;
    }

    public InventoryItemView getCurrentItem() {
        Gun c=inventory.getCurrentGun();
        if(c==null) {
            return null;
        }
        return new InventoryItemView(c.getName(),c.getWeight(),c.getDescription(),c.getAdditionalDamage());
    }

    public void showInventoryView(){
        inventoryView.setVisible(!inventoryView.isVisible());
        updateInventoryView();
    }
}
