package model.items;

import model.Direction;

public abstract class Item{
    private final String name;
    private final double weight;
    private final String description;
    private final TypesOfItem type;

    public Item(String name, double weight, String description, TypesOfItem type) {
        this.name = name;
        this.weight = weight;
        this.description = description;
        this.type = type;
    }

    public String getName() {
            return name;
    }

    public double getWeight() {
            return weight;
        }

    @Override
    public String toString() {
        return name+" - "+weight;
    }

    public TypesOfItem getType() {
        return type;
    }

    public String getDescription(){
        StringBuilder result = new StringBuilder();
        String[] descriptions = description.split(" ");//Розділяємо слова
        int size=0;//Кількість виведених символів
        for(String s : descriptions){
            if (size>=35){
                /*Якщо вивело 35 символів або більше, слів
                додаємо символ нового рядку, це зроблено, щоб не
                розділяти слова і був певний розмір рядка в описі
                 */
                result.append('\n');
                size=0;
            }
            result.append(s).append(" ");
            size+=s.length();//Додаємо довжину слова до кількості виведених символів
        }
        return result.toString();
    }
}

