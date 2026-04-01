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

    public void connect() {
    try {
        String connectionString = "mongodb+srv://player1:1234@tetrisusers.xk6hppj.mongodb.net/?retryWrites=true&w=majority&appName=tetrisUsers";
        
        MongoClientURI uri = new MongoClientURI(connectionString);
        MongoClient mongoClient = new MongoClient(uri);
        
        // Let's use a clear database name
        MongoDatabase database = mongoClient.getDatabase("TetrisGame");
        
        // We'll use one collection for both profile and scores
        scoreCollection = database.getCollection("players");
        
        System.out.println("Successfully connected to MongoDB Atlas!");
        
    } catch (Exception e) {
        System.out.println("Connection Error: " + e.getMessage());
        e.printStackTrace();
    }
}

    public void saveScore(String user, int score) {
    // This updates the existing player document rather than creating a new one
    Document query = new Document("username", user);
    Document update = new Document("$set", new Document("highScore", score)
                                           .append("lastPlayed", new java.util.Date()));
    
    scoreCollection.updateOne(query, update);
    System.out.println("Score updated in Atlas for " + user);
}

    public void showLeaderboard() {
        // Find all, sort by score descending, take the top 5
        FindIterable<Document> results = scoreCollection.find()
                                                .sort(descending("score"))
                                                .limit(5);

        for (Document d : results) {
            System.out.println(d.getString("name") + ": " + d.getInteger("score"));
        }
    }

        // 1. REGISTER: Create a new player
    public void registerPlayer(String user, String pass) {
        // Check if player already exists first
        Document existing = scoreCollection.find(new Document("username", user)).first();
        
        if (existing == null) {
            Document newPlayer = new Document("username", user)
                                .append("password", pass) // Note: See security warning below!
                                .append("highScore", 0);
            scoreCollection.insertOne(newPlayer);
            System.out.println("Player registered!");
        } else {
            System.out.println("Username already taken.");
        }
    }

        // 2. LOGIN: Check if credentials match
    public boolean login(String user, String pass) {
        Document query = new Document("username", user).append("password", pass);
        Document player = scoreCollection.find(query).first();
        
        return player != null; // Returns true if found, false if not
    }

        // 3. UPDATE SCORE: Only if the new score is higher
    public void updateIfHighScore(String user, int newScore) {
        Document query = new Document("username", user);
        Document player = scoreCollection.find(query).first();

        if (player != null) {
            int currentHighScore = player.getInteger("highScore");
            if (newScore > currentHighScore) {
                scoreCollection.updateOne(query, new Document("$set", new Document("highScore", newScore)));
                System.out.println("New High Score!");
            }
        }
    }
}