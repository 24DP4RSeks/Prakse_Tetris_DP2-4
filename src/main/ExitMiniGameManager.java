package main;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.BasicStroke;

public class ExitMiniGameManager {
    private PlayManager pm;

    public ExitMiniGameManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        // Check for ESC to cancel
        if(KeyHandler.menuPressed) {
            pm.gameState = GameState.PLAYING; // Return to pause menu
            pm.isPaused = true;
            pm.pauseMenuSelection = 0;
            KeyHandler.menuPressed = false;
            return;
        }
        
        // Navigation
        if(KeyHandler.leftPressed) {
            pm.confirmSelection = 0; // No
            KeyHandler.leftPressed = false;
        }
        if(KeyHandler.rightPressed) {
            pm.confirmSelection = 1; // Yes
            KeyHandler.rightPressed = false;
        }
        
        // Confirm
        if(KeyHandler.ePressed) {
            if(pm.confirmSelection == 0) {
                // No, return to pause menu
                pm.gameState = GameState.PLAYING;
                pm.isPaused = true;
                pm.pauseMenuSelection = 0;
            } else {
                // Yes, exit to menu
                pm.gameState = GameState.MENU;
                pm.menuSelection = 0;
                pm.isPaused = false;
                pm.pauseMenuSelection = 0;
            }
            KeyHandler.ePressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        // Draw the game in background
        GameRenderer.drawGame(pm, g2);
        
        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        // Main box
        int menuX = GamePanel.WIDTH/2 - 250;
        int menuY = GamePanel.HEIGHT/2 - 200;
        int menuWidth = 500;
        int menuHeight = 400;
        
        g2.setColor(Color.black);
        g2.fillRoundRect(menuX, menuY + 50, menuWidth, menuHeight / 2, 20, 20);

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(menuX, menuY + 50, menuWidth, menuHeight / 2, 20, 20);
        
        // Title
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        g2.drawString("You sure?", menuX + menuWidth / 2 / 2 + 30, menuY + 100);
        
        // No button
        int buttonWidth = 80;
        int buttonHeight = 40;
        int noX = menuX + 50;
        int buttonY = menuY + 100;
        
        if(pm.confirmSelection == 0) {
            g2.setColor(Color.yellow);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("No", noX + 20, buttonY + 75);
        
        // Yes button
        int yesX = menuX + menuWidth - 50 - buttonWidth;
        
        if(pm.confirmSelection == 1) {
            g2.setColor(Color.yellow);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("Yes", yesX + 20, buttonY + 75);
        
        // Instructions
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        g2.drawString("Use A/D to select, E to confirm", menuX + menuX / 2 - 60, menuHeight);

    }
}