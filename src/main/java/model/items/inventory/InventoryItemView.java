package model.items.inventory;

import javafx.scene.layout.HBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class InventoryItemView extends HBox {
    private final Text itemName;//Назва і кількість предметів в дужках
    private final String description;

    private boolean selected = false;

    public InventoryItemView(String name, double weight,String description,int damage) {
        setSpacing(10);
        setMinSize(250,28);
        setMaxSize(250, 30);
        setStyle("-fx-background-color: black; -fx-padding: 5px;");

        itemName = new Text(name);
        itemName.setFill(Color.GREEN);

        Text itemWeight = new Text(String.format("%.1f кг", weight));
        itemWeight.setFill(Color.GRAY);

        Text itemDamage = new Text(String.valueOf(damage));
        itemDamage.setFill(Color.GRAY);

        getChildren().addAll(itemName, itemWeight);

        this.description = description;
    }

    public void select() {
        selected = true;
        setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
    }

    public void deselect() {
        selected = false;
        setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
    }

    public String getItemName() {
        return itemName.getText();
    }

    public String getDescription() {
        return description;
    }

    public boolean isSelected() {
        return selected;
    }
}
