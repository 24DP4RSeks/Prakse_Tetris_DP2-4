package main.java.main;

import java.awt.*;
import java.awt.event.KeyEvent;

public class LoginManager {
    private PlayManager pm;
    private String username = "";
    private String password = "";
    private int activeField = 0; 
    private String message = "";
    private Color messageColor = Color.white;
    private DatabaseHandler db;

    public LoginManager(PlayManager pm) {
        this.pm = pm;
        this.db = new DatabaseHandler();
        new Thread(() -> db.connect()).start();
    }

    public DatabaseHandler getDb() {
        return db;
    }

    public void update() {
        // Allow exiting via ESC key directly
        if (KeyHandler.menuPressed) {
            goToMenu();
            KeyHandler.menuPressed = false;
            return;
        }

        handleInput();

        if (KeyHandler.ePressed) {
            if (activeField == 2) {
                handleAuth();
            } else if (activeField == 3) {
                goToMenu();
            }
            KeyHandler.ePressed = false;
        }
    }

    private void goToMenu() {
        pm.gameState = GameState.MENU;
        pm.menuSelection = 0;
        
        KeyHandler.ePressed = false; 
        
        username = "";
        password = "";
        activeField = 0;
        message = "";
    }


    private void handleAuth() {
        if (pm.gameState == GameState.LOGIN) {
            if (db.login(username, password)) {
                pm.currentUsername = this.username;
                pm.isGuest = false;
                pm.startGame();
                KeyHandler.ePressed = false;
            } else {
                message = "Login Failed";
                messageColor = Color.red;
            }
        } else {
            db.registerPlayer(username, password);
            message = "Registered! Please Login.";
            messageColor = Color.green;
        }
    }

    private void handleInput() {
        if (KeyHandler.upPressed) { 
            activeField = (activeField > 0) ? activeField - 1 : 3; 
            KeyHandler.upPressed = false; 
        }
        if (KeyHandler.downPressed) { 
            activeField = (activeField < 3) ? activeField + 1 : 0; 
            KeyHandler.downPressed = false; 
        }
        
        if (activeField < 2 && KeyHandler.lastTypedChar != Character.MIN_VALUE) {
            char c = KeyHandler.lastTypedChar;
            if (Character.isLetterOrDigit(c)) {
                if (activeField == 0) username += c; else password += c;
            } else if (c == (char)KeyEvent.VK_BACK_SPACE) {
                if (activeField == 0 && username.length() > 0) username = username.substring(0, username.length() - 1);
                else if (activeField == 1 && password.length() > 0) password = password.substring(0, password.length() - 1);
            }
            KeyHandler.lastTypedChar = Character.MIN_VALUE;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0,0,0,200));
        g2.fillRect(0,0, GamePanel.WIDTH, GamePanel.HEIGHT);

        String title = pm.gameState == GameState.LOGIN ? "LOGIN" : "REGISTER";
        g2.setColor(ColorManager.getColor(Color.white));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        g2.drawString(title, GamePanel.WIDTH/2 - 100, 150);

        drawField(g2, "Username:", username, 250, activeField == 0);
        drawField(g2, "Password:", "*".repeat(password.length()), 350, activeField == 1);

        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        g2.setColor(activeField == 2 ? ColorManager.getColor(Color.yellow) : ColorManager.getColor(Color.white));
        String btnText = pm.gameState == GameState.LOGIN ? "> LOGIN <" : "> REGISTER <";
        g2.drawString(btnText, GamePanel.WIDTH/2 - 100, 480);

        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.setColor(ColorManager.getColor(messageColor));
        g2.drawString(message, GamePanel.WIDTH/2 - 150, 550);

        g2.setColor(activeField == 3 ? ColorManager.getColor(Color.yellow) : ColorManager.getColor(Color.white));
        g2.drawString("> BACK TO MENU <", GamePanel.WIDTH/2 - 100, 600);

    }

    private void drawField(Graphics2D g2, String label, String val, int y, boolean selected) {
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
        g2.setColor(ColorManager.getColor(Color.white));
        g2.drawString(label, GamePanel.WIDTH/2 - 250, y);
        g2.setColor(selected ? ColorManager.getColor(Color.yellow) : ColorManager.getColor(Color.white));
        g2.drawRect(GamePanel.WIDTH/2 - 50, y - 30, 300, 40);
        g2.drawString(val + (selected && (System.currentTimeMillis() / 500 % 2 == 0) ? "|" : ""), GamePanel.WIDTH/2 - 40, y);
    }

    public void reset() {
        username = ""; password = ""; message = ""; activeField = 0;
    }
}