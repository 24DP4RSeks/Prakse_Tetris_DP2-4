package main.java.main;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import static com.mongodb.client.model.Sorts.descending;

public class DatabaseHandler {

    private MongoCollection<Document> scoreCollection;

    /**
     * funkcija connect pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī funkcija izveido savienojumu ar MongoDB Atlas.
     */
    public void connect() {
        try {
            String connectionString = "mongodb+srv://player1:1234@tetrisusers.xk6hppj.mongodb.net/?retryWrites=true&w=majority&appName=tetrisUsers";
            
            MongoClientURI uri = new MongoClientURI(connectionString);
            MongoClient mongoClient = new MongoClient(uri);
            
            MongoDatabase database = mongoClient.getDatabase("TetrisGame");
            scoreCollection = database.getCollection("players");
            
            System.out.println("Successfully connected to MongoDB Atlas!");
            
        } catch (Exception e) {
            System.err.println("Connection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * funkcija registerPlayer pieņem String tipa vērtību user un String tipa vērtību pass un atgriež void tipa vērtību null.
     * Šī funkcija reģistrē jaunu lietotāju MongoDB kolekcijā, ja lietotājs vēl nepastāv.
     */
    public void registerPlayer(String user, String pass) {
        if (scoreCollection == null) return;
        
        Document existing = scoreCollection.find(new Document("username", user)).first();
        if (existing == null) {
            Document newPlayer = new Document("username", user)
                                .append("password", pass)
                                .append("highScore", 0);
            scoreCollection.insertOne(newPlayer);
            System.out.println("Player registered: " + user);
        }
    }

    /**
     * funkcija login pieņem String tipa vērtību user un String tipa vērtību pass un atgriež boolean tipa vērtību.
     * Šī funkcija pārbauda, vai norādītie akreditācijas dati ir pareizi.
     */
    public boolean login(String user, String pass) {
        if (scoreCollection == null) return false;
        
        Document query = new Document("username", user).append("password", pass);
        Document player = scoreCollection.find(query).first();
        return player != null;
    }

    /**
     * funkcija updateIfHighScore pieņem String tipa vērtību user un int tipa vērtību newScore un atgriež void tipa vērtību null.
     * Šī funkcija atjaunina spēlētāja rekordpunktu, ja jauns rezultāts ir lielāks par pašreizējo.
     */
    public void updateIfHighScore(String user, int newScore) {
        // Guard against null connection or empty user
        if (scoreCollection == null || user == null || user.isEmpty()) {
            System.err.println("Cannot update score: DB not ready or user empty.");
            return;
        }

        try {
            Document query = new Document("username", user);
            Document player = scoreCollection.find(query).first();

            if (player != null) {
                // RULE: Use Number class for safe casting between Integer/Long
                Object scoreObj = player.get("highScore");
                int currentHighScore = 0;
                
                if (scoreObj instanceof Number) {
                    currentHighScore = ((Number) scoreObj).intValue();
                }

                if (newScore > currentHighScore) {
                    scoreCollection.updateOne(query, new Document("$set", new Document("highScore", newScore)));
                    System.out.println("DB Updated: " + user + " reached " + newScore);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating high score: " + e.getMessage());
        }
    }

    private java.util.List<String> cachedTopPlayers = new java.util.ArrayList<>();
    private long lastLeaderboardFetchTime = 0;

    /**
     * funkcija getTopPlayers pieņem int tipa vērtību limit un atgriež java.util.List<String> tipa vērtību topList.
     * Šī funkcija atgriež labāko spēlētāju sarakstu un kešē to uz vienu sekundi.
     */
    public java.util.List<String> getTopPlayers(int limit) {
        // Cache the leaderboard for 1 second to avoid repeated DB roundtrips each frame
        long now = System.currentTimeMillis();
        if (scoreCollection != null && now - lastLeaderboardFetchTime < 1000 && !cachedTopPlayers.isEmpty()) {
            return new java.util.ArrayList<>(cachedTopPlayers);
        }

        java.util.List<String> topList = new java.util.ArrayList<>();
        if (scoreCollection == null) return topList;

        FindIterable<Document> topPlayers = scoreCollection.find().sort(descending("highScore")).limit(limit);
        for (Document doc : topPlayers) {
            String name = doc.getString("username");
            Object score = doc.get("highScore");
            topList.add(String.format("%s - %s", name, score));
        }

        cachedTopPlayers = new java.util.ArrayList<>(topList);
        lastLeaderboardFetchTime = now;
        return topList;
    }

    /**
     * funkcija showLeaderboard pieņem void tipa vērtību null un atgriež void tipa vērtību null.
     * Šī funkcija izvada konsolē top 10 spēlētāju sarakstu.
     */
    public void showLeaderboard() {
        if (scoreCollection == null) return;
        System.out.println("\n--- LEADERBOARD ---");
        FindIterable<Document> topPlayers = scoreCollection.find().sort(descending("highScore")).limit(10);
        for (Document doc : topPlayers) {
            System.out.println(doc.getString("username") + ": " + doc.get("highScore"));
        }
    }
}