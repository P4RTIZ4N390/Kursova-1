package model.items.inventory;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class InventoryView extends Pane {
    private final VBox itemList;
    private final HBox currentItemBox;
    private final Text currentItemText;//Назва вибраного предмета
    private final Text itemDetails;//Опис предмета
    private InventoryItemView lastSelectedItem = null; // Запам’ятовує останній вибраний предмет
    private final InventoryController inventoryController;

    public InventoryView(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        setPrefSize(600, 400);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-border-color: #868585; -fx-border-width: 2px;");
        setLayoutX(100);
        setLayoutY(150);

        // Список предметів
        itemList = new VBox(10);
        itemList.setStyle("-fx-background-color: black;");
        itemList.setPadding(new Insets(10));

        // ScrollPane обгортає itemList
        ScrollPane scrollPane = new ScrollPane(itemList);
        scrollPane.setPrefSize(250, 300);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        getChildren().add(scrollPane);

        // Область для поточного предмета
        currentItemBox = new HBox();
        currentItemBox.setPrefSize(180, 50);
        currentItemBox.setStyle("-fx-background-color: #133f22; -fx-padding: 10px;");
        currentItemBox.setLayoutY(350);

        currentItemText = inventoryController.getCurrentItem()!=null?new Text(inventoryController.getCurrentItem().getItemName()):new Text("Немає вибраного предмета");
        currentItemText.setFill(Color.WHITE);
        currentItemBox.getChildren().add(currentItemText);

        // Панель деталей предмета
        itemDetails = new Text("Оберіть предмет...");
        itemDetails.setFill(Color.WHITE);

        VBox detailsBox = new VBox(10, itemDetails);
        detailsBox.setStyle("-fx-background-color: black; -fx-padding: 10px;");
        detailsBox.setPrefSize(300, 200);

        HBox mainContent = new HBox(10, scrollPane, detailsBox);

        getChildren().add(mainContent);
        getChildren().add(currentItemBox);
        setVisible(false);
    }

    public void addItem(InventoryItemView itemView) {
        itemList.getChildren().add(itemView);

        // Обробка вибору предмета (перше натискання - деталі, друге - встановлення як current)
        itemView.setOnMouseClicked(e -> {
            if (lastSelectedItem == itemView) {
                setCurrentItem(itemView); // Якщо вже вибраний – встановлюємо як поточний
                itemView.deselect();
            } else {
                if (lastSelectedItem != null) {
                    lastSelectedItem.deselect();
                }
                showItemDetails(itemView);// Інакше просто показуємо інформацію
                lastSelectedItem = itemView;
            }
        });
    }

    private void showItemDetails(InventoryItemView itemView) {
        //Показ опису предмета
        lastSelectedItem = itemView;
        itemView.select();
        itemDetails.setText("Предмет: " + itemView.getItemName()+'\n'+"Опис:"+itemView.getDescription());
    }

    public void setCurrentItem(InventoryItemView itemView) {
        currentItemBox.getChildren().clear();
        currentItemText.setText("Вибрано: " + itemView.getItemName());
        if (inventoryController.IsGun(itemView.getItemName())) {
            currentItemBox.getChildren().add(currentItemText);
            inventoryController.setCurrentGun(itemView.getItemName());
            itemDetails.setText("Тепер активний: " + itemView.getItemName());
            return;
        }
        itemDetails.setText("Це не зброя, її не можливо вибрати " + itemView.getItemName());
    }

    public void clearItems() {
        itemList.getChildren().clear();
    }
}


