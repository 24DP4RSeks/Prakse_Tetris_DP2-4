package main.java.main;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorManager {
    private static final Map<Color, Color> grayscaleCache = new HashMap<>();
    private static boolean colorblindMode = false;

    public static void setColorblindMode(boolean enabled) {
        if (colorblindMode != enabled) {
            colorblindMode = enabled;
            // Clear cache when mode changes
            grayscaleCache.clear();
        }
    }

    public static Color getColor(Color original) {
        if (!colorblindMode) {
            return original;
        }

        // Check cache first
        Color cached = grayscaleCache.get(original);
        if (cached != null) {
            return cached;
        }

        // Calculate and cache grayscale
        int gray = (int)(original.getRed() * 0.299 + original.getGreen() * 0.587 + original.getBlue() * 0.114);
        Color grayscale = new Color(gray, gray, gray);
        grayscaleCache.put(original, grayscale);
        return grayscale;
    }
}