package model.items.inventory;

import com.almasb.fxgl.input.UserAction;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.items.Item;


import javafx.scene.layout.VBox;

import com.almasb.fxgl.dsl.FXGL;


import java.util.*;

public class InventoryInteractionView extends HBox {//singleton pattern

    private static InventoryInteractionView instance;

    public static InventoryInteractionView getInstance(Inventory playerInventory) {
        if (instance == null) {
            instance=new InventoryInteractionView(playerInventory,Inventory.getInventory(50));
        }
        instance.closeInventory();
        FXGL.getGameScene().addUINode(instance);
        return instance;
    }
    //Контейнер, це істота та скриня і так далі
    private final VBox playerInventoryView;//панель інвентарю гравця
    private final VBox containerInventoryView;//панель інвентарю гравця
    private final Inventory playerInventory;
    private Inventory containerInventory;
    private final List<String> playerItems=new ArrayList<>();//список інвентарю гравця
    private final List<String> containerItems=new ArrayList<>();//список контейнера гравця
    private int selectedPlayerIndex = -1;//який елемент списку інвентарю гравця вибрано
    private int selectedContainerIndex = -1;//який елемент списку інвентарю контейнера вибрано
    private boolean isPlayerSide = true; // Чи активна панель гравця
    private final Text containerTitle;

    public void setContainerInventory(Inventory containerInventory) {
        this.containerInventory = containerInventory;
    }

    public void setPlayerSide(boolean playerSide) {
        isPlayerSide = playerSide;
    }

    private InventoryInteractionView(Inventory playerInventory, Inventory containerInventory) {
        this.playerInventory = playerInventory;
        this.containerInventory = containerInventory;

        //Фон
        setSpacing(20);
        setPrefSize(600, 400);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-border-color: #868585; -fx-border-width: 2px;");
        setLayoutX(100);
        setLayoutY(150);

        // Панель інвентаря гравця
        playerInventoryView = new VBox(5);//аргумент, це відступ між елементами
        playerInventoryView.setPrefSize(250, 350);//Рекомендовані розміри панелі
        playerInventoryView.setStyle("-fx-background-color: black;");

        // ScrollPane обгортає playerInventoryView
        ScrollPane scrollPlayer = new ScrollPane(playerInventoryView);
        scrollPlayer.setPrefSize(250, 350);
        scrollPlayer.setFitToWidth(true);
        scrollPlayer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPlayer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Панель інвентаря контейнера
        containerInventoryView = new VBox(5);
        containerInventoryView.setPrefSize(250, 350);
        containerInventoryView.setStyle("-fx-background-color: black;");

        // ScrollPane обгортає playerInventoryView
        ScrollPane containerInventoryScroll = new ScrollPane(containerInventoryView);
        containerInventoryScroll.setPrefSize(250, 350);
        containerInventoryScroll.setFitToWidth(true);
        containerInventoryScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        containerInventoryScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Заголовки
        Text playerTitle = new Text("Інвентар гравця");
        playerTitle.setFill(Color.WHITE);
        containerTitle = new Text("Невідомо");//Не забути додати ім'я панелі
        containerTitle.setFill(Color.WHITE);

        VBox playerPanel = new VBox(10, playerTitle,scrollPlayer);//10 це відступ між елементами
        VBox containerPanel = new VBox(10, containerTitle, containerInventoryScroll);

        getChildren().addAll(playerPanel, containerPanel);//Додаємо списки

        loadItems();
        setupControls();
    }

    private void loadItems() {
        //Очищаємо панелі та списки
        playerInventoryView.getChildren().clear();
        containerInventoryView.getChildren().clear();
        playerItems.clear();
        containerItems.clear();

        for (Item item : playerInventory.getItems().keySet()) {
            InventoryItemView itemView = new InventoryItemView(item.getName()+"("+playerInventory.getItems().get(item)+")", item.getWeight()*playerInventory.getItems().get(item), item.getDescription(), 0);
            addContextMenu(itemView, item, true);
            playerInventoryView.getChildren().add(itemView);//Додаємо предмет до панелі
            playerItems.add(item.getName());//Додаємо предмет до списку
        }

        for (Item item : containerInventory.getItems().keySet()) {
            InventoryItemView itemView = new InventoryItemView(item.getName()+"("+containerInventory.getItems().get(item)+")", item.getWeight()*containerInventory.getItems().get(item), item.getDescription(), 0);
            addContextMenu(itemView, item, false);
            containerInventoryView.getChildren().add(itemView);//Додаємо предмет до панелі
            containerItems.add(item.getName());//Додаємо предмет до списку
        }
    }

    private void addContextMenu(InventoryItemView itemView, Item item, boolean isPlayerItem) {
        //При клацанні правою кнопкою по предмету, викликати контекстне меню
        ContextMenu menu = new ContextMenu();
        MenuItem transferItem = new MenuItem("Передати");//На даний час, просто передати один вибраний предмет

        transferItem.setOnAction(e -> transferItem(item,1,isPlayerItem));//Встановлення в дію, метода
        menu.getItems().add(transferItem);//Додавання дій "Передачі"
        itemView.setOnMouseClicked(event -> {//При клацанні курсором
            if (event.getButton() == MouseButton.SECONDARY) {//Перевірка,яка кнопка натиснута
                menu.show(itemView, event.getScreenX(), event.getScreenY());//Показати (1)на який елемент натиснуто, (2,3)координати де буде показано
            }
        });
    }

    protected void transferItem(Item item,int amount,boolean isFromPlayer) {
        //Передача предметів
        if (isFromPlayer) {
                playerInventory.removeItem(item, amount);
                containerInventory.addItems(item,amount);
        } else {
                containerInventory.removeItem(item, amount);
                playerInventory.addItems(item,amount);
        }
        loadItems();
    }

