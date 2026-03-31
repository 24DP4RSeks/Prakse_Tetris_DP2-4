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
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        g2.drawString("TETRIS", GamePanel.WIDTH/2 - 150, 150);
        
        Font menuFont = new Font("Comic Sans MS", Font.PLAIN, 30);
        g2.setFont(menuFont);
        
        // Draw Start button
        if(pm.menuSelection == 0) {
            g2.setColor(Color.yellow);
            g2.drawString("> START", GamePanel.WIDTH/2 - 80, 280);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  START", GamePanel.WIDTH/2 - 80, 280);
        }
        
        // Draw Settings button
        if(pm.menuSelection == 1) {
            g2.setColor(Color.yellow);
            g2.drawString("> SETTINGS", GamePanel.WIDTH/2 - 120, 340);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  SETTINGS", GamePanel.WIDTH/2 - 120, 340);
        }
        
        // Draw Exit button
        if(pm.menuSelection == 2) {
            g2.setColor(Color.yellow);
            g2.drawString("> EXIT", GamePanel.WIDTH/2 - 80, 400);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  EXIT", GamePanel.WIDTH/2 - 80, 400);
        }
        
        g2.setColor(Color.gray);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.drawString("Use W/UP or S/DOWN to navigate, E to select", GamePanel.WIDTH/2 - 250, 550);
        g2.drawString("Press F for fullscreen", GamePanel.WIDTH/2 - 100, 590);
        g2.drawString("Press R during game to restart", GamePanel.WIDTH/2 - 140, 630);
        
        // Draw title
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        g2.drawString("Simple Tetris Game", GamePanel.WIDTH/2 - 80, GamePanel.HEIGHT - 20);
    }
}