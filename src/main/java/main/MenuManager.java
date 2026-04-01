package main.java.main;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class MenuManager {
    private PlayManager pm;
    private final String[] options = {"REGISTER", "LOGIN", "GUEST PLAY", "SETTINGS", "EXIT"};

    public MenuManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        if(KeyHandler.upPressed) {
            pm.menuSelection--;
            if(pm.menuSelection < 0) pm.menuSelection = options.length - 1;
            KeyHandler.upPressed = false;
        }
        if(KeyHandler.downPressed) {
            pm.menuSelection++;
            if(pm.menuSelection > options.length - 1) pm.menuSelection = 0;
            KeyHandler.downPressed = false;
        }
        if(KeyHandler.ePressed || KeyHandler.spacePressed) {
            switch(pm.menuSelection) {
                case 0: // Register
                    pm.gameState = GameState.REGISTER;
                    pm.loginManager.reset();
                    break;
                case 1: // Login
                    pm.gameState = GameState.LOGIN;
                    pm.loginManager.reset();
                    break;
                case 2: // Guest
                    pm.isGuest = true;
                    pm.currentUsername = "Guest";
                    pm.startGame();
                    break;
                case 3: // Settings
                    pm.gameState = GameState.SETTINGS;
                    pm.settingsSelection = 0;
                    break;
                case 4: // Exit
                    System.exit(0);
                    break;
            }
            KeyHandler.ePressed = false;
            KeyHandler.spacePressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 80));
        g2.drawString("TETRIS", GamePanel.WIDTH/2 - 150, 150);
        
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        for(int i = 0; i < options.length; i++) {
            if(pm.menuSelection == i) {
                g2.setColor(Color.yellow);
                g2.drawString("> " + options[i], GamePanel.WIDTH/2 - 150, 280 + (i * 65));
            } else {
                g2.setColor(Color.white);
                g2.drawString("  " + options[i], GamePanel.WIDTH/2 - 150, 280 + (i * 65));
            }
        }
        
        g2.setColor(Color.gray);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.drawString("Use W/UP or S/DOWN to navigate, E/SPACE to select", GamePanel.WIDTH/2 - 250, 650);
    }
}