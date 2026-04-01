package main.java.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import main.java.mino.*;

public class GameRenderer {
    
    public static void drawGame(PlayManager pm, Graphics2D g2) {
        // 1. DRAW BACKGROUND & UI ELEMENTS
        g2.setColor(ColorManager.getColor(Color.white));
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(pm.left_x - 4, pm.top_y, pm.WIDTH + 8, pm.HEIGHT + 8, 15, 15);

        // Draw Grid
        g2.setColor(new Color(100, 100, 100, 50));
        g2.setStroke(new BasicStroke(1f));
        for (int x = pm.left_x; x <= pm.right_x; x += Block.SIZE) {
            g2.drawLine(x, pm.top_y, x, pm.bottom_y);
        }
        for (int y = pm.top_y; y <= pm.bottom_y; y += Block.SIZE) {
            g2.drawLine(pm.left_x, y, pm.right_x, y);
        }

       

        // Draw Sidebar Boxes
        g2.setColor(ColorManager.getColor(Color.white));
        int sx = pm.right_x + 100;
        int sy = pm.bottom_y - 200;
        g2.drawRoundRect(sx, sy, 200, 200, 15, 15);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", sx + 60, sy + 60);


        g2.drawRoundRect(sx, pm.top_y + 140, 200, 250, 15, 15); // Made box slightly taller
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.drawString("SCORE", sx + 45, pm.top_y + 130);

        
        
        int textX = sx + 20;
        int textY = pm.top_y + 180;
        g2.drawString("LEVEL: " + pm.level, textX, textY); textY += 50;
        g2.drawString("LINES: " + pm.lines, textX, textY); textY += 50;

        if (pm.combo > 0) {
            g2.setColor(pm.combo >= 2 ? ColorManager.getColor(Color.yellow) : ColorManager.getColor(Color.white));
            g2.drawString("COMBO: x" + pm.combo, textX, textY);
            textY += 50;
        }
        

        g2.setColor(ColorManager.getColor(Color.white));
        g2.drawString("PTS: " + pm.score, textX, textY);

        // 2. DRAW MINOS
        if (pm.currentMino != null) {
            pm.currentMino.draw(g2, pm.colorblindMode);
        }
        if (pm.nextMino != null) {
            pm.nextMino.draw(g2, pm.colorblindMode);
        }

        for (int i = 0; i < pm.staticBlocks.size(); i++) {
            pm.staticBlocks.get(i).draw(g2, pm.colorblindMode);
        }

        // 3. LINE CLEAR EFFECT
        // Note: Using pm.comboEffectCounter as the generic timer for the red line effect
        if (pm.effectCounterOn) {
            pm.comboEffectCounter++; // Using existing counter
            g2.setColor(ColorManager.getColor(Color.red));
            for (int i = 0; i < pm.effectY.size(); i++) {
                g2.fillRect(pm.left_x, pm.effectY.get(i), pm.WIDTH, Block.SIZE);
            }
            if (pm.comboEffectCounter >= 10) {
                pm.effectCounterOn = false;
                pm.comboEffectCounter = 0;
                pm.effectY.clear();
            }
        }

        // 4. COMBO POPUP EFFECT
        if (pm.comboEffectOn) {
            pm.comboEffectCounter++;
            float alpha = 1.0f - (pm.comboEffectCounter / 60.0f);
            if (alpha > 0) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                String comboText = "COMBO x" + pm.combo + "!";
                int comboX = pm.right_x - 150;
                int comboY = pm.top_y + 300 - pm.comboEffectCounter;
                
                // Shadow
                g2.setColor(new Color(0, 0, 0, (int)(alpha * 200)));
                g2.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
                g2.drawString(comboText, comboX + 2, comboY + 2);
                
                // Main Text
                g2.setColor(ColorManager.getColor(Color.yellow));
                g2.drawString(comboText, comboX, comboY);
                
                if (pm.comboEffectCounter >= 60) {
                    pm.comboEffectOn = false;
                    pm.comboEffectCounter = 0;
                }
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        

        // 5. PAUSE MENU
        if (pm.isPaused) {
            drawPauseMenu(pm, g2);
        }

        
    }



    private static void drawPauseMenu(PlayManager pm, Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        int menuX = GamePanel.WIDTH / 2 - 250;
        int menuY = GamePanel.HEIGHT / 2 - 200;
        int menuWidth = 500;
        int menuHeight = 400;

        g2.setColor(Color.black);
        g2.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);
        g2.setColor(ColorManager.getColor(Color.white));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);

        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        g2.drawString("PAUSED", GamePanel.WIDTH / 2 - 110, menuY + 70);

        String[] pauseOptions = {
            "RESUME", 
            "MUSIC: " + (pm.isMuted ? "OFF" : "ON"), 
            "THEME: " + (pm.currentMusicTheme + 1), 
            "QUIT TO MENU"
        };

        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 28));
        for (int i = 0; i < pauseOptions.length; i++) {
            int optionY = menuY + 150 + (i * 60);
            if (pm.pauseMenuSelection == i) {
                g2.setColor(ColorManager.getColor(Color.yellow));
                g2.drawString("> " + pauseOptions[i], GamePanel.WIDTH / 2 - 120, optionY);
            } else {
                g2.setColor(ColorManager.getColor(Color.white));
                g2.drawString("  " + pauseOptions[i], GamePanel.WIDTH / 2 - 120, optionY);
            }
        }
    }
}