package main.java.main;




import java.awt.Graphics2D;

import main.java.mino.*;

import java.awt.Font;
import java.awt.Color;
import java.awt.BasicStroke;

public class GameManager {
    private PlayManager pm;

    public GameManager(PlayManager pm) {
        this.pm = pm;
    }

    public void update() {
        try {
            // Check if 1 button pressed to toggle pause
            if(KeyHandler.pausePressed) {
                pm.isPaused = !pm.isPaused;  // Toggle the pause state
                KeyHandler.pausePressed = false;  // Clear the toggle flag immediately
                pm.pauseMenuSelection = 0;  // Reset menu selection
            }
            
            // Handle pause menu if paused
            if(pm.isPaused) {
                updatePauseMenu();
                return;  // Don't process game logic while paused
            }
        
        // Check if R pressed to restart game
        if(KeyHandler.restartPressed) {
            GameResetManager.resetGame(pm);
            KeyHandler.restartPressed = false;
            return;
        }

        
        
        // Check if ESC pressed to return to menu (only when NOT paused)
        if(KeyHandler.menuPressed) {
            pm.gameState = GameState.MENU;
            pm.menuSelection = 0;
            KeyHandler.menuPressed = false;
            return;
        }
        
        // Check if the currentMino is active
        if(pm.currentMino.active == false){

            // if the mino is not active, put it into the staticBlock
            pm.staticBlocks.add(pm.currentMino.b[0]);
            pm.staticBlocks.add(pm.currentMino.b[1]);
            pm.staticBlocks.add(pm.currentMino.b[2]);
            pm.staticBlocks.add(pm.currentMino.b[3]);
            
            // Play block hit sound
            GamePanel.se.play(4, false);

            //check if the game is over
            if(pm.currentMino.b[0].x == pm.MINO_START_X && pm.currentMino.b[0].y == pm.MINO_START_Y){
                // this means the currentMino immediately collided a block and couldnt move at all
                // so its xy are the same with the nextMino
                pm.gameOver = true;
                pm.gameState = GameState.GAME_OVER;
            }

            pm.currentMino.deactivating = false;

            // replace the currentMino with the nextMino
            pm.currentMino = pm.nextMino;
            pm.currentMino.setXY(pm.MINO_START_X, pm.MINO_START_Y);
            pm.nextMino = MinoFactory.pickMino();
            pm.nextMino.setXY(pm.NEXTMINO_X, pm.NEXTMINO_Y);

            // When line is full = delete line
            LineClearManager.checkDelete(pm);

        }
        else {
            pm.currentMino.update();
        }
        } catch(Exception ex) {
            // Handle any unexpected errors gracefully
        }
    }

    private void updatePauseMenu() {
        // Navigation - must check and clear immediately
        if(KeyHandler.upPressed) {
            pm.pauseMenuSelection--;
            if(pm.pauseMenuSelection < 0) pm.pauseMenuSelection = 3;
            KeyHandler.upPressed = false;
        } else if(KeyHandler.downPressed) {
            pm.pauseMenuSelection++;
            if(pm.pauseMenuSelection > 3) pm.pauseMenuSelection = 0;
            KeyHandler.downPressed = false;
        }
        
        // Selection with SPACE or ENTER
        if(KeyHandler.ePressed) {
            executePauseMenuSelection();
            KeyHandler.ePressed = false;
        }
    }

    private void executePauseMenuSelection() {
        if(pm.pauseMenuSelection == 0) {
            // Resume
            pm.isPaused = false;
            pm.pauseMenuSelection = 0;
        } else if(pm.pauseMenuSelection == 1) {
            // Toggle mute
            pm.isMuted = !pm.isMuted;
            if(pm.isMuted) {
                GamePanel.music.stop();
            } else {
                GamePanel.music.play(5 + pm.currentMusicTheme, true);
                GamePanel.music.loop();
            }
        } else if(pm.pauseMenuSelection == 2) {
            // Change music theme
            pm.currentMusicTheme = (pm.currentMusicTheme + 1) % 4;
            if(!pm.isMuted) {
                GamePanel.music.stop();
                GamePanel.music.play(5 + pm.currentMusicTheme, true);
                GamePanel.music.loop();
            }
        } else if(pm.pauseMenuSelection == 3) {
            // Exit confirmation
            pm.gameState = GameState.EXIT_MINI_GAME;
            pm.confirmSelection = 0; // 0 = No, 1 = Yes
        }
    }

