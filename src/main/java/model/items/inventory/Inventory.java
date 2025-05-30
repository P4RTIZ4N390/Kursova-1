package model.items.inventory;

import model.items.Item;
import model.items.firearms.Gun;
import org.jetbrains.annotations.NotNull;
import utilies.ConsoleHelper;
import java.util.*;

public class Inventory implements Comparable<Inventory>,Cloneable{//клас-місце де будуть зберігатись предмети
    private Map<Item, Integer> items = new HashMap<>();//мапа предметів, де ключ предмет, а значення кількість
    private double maxWeight;//максимальна вага яку можна переносити
    private double currentWeight = 0;//скільки важать предмети зараз

    private final InventoryController inventoryController;//контролер для предметів

    private Gun currentGun;//зброя яка наразі використовується

    private Inventory(Gun currentGun, double maxWeight) {//для гравця
        this.currentGun = currentGun;
        this.inventoryController = InventoryController.getInventoryControllerForPlayer(this);
        this.maxWeight = maxWeight;
    }

    private Inventory(double maxWeight) {
        //Для створінь і сховищ
        this.maxWeight = maxWeight;
        inventoryController = InventoryController.getInventoryControllerForEntity(this);
    }

    private Inventory(Gun currentGun, InventoryController inventoryController, double currentWeight, double maxWeight, Map<Item, Integer> items) {//Це на потім
        this.currentGun = currentGun;
        this.inventoryController = inventoryController;
        this.currentWeight = currentWeight;
        this.maxWeight = maxWeight;
        this.items = items;
    }

    private Inventory(InventoryController inventoryController, double currentWeight) {//Звичайний конструктор
        this.inventoryController = inventoryController;
        this.currentWeight = currentWeight;
    }

    public static Inventory getInventory(double maxWeight) {//Для сховищ
        return new Inventory(maxWeight);
    }

    public static Inventory getInventory(double maxWeight,Gun startGun) {//Для створінь
        Inventory inventory = new Inventory(maxWeight);
        inventory.addItem(startGun);
        inventory.setCurrentGun(startGun.getName());
        return inventory;
    }

    public static Inventory getInventoryForPlayer(double maxWeight,Gun startGun) {
        Inventory inventory = new Inventory(startGun,maxWeight);
        inventory.addItem(startGun);

        return inventory;
    }

    public void addItem(Item item) {//додати предмет
        if (currentWeight + item.getWeight() > maxWeight) {//Перевірка на додавання
            ConsoleHelper.writeMessage("Неможливо додати " + item.getName() + ". Перевищено ліміт ваги!");
            return;
        }

        Item searchedItem = checkItemAndReturn(item.getName());//Пошук, чи є ще такий самий предмет

        if (searchedItem != null) {//якщо є
            items.put(searchedItem, items.get(searchedItem) + 1);//Змінюємо кількість предметів
        } else {//якщо немає
            items.put(item, 1);//Додаємо новий предмет
        }

        currentWeight += item.getWeight();//Оновлюємо вагу

        ConsoleHelper.writeMessage(item.getName() + " (1 шт.) додано в інвентар.");
        inventoryController.updateInventoryView();//Оновлюємо перегляд інвентарю
    }

    public void addItems(Item item, int amount) {
        if (amount<=0){
            ConsoleHelper.writeMessage("Не можна додати,0 або відємну кількість предметів!");
            return;
        }

        if (currentWeight + item.getWeight()*amount > maxWeight) {//Перевірка на додавання
            ConsoleHelper.writeMessage("Неможливо додати " + item.getName() +"("+amount+")" + ". Перевищено ліміт ваги!");
            return;
        }

        Item searchedItem = checkItemAndReturn(item.getName());//Пошук, чи є ще такий самий предмет

        if (searchedItem != null) {//якщо є
            items.put(searchedItem, items.get(searchedItem) + amount);//змінюємо кількість предметів
        } else {//якщо немає
            items.put(item, amount);//додаємо нові предмети
        }

        currentWeight += item.getWeight()*amount;//оновлюємо вагу

//        ConsoleHelper.writeMessage(item.getName() + "("+amount+") додано в інвентар.");
        inventoryController.updateInventoryView();//оновлюємо перегляд інвентарю
    }

