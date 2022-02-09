package it.unipi.lsmdb.persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.config.InfoConfig;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.*;
import static it.unipi.lsmdb.utils.Utils.calculateAge;

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

        ArrayList<Document> res= new ArrayList<>(results);
        closeConnection();
        return getBeanFromDocuments(res);
    }

    public static boolean addUser(User u){
        openConnection("Users");

        try{

            Document doc= new Document();

            doc.append("gender", u.getGender());

            Document doc_name = new Document();
                doc_name.append("first", u.getFirst());
                doc_name.append("last", u.getLast());
            doc.append("name", doc_name);

            doc.append("email", u.getEmail());

            Document doc_login = new Document();
                doc_login.append("username", u.getUsername());
                doc_login.append("password", u.getPassword());
            doc.append("login", doc_login);

            Document doc_dob = new Document();
                doc_dob.append("date", u.getDob());
                LocalDate lt = LocalDate.now();
                doc_dob.append("age", calculateAge(u.getDob(), lt));
            doc.append("dob", doc_dob);

            doc.append("cell", u.getCell());

            //try {
            System.out.println(doc);
                collection.insertOne(doc);
                String id;
                id = doc.getObjectId("_id").toString();
                DataSession.IdUserLogged(id);
            /*} catch (Exception e){
                e.printStackTrace();
            }*/


        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static ArrayList<User> getBeerOfTheMonth(){

        openConnection("Users");

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        cal.add(Calendar.MONTH, -6);
        Date lastMonth = cal.getTime();

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson unwindOrders = unwind("$orders");
        Bson matchDate = match(and(gte("orders.confirmation_date", lastMonth),
                        lte("orders.confirmation_date", currentDate)));
        Bson unwindOrdersList = unwind("$orders.order_list");
        Bson groupId = group("$orders.order_list.beer_id", sum("total_purchased", 1));
        Bson sort = sort(descending("total_purchased"));
        Bson limitResults = limit(20);
        Bson projectFields = project(fields(excludeId(),
                include("beer_name"),
                computed("Total Purchased", "$total_purchased")));

        try {
            collection.aggregate(Arrays.asList(unwindOrders, matchDate, unwindOrdersList,
                    groupId, sort, limitResults, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return getBeanFromDocuments(results);
    }
}
