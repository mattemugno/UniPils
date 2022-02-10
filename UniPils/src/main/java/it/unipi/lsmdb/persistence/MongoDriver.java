package it.unipi.lsmdb.persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Order;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.config.InfoConfig;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
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

    //#############         CRUD OPERATIONS         ##############

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

            try {
                collection.insertOne(doc);
                String id;
                id = doc.getObjectId("_id").toString();
                DataSession.IdUserLogged(id);
            } catch (Exception e){
                e.printStackTrace();
            }

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static ArrayList<User> getUser(String username){
        openConnection("Users");
        ArrayList<Document> results = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection.find(eq("login.username", username)).iterator()){
            while(cursor.hasNext()){
                Document user = cursor.next();
                if (user == null) return null;
                else results.add(user);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(results);
        return getBeanFromDocuments(results);
    }

    public static boolean deleteUser(User u){
        openConnection("Users");
        try{
            collection.deleteOne(Filters.eq("username", u.getUsername()));
        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static boolean updateUser(User u){
        openConnection("Users");
        try{
            boolean res=deleteUser(u);
            if(!res)
            {
                System.out.println("A problem has occurred in modify user");
                return false;
            }

            res= addUser(u);
            if(!res)
            {
                System.out.println("A problem has occurred in modify user");
                return false;
            }

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static boolean addOrder(User u, Order o){
        openConnection("Users");
        System.out.println(u.getUsername());
        try{
            Document doc = new Document();

            doc.append("id_order",o.getIdOrder());
            doc.append("order_list",Arrays.asList(o.getOrderList()));
            doc.append("delivery-date",o.getDeliveryDate());
            doc.append("feedback",o.getFeedback());
            doc.append("total_cost",o.getTotalCost());
            doc.append("confirmation_date",o.getConfirmationDate());

            Bson filter = Filters.eq("username", u.getUsername()); //get the parent-document
            Bson setUpdate;
            if(u.getOrders() != null && u.getOrders().size() > 0)
                setUpdate = Updates.push("orders", doc);
            else {
                ArrayList<Document> orderList = new ArrayList<>();
                orderList.add(doc);
                setUpdate = Updates.set("orders", orderList);
            }

            collection.updateOne(filter, setUpdate);

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static boolean addBeer(Beer b){
        openConnection("Beers");
        try{
            Document doc= new Document();
            doc.append("_id",b.getId());
            doc.append("name", b.getName());
            doc.append("state",b.getName());
            doc.append("country",b.getCountry());
            doc.append("style",b.getStyle());
            doc.append("availability",b.getAvailability());
            doc.append("abv",b.getAbv());
            doc.append("volume",b.getVolume());
            doc.append("price",b.getPrice());

            Document doc_brewery = new Document();
                doc_brewery.append("id", b.getBrewery_id());
                doc_brewery.append("name",b.getBrewery_name());
                doc_brewery.append("city",b.getBrewery_city());
                doc_brewery.append("types", b.getBrewery_types());
            doc.append("Brewery", doc_brewery);

            doc.append("view_count",b.getView_count());

            System.out.println(doc);
            collection.insertOne(doc);

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static boolean deleteBeer(Beer b){
        openConnection("Beers");
        try{
            collection.deleteOne(Filters.eq("_id", b.getId()));
        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static boolean updateBeer(Beer b){
        openConnection("Beers");
        try{
            boolean res=deleteBeer(b);
            if(!res)
            {
                System.out.println("A problem has occurred in modify beer");
                return false;
            }

            res= addBeer(b);
            if(!res)
            {
                System.out.println("A problem has occurred in modify beer");
                return false;
            }

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    //#############  AGGREGATION ###########

    public static ArrayList<User> getBeerOfTheMonth(){

        openConnection("Users");

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        Date lastMonth = cal.getTime();

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson unwindOrders = unwind("$orders");
        Bson matchDate = match(and(gte("orders.confirmation_date", lastMonth),
                        lte("orders.confirmation_date", currentDate)));
        Bson unwindOrdersList = unwind("$orders.order_list");
        Bson groupId = group("$orders.order_list.beer_id", sum("total_purchased", 1),
                first("beer_name", "$orders.order_list.beer_name"));
        Bson sort = sort(descending("total_purchased"));
        Bson limitResults = limit(20);
        Bson projectFields = project(fields(excludeId(),
                include("beer_name"),
                computed("TotalPurchased", "$total_purchased")));

        try {
            collection.aggregate(Arrays.asList(unwindOrders, matchDate, unwindOrdersList,
                    groupId, sort, limitResults, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return getBeanFromDocuments(results);
    }

    public static ArrayList<User> getCheapestBeerByStyle(String style){

        openConnection("Beers");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson matchStyle = match(eq("style", style));
        Bson groupPrice = group("$price", push("beer_name", "$name"));
        Bson sort = sort(ascending("_id"));
        Bson limit = limit(20);
        Bson projectFields = project(
                fields(excludeId(), computed("price", "$_id"), computed("Beer Name", "$beer_name"))
        );

        try {
            collection.aggregate(Arrays.asList(matchStyle, groupPrice, sort, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(results);
        closeConnection();
        return getBeanFromDocuments(results);
    }

    public static ArrayList<User> getMostPopularEachState(){

        openConnection("Beers");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson matchState = match(eq("country", "US"));
        Bson groupStateStyle = group(fields(
                eq("style", "$style"),
                eq("state", "$state")),
                sum("beers", 1)
        );
        Bson sort = sort(descending("beers"));
        Bson groupState = group("$_id.state", first("style", "$_id.style"),
                max("mostPopularStyleCount", "$beers"));
        Bson sortMostPopular = sort(descending("mostPopularStyleCount"));
        Bson limit = limit(15);
        Bson projectFields = project(fields(excludeId(),
                computed("state", "$_id"),
                include("style"),
                computed("MostPopularStyleCountry", "$mostPopularStyleCount")
        ));

        try {
            collection.aggregate(Arrays.asList(matchState, groupStateStyle, sort, groupState, sortMostPopular, limit)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(results);
        closeConnection();
        return getBeanFromDocuments(results);
    }

    public static ArrayList<User> getBuyers(){

        openConnection("Users");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson unwindOrders = unwind("$orders");
        Bson matchBuyers = match(exists("orders", true));
        Bson groupUser = group("$_id", sum("numberOrders", 1));
        Bson groupTotalBuyers = group("$null", sum("numberOfBuyers", 1), avg("AvgOrders", "$numberOrders"));
        Bson projectFields = project(fields(excludeId(),
                include("numberOfBuyers"),
                include("AvgOrders")
        ));

        try {
            collection.aggregate(Arrays.asList(unwindOrders, matchBuyers, groupUser, groupTotalBuyers, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(results);
        closeConnection();
        return getBeanFromDocuments(results);
    }
}



