package main.config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {
    private static final Map<String, BufferedImage> textureCache = new HashMap<>();

    public static BufferedImage getTexture(String fileName) {
        if (textureCache.containsKey(fileName)) {
            return textureCache.get(fileName);
        }

        try {
            File file = new File("assets/images/" + fileName);
            if (file.exists()) {
                BufferedImage image = ImageIO.read(file);
                textureCache.put(fileName, image);
                return image;
            }
        } catch (Exception e) {
            System.err.println("Помилка завантаження спрайта " + fileName + ": " + e.getMessage());
        }

        return null;
    }
}
