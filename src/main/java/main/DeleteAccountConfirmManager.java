package main.java.main;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.BasicStroke;

public class DeleteAccountConfirmManager {
    private PlayManager pm;

    /**
     * funkcija DeleteAccountConfirmManager pieņem PlayManager tipa vērtību pm un atgriež void tipa vērtību null.
     * Šī konstruktorfunkcija inicializē DeleteAccountConfirmManager ar PlayManager referenci.
     */
    public DeleteAccountConfirmManager(PlayManager pm) {
        this.pm = pm;
    }

    /**
     * funkcija update pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī funkcija apstrādā apstiprināšanas izvēlnes tastatūras ievadi un veic konta dzēšanu vai atcelšanu.
     */
    public void update() {
        // ESC cancels and returns to menu with existing user session
        if(KeyHandler.menuPressed) {
            pm.gameState = GameState.MENU;
            pm.menuSelection = 0;
            KeyHandler.menuPressed = false;
            return;
        }

        // Navigation via left/right
        if(KeyHandler.leftPressed) {
            pm.confirmSelection = 0; // No
            KeyHandler.leftPressed = false;
        }
        if(KeyHandler.rightPressed) {
            pm.confirmSelection = 1; // Yes
            KeyHandler.rightPressed = false;
        }

        if(KeyHandler.ePressed || KeyHandler.spacePressed) {
            if(pm.confirmSelection == 0) {
                pm.gameState = GameState.MENU;
                pm.menuSelection = 0;
            } else {
                if(pm.db != null && pm.currentUsername != null && !pm.currentUsername.isBlank() && !pm.currentUsername.equals("Guest")) {
                    pm.db.deleteAccount(pm.currentUsername);
                }
                pm.isGuest = true;
                pm.currentUsername = "Guest";
                pm.gameState = GameState.MENU;
                pm.menuSelection = 0;
            }
            KeyHandler.ePressed = false;
            KeyHandler.spacePressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        int menuX = GamePanel.WIDTH/2 - 250;
        int menuY = GamePanel.HEIGHT/2 - 200;
        int menuWidth = 500;
        int menuHeight = 200;

        g2.setColor(Color.black);
        g2.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);

        g2.setColor(ColorManager.getColor(Color.white));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);

        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        g2.drawString("Delete account?", menuX + 90, menuY + 50);

        int noX = menuX + 100;
        int yesX = menuX + 340;

        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        g2.setColor(pm.confirmSelection == 0 ? ColorManager.getColor(Color.yellow) : ColorManager.getColor(Color.white));
        g2.drawString("No", noX, menuY + 120);

        g2.setColor(pm.confirmSelection == 1 ? ColorManager.getColor(Color.yellow) : ColorManager.getColor(Color.white));
        g2.drawString("Yes", yesX, menuY + 120);

        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        g2.setColor(ColorManager.getColor(Color.white));
        g2.drawString("Use LEFT/RIGHT to choose, ENTER to confirm, ESC to cancel", menuX + 20, menuY + menuHeight - 20);
    }
}
