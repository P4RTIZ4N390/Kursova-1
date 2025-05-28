package utilies;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.util.*;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageLoader {
    /**Працює все через FileInputStream
     *@param path шлях до зображення
     */
    public static Texture loadExternalTexture(String path) {
        try {
            Image image = new Image(new FileInputStream(path));
            return new Texture(image);
        } catch (FileNotFoundException e) {
            System.err.println("Файл не знайдено: " + path);
            return null;
        }
    }

    /**Працює все через FileInputStream
     *@param path шлях до зображення
     *@param frameWidth ширина одного кадру
     *@param frameHeight висота одного кадру
     *@param frameCount кількість кадрів в спрайті
     *@param duration тривалість повного циклу в секундах
     */
    public static AnimatedTexture loadExternalAnimatedTexture(String path, int frameCount, int frameWidth, int frameHeight, double duration) {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("❌ Файл не знайдено: " + path);
            return null;
        }

        try {
            Image image = new Image(new FileInputStream(path));

            AnimationChannel animationChannel = new AnimationChannel(
                    image,
                    frameCount,    // Кількість кадрів
                    frameWidth,    // Ширина одного кадру
                    frameHeight,   // Висота одного кадру
                    Duration.seconds(duration),
                    0, frameCount - 1 // Індекси кадрів
            );

            return new AnimatedTexture(animationChannel);
        } catch (FileNotFoundException e) {
            System.err.println("❌ Помилка завантаження файлу: " + path);
            return null;
        }
    }

    /**Працює все через FileInputStream
     *@param path шлях до зображення
     */
    public static Image loadSpriteSheet(String path) {
        try {
            return new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            System.err.println("Файл не знайдено: " + path);
            return null;
        }
    }

    /**Працює все через FileInputStream
     *@param spriteSheet зображення, або спрайтШіт
     *@param frameHeight висота одного кадру
     *@param frameWidth ширина одного кадру
     *@return список кадрів зі спрайтШіт
     */
    public static List<Image> splitSpriteSheet(Image spriteSheet, int frameWidth, int frameHeight) {
        List<Image> frames = new ArrayList<>();

        PixelReader reader = spriteSheet.getPixelReader();
        int columns = (int) (spriteSheet.getWidth() / frameWidth);
        int rows = (int) (spriteSheet.getHeight() / frameHeight);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                WritableImage frame = new WritableImage(reader, x * frameWidth, y * frameHeight, frameWidth, frameHeight);
                frames.add(frame);
            }
        }

        return frames;
    }

    //Не робоче самого початку, і відкинуте для оптимізації
    public static HitBox createDynamicHitBox(Texture texture) {
        Image sprite = texture.getImage();
        PixelReader reader = sprite.getPixelReader();

        int minX = (int) sprite.getWidth();
        int minY = (int) sprite.getHeight();
        int maxX = 0;
        int maxY = 0;

        for (int y = 0; y < sprite.getHeight(); y++) {
            for (int x = 0; x < sprite.getWidth(); x++) {
                if (reader.getColor(x, y).getOpacity() > 0.05) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        texture.setTranslateX(-minX);
        texture.setTranslateY(-minY);

        return new HitBox("DYNAMIC_HITBOX", new Point2D(minX, minY), BoundingShape.box(width, height));
    }

    /**Найбільш потрібне
     * @param spriteSheet спрайтШіт
     * @param framesCount кількість кадрів
     * @param frameWidth ширина одного кадру
     * @param frameHeight висота одного кадру
     * @param  duration тривалість повного циклу в секундах
     * @return повертає AnimationChannel
     */
    public static AnimationChannel loadAnimationChannel(Image spriteSheet,int framesCount, int frameWidth, int frameHeight, double duration) {
        return new AnimationChannel(spriteSheet,framesCount,frameWidth,frameHeight,Duration.seconds(duration),0, framesCount - 1);
    }
}
