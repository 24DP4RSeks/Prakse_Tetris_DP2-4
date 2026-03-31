package main;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class PlayManager {

    // Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Others
    public static int dropInterval = 60; // mino drops in every 60 frames
    boolean gameOver;
    boolean isPaused = false;  // Dedicated pause state (different from pausePressed toggle)
    public GameState gameState = GameState.MENU;
    int menuSelection = 0; // Menu: 0 = Start, 1 = Settings, 2 = Exit
    int settingsSelection = 0; // Settings: 0 = Music, 1 = Colorblind, 2 = Back
    int pauseMenuSelection = 0; // Pause Menu: 0 = Resume, 1 = Sound, 2 = Settings, 3 = Menu
    int confirmSelection = 0; // Confirm: 0 = No, 1 = Yes
    
    // Settings
    public boolean isMuted = false;
    public boolean colorblindMode = false;
    public int currentMusicTheme = 0;  // 0 = Theme 1, 1 = Theme 2, 2 = Theme 3

    // Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Combo System
    int combo = 0;
    int comboEffectCounter = 0;
    boolean comboEffectOn = false;
    long lastLinesClearedTime = 0;
    final long COMBO_TIMEOUT = 2000; // 2 seconds to maintain combo

    // Exit Mini Game

    // Score
    int level = 1;
    int lines;
    int score;

    public PlayManager() {

        // Main Play Area Frame
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        // Set the starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    }
    private Mino pickMino() {

        // Pick random mino
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch(i) {
            case 0: mino = new Mino_L1();break;
            case 1: mino = new Mino_L2();break;
            case 2: mino = new Mino_Square();break;
            case 3: mino = new Mino_Bar();break;
            case 4: mino = new Mino_T();break;
            case 5: mino = new Mino_Z1();break;
            case 6: mino = new Mino_Z2();break;
        }
        return mino;
    }
    public void update() {
        if(gameState == GameState.MENU) {
            updateMenu();
        }
        else if(gameState == GameState.SETTINGS) {
            updateSettings();
        }
        else if(gameState == GameState.PLAYING) {
            updateGame();
        }
        else if(gameState == GameState.GAME_OVER) {
            updateGameOver();
        }
        else if(gameState == GameState.EXIT_MINI_GAME) {
            updateExitMiniGame();
        }
    }
    
    private void updateMenu() {
        // Handle menu navigation
        if(KeyHandler.upPressed) {
            menuSelection--;
            if(menuSelection < 0) menuSelection = 2;
            KeyHandler.upPressed = false;
        }
        if(KeyHandler.downPressed) {
            menuSelection++;
            if(menuSelection > 2) menuSelection = 0;
            KeyHandler.downPressed = false;
        }
        if(KeyHandler.ePressed) {
            if(menuSelection == 0) {
                startGame();
            } else if(menuSelection == 1) {
                gameState = GameState.SETTINGS;
                settingsSelection = 0;
            } else if(menuSelection == 2) {
                System.exit(0);
            }
            KeyHandler.ePressed = false;
        }
    }
    
    private void updateSettings() {
        // Handle settings navigation
        if(KeyHandler.upPressed) {
            settingsSelection--;
            if(settingsSelection < 0) settingsSelection = 3;
            KeyHandler.upPressed = false;
        }
        if(KeyHandler.downPressed) {
            settingsSelection++;
            if(settingsSelection > 3) settingsSelection = 0;
            KeyHandler.downPressed = false;
        }
        if(KeyHandler.ePressed) {
            if(settingsSelection == 0) {
                // Toggle mute
                isMuted = !isMuted;
                if(isMuted) {
                    GamePanel.music.stop();
                } else {
                    // Play current selected theme
                    GamePanel.music.play(5 + currentMusicTheme, true);
                    GamePanel.music.loop();
                }
            } else if(settingsSelection == 1) {
                // Switch music theme (right now cycling through 3)
                currentMusicTheme = (currentMusicTheme + 1) % 4;
                // Restart music with new theme if music is on
                if(!isMuted) {
                    GamePanel.music.stop();
                    GamePanel.music.play(5 + currentMusicTheme, true);
                    GamePanel.music.loop();
                }
            } else if(settingsSelection == 2) {
                // Toggle colorblind mode
                colorblindMode = !colorblindMode;
            } else if(settingsSelection == 3) {
                // Back to menu
                gameState = GameState.MENU;
                menuSelection = 0;
            }
            KeyHandler.ePressed = false;
        }
    }
    
    private void updateGame() {
        try {
            // Check if 1 button pressed to toggle pause
            if(KeyHandler.pausePressed) {
                isPaused = !isPaused;  // Toggle the pause state
                KeyHandler.pausePressed = false;  // Clear the toggle flag immediately
                pauseMenuSelection = 0;  // Reset menu selection
            }
            
            // Handle pause menu if paused
            if(isPaused) {
                handlePauseMenu();
                return;  // Don't process game logic while paused
            }
        
        // Check if R pressed to restart game
        if(KeyHandler.restartPressed) {
            resetGame();
            KeyHandler.restartPressed = false;
            return;
        }
        
        // Check if ESC pressed to return to menu (only when NOT paused)
        if(KeyHandler.menuPressed) {
            gameState = GameState.MENU;
            menuSelection = 0;
            KeyHandler.menuPressed = false;
            return;
        }
        
        // Check if the currentMino is active
        if(currentMino.active == false){

            // if the mino is not active, put it into the staticBlock
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);
            
            // Play block hit sound
            GamePanel.se.play(4, false);

            //check if the game is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                // this means the currentMino immediately collided a block and couldnt move at all
                // so its xy are the same with the nextMino
                gameOver = true;
                gameState = GameState.GAME_OVER;
            }

            currentMino.deactivating = false;

            // replace the currentMino with the nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            // When line is full = delete line
            checkDelete();

        }
        else {
            currentMino.update();
        }
        } catch(Exception ex) {
            // Handle any unexpected errors gracefully
        }
    }
    
    private void updateGameOver() {
        if(KeyHandler.spacePressed || KeyHandler.menuPressed) {
            resetGame();
            gameState = GameState.MENU;
            menuSelection = 0;
            KeyHandler.spacePressed = false;
            KeyHandler.menuPressed = false;
        }
    }
    
    private void startGame() {
        gameState = GameState.PLAYING;
        resetGame();
    }
    
    private void handlePauseMenu() {
        // Navigation - must check and clear immediately
        if(KeyHandler.upPressed) {
            pauseMenuSelection--;
            if(pauseMenuSelection < 0) pauseMenuSelection = 3;
            KeyHandler.upPressed = false;
        } else if(KeyHandler.downPressed) {
            pauseMenuSelection++;
            if(pauseMenuSelection > 3) pauseMenuSelection = 0;
            KeyHandler.downPressed = false;
        }
        
        // Selection with SPACE or ENTER
        if(KeyHandler.ePressed) {
            KeyHandler.ePressed = false;
            executeMenuSelection();
        }
    }
    
    private void executeMenuSelection() {
        if(pauseMenuSelection == 0) {
            // Resume
            isPaused = false;
            pauseMenuSelection = 0;
        } else if(pauseMenuSelection == 1) {
            // Toggle mute
            isMuted = !isMuted;
            if(isMuted) {
                GamePanel.music.stop();
            } else {
                GamePanel.music.play(5 + currentMusicTheme, true);
                GamePanel.music.loop();
            }
        } else if(pauseMenuSelection == 2) {
            // Change music theme
            currentMusicTheme = (currentMusicTheme + 1) % 4;
            if(!isMuted) {
                GamePanel.music.stop();
                GamePanel.music.play(5 + currentMusicTheme, true);
                GamePanel.music.loop();
            }
        } else if(pauseMenuSelection == 3) {
            // Exit confirmation
            gameState = GameState.EXIT_MINI_GAME;
            confirmSelection = 0; // 0 = No, 1 = Yes
        }
    }
    
    private void updateExitMiniGame() {
        // Check for ESC to cancel
        if(KeyHandler.menuPressed) {
            gameState = GameState.PLAYING; // Return to pause menu
            isPaused = true;
            pauseMenuSelection = 0;
            KeyHandler.menuPressed = false;
            return;
        }
        
        // Navigation
        if(KeyHandler.leftPressed) {
            confirmSelection = 0; // No
            KeyHandler.leftPressed = false;
        }
        if(KeyHandler.rightPressed) {
            confirmSelection = 1; // Yes
            KeyHandler.rightPressed = false;
        }
        
        // Confirm
        if(KeyHandler.ePressed) {
            if(confirmSelection == 0) {
                // No, return to pause menu
                gameState = GameState.PLAYING;
                isPaused = true;
                pauseMenuSelection = 0;
            } else {
                // Yes, exit to menu
                gameState = GameState.MENU;
                menuSelection = 0;
                isPaused = false;
                pauseMenuSelection = 0;
            }
            KeyHandler.ePressed = false;
        }
    }
    
    private void resetGame() {
        staticBlocks.clear();
        level = 1;
        lines = 0;
        score = 0;
        combo = 0;
        comboEffectOn = false;
        comboEffectCounter = 0;
        lastLinesClearedTime = 0;
        isPaused = false;
        pauseMenuSelection = 0;
        gameOver = false;
        dropInterval = 60;  // Reset to initial speed
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }
    private void checkDelete() {

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while(x < right_x && y < bottom_y) {

            for(int i = 0; i < staticBlocks.size(); i++){
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                    // increase the count if there is a static block
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if(x == right_x) {

                // if the blockCount hits 12 that means the current y line is all filled with Blocks
                // so we can delete them
                if(blockCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);

                    for(int i = staticBlocks.size()-1; i > -1; i--){
                        // remove all the block in the current line
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                            
                        }
                    }

                    lineCount++;
                    lines++;

                    // a line has been deleted so need to slide down blocks that are shove it
                    for(int i = 0; i < staticBlocks.size(); i++){
                        // if a block is above the current y, move it down by the block size
                        if(staticBlocks.get(i).y < y){
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
                
            }
        }

        // Add Score with Combo System
        if(lineCount > 0){
            // Play line delete sound
            GamePanel.se.play(1, false);
            
            long currentTime = System.currentTimeMillis();
            
            // Check if combo should continue or reset
            if(currentTime - lastLinesClearedTime > COMBO_TIMEOUT) {
                // Combo timed out or first clear, reset combo
                combo = 1;
            } else {
                // Combo continues! Increase it
                combo++;
            }
            
            lastLinesClearedTime = currentTime;
            
            // Calculate base score based on lines cleared (Tetris-style scoring)
            // 1 line: 100, 2 lines: 300, 3 lines: 500, 4 lines (Tetris): 800
            int lineScoreBase;
            switch(lineCount) {
                case 1: lineScoreBase = 100; break;
                case 2: lineScoreBase = 300; break;
                case 3: lineScoreBase = 500; break;
                case 4: lineScoreBase = 800; break;
                default: lineScoreBase = 50 * lineCount; break;
            }
            
            // Apply level multiplier (level 1 = 1x, level 2 = 2x, etc.)
            // Apply level multiplier with scaling (1 + 0.1 * level bonus)
            // Level 1 = 1.0x, Level 2 = 1.1x, Level 3 = 1.2x, etc.
            float levelMultiplier = 1.0f + (level - 1) * 0.1f;
            int baseScore = (int)(lineScoreBase * levelMultiplier);
            
            // Calculate score with combo multiplier
            // Combo multiplier: 1x = 1.0, 2x = 1.5, 3x = 2.0, 4x = 2.5, etc.
            float comboMultiplier = 1.0f + (combo - 1) * 0.5f;
            
            int comboScore = (int)(baseScore * comboMultiplier);
            
            score += comboScore;
            
            // Trigger combo effect if combo is 2 or more
            if(combo >= 2) {
                comboEffectOn = true;
                comboEffectCounter = 0;
                GamePanel.se.play(2, false);  // Play a special sound for combo
            }
            
            // Update difficulty based on lines cleared
            updateDifficulty();
        }


    }
    
    private void updateDifficulty() {
        // Level up based on lines cleared: every 5 lines = 1 level
        level = 1 + (lines / 5);
        
        // Speed up drop interval based on both lines and score
        // Increases difficulty as game progresses
        int speedIncrease = (lines / 5) + (score / 1000);
        dropInterval = Math.max(10, 60 - (speedIncrease * 3));
    }
    public void draw(Graphics2D g2) {
        if(gameState == GameState.MENU) {
            drawMenu(g2);
        }
        else if(gameState == GameState.SETTINGS) {
            drawSettings(g2);
        }
        else if(gameState == GameState.PLAYING) {
            drawGame(g2);
        }
        else if(gameState == GameState.GAME_OVER) {
            drawGameOver(g2);
        }
        else if(gameState == GameState.EXIT_MINI_GAME) {
            drawExitMiniGame(g2);
        }
    }
    
    private void drawMenu(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        g2.drawString("TETRIS", GamePanel.WIDTH/2 - 150, 150);
        
        Font menuFont = new Font("Comic Sans MS", Font.PLAIN, 30);
        g2.setFont(menuFont);
        
        // Draw Start button
        if(menuSelection == 0) {
            g2.setColor(Color.yellow);
            g2.drawString("> START", GamePanel.WIDTH/2 - 80, 280);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  START", GamePanel.WIDTH/2 - 80, 280);
        }
        
        // Draw Settings button
        if(menuSelection == 1) {
            g2.setColor(Color.yellow);
            g2.drawString("> SETTINGS", GamePanel.WIDTH/2 - 120, 340);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  SETTINGS", GamePanel.WIDTH/2 - 120, 340);
        }
        
        // Draw Exit button
        if(menuSelection == 2) {
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
    
    private void drawSettings(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        g2.drawString("SETTINGS", GamePanel.WIDTH/2 - 180, 150);
        
        Font settingsFont = new Font("Comic Sans MS", Font.PLAIN, 30);
        g2.setFont(settingsFont);
        
        // Draw Music button (Mute/Unmute)
        if(settingsSelection == 0) {
            g2.setColor(Color.yellow);
            g2.drawString("> MUSIC: " + (isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 150, 260);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  MUSIC: " + (isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 150, 260);
        }
        
        // Draw Music Theme button
        if(settingsSelection == 1) {
            g2.setColor(Color.yellow);
            g2.drawString("> THEME: " + (currentMusicTheme + 1), GamePanel.WIDTH/2 - 150, 300);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  THEME: " + (currentMusicTheme + 1), GamePanel.WIDTH/2 - 150, 300);
        }
        
        // Draw Colorblind mode button
        if(settingsSelection == 2) {
            g2.setColor(Color.yellow);
            g2.drawString("> COLORBLIND: " + (colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 220, 340);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  COLORBLIND: " + (colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 220, 340);
        }
        
        // Draw Back button
        if(settingsSelection == 3) {
            g2.setColor(Color.yellow);
            g2.drawString("> BACK", GamePanel.WIDTH/2 - 80, 380);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  BACK", GamePanel.WIDTH/2 - 80, 380);
        }
        
        g2.setColor(Color.gray);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.drawString("Use W/UP or S/DOWN to navigate, E to toggle/select", GamePanel.WIDTH/2 - 280, 550);
    }
    
    private void drawGame(Graphics2D g2) {
        // Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(left_x-4, top_y, WIDTH+8, HEIGHT+8, 15, 15);

        // Draw faint grid lines for navigation
        g2.setColor(new Color(100, 100, 100, 50));  // Dark gray, semi-transparent
        g2.setStroke(new BasicStroke(1f));
        
        // Draw vertical grid lines
        for(int x = left_x; x <= right_x; x += Block.SIZE) {
            g2.drawLine(x, top_y, x, bottom_y);
        }
        
        // Draw horizontal grid lines
        for(int y = top_y; y <= bottom_y; y += Block.SIZE) {
            g2.drawLine(left_x, y, right_x, y);
        }

        g2.setColor(Color.white);
        // Draw Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRoundRect(x, y, 200, 200, 15, 15);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        // Draw Score Frame
        g2.drawRoundRect(x, top_y + 140, 200, 200, 15, 15);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.drawString("SCORE", x+45,top_y + 130);
        x += 20;
        y = top_y + 180;
        g2.drawString("LEVEL: " + level,x ,y); y+=50;
        g2.drawString("LINES: " + lines,x ,y); y+=50;
        
        // Draw Combo Display
        if(combo > 0) {
            g2.setColor(combo >= 2 ? Color.yellow : Color.white);
            g2.drawString("COMBO: x" + combo, x, y); 
            y += 50;
        }
        
        g2.setColor(Color.white);
        g2.drawString("SCORE: " + score,x ,y);

        // Draw the currentMino
        if(currentMino != null) {
            currentMino.draw(g2, colorblindMode);
        }
        // Draw the nextMino
        nextMino.draw(g2, colorblindMode);

        // Draw Static Blocks
        for(int i = 0; i < staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2, colorblindMode);
        }

        // Draw Effect
        if(effectCounterOn){
            effectCounter++;

            g2.setColor(Color.red);
            for(int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if(effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Draw Combo Effect
        if(comboEffectOn) {
            comboEffectCounter++;
            
            // Font size increases and fades out
            float alpha = 1.0f - (comboEffectCounter / 60.0f);
            
            if(alpha > 0) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(Color.yellow);
                g2.setFont(new Font("Comic Sans MS", Font.BOLD, 40 + comboEffectCounter));
                
                String comboText = "COMBO x" + combo + "!";
                int textX = right_x - 150;
                int textY = top_y + 300 - comboEffectCounter;
                
                // Draw shadow for better visibility
                g2.setColor(new Color(0, 0, 0, (int)(alpha * 200)));
                g2.drawString(comboText, textX + 2, textY + 2);
                
                // Draw main text
                g2.setColor(Color.yellow);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.drawString(comboText, textX, textY);
                
                if(comboEffectCounter >= 60) {
                    comboEffectOn = false;
                    comboEffectCounter = 0;
                }
            }
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // Draw pause menu
        if(isPaused) {
            // Draw semi-transparent overlay
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
            
            // Draw pause menu box
            int menuX = GamePanel.WIDTH/2 - 250;
            int menuY = GamePanel.HEIGHT/2 - 200;
            int menuWidth = 500;
            int menuHeight = 400;
            
            g2.setColor(Color.black);
            g2.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);
            
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);
            
            // Draw title
            g2.setColor(Color.white);
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            g2.drawString("PAUSED", GamePanel.WIDTH/2 - 130, menuY + 70);
            
            // Draw menu options
            Font optionFont = new Font("Comic Sans MS", Font.PLAIN, 28);
            g2.setFont(optionFont);
            
            int optionY = menuY + 130;
            int lineHeight = 70;
            
            // Resume option
            if(pauseMenuSelection == 0) {
                g2.setColor(Color.yellow);
                g2.drawString("> RESUME", GamePanel.WIDTH/2 - 100, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  RESUME", GamePanel.WIDTH/2 - 100, optionY);
            }
            
            // Sound option
            optionY += lineHeight;
            if(pauseMenuSelection == 1) {
                g2.setColor(Color.yellow);
                g2.drawString("> Music: " + (isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 130, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  Music: " + (isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 130, optionY);
            }
            
            // Theme option
            optionY += lineHeight;
            if(pauseMenuSelection == 2) {
                g2.setColor(Color.yellow);
                g2.drawString("> THEME: " + (currentMusicTheme + 1), GamePanel.WIDTH/2 - 120, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  THEME: " + (currentMusicTheme + 1), GamePanel.WIDTH/2 - 120, optionY);
            }
            
            // Menu option
            optionY += lineHeight;
            if(pauseMenuSelection == 3) {
                g2.setColor(Color.yellow);
                g2.drawString("> MENU", GamePanel.WIDTH/2 - 80, optionY);
            } else {
                g2.setColor(Color.white);
                g2.drawString("  MENU", GamePanel.WIDTH/2 - 80, optionY);
            }
            
            // Draw instructions
            g2.setColor(Color.gray);
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            g2.drawString("Use UP/DOWN to navigate, E to select", GamePanel.WIDTH/2 - 220, menuY + menuHeight - 20);
        }
    }
    
    private Color getColorForMode(Color c) {
        if(colorblindMode) {
            // Convert to grayscale
            int gray = (int)(c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
            return new Color(gray, gray, gray);
        }
        return c;
    }
    
    private void drawGameOver(Graphics2D g2) {
        drawGame(g2);
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        g2.setColor(Color.red);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        g2.drawString("GAME OVER", GamePanel.WIDTH/2 - 200, 250);
        
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.drawString("Score: " + score, GamePanel.WIDTH/2 - 80, 350);
        g2.drawString("Press SPACE to return to menu", GamePanel.WIDTH/2 - 200, 450);
        g2.drawString("or ESC", GamePanel.WIDTH/2 - 40, 500);
    }
    
    private void drawExitMiniGame(Graphics2D g2) {
        // Draw the game in background
        drawGame(g2);
        
        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        
        // Main box
        int menuX = GamePanel.WIDTH/2 - 250;
        int menuY = GamePanel.HEIGHT/2 - 200;
        int menuWidth = 500;
        int menuHeight = 400;
        
        g2.setColor(Color.black);
        g2.fillRoundRect(menuX, menuY + 50, menuWidth, menuHeight / 2, 20, 20);

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(menuX, menuY + 50, menuWidth, menuHeight / 2, 20, 20);
        
        // Title
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        g2.drawString("You sure?", menuX + menuWidth / 2 / 2 + 30, menuY + 100);
        
        // No button
        int buttonWidth = 80;
        int buttonHeight = 40;
        int noX = menuX + 50;
        int buttonY = menuY + 100;
        
        if(confirmSelection == 0) {
            g2.setColor(Color.yellow);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("No", noX + 20, buttonY + 75);
        
        // Yes button
        int yesX = menuX + menuWidth - 50 - buttonWidth;
        
        if(confirmSelection == 1) {
            g2.setColor(Color.yellow);
        } else {
            g2.setColor(Color.white);
        }
        g2.drawString("Yes", yesX + 20, buttonY + 75);
        
        // Instructions
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        g2.drawString("Use A/D to select, E to confirm", menuX + menuX / 2 - 60, menuHeight);
    }
    
}