    private void setupControls() {
        //Встановити керування
        UserAction moveUpAction = new UserAction("Move Up Inventory") {
            //Переміщення по вибранні панелі вгору
            @Override
            protected void onActionBegin() {//Як дія почалась
                if (isPlayerSide) {//Яка панель зараз вибрана істина-гравця, хибна-контейнер
                    if (selectedPlayerIndex <= 0)
                        selectedPlayerIndex = playerInventory.getItems().size() - 1;
                    else
                        selectedPlayerIndex--;
                } else {
                    if (selectedContainerIndex <= 0)
                        selectedContainerIndex = containerInventory.getItems().size() - 1;
                    else
                        selectedContainerIndex--;
                }
                highlightSelection();//Підсвіти вибір
            }
        };

        UserAction moveDownAction = new UserAction("Move Down Inventory") {
            //Переміщення по вибранні панелі донизу
            @Override
            protected void onActionBegin() {
                if (isPlayerSide) {//Яка панель зараз вибрана істина-гравця, хибна-контейнер
                    if (selectedPlayerIndex < playerInventory.getItems().size() - 1)
                        selectedPlayerIndex++;
                    else
                        selectedPlayerIndex = 0;
                } else {
                    if (selectedContainerIndex < containerInventory.getItems().size() - 1)
                        selectedContainerIndex++;
                    else
                        selectedContainerIndex = 0;
                }
                highlightSelection();//Підсвіти вибір
            }
        };



        UserAction switchSideActionRight = new UserAction("Switch Side Right") {
            // Код для перемикання на панель контейнера
            @Override
            protected void onActionBegin() {
                isPlayerSide = false;
            }
        };

        UserAction switchSideActionLeft = new UserAction("Switch Side Left") {
            // Код для перемикання на панель гравця
            @Override
            protected void onActionBegin() {

                isPlayerSide = true;
            }
        };


        UserAction transferItemAction = new UserAction("Transfer Interaction") {
            @Override
            protected void onActionBegin() {
                // Передача предмета
                if (isPlayerSide) {
                    if(selectedPlayerIndex < 0) return ;//Це щоб не кидало виняток при випадковому натиску на Enter
                    showAmountSelectionDialog(playerInventory.checkItemAndReturn(playerItems.get(selectedPlayerIndex)),true);
                }else{
                    if(selectedContainerIndex < 0) return ;//Це щоб не кидало виняток при випадковому натиску на Enter
                    showAmountSelectionDialog(containerInventory.checkItemAndReturn(containerItems.get(selectedContainerIndex)),false);
                }
                //Якщо, було натиснуто передачу ще раз не передавало випадкові предмети
                selectedContainerIndex=-1;
                selectedPlayerIndex=-1;
            }
        };
        
        // Призначаємо дії на клавіші
        FXGL.getInput().addAction(moveUpAction, KeyCode.UP);
        FXGL.getInput().addAction(moveDownAction, KeyCode.DOWN);
        FXGL.getInput().addAction(switchSideActionRight, KeyCode.RIGHT);
        FXGL.getInput().addAction(switchSideActionLeft, KeyCode.LEFT);
        FXGL.getInput().addAction(transferItemAction, KeyCode.ENTER);
    }

    public void closeInventory() {
        selectedContainerIndex=-1;
        selectedPlayerIndex=-1;
        playerInventory.getInventoryController().updateInventoryView();//Оновлюємо перегляд інвентарю гравця
        this.setVisible(false);
    }


    private void highlightSelection() {
        if(isPlayerSide){
            for (int i = 0; i < playerInventoryView.getChildren().size(); i++) {
                playerInventoryView.getChildren().get(i).setStyle(i == selectedPlayerIndex ? "-fx-background-color: gray;" : "-fx-background-color: black;");
            }
        } else {
            for (int i = 0; i < containerInventoryView.getChildren().size(); i++) {
                containerInventoryView.getChildren().get(i).setStyle(i == selectedContainerIndex ? "-fx-background-color: gray;" : "-fx-background-color: black;");
            }
        }
    }

    private void showAmountSelectionDialog(Item item, boolean isPlayerSide) {
        if (item == null) return;

        // Отримуємо максимальну кількість предметів у інвентарі (або 1, якщо немає в мапі)
        int maxAmount = isPlayerSide ? playerInventory.getItems().getOrDefault(item, 1)
                : containerInventory.getItems().getOrDefault(item, 1);

        // Якщо предметів тільки один, одразу передаємо його без відкриття вікна
        if (maxAmount == 1) {
            transferItem(item, 1, isPlayerSide);
            return;
        }

        // Текст для показу вибраної кількості
        Text amountText = new Text("Кількість: 1");

        // Створюємо повзунок
        Slider slider = new Slider(1, maxAmount, 1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);

        // Оновлення тексту при зміні значення повзунка
        slider.valueProperty().addListener((obs, oldVal, newVal) -> amountText.setText("Кількість: " + newVal.intValue()));

        // Кнопка підтвердження
        Button confirmButton = new Button("Передати");
        confirmButton.setOnAction(e -> {
            int selectedAmount = (int) slider.getValue();
            transferItem(item, selectedAmount, isPlayerSide);
        });

        // Додаємо елементи у вікно
        VBox vbox = new VBox(10, new Text("Виберіть кількість"), amountText, slider, confirmButton);
        vbox.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        // Відображаємо вікно
        FXGL.getDialogService().showBox("Передача предмета", vbox, confirmButton);
    }

    public void setNewInteraction(Inventory interaction,String containerName) {
        setContainerInventory(interaction);
        containerTitle.setText(containerName);
        setPlayerSide(true);
        setVisible(true);
        selectedContainerIndex=-1;
        selectedPlayerIndex=-1;
        loadItems();
    }
}
