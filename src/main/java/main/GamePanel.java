package main.java.main;



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

    /**
     * funkcija GamePanel pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī konstruktorfunkcija iestata paneli, pievieno KeyHandler un inicializē PlayManager.
     */
    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);
        pm = new PlayManager();

    }
    /**
     * funkcija launchGame pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī funkcija uzsāk spēles vītni un atskaņo mūziku.
     */
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
        music.play(5 + pm.currentMusicTheme, true);
        music.loop();
    }

    /**
     * funkcija run pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī funkcija izpilda spēles cilpu ar fiksētu FPS, atjaunojot stāvokli un pārbildējot.
     */
    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            } else {
                // Yield CPU when waiting for next frame to reduce busy-wait and keep input responsive
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * funkcija update pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī funkcija apstrādā globālās komandas un izsauc PlayManager atjauninājumu.
     */
    private void update() {
        // GLOBAL ESCAPE HANDLING
        if(KeyHandler.menuPressed && pm.gameState != GameState.LOGIN && pm.gameState != GameState.REGISTER) {
            pm.gameState = GameState.MENU;
            pm.menuSelection = 0;
            KeyHandler.menuPressed = false;
            return;
        }
        
        // UPDATED: Added LOGIN and REGISTER to the list of allowed updates
        if(pm.gameState == GameState.MENU || 
           pm.gameState == GameState.SETTINGS || 
           pm.gameState == GameState.PLAYING || 
           pm.gameState == GameState.GAME_OVER || 
           pm.gameState == GameState.EXIT_MINI_GAME ||
           pm.gameState == GameState.LOGIN || 
           pm.gameState == GameState.REGISTER) {
            pm.update();
        }

        if(KeyHandler.fullscreenPressed) {
            Main.toggleFullscreen();
            KeyHandler.fullscreenPressed = false;
        }
    }

    /**
     * funkcija paintComponent pieņem Graphics tipa vērtību g un atgriež void tipa vērtību null.
     * Šī funkcija zīmē spēles atveidi uz ekrāna ar attiecīgo mērogošanu.
     */
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
