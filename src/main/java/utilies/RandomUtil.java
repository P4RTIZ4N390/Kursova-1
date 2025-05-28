package utilies;

import java.util.Random;

// Клас для генерації випадкових значень
public class RandomUtil {
    private static final Random random = new Random(); // Статичний екземпляр класу Random

    /**
     * @param maxExperiencePoint максимальна кількість досвіду, яка випаде
     * @return випадкову кількість досвіду
     */
    public static double getRandomExperiencePoint(int maxExperiencePoint) {
        return Math.round(random.nextDouble(maxExperiencePoint) * 100.0) / 100.0;
    }

    /**
     * @param min мінімальне ціле число
     * @param max максимальне ціле число
     * @return випадкове цілого числа у діапазоні [min, max]
     */
    public static int getRandomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * @param max не ціле максимальне число
     * @return випадкове цілого числа у діапазоні [-max, max]
     */
    public static double getRandomDoubleBetweenMinusAndPlus(double max) {
        return (Math.random() * 2 - 1) * max;
    }
}
