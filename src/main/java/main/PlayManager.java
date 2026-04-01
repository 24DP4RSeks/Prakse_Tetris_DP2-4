package main.java.main;

import java.awt.Graphics2D;
import java.util.ArrayList;
import main.java.mino.*;

public class PlayManager {

    // Main Play Area
    public final int WIDTH = 360;
    public final int HEIGHT = 600;
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
    public static int dropInterval = 60; 
    public boolean gameOver;
    public boolean isPaused = false;
    public GameState gameState = GameState.MENU;
    public int menuSelection = 0; 
    public int settingsSelection = 0; 
    public int pauseMenuSelection = 0;
    public int confirmSelection = 0;

    // Scoring & Stats
    public int level = 1;
    public int lines = 0;
    public int score = 0;
    public int combo = 0;
    public boolean comboEffectOn = false;
    public int comboEffectCounter = 0;
    public long lastLinesClearedTime = 0;
    public final long COMBO_TIMEOUT = 2000;
    public boolean effectCounterOn = false;
    public ArrayList<Integer> effectY = new ArrayList<>();

    // Music & Settings
    public boolean isMuted = false;
    public int currentMusicTheme = 0;
    public boolean colorblindMode = false;

    // --- DATABASE & AUTH FIELDS ---
    public String currentUsername = "Guest";
    public boolean isGuest = true;
    public DatabaseHandler db;
    
    // Managers
    public MenuManager menuManager;
    public SettingsManager settingsManager;
    public GameManager gameManager;
    public GameOverManager gameOverManager;
    public ExitMiniGameManager exitMiniGameManager;
    public LoginManager loginManager; // Added this

    public PlayManager() {
        left_x = (GamePanel.WIDTH - WIDTH) / 2;
        right_x = left_x + WIDTH;
        top_y = (GamePanel.HEIGHT - HEIGHT) / 2;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        // 1. Initialize Database First
        db = new DatabaseHandler();
        db.connect();

        // 2. Initialize Managers (LoginManager must be before MenuManager if menu calls it)
        menuManager = new MenuManager(this);
        settingsManager = new SettingsManager(this);
        gameManager = new GameManager(this);
        gameOverManager = new GameOverManager(this);
        exitMiniGameManager = new ExitMiniGameManager(this);
        loginManager = new LoginManager(this); // Initialized
    }

    public void update() {
        switch(gameState) {
            case MENU: menuManager.update(); break;
            case SETTINGS: settingsManager.update(); break;
            case PLAYING: gameManager.update(); break;
            case GAME_OVER: gameOverManager.update(); break;
            case EXIT_MINI_GAME: exitMiniGameManager.update(); break;
            case LOGIN:
            case REGISTER: loginManager.update(); break; // Added routing
        }
    }

    public void startGame() {
        gameState = GameState.PLAYING;
        GameResetManager.resetGame(this);
    }

    public void draw(Graphics2D g2) {
        switch(gameState) {
            case MENU: menuManager.draw(g2); break;
            case SETTINGS: settingsManager.draw(g2); break;
            case PLAYING: gameManager.draw(g2); break;
            case GAME_OVER: gameOverManager.draw(g2); break;
            case EXIT_MINI_GAME: exitMiniGameManager.draw(g2); break;
            case LOGIN:
            case REGISTER: loginManager.draw(g2); break; // Added routing
        }
    }

    
}