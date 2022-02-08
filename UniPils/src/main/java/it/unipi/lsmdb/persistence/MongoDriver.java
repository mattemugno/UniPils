package it.unipi.lsmdb.persistence;

import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.InfoConfig;
import com.mongodb.client.*;
import com.mongodb.ConnectionString;
import org.bson.Document;

public class MongoDriver {

    private static MongoClient myClient;
    private static MongoDatabase db;
    private static MongoCollection<org.bson.Document> collection;

    private static final String connectionString = "mongodb://" + InfoConfig.getMongoFirstIp() + ":" + InfoConfig.getMongoFirstPort() +
            "," + InfoConfig.getMongoSecondIp() + ":" + InfoConfig.getMongoSecondPort() +
            "," + InfoConfig.getMongoThirdIp() + ":" + InfoConfig.getMongoThirdPort() +
            "/?retryWrites=true&w=3&wtimeoutMS=5000&readPreference=nearest";

    private static final ConnectionString uri= new ConnectionString(connectionString);

    private static void openUserConnection(){
        try{
            myClient= MongoClients.create(uri);
            db= myClient.getDatabase(InfoConfig.getMongoDbName());
            collection= db.getCollection("users");
        }catch (Exception ex){
            System.out.println("Impossible open connection with MongoDB");
        }
    }

    private static void openBeersConnection(){
        try{
            myClient= MongoClients.create(uri);
            db= myClient.getDatabase(InfoConfig.getMongoDbName());
            collection= db.getCollection("beers");
        }catch (Exception ex){
            System.out.println("Impossible open connection with MongoDB");
        }
    }


    private static void closeConnection(){
        try{
            myClient.close();
        }catch (Exception ex){
            System.out.println("Impossible close connection with MongoDB");
        }
    }

    public static boolean addUser(User u){
        openUserConnection();

        try{

            Document doc= new Document();
            doc.append("username", u.getUsername());
            doc.append("email", u.getEmail());
            doc.append("first_name", u.getFirst());
            doc.append("last_name", u.getLast());
            doc.append("DOB", u.getDob());
            doc.append("gender", u.getGender());
            doc.append("cellular", u.getCellular());

            collection.insertOne(doc);

    }catch(Exception ex){
        closeConnection();
        return false;
    }
    closeConnection();
        return true;
}

}
