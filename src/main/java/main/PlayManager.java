package main.java.main;



import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import main.java.main.mino.*;

public class PlayManager {

    // Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    public Mino currentMino;
    public final int MINO_START_X;
    public final int MINO_START_Y;
    public Mino nextMino;
    public final int NEXTMINO_X;
    public final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Others
    public static int dropInterval = 60; // mino drops in every 60 frames
    public boolean gameOver;
    public boolean isPaused = false;  // Dedicated pause state (different from pausePressed toggle)
    public GameState gameState = GameState.MENU;
    public int menuSelection = 0; // Menu: 0 = Start, 1 = Settings, 2 = Exit
    public int settingsSelection = 0; // Settings: 0 = Music, 1 = Colorblind, 2 = Back
    public int pauseMenuSelection = 0; // Pause Menu: 0 = Resume, 1 = Sound, 2 = Settings, 3 = Menu
    public int confirmSelection = 0; // Confirm: 0 = No, 1 = Yes
    
    // Managers
    private MenuManager menuManager;
    private SettingsManager settingsManager;
    private GameManager gameManager;
    private GameOverManager gameOverManager;
    private ExitMiniGameManager exitMiniGameManager;
    
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
    public int level = 1;
    public int lines;
    public int score;

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

        // Set the starting Mino from dedicated factory
        currentMino = MinoFactory.pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = MinoFactory.pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

        // Initialize managers
        menuManager = new MenuManager(this);
        settingsManager = new SettingsManager(this);
        gameManager = new GameManager(this);
        gameOverManager = new GameOverManager(this);
        exitMiniGameManager = new ExitMiniGameManager(this);
    }
    public void update() {
        if(gameState == GameState.MENU) {
            menuManager.update();
        }
        else if(gameState == GameState.SETTINGS) {
            settingsManager.update();
        }
        else if(gameState == GameState.PLAYING) {
            gameManager.update();
        }
        else if(gameState == GameState.GAME_OVER) {
            gameOverManager.update();
        }
        else if(gameState == GameState.EXIT_MINI_GAME) {
            exitMiniGameManager.update();
        }
    }
    
    public void startGame() {
        gameState = GameState.PLAYING;
        GameResetManager.resetGame(this);
    }

    public void draw(Graphics2D g2) {
        if(gameState == GameState.MENU) {
            menuManager.draw(g2);
        }
        else if(gameState == GameState.SETTINGS) {
            settingsManager.draw(g2);
        }
        else if(gameState == GameState.PLAYING) {
            gameManager.draw(g2);
        }
        else if(gameState == GameState.GAME_OVER) {
            gameOverManager.draw(g2);
        }
        else if(gameState == GameState.EXIT_MINI_GAME) {
            exitMiniGameManager.draw(g2);
        }
    }
    
    public void drawGame(Graphics2D g2) {
        GameRenderer.drawGame(this, g2);
    }
}
