package main.java.main;

import java.awt.*;
import java.util.List;
import org.bson.Document;

public class LeaderboardManager {
    private PlayManager pm;
    private DatabaseHandler db;
    private List<Document> topPlayers;
    private String searchFilter = "";
    private boolean isSearching = false;
    private long lastFetchTime = 0;
    private final long FETCH_COOLDOWN = 5000; // 5 seconds cache

    public LeaderboardManager(PlayManager pm, DatabaseHandler db) {
        this.pm = pm;
        this.db = db;
        refreshLeaderboard();
    }

    public void refreshLeaderboard() {
    // Add a null check for the DB
    if (db != null) {
        this.topPlayers = db.getLeaderboard(searchFilter);
    } else {
        System.err.println("Leaderboard Error: DatabaseHandler is null");
    }
    this.lastFetchTime = System.currentTimeMillis();
    }

    public void update() {
        if (KeyHandler.menuPressed) {
            pm.gameState = GameState.MENU;
            KeyHandler.menuPressed = false;
            return;
        }

        // Toggle search with ENTER
        if (KeyHandler.searchPressed) {
            isSearching = !isSearching;
            KeyHandler.searchPressed = false; // Reset the flag
            KeyHandler.lastTypedChar = Character.MIN_VALUE; // Clear buffer
            if (!isSearching) refreshLeaderboard();
        }

        if (isSearching) {
            handleTyping();
        }

        if (System.currentTimeMillis() - lastFetchTime > FETCH_COOLDOWN && !isSearching) {
            refreshLeaderboard();
        }
    }

    private void handleTyping() {
        char c = KeyHandler.lastTypedChar;
        if (c == Character.MIN_VALUE) return;

        // 1. Handle Backspace
        if (c == '\b') {
            if (searchFilter.length() > 0) {
                searchFilter = searchFilter.substring(0, searchFilter.length() - 1);
                refreshLeaderboard();
            }
        } 
        // 2. Handle Letters/Numbers (Ignore ENTER/ESC/Tab)
        else if (c != '\n' && c != '\r' && c != '\t' && c != '\u001B') {
            if (searchFilter.length() < 15) {
                searchFilter += c;
                refreshLeaderboard();
            }
        }

        // 3. IMPORTANT: Reset the character so it's not processed again
        KeyHandler.lastTypedChar = Character.MIN_VALUE;
}

    public void draw(Graphics2D g2) {
        // Draw background
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // Draw Title
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        g2.setColor(ColorManager.getColor(Color.white));
        g2.drawString("LEADERBOARD", GamePanel.WIDTH / 2 - 200, 80);

        // Draw Search Bar
        int searchY = 130;
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.setColor(isSearching ? ColorManager.getColor(Color.yellow) : ColorManager.getColor(Color.gray));
        g2.drawRoundRect(GamePanel.WIDTH / 2 - 150, searchY, 300, 40, 10, 10);
        g2.drawString("Search: " + searchFilter + (isSearching ? "_" : ""), GamePanel.WIDTH / 2 - 140, searchY + 28);
        g2.setFont(new Font("Comic Sans MS", Font.ITALIC, 15));
        g2.drawString("Press TAB to toggle search", GamePanel.WIDTH / 2 - 100, searchY + 60);

        // Draw Table Headers
        int startY = 220;
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        g2.setColor(ColorManager.getColor(Color.cyan));
        g2.drawString("Rank", GamePanel.WIDTH / 2 - 250, startY);
        g2.drawString("Player", GamePanel.WIDTH / 2 - 100, startY);
        g2.drawString("Score", GamePanel.WIDTH / 2 + 150, startY);

        // Draw Player List
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
        if (topPlayers == null || topPlayers.isEmpty()) {
            g2.setColor(ColorManager.getColor(Color.red));
            g2.drawString("No players found...", GamePanel.WIDTH / 2 - 100, startY + 100);
        } else {
            for (int i = 0; i < topPlayers.size(); i++) {
                Document doc = topPlayers.get(i);
                int y = startY + 50 + (i * 45);
                
                // Highlight current user
                if (doc.getString("username").equalsIgnoreCase(pm.currentUsername)) {
                    g2.setColor(ColorManager.getColor(Color.yellow));
                } else {
                    g2.setColor(ColorManager.getColor(Color.white));
                }

                g2.drawString("#" + (i + 1), GamePanel.WIDTH / 2 - 240, y);
                g2.drawString(doc.getString("username"), GamePanel.WIDTH / 2 - 100, y);
                
                // Get score (handle both "score" and "highScore" keys from your DB class)
                Object scoreObj = doc.get("score") != null ? doc.get("score") : doc.get("highScore");
                g2.drawString(String.valueOf(scoreObj), GamePanel.WIDTH / 2 + 150, y);
            }
        }

        // Footer
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.setColor(ColorManager.getColor(Color.white));
        g2.drawString("Press ESC to return to Menu", GamePanel.WIDTH / 2 - 130, GamePanel.HEIGHT - 5);
    }
}