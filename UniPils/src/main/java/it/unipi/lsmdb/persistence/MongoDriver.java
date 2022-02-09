package it.unipi.lsmdb.persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mongodb.client.model.*;
import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.InfoConfig;
import com.mongodb.client.*;
import com.mongodb.ConnectionString;
import it.unipi.lsmdb.utils.Utils;
import org.bson.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Projections.*;

public class MongoDriver {

    private static MongoClient myClient;
    private static MongoDatabase db;
    private static MongoCollection<org.bson.Document> collection;

    private static final String connectionString = "mongodb://" + InfoConfig.getMongoFirstIp() + ":" + InfoConfig.getMongoFirstPort() +
            "," + InfoConfig.getMongoSecondIp() + ":" + InfoConfig.getMongoSecondPort() +
            "," + InfoConfig.getMongoThirdIp() + ":" + InfoConfig.getMongoThirdPort() +
            "/?retryWrites=true&w=3&wtimeoutMS=5000&readPreference=nearest";

    private static final ConnectionString uri= new ConnectionString(connectionString);

    private static void openConnection(String nameCollection){
        try{
            myClient= MongoClients.create(uri);
            db= myClient.getDatabase(InfoConfig.getMongoDbName());
            collection= db.getCollection(nameCollection);
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

    private static ArrayList<User> getBeanFromDocuments(ArrayList<Document> results){

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<User> users = new ArrayList<>();
        try {
            for (Document doc : results) {
                User user = objectMapper.readValue(doc.toJson(), User.class);
                users.add(user);
            }

            return users;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }




    public static ArrayList<User> getBeersFromUsername(String username){
        List<Document> results;

        openConnection("User");
        results=Arrays.asList(new Document("$unwind",
                        new Document("path", "$orders")),
                new Document("$unwind",
                        new Document("path", "$orders.order_list")),
                new Document("$match",
                        new Document("login.username", "silversnake781")),
                new Document("$project",
                        new Document("orders.order_list", 1L)));
        //Bson myMatch = Aggregates.match(Filters.eq("login.username", username));
        //Bson mySort = sort(Sorts.descending("datePublished"));
        //Bson projection = Aggregates.project( fields(excludeId(), include("recipeId","name", "authorName", "recipeCategory", "datePublished")));

        //results = collection.aggregate(Arrays.asList(myMatch, mySort, projection))
             //   .into(new ArrayList<>());
        ArrayList<Document> res=new ArrayList<Document>(results);
        closeConnection();
        return getBeanFromDocuments(res);
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
