package main;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class SettingsManager {
    private PlayManager pm;

    public SettingsManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        // Handle settings navigation
        if(KeyHandler.upPressed) {
            pm.settingsSelection--;
            if(pm.settingsSelection < 0) pm.settingsSelection = 3;
            KeyHandler.upPressed = false;
        }
        if(KeyHandler.downPressed) {
            pm.settingsSelection++;
            if(pm.settingsSelection > 3) pm.settingsSelection = 0;
            KeyHandler.downPressed = false;
        }
        if(KeyHandler.ePressed) {
            if(pm.settingsSelection == 0) {
                // Toggle mute
                pm.isMuted = !pm.isMuted;
                if(pm.isMuted) {
                    GamePanel.music.stop();
                } else {
                    // Play current selected theme
                    GamePanel.music.play(5 + pm.currentMusicTheme, true);
                    GamePanel.music.loop();
                }
            } else if(pm.settingsSelection == 1) {
                // Switch music theme
                pm.currentMusicTheme = (pm.currentMusicTheme + 1) % 4;
                // Restart music with new theme if music is on
                if(!pm.isMuted) {
                    GamePanel.music.stop();
                    GamePanel.music.play(5 + pm.currentMusicTheme, true);
                    GamePanel.music.loop();
                }
            } else if(pm.settingsSelection == 2) {
                // Toggle colorblind mode
                pm.colorblindMode = !pm.colorblindMode;
            } else if(pm.settingsSelection == 3) {
                // Back to menu
                pm.gameState = GameState.MENU;
                pm.menuSelection = 0;
            }
            KeyHandler.ePressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.drawString("SETTINGS", GamePanel.WIDTH/2 - 120, 120);
        
        Font settingsFont = new Font("Arial", Font.PLAIN, 25);
        g2.setFont(settingsFont);
        
        int optionY = 200;
        int spacing = 40;
        
        // Draw Music toggle
        if(pm.settingsSelection == 0) {
            g2.setColor(Color.yellow);
            g2.drawString("> Music: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 120, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  Music: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 120, optionY);
        }
        
        optionY += spacing;
        // Draw Music theme
        if(pm.settingsSelection == 1) {
            g2.setColor(Color.yellow);
            g2.drawString("> Theme: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 120, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  Theme: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 120, optionY);
        }
        
        optionY += spacing;
        // Draw Colorblind mode
        if(pm.settingsSelection == 2) {
            g2.setColor(Color.yellow);
            g2.drawString("> Colorblind: " + (pm.colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 120, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  Colorblind: " + (pm.colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 120, optionY);
        }
        
        optionY += spacing;
        // Draw Back
        if(pm.settingsSelection == 3) {
            g2.setColor(Color.yellow);
            g2.drawString("> BACK", GamePanel.WIDTH/2 - 120, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  BACK", GamePanel.WIDTH/2 - 120, optionY);
        }
        
        // Draw instructions
        g2.setColor(Color.gray);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Use UP/DOWN to navigate, E to select", GamePanel.WIDTH/2 - 220, optionY + 50);
    }
}