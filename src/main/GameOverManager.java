package main;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class GameOverManager {
    private PlayManager pm;

    public GameOverManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        if(KeyHandler.spacePressed || KeyHandler.menuPressed) {
            GameResetManager.resetGame(pm);
            pm.gameState = GameState.MENU;
            pm.menuSelection = 0;
            KeyHandler.spacePressed = false;
            KeyHandler.menuPressed = false;
        }
    }

    public void draw(Graphics2D g2) {
        GameRenderer.drawGame(pm, g2);
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        g2.setColor(Color.red);
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        g2.drawString("GAME OVER", GamePanel.WIDTH/2 - 200, 250);
        
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.drawString("Score: " + pm.score, GamePanel.WIDTH/2 - 80, 350);
        g2.drawString("Press SPACE to return to menu", GamePanel.WIDTH/2 - 200, 450);
        g2.drawString("or ESC", GamePanel.WIDTH/2 - 40, 500);
    }
}