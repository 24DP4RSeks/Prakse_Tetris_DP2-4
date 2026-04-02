package main.java.main;

import java.awt.*;

public class GameOverManager {
    private PlayManager pm;
    private boolean scoreUpdated = false;
    private int gameOverSelection = 0; // 0 = Retry, 1 = Menu

    public GameOverManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        // Update high score once after game over
        if (pm.gameOver && !scoreUpdated) {
            if (!pm.isGuest && pm.db != null) {
                pm.db.updateIfHighScore(pm.currentUsername, pm.score);
            }
            scoreUpdated = true;
        }

        // Game over menu navigation
        if (pm.gameOver) {
            if (KeyHandler.upPressed || KeyHandler.downPressed) {
                gameOverSelection = 1 - gameOverSelection;
                KeyHandler.upPressed = false;
                KeyHandler.downPressed = false;
            }

            if (KeyHandler.spacePressed || KeyHandler.ePressed) {
                if (gameOverSelection == 0) {
                    // Retry
                    GameResetManager.resetGame(pm);
                    pm.gameState = GameState.PLAYING;
                } else {
                    // Return to main menu
                    GameResetManager.resetGame(pm);
                    pm.gameState = GameState.MENU;
                    pm.menuSelection = 0;
                }
                scoreUpdated = false;
                pm.gameOver = false;
                KeyHandler.spacePressed = false;
                KeyHandler.ePressed = false;
            }
        }
    }

    public void draw(Graphics2D g2) {
        GameRenderer.drawGame(pm, g2);

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        g2.setColor(ColorManager.getColor(Color.red));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        g2.drawString("GAME OVER", GamePanel.WIDTH/2 - 200, 250);
        
        g2.setColor(ColorManager.getColor(Color.white));
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.drawString("Player: " + pm.currentUsername, GamePanel.WIDTH/2 - 100, 320);
        g2.drawString("Score: " + pm.score, GamePanel.WIDTH/2 - 80, 370);
        
        // Guest Warning
        if (pm.isGuest) {
            g2.setColor(ColorManager.getColor(Color.orange));
            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 20));
            g2.drawString("(Guest scores are not saved to Leaderboard)", GamePanel.WIDTH/2 - 210, 420);
        } else {
                
            g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 20));
            g2.drawString("Score synced to Cloud!", GamePanel.WIDTH/2 - 110, 420);
        }

        // Draw menu choices
        int menuY = 470;
        int optionSpacing = 40;

        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 28));
        if (gameOverSelection == 0) {
            g2.setColor(ColorManager.getColor(Color.yellow));
            g2.drawString("> RETRY", GamePanel.WIDTH/2 - 100, menuY);
            g2.setColor(ColorManager.getColor(Color.white));
            g2.drawString("  MENU", GamePanel.WIDTH/2 - 100, menuY + optionSpacing);
        } else {
            g2.setColor(ColorManager.getColor(Color.white));
            g2.drawString("  RETRY", GamePanel.WIDTH/2 - 100, menuY);
            g2.setColor(ColorManager.getColor(Color.yellow));
            g2.drawString("> MENU", GamePanel.WIDTH/2 - 100, menuY + optionSpacing);
        }

        g2.setColor(ColorManager.getColor(Color.lightGray));
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        g2.drawString("Use UP / DOWN to switch, SPACE or ENTER to select", GamePanel.WIDTH/2 - 215, menuY + optionSpacing + 40);
    }
}