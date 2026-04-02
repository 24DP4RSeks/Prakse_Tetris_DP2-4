package main.java.main;



import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class SettingsManager {
    private PlayManager pm;

    /**
     * funkcija SettingsManager pieņem PlayManager tipa vērtību pm un atgriež void tipa vērtību null.
     * Šī konstruktorfunkcija inicializē iestatījumu pārvaldnieku ar PlayManager objektu.
     */
    public SettingsManager(PlayManager pm) {
        this.pm = pm;
    }

    /**
     * funkcija update pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī funkcija pārvalda iestatījumu navigāciju un attiecīgo opciju aktivizēšanu.
     */
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
                ColorManager.setColorblindMode(pm.colorblindMode);
            } else if(pm.settingsSelection == 3) {
                // Back to menu
                pm.gameState = GameState.MENU;
                pm.menuSelection = 0;
            }
            KeyHandler.ePressed = false;
        }
    }

    /**
     * funkcija draw pieņem Graphics2D tipa vērtību g2 un atgriež void tipa vērtību null.
     * Šī funkcija zīmē iestatījumu ekrānu ar pieejamajām opcijām.
     */
    public void draw(Graphics2D g2) {
        g2.setColor(ColorManager.getColor(Color.white));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        g2.drawString("SETTINGS", GamePanel.WIDTH/2 - 180, 150);
        
        Font settingsFont = new Font("Comic Sans MS", Font.PLAIN, 30);
        g2.setFont(settingsFont);
        
        // Draw Music button (Mute/Unmute)
        if(pm.settingsSelection == 0) {
            g2.setColor(ColorManager.getColor(Color.yellow));
            g2.drawString("> MUSIC: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 150, 260);
        } else {
            g2.setColor(ColorManager.getColor(Color.white));
            g2.drawString("  MUSIC: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 150, 260);
        }
        
        // Draw Music Theme button
        if(pm.settingsSelection == 1) {
            g2.setColor(ColorManager.getColor(Color.yellow));
            g2.drawString("> THEME: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 150, 300);
        } else {
            g2.setColor(ColorManager.getColor(Color.white));
            g2.drawString("  THEME: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 150, 300);
        }
        
        // Draw Colorblind mode button
        if(pm.settingsSelection == 2) {
            g2.setColor(ColorManager.getColor(Color.yellow));
            g2.drawString("> COLORBLIND: " + (pm.colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 220, 340);
        } else {
            g2.setColor(ColorManager.getColor(Color.white));
            g2.drawString("  COLORBLIND: " + (pm.colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 220, 340);
        }
        
        // Draw Back button
        if(pm.settingsSelection == 3) {
            g2.setColor(ColorManager.getColor(Color.yellow));
            g2.drawString("> BACK", GamePanel.WIDTH/2 - 80, 380);
        } else {
            g2.setColor(ColorManager.getColor(Color.white));
            g2.drawString("  BACK", GamePanel.WIDTH/2 - 80, 380);
        }
        
        g2.setColor(ColorManager.getColor(Color.gray));
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.drawString("Use ARRUP / ARRDOWN to navigate, ENTER to toggle/select", GamePanel.WIDTH/2 - 280, 550);

    }

}