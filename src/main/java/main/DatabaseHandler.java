package main.java.main;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import static com.mongodb.client.model.Sorts.descending;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.ArrayList;
import java.util.List;

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
                                .append("highScore", 0)
                                .append("gameCount", 0);
            scoreCollection.insertOne(newPlayer);
            System.out.println("Player registered: " + user);
        }
    }

    public void incrementGameCount(String username) {
        if (scoreCollection == null || username == null || username.equalsIgnoreCase("Guest")) return;
        
        try {
            // Using the inc update operator
            scoreCollection.updateOne(eq("username", username), inc("gameCount", 1));
        } catch (Exception e) {
            System.err.println("Error incrementing game count: " + e.getMessage());
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
    public void saveScore(String user, int score) {
    if (scoreCollection == null || user == null || user.equalsIgnoreCase("Guest")) return;
    
    try {
        Document found = scoreCollection.find(eq("username", user)).first();
        if (found != null) {
            // Change "score" to "highScore"
            int oldScore = found.getInteger("highScore", 0); 
            if (score > oldScore) {
                // Change "score" to "highScore"
                scoreCollection.updateOne(eq("username", user), set("highScore", score));
            }
        } else {
            Document newPlayer = new Document("username", user)
                    .append("highScore", score) // Change "score" to "highScore"
                    .append("gameCount", 1);
            scoreCollection.insertOne(newPlayer);
        }
    } catch (Exception e) {
        System.err.println("Error saving score: " + e.getMessage());
    }
}

    private java.util.List<String> cachedTopPlayers = new java.util.ArrayList<>();
    private long lastLeaderboardFetchTime = 0;

    

    /**
     * funkcija getTopPlayers pieņem int tipa vērtību limit un atgriež java.util.List<String> tipa vērtību topList.
     * Šī funkcija atgriež labāko spēlētāju sarakstu un kešē to uz vienu sekundi.
     */
    public List<Document> getLeaderboard(String nameFilter, String sortBy) { // Added sortBy
    List<Document> list = new ArrayList<>();
    if (scoreCollection == null) return list;

    try {
        FindIterable<Document> iterable;
        // Default to highScore if sortBy is null
        String field = (sortBy == null || sortBy.isEmpty()) ? "highScore" : sortBy;

        if (nameFilter == null || nameFilter.trim().isEmpty()) {
            iterable = scoreCollection.find().sort(descending(field)).limit(10);
        } else {
            iterable = scoreCollection.find(regex("username", "^" + nameFilter, "i"))
                                     .sort(descending(field)).limit(10);
        }

        for (Document doc : iterable) {
            list.add(doc);
        }
    } catch (Exception e) {
        System.err.println("Error fetching leaderboard: " + e.getMessage());
    }
    return list;
}

    /**
     * funkcija deleteAccount pieņem String tipa vērtību user un atgriež void tipa vērtību null.
     * Šī funkcija izdzēš lietotāja kontu no MongoDB kolekcijas.
     */
    public void deleteAccount(String user) {
        if (scoreCollection == null || user == null || user.isBlank()) {
            System.err.println("Cannot delete account: DB not ready or invalid user.");
            return;
        }

        try {
            scoreCollection.deleteOne(new Document("username", user));
            System.out.println("Deleted account: " + user);
        } catch (Exception e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }
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