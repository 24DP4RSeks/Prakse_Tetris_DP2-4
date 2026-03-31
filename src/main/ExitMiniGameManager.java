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
        if(KeyHandler.spacePressed) {
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
            KeyHandler.spacePressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        // Draw the game in background
        GameRenderer.drawGame(pm, g2);
        
        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        // Main box
        int boxWidth = 400;
        int boxHeight = 200;
        int boxX = GamePanel.WIDTH/2 - boxWidth/2;
        int boxY = GamePanel.HEIGHT/2 - boxHeight/2;
        
        g2.setColor(new Color(50, 50, 50, 220));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        
        // Title
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString("Are you sure you want to leave?", boxX + 20, boxY + 50);
        
        // No button
        int buttonWidth = 80;
        int buttonHeight = 40;
        int noX = boxX + 50;
        int buttonY = boxY + 100;
        
        if(pm.confirmSelection == 0) {
            g2.setColor(Color.yellow);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("No", noX + 20, buttonY + 25);
        
        // Yes button
        int yesX = boxX + boxWidth - 50 - buttonWidth;
        
        if(pm.confirmSelection == 1) {
            g2.setColor(Color.yellow);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("Yes", yesX + 20, buttonY + 25);
        
        // Instructions
        g2.setColor(Color.gray);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Use LEFT/RIGHT to select, SPACE to confirm", boxX + 20, boxY + boxHeight - 20);
    }
}