    public void draw(Graphics2D g2) {
        // Draw the play area
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(pm.left_x - 4, pm.top_y - 4, pm.WIDTH + 8, pm.HEIGHT + 8);
        
        // Draw next mino area
        int x = pm.right_x + 100;
        int y = pm.bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setColor(Color.white);
        g2.drawString("NEXT", x + 60, y + 60);
        
        // Draw score area
        g2.drawRect(x, pm.top_y, 250, 300);
        x += 40;
        y = pm.top_y + 90;
        g2.drawString("LEVEL: " + pm.level, x, y); y += 70;
        g2.drawString("LINES: " + pm.lines, x, y); y += 70;
        g2.drawString("SCORE: " + pm.score, x, y);
        
        // Draw the currentMino
        if(pm.currentMino != null) {
            pm.currentMino.draw(g2);
        }
        
        // Draw the nextMino
        if(pm.nextMino != null) {
            pm.nextMino.draw(g2);
        }
        
        // Draw static blocks
        for(int i = 0; i < pm.staticBlocks.size(); i++) {
            pm.staticBlocks.get(i).draw(g2);
        }

        drawLeaderboard(g2);
        
        // Draw pause menu if paused
        if(pm.isPaused) {
            drawPauseMenu(g2);
        }
        
        // Draw effect
        if(pm.comboEffectOn) {
            g2.setColor(Color.red);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("COMBO x" + pm.combo, pm.left_x + 50, pm.top_y + 200);
            pm.comboEffectCounter++;
            if(pm.comboEffectCounter > 60) {
                pm.comboEffectOn = false;
                pm.comboEffectCounter = 0;
            }
        }
    }

    private void drawLeaderboard(Graphics2D g2) {
        java.util.List<String> topPlayers = (pm.db != null) ? pm.db.getTopPlayers(10) : java.util.Collections.emptyList();

        int boxX = 20;
        int boxY = pm.top_y;
        int boxW = 240;
        int boxH = 340;

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(boxX, boxY, boxW, boxH);

        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        g2.drawString("TOP 10 LEADERBOARD", boxX + 10, boxY + 30);

        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        for (int i = 0; i < Math.min(topPlayers.size(), 10); i++) {
            g2.drawString((i + 1) + ". " + topPlayers.get(i), boxX + 10, boxY + 60 + (i * 22));
        }

        if (topPlayers.isEmpty()) {
            g2.setColor(Color.gray);
            g2.drawString("No data yet", boxX + 10, boxY + 60);
        }
    }

    private void drawPauseMenu(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        g2.drawString("PAUSED", GamePanel.WIDTH/2 - 100, 200);
        
        Font pauseFont = new Font("Comic Sans MS", Font.PLAIN, 30);
        g2.setFont(pauseFont);
        
        int menuY = 300;
        int menuHeight = 200;
        int optionY = menuY + 50;
        int spacing = 40;
        
        // Draw Resume
        if(pm.pauseMenuSelection == 0) {
            g2.setColor(Color.yellow);
            g2.drawString("> RESUME", GamePanel.WIDTH/2 - 100, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  RESUME", GamePanel.WIDTH/2 - 100, optionY);
        }
        
        optionY += spacing;
        // Draw Sound toggle
        if(pm.pauseMenuSelection == 1) {
            g2.setColor(Color.yellow);
            g2.drawString("> SOUND: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 100, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  SOUND: " + (pm.isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 100, optionY);
        }
        
        optionY += spacing;
        // Draw Theme
        if(pm.pauseMenuSelection == 2) {
            g2.setColor(Color.yellow);
            g2.drawString("> THEME: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 100, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  THEME: " + (pm.currentMusicTheme + 1), GamePanel.WIDTH/2 - 100, optionY);
        }
        
        optionY += spacing;
        // Draw Menu
        if(pm.pauseMenuSelection == 3) {
            g2.setColor(Color.yellow);
            g2.drawString("> MENU", GamePanel.WIDTH/2 - 80, optionY);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  MENU", GamePanel.WIDTH/2 - 80, optionY);
        }
        
        // Draw instructions
        g2.setColor(Color.gray);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        g2.drawString("Use UP/DOWN to navigate, SPACE to select", GamePanel.WIDTH/2 - 150, menuY + menuHeight);
    }
}