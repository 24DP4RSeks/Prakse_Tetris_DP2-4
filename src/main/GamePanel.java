package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    PlayManager pm;
    public static Sound music = new Sound();
    public static Sound se = new Sound();
    
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    public GamePanel() {

        // Panel settings
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);
        // Implement KeyListener
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);

        pm = new PlayManager();

    }
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();

        // Play the selected music theme
        music.play(5 + pm.currentMusicTheme, true);
        music.loop();
    }

    @Override
    public void run() {
        // Game Loop
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }

    }
    private void update() {
        // Ensure this panel has focus for keyboard input
        if(!this.hasFocus()) {
            this.requestFocus();
        }
        
        // Handle fullscreen toggle
        if(KeyHandler.fullscreenPressed) {
            Main.toggleFullscreen();
            KeyHandler.fullscreenPressed = false;
        }
        
        // Handle ESC to return to menu (only when NOT paused)
        if(KeyHandler.menuPressed && pm.gameState == GameState.PLAYING && !pm.isPaused) {
            System.out.println("[GAMEPANEL] ESC pressed, returning to menu");
            System.out.flush();
            pm.gameState = GameState.MENU;
            pm.menuSelection = 0;
            KeyHandler.menuPressed = false;
            return;
        }
        
        if(pm.gameState == GameState.MENU) {
            pm.update();  // Menu always updates for navigation
        }
        else if(pm.gameState == GameState.SETTINGS) {
            pm.update();  // Settings menu updates for navigation
        }
        else if(pm.gameState == GameState.PLAYING) {
            // Always update, even when paused - pausePressed is handled inside updateGame()
            pm.update();
        }
        else if(pm.gameState == GameState.GAME_OVER) {
            pm.update();  // Game Over state updates for input
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        
        // Calculate scaling based on current panel size
        scaleX = (float)this.getWidth() / WIDTH;
        scaleY = (float)this.getHeight() / HEIGHT;
        
        // Apply scaling transformation
        AffineTransform transform = new AffineTransform();
        transform.scale(scaleX, scaleY);
        g2.transform(transform);
        
        pm.draw(g2);
    }
}
