package utilies;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class ConsoleHelper {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleHelper.class);//Якщо буде не лінь перед здачею зроблю логування

    private static final TextFlow textArea = new TextFlow();

    public static final Font font = Font.font("Consolas",18);//Шрифт для звичайних повідомлень
    public static final Font smallFont = Font.font("Consolas",12);//Шрифт для не важливих повідомлень або для показу інформації

    private static final BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));//Зчитувач з консолі

    private static final Queue<MessageRequest> messageQueue = new LinkedList<>();//Черга виводу повідомлень в вікні
    private static boolean isShowingMessage = false;

    /**
     * @param message повідомлення що буде введеним
     */
    public static void writeMessage(String message){//Output method
        System.out.println(message);
    }

    // Виводить знак розділення
    public static void writeSeparator(){System.out.println("===========================================================================================");}

    // Зчитати String з консолі
    public static String readString(){
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Помилка при спробі введення тексту.Спробуйте ще раз.");
            return readString();
        }
    }

    //read whole numbers from console or field to input
    public static int readInt(){
        try {
            return Integer.parseInt(readString());
        } catch (NumberFormatException e) {
            System.out.println("Помилка при спробі введення числа.Спробуйте ще раз.");
            return readInt();
        }
    }

    // Зчитати не ціле число
    public static double readDouble(){
        try {
            return Double.parseDouble(readString());
        }catch (NumberFormatException e){
            System.out.println("Помилка при спробі введення числа.Спробуйте ще раз.");
            return readDouble();
        }
    }

    //Це мій улюблений метод перевірка чи об'єкт дорівнює null
    public static void isNull(Object object){
        if(object == null){
            System.out.println("Сука ,воно чомусь null.");
        }
    }

    public static void writeMessageInTextArea(String message){
        Text text = new Text(message+"\n");
        text.setFill(Color.LIGHTGREEN);
        text.setFont(font);

        if (textArea.getHeight() > textArea.getPrefHeight()) {
            FXGL.getGameTimer().runOnceAfter(() -> writeMessageInTextArea(message), Duration.seconds(4));
            return;
        }

        textArea.getChildren().addLast(text);

        FXGL.getGameTimer().runOnceAfter(() -> textArea.getChildren().remove(text), Duration.seconds(3.5));
    }

    public static TextFlow getTextArea() {
        return textArea;
    }

    /**
     * @param message повідомлення
     * @param durationInSeconds скільки часу буде видно повідомлення
     * @param width ширина вікна
     * @param height висота вікна
     */
    public static void writeMessageInLabelInRightCorner(String message,double durationInSeconds,int width, int height){
        messageQueue.add(new MessageRequest(message, durationInSeconds,width,height));//Додавання повідомлення в чергу
        processNextMessage();//
    }

    private static void processNextMessage(){
        /*
          Якщо якесь повідомлення, ще близько до нижнього краю то нове не виводиться
          і якщо черга пуста рекурсія припиняється
        */

        if(isShowingMessage || messageQueue.isEmpty()) return;

        isShowingMessage = true;

        MessageRequest message = messageQueue.poll();

        Label label = new Label(wrapTextByWords(message.message,33));
        // Розділяємо на окремі рядки
        label.setFont(font);
        label.setTextFill(Color.LIGHTGREEN);
        //встановлюємо стиль
        label.setLayoutX(message.width - 320); // 400 + 20 padding
        label.setLayoutY(message.height-10);// приблизно знизу
        // Встановлюємо розташування
        int amount = (int) Math.ceil((message.durationInSeconds-1.5)/0.02);
        // Кількість разів перенесення вгору
        FXGL.getGameTimer().runAtInterval(() -> label.setLayoutY(label.getLayoutY() - 1),
                Duration.seconds(.02),amount);//По суті новий потік, що працює по типу циклу з лічильником, після завершення коду вичікує певний інтервал
        FXGL.getGameScene().addUINode(label);//Додаємо до сцени повідомлення

        FXGL.getGameTimer().runOnceAfter(()->FXGL.getGameScene().removeUINode(label),Duration.seconds(message.durationInSeconds));
        //Видаляємо повідомлення зі сцени, після закінчення часу

        FXGL.getGameTimer().runOnceAfter(()->{
            isShowingMessage = false;
            processNextMessage();
        }, Duration.seconds(1.5));//Через півтори секунди обробляємо наступне повідомлення
    }


    private record MessageRequest(String message, double durationInSeconds,int width,int height) {}

    /**
     * @param text який буде оброблено
     * @param amountOfSymbols через скільки розділяти символом нового рядка(\n)
     * @return розділений кожні amountOfSymbols, символом нового рядка(\n) текст
     */
    private static String wrapTextByWords(String text, int amountOfSymbols) {
        StringBuilder result = new StringBuilder();
        String[] split = text.split(" "); // Розділяємо текст на слова
        int size = 0; // Кількість символів у поточному рядку

        for (String s : split) {
            if (s.equals("\n")) size=0;
            if (size >= amountOfSymbols) {
                // Якщо ліміт символів досягнуто — починаємо новий рядок
                result.append('\n');
                size = 0;
            }
            result.append(s).append(" ");
            size += s.length(); // Додаємо довжину слова до поточного розміру рядка
        }
        return result.toString();
    }
}
