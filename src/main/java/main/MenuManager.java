package main.java.main;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class MenuManager {
    private PlayManager pm;

    public MenuManager(PlayManager pm) {
        this.pm = pm;
    }

    private String[] getOptions() {
        if (!pm.isGuest && ap_hasLoggedInUser(pm.currentUsername)) {
            return new String[] {"PLAY", "LOGOUT", "SETTINGS", "EXIT"};
        }
        return new String[] {"REGISTER", "LOGIN", "GUEST PLAY", "SETTINGS", "EXIT"};
    }

    private boolean ap_hasLoggedInUser(String username) {
        return username != null && !username.isBlank() && !username.equalsIgnoreCase("Guest");
    }

    public void update() {
        String[] options = getOptions();

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

        if(pm.menuSelection >= options.length) pm.menuSelection = 0;

        if(KeyHandler.ePressed || KeyHandler.spacePressed) {
            String selected = options[pm.menuSelection];
            switch(selected) {
                case "REGISTER":
                    pm.gameState = GameState.REGISTER;
                    pm.loginManager.reset();
                    break;
                case "LOGIN":
                    pm.gameState = GameState.LOGIN;
                    pm.loginManager.reset();
                    break;
                case "GUEST PLAY":
                    pm.isGuest = true;
                    pm.currentUsername = "Guest";
                    pm.startGame();
                    break;
                case "PLAY":
                    pm.startGame();
                    break;
                case "LOGOUT":
                    pm.isGuest = true;
                    pm.currentUsername = "Guest";
                    pm.gameState = GameState.MENU;
                    pm.menuSelection = 0;
                    break;
                case "SETTINGS":
                    pm.gameState = GameState.SETTINGS;
                    pm.settingsSelection = 0;
                    break;
                case "EXIT":
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

        String[] options = getOptions();

        if (ap_hasLoggedInUser(pm.currentUsername)) {
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
            g2.setColor(Color.green);
            g2.drawString("Welcome: " + pm.currentUsername, GamePanel.WIDTH/2 - 140, 220);
        }

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
        g2.drawString("Use ARRUP or ARRDOWN to navigate, ENTER to select", GamePanel.WIDTH/2 - 250, 650);
    }
}