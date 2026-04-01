package main.java.main;



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import main.java.mino.*;

import java.awt.BasicStroke;

public class GameRenderer {
    public static void drawGame(PlayManager pm, Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(pm.left_x-4, pm.top_y, pm.WIDTH+8, pm.HEIGHT+8, 15, 15);

        g2.setColor(new Color(100, 100, 100, 50));
        g2.setStroke(new BasicStroke(1f));

        for(int x = pm.left_x; x <= pm.right_x; x += Block.SIZE) {
            g2.drawLine(x, pm.top_y, x, pm.bottom_y);
        }

        for(int y = pm.top_y; y <= pm.bottom_y; y += Block.SIZE) {
            g2.drawLine(pm.left_x, y, pm.right_x, y);
        }

        g2.setColor(Color.white);
        int x = pm.right_x + 100;
        int y = pm.bottom_y - 200;
        g2.drawRoundRect(x, y, 200, 200, 15, 15);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        g2.drawRoundRect(x, pm.top_y + 140, 200, 200, 15, 15);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.drawString("SCORE", x+45, pm.top_y + 130);
        x += 20;
        y = pm.top_y + 180;
        g2.drawString("LEVEL: " + pm.level, x , y); y += 50;
        g2.drawString("LINES: " + pm.lines, x , y); y += 50;

        if(pm.combo > 0) {
            g2.setColor(pm.combo >= 2 ? Color.yellow : Color.white);
            g2.drawString("COMBO: x" + pm.combo, x, y);
            y += 50;
        }

        g2.setColor(Color.white);
        g2.drawString("SCORE: " + pm.score, x , y);

        if(pm.currentMino != null) {
            pm.currentMino.draw(g2, pm.colorblindMode);
        }
        pm.nextMino.draw(g2, pm.colorblindMode);

        for(int i = 0; i < pm.staticBlocks.size(); i++) {
            pm.staticBlocks.get(i).draw(g2, pm.colorblindMode);
        }

        if(pm.effectCounterOn) {
            pm.effectCounter++;
            g2.setColor(Color.red);
            for(int i = 0; i < pm.effectY.size(); i++) {
                g2.fillRect(pm.left_x, pm.effectY.get(i), pm.WIDTH, Block.SIZE);
            }
            if(pm.effectCounter == 10) {
                pm.effectCounterOn = false;
                pm.effectCounter = 0;
                pm.effectY.clear();
            }
        }

        if(pm.comboEffectOn) {
            pm.comboEffectCounter++;
            float alpha = 1.0f - (pm.comboEffectCounter / 60.0f);
            if(alpha > 0) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
                g2.setColor(Color.yellow);
                g2.setFont(new Font("Comic Sans MS", Font.BOLD, 40 + pm.comboEffectCounter));
                String comboText = "COMBO x" + pm.combo + "!";
                int textX = pm.right_x - 150;
                int textY = pm.top_y + 300 - pm.comboEffectCounter;
                g2.setColor(new Color(0, 0, 0, (int)(alpha * 200)));
                g2.drawString(comboText, textX + 2, textY + 2);
                g2.setColor(Color.yellow);
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
                g2.drawString(comboText, textX, textY);
                if(pm.comboEffectCounter >= 60) {
                    pm.comboEffectOn = false;
                    pm.comboEffectCounter = 0;
                }
            }
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
        }

        if(pm.isPaused) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
            int menuX = GamePanel.WIDTH/2 - 250;
            int menuY = GamePanel.HEIGHT/2 - 200;
            int menuWidth = 500;
            int menuHeight = 400;
            g2.setColor(Color.black);
            g2.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);
            g2.setColor(Color.white);
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            g2.drawString("PAUSED", GamePanel.WIDTH/2 - 130, menuY + 70);
            Font optionFont = new Font("Comic Sans MS", Font.PLAIN, 28);
            g2.setFont(optionFont);
            int optionY = menuY + 130;
            int lineHeight = 70;
            if(pm.pauseMenuSelection == 0) {
                g2.setColor(Color.yellow);
                g2.drawString("> RESUME", GamePanel.WIDTH/2 - 100, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  RESUME", GamePanel.WIDTH/2 - 100, optionY);
            }
            optionY += lineHeight;
            if(pm.pauseMenuSelection == 1) {
                g2.setColor(Color.yellow);
                g2.drawString("> Music: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 130, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  Music: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 130, optionY);
            }
            optionY += lineHeight;
            if(pm.pauseMenuSelection == 2) {
                g2.setColor(Color.yellow);
                g2.drawString("> THEME: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 120, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  THEME: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 120, optionY);
            }
            optionY += lineHeight;
            if(pm.pauseMenuSelection == 3) {
                g2.setColor(Color.yellow);
                g2.drawString("> MENU", GamePanel.WIDTH/2 - 80, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  MENU", GamePanel.WIDTH/2 - 80, optionY);
            }
            g2.setColor(Color.gray);
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            g2.drawString("Use UP/DOWN to navigate, E to select", GamePanel.WIDTH/2 - 220, menuY + menuHeight - 20);
        }
    }

    public static Color getColorForMode(PlayManager pm, Color c) {
        if(pm.colorblindMode) {
            int gray = (int)(c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
            return new Color(gray, gray, gray);
        }
        return c;
    }
}
