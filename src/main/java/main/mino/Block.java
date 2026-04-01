package main.java.main.mino;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Block extends Rectangle{

    public int x, y;
    public static final int SIZE = 30;
    public Color c;

    public Block(Color c) {
        this.c = c;
    }
    public void draw(Graphics2D g2) {
        draw(g2, false);
    }
    
    public void draw(Graphics2D g2, boolean colorblindMode) {
        int margine = 2;
        Color drawColor = c;
        if(colorblindMode) {
            // Convert to grayscale
            int gray = (int)(c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
            drawColor = new Color(gray, gray, gray);
        }
        g2.setColor(drawColor);
        g2.fillRoundRect(x+margine, y+margine, SIZE-(margine*2), SIZE-(margine*2), 8, 8);
    }
}
