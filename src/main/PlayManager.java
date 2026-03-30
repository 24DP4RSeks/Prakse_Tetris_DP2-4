package main;

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
    public GameState gameState = GameState.MENU;
    int menuSelection = 0; // Menu: 0 = Start, 1 = Settings, 2 = Exit
    int settingsSelection = 0; // Settings: 0 = Music, 1 = Colorblind, 2 = Back
    
    // Settings
    public boolean isMuted = false;
    public boolean colorblindMode = false;

    // Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

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
        if(KeyHandler.spacePressed) {
            if(menuSelection == 0) {
                startGame();
            } else if(menuSelection == 1) {
                gameState = GameState.SETTINGS;
                settingsSelection = 0;
            } else if(menuSelection == 2) {
                System.exit(0);
            }
            KeyHandler.spacePressed = false;
        }
    }
    
    private void updateSettings() {
        // Handle settings navigation
        if(KeyHandler.upPressed) {
            settingsSelection--;
            if(settingsSelection < 0) settingsSelection = 2;
            KeyHandler.upPressed = false;
        }
        if(KeyHandler.downPressed) {
            settingsSelection++;
            if(settingsSelection > 2) settingsSelection = 0;
            KeyHandler.downPressed = false;
        }
        if(KeyHandler.spacePressed) {
            if(settingsSelection == 0) {
                // Toggle mute
                isMuted = !isMuted;
                if(isMuted) {
                    GamePanel.music.stop();
                } else {
                    GamePanel.music.play(0, true);
                    GamePanel.music.loop();
                }
            } else if(settingsSelection == 1) {
                // Toggle colorblind mode
                colorblindMode = !colorblindMode;
            } else if(settingsSelection == 2) {
                // Back to menu
                gameState = GameState.MENU;
                menuSelection = 0;
            }
            KeyHandler.spacePressed = false;
        }
    }
    
    private void updateGame() {
        // Check if ESC pressed to return to menu
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
    
    private void resetGame() {
        staticBlocks.clear();
        level = 1;
        lines = 0;
        score = 0;
        gameOver = false;
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

        // Add Score
        if(lineCount > 0){
            // Play line delete sound
            GamePanel.se.play(1, false);
            
            int singleLineScore = 10 * level;
            score +=  singleLineScore + lineCount;
            
            // Update difficulty based on score
            updateDifficulty();
        }


    }
    
    private void updateDifficulty() {
        // Speed up drop interval every 100 points
        // Every 100 score: decrease dropInterval by 5 frames (speeds up)
        // Minimum dropInterval is 10 frames to avoid making it too fast
        int scoreThreshold = (score / 100) * 100; // Get current score threshold
        int difficultyLevel = score / 100;
        
        // Start with 60, subtract (5 * difficultyLevel) but never go below 10
        dropInterval = Math.max(10, 60 - (difficultyLevel * 5));
        
        // Update level based on score milestones too
        level = 1 + (score / 100);
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
        g2.drawString("Use UP/DOWN to navigate, SPACE to select", GamePanel.WIDTH/2 - 250, 550);
        g2.drawString("Press F for fullscreen", GamePanel.WIDTH/2 - 100, 590);
    }
    
    private void drawSettings(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        g2.drawString("SETTINGS", GamePanel.WIDTH/2 - 180, 150);
        
        Font settingsFont = new Font("Comic Sans MS", Font.PLAIN, 30);
        g2.setFont(settingsFont);
        
        // Draw Music button
        if(settingsSelection == 0) {
            g2.setColor(Color.yellow);
            g2.drawString("> MUSIC: " + (isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 150, 280);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  MUSIC: " + (isMuted ? "OFF" : "ON"), GamePanel.WIDTH/2 - 150, 280);
        }
        
        // Draw Colorblind mode button
        if(settingsSelection == 1) {
            g2.setColor(Color.yellow);
            g2.drawString("> COLORBLIND: " + (colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 220, 340);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  COLORBLIND: " + (colorblindMode ? "ON" : "OFF"), GamePanel.WIDTH/2 - 220, 340);
        }
        
        // Draw Back button
        if(settingsSelection == 2) {
            g2.setColor(Color.yellow);
            g2.drawString("> BACK", GamePanel.WIDTH/2 - 80, 400);
        } else {
            g2.setColor(Color.white);
            g2.drawString("  BACK", GamePanel.WIDTH/2 - 80, 400);
        }
        
        g2.setColor(Color.gray);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.drawString("Use UP/DOWN to navigate, SPACE to toggle/select", GamePanel.WIDTH/2 - 280, 550);
    }
    
    private void drawGame(Graphics2D g2) {
        // Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y, WIDTH+8, HEIGHT+8);

        // Draw Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        // Draw Score Frame
        g2.drawRect(x, top_y + 140, 200, 200);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        g2.drawString("SCORE", x+45,top_y + 130);
        x += 20;
        y = top_y + 180;
        g2.drawString("LEVEL: " + level,x ,y); y+=70;
        g2.drawString("LINES: " + lines,x ,y); y+=70;
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

        // Draw pause
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(KeyHandler.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
            
            // Show pause menu instructions
            g2.setColor(Color.white);
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            g2.drawString("Press 1 to continue", left_x + 80, top_y + 400);
            g2.drawString("Press ESC to return to menu", left_x + 60, top_y + 430);
        }

        x = 35;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 60));
        g2.drawString("Simple Tetris", x+20, y);
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

    
}
