package main.java.main;

import java.awt.*;

public class GameOverManager {
    private PlayManager pm;
    private boolean scoreUpdated = false;

    public GameOverManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        // Send score once when game over occurs
        if (!scoreUpdated && !pm.isGuest && pm.gameOver) {
            // Check if db is connected before attempting update
            if (pm.db != null) {
                pm.db.updateIfHighScore(pm.currentUsername, pm.score);
            }
            scoreUpdated = true;
        }

        if(KeyHandler.spacePressed || KeyHandler.menuPressed) {
            GameResetManager.resetGame(pm);
            pm.gameState = GameState.MENU;
            pm.menuSelection = 0;
            scoreUpdated = false;
            KeyHandler.spacePressed = false;
            KeyHandler.menuPressed = false;
        }

        if(pm.gameOver && !scoreUpdated) {
            pm.db.updateIfHighScore(pm.currentUsername, pm.score);
            scoreUpdated = true;
}
    }

    public void draw(Graphics2D g2) {
        GameRenderer.drawGame(pm, g2);

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        g2.setColor(Color.red);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        g2.drawString("GAME OVER", GamePanel.WIDTH/2 - 200, 250);
        
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.drawString("Player: " + pm.currentUsername, GamePanel.WIDTH/2 - 100, 320);
        g2.drawString("Score: " + pm.score, GamePanel.WIDTH/2 - 80, 370);
        
        // Guest Warning
        if (pm.isGuest) {
            g2.setColor(Color.orange);
            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 20));
            g2.drawString("(Guest scores are not saved to Leaderboard)", GamePanel.WIDTH/2 - 210, 420);
        } else {
            g2.setColor(Color.green);
            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 20));
            g2.drawString("Score synced to Cloud!", GamePanel.WIDTH/2 - 110, 420);
        }

        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
        g2.drawString("Press SPACE to return to Menu", GamePanel.WIDTH/2 - 220, 550);
    }
}