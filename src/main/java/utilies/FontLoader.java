package utilies;

import javafx.scene.text.Font;
import java.io.InputStream;

public class FontLoader {

    /**
     * @param size розміри шрифту
     * @return шрифт
     */
    public static Font loadPirataFont(double size) {
        InputStream fontStream = FontLoader.class
                .getResourceAsStream("/assets/ui/fonts/PirataOne-Regular.ttf");

        if (fontStream == null) {
            throw new RuntimeException("Шрифт не знайдено!");
        }

        return Font.loadFont(fontStream, size);
    }
}