    public void removeItem(Item item, int amount) {
        if (amount <= 0) {
            ConsoleHelper.writeMessage("Не можна видалити 0 або від'ємну кількість предметів!");
            return;
        }

        if (!items.containsKey(item)) {
            ConsoleHelper.writeMessage("Предмет не знайдено.");
            return;
        }

        removeItems(amount, item);
        inventoryController.updateInventoryView();//Оновлюємо перегляд інвентарю
    }

    public void removeItem(String itemName, int amount) {
        if (amount <= 0) {
            ConsoleHelper.writeMessage("Не можна видалити 0 або від'ємну кількість предметів!");
            return;
        }

        if (checkItemAndReturn(itemName) == null) {
            ConsoleHelper.writeMessage("Предмет не знайдено.");
            return;
        }

        Item item = checkItemAndReturn(itemName);

        removeItems(amount, item);
        inventoryController.updateInventoryView();//Оновлюємо перегляд інвентарю
    }

    private void removeItems(int amount, Item item) {
        int currentAmount = items.get(item);//скільки предметів наразі

        if (currentAmount < amount) {
            ConsoleHelper.writeMessage("Неможливо видалити більше, ніж є в наявності! У вас тільки " + currentAmount + " шт.");
            return;
        }

        if (currentAmount == amount) {
            items.remove(item);
        } else {
            items.put(item, currentAmount - amount);
        }

        currentWeight -= item.getWeight() * amount;
        ConsoleHelper.writeMessage(item.getName() + " (" + amount + " шт.) видалено з інвентаря.");
    }

    public void showInventory() {//Вивести інвентар в консоль
        ConsoleHelper.writeMessage("\n=== Інвентар ===");
        for (Item item : items.keySet()) {
            System.out.println(item+": "+items.get(item));
        }
        ConsoleHelper.writeMessage("Загальна вага: " + currentWeight + "/" + maxWeight + " кг");
        ConsoleHelper.writeSeparator();
    }

    public void setItems(Map<Item,Integer> items) {
        this.items = items;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }


    public Item checkItemAndReturn(Item item) {//швидше за все, стане непотрібним
        if (items.containsKey(item)) {
            return item;
        }

        return null;
    }

    public Item checkItemAndReturn(String itemName) {//пошук і повернення предмета за назвою
        for (Item item : items.keySet()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    public int checkItemAndReturnAmount(String itemName) {//пошук і повернення предмета за назвою
        int result = 0;
        for (Item item : items.keySet()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                result = items.get(item);
            }
        }
        return result;
    }

    public Gun checkGunAndReturn(String name) {
        //Для вибору зброй
        for (Item item : items.keySet()) {
            if (item instanceof Gun && item.getName().equalsIgnoreCase(name)) {
                return (Gun) item;
            }
        }
        return null;
    }

    public boolean checkItemAndReturnAmount(String itemName, int amount) {
        return checkItemAndReturnAmount(itemName) >= amount;
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    public Gun getCurrentGun() {//ще знадобиться
        return currentGun;
    }

    public void setCurrentGun(String itemName) {
        Gun gun = checkGunAndReturn(itemName);

        if (gun== null) {
            ConsoleHelper.writeMessage("Предмет не знайдено: " + itemName);
            return;
        }

        currentGun = gun;

        ConsoleHelper.writeMessage("Вибрано: " + currentGun.getName());
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public void showInventoryView() {
        inventoryController.showInventoryView();
    }


    @Override
    public int compareTo(@NotNull Inventory o) {
        return 0;
    }


    @Override
    public Inventory clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (Inventory) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void clear(){
        items.clear();
    }


}