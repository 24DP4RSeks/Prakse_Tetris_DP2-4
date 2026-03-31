package dataBase;

import java.util.ArrayList;

public class DatabaseConnection {
    public static void main(String[] args) {
        // This is the string from your screenshot
        
        try {MongoClient mongoClient = new MongoClients("localhost:27017");
             
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
            databses.forEach(db -> System.out.println(db.toJson()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}