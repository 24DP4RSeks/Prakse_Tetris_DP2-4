package main;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class MenuManager {
    private PlayManager pm;

    public MenuManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        // Handle menu navigation
        if(KeyHandler.upPressed) {
            pm.menuSelection--;
            if(pm.menuSelection < 0) pm.menuSelection = 2;
            KeyHandler.upPressed = false;
        }
        if(KeyHandler.downPressed) {
            pm.menuSelection++;
            if(pm.menuSelection > 2) pm.menuSelection = 0;
            KeyHandler.downPressed = false;
        }
        if(KeyHandler.ePressed) {
            if(pm.menuSelection == 0) {
                pm.startGame();
            } else if(pm.menuSelection == 1) {
                pm.gameState = GameState.SETTINGS;
                pm.settingsSelection = 0;
            } else if(pm.menuSelection == 2) {
                System.exit(0);
            }
            KeyHandler.ePressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.drawString("TETRIS", GamePanel.WIDTH/2 - 150, 150);
        
        Font menuFont = new Font("Arial", Font.PLAIN, 30);
        g2.setFont(menuFont);
        
        int optionY = 250;
        int spacing = 50;
        
        // Draw Start button
        if(pm.menuSelection == 0) {
            g2.setColor(Color.yellow);
            g2.drawString("> START", GamePanel.WIDTH/2 - 80, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  START", GamePanel.WIDTH/2 - 80, optionY);
        }
        
        optionY += spacing;
        // Draw Settings button
        if(pm.menuSelection == 1) {
            g2.setColor(Color.yellow);
            g2.drawString("> SETTINGS", GamePanel.WIDTH/2 - 80, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  SETTINGS", GamePanel.WIDTH/2 - 80, optionY);
        }
        
        optionY += spacing;
        // Draw Exit button
        if(pm.menuSelection == 2) {
            g2.setColor(Color.yellow);
            g2.drawString("> EXIT", GamePanel.WIDTH/2 - 80, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  EXIT", GamePanel.WIDTH/2 - 80, optionY);
        }
        
        // Draw instructions
        g2.setColor(Color.gray);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Use UP/DOWN to navigate, E to select", GamePanel.WIDTH/2 - 220, optionY + 50);
    }
}