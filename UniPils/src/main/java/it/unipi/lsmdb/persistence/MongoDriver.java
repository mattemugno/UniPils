package it.unipi.lsmdb.persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Order;
import it.unipi.lsmdb.bean.OrderList;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.config.InfoConfig;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

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
            "/?retryWrites=true&w=1&wtimeoutMS=5000&readPreference=primaryPreferred";

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

    //#############         CRUD OPERATIONS         ##############

    // CRUD USERS //

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
                doc_dob.append("date", u.getDob().toString());
                LocalDate lt = LocalDate.now();
                doc_dob.append("age", calculateAge(u.getDob().toLocalDate(), lt));
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

    public static User getUserFromUsername(String username){
        openConnection("Users");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<Document> results = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection.find(eq("login.username", username)).iterator()){
            while(cursor.hasNext()){
                Document doc = cursor.next();
                results.add(doc);
            }

            User user = objectMapper.readValue(results.get(0).toJson(), User.class);

            closeConnection();
            return user;
        } catch (Exception e){
            e.printStackTrace();
            closeConnection();
            return null;
        }
    }

    public static boolean deleteUser(String u){
        openConnection("Users");
        try{
            collection.deleteOne(Filters.eq("username", u));
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
            boolean res=deleteUser(u.getUsername());
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

    // CRUD ORDER

    public static boolean addOrder(String username, Order order){
        openConnection("Users");

        ArrayList<Document> results = new ArrayList<>();
        ArrayList<Document> beer_list = new ArrayList<>();

        try{
            Document doc = new Document();

            doc.append("id_order", order.getIdOrder());
            ArrayList<OrderList> items = order.getOrderList();

                for (OrderList item: items){

                    Document docList = new Document();
                    docList.append("beer_id", item.getBeerId());
                    docList.append("beer_name", item.getBeerName());
                    docList.append("beer_price", item.getBeerPrice());
                    docList.append("quantity", item.getQuantity());

                    beer_list.add(docList);
                }

            doc.append("order_list", beer_list);
            doc.append("delivery-date", order.getDeliveryDate());
            doc.append("feedback", order.getFeedback());
            doc.append("total_cost", order.getTotalCost());
                Document docDate = new Document();
                docDate.append("$date", order.getConfirmationDate());
            doc.append("confirmation_date", docDate);


            Bson setUpdate;

            User user = MongoDriver.getUserFromUsername(username);
            user.setOrders(getUserOrders(username));

            if(user.getOrders() != null && user.getOrders().size() > 0)
                setUpdate = Updates.push("orders", doc);

            else {
                results.add(doc);
                setUpdate = Updates.set("orders", results);
            }

            UpdateOptions options = new UpdateOptions().upsert(true);

            openConnection("Users");
            collection.updateOne(eq("login.username", username), setUpdate);
            closeConnection();

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static ArrayList<Order> getUserOrders(String username){
        openConnection("Users");

        ArrayList<Order> ordersByUsername = new ArrayList<>();
        ArrayList<Document> results = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection.find(eq("login.username", username)).iterator()){
            while(cursor.hasNext()){
                Document doc = cursor.next();
                results.add(doc);
            }

            JSONObject object = (JSONObject) new JSONParser().parse(results.get(0).toJson());
            JSONArray orders_list = (JSONArray) object.get("orders");

            for (int i = 0; i < orders_list.size(); i++){
                // take each order
                JSONObject takeOrder = (JSONObject) orders_list.get(i);
                double feedback;

                // initialize order object
                Order order = new Order();
                order.setIdOrder(((Long) takeOrder.get("id_order")).intValue());
                order.setDeliveryDate((String) takeOrder.get("delivery_date"));
                if (takeOrder.get("feedback") instanceof Long)
                    feedback = (Long) takeOrder.get("feedback");
                else feedback = (Double) takeOrder.get("feedback");
                order.setFeedback(feedback);
                order.setTotalCost(((Long) takeOrder.get("total_cost")).intValue());
                JSONObject date = (JSONObject) new JSONParser().parse(takeOrder.get("confirmation_date").toString());
                order.setConfirmationDate((String) date.get("$date"));

                //get list of beers
                JSONArray beer_list = (JSONArray) takeOrder.get("order_list");

                for (int n = 0; n < beer_list.size(); n++){
                    // get type of beer
                    JSONObject item = (JSONObject) beer_list.get(n);

                    // initialize orderList object
                    OrderList beerType = new OrderList();
                    beerType.setBeerId(((Long) item.get("beer_id")).intValue());
                    beerType.setQuantity(((Long) item.get("quantity")).intValue());
                    beerType.setBeerName((String) item.get("beer_name"));
                    beerType.setBeerPrice(((Long) item.get("beer_price")).intValue());

                    order.setOrderList(beerType);
                }
                ordersByUsername.add(order);
            }

            User user = getUserFromUsername(username);
            user.setOrders(ordersByUsername);

        } catch (Exception e){
            e.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return ordersByUsername;
    }

    public static int getMaxIdOrder(String username){

        openConnection("Users");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson unwindOrders = unwind("$orders");
        Bson group = group("$null", max("maxId", "$orders.id_order"));
        Bson projectFields = project(fields(excludeId(), include("maxId")));

        try {
            collection.aggregate(Arrays.asList(unwindOrders, group, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return results.get(0).getInteger("maxId");
    }

    // CRUD BEER

    public static boolean addBeer(Beer b){
        openConnection("Beers");
        try{
            Document doc= new Document();
            doc.append("_id",b.get_id());
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

            collection.insertOne(doc);

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static boolean deleteBeer(int b){
        openConnection("Beers");
        try{
            collection.deleteOne(Filters.eq("_id", b));
        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static boolean updateBeerViewCount(Beer b){
        openConnection("Beers");
        try{
            Bson setUpdate = Updates.inc("view_count", 1);
            collection.updateOne(eq("_id", b.get_id()), setUpdate);

        }catch(Exception ex){
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static int getMaxBeerId(){
        ArrayList<Document> result;

        openConnection("Beers");
        result=collection.find().sort(descending("_id")).limit(1).into(new ArrayList<>());
        closeConnection();
        try {
            return result.get(0).getInteger("_id");
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static Beer getBeerById(int id){
        openConnection("Beers");
        Beer beer;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<Document> results = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection.find(eq("_id", id)).iterator()){
            while(cursor.hasNext()){
                Document doc = cursor.next();
                results.add(doc);
            }
            beer = objectMapper.readValue(results.get(0).toJson(), Beer.class);

            closeConnection();
            return beer;
        } catch (Exception e){
            e.printStackTrace();
            closeConnection();
            return null;
        }
    }

    // definire anche per gli altri parametri
    public static ArrayList<Beer> getBeersByFilter(String fieldName, String fieldValue){

        openConnection("Beers");

        ArrayList<Document> r;
        r = collection.find(eq(fieldName, fieldValue)).into(new ArrayList<>());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ArrayList<Beer> beers = new ArrayList<>();

        try{
            for (Document doc: r){
                Beer beer = objectMapper.readValue(doc.toJson(), Beer.class);
                beers.add(beer);
            }

            return beers;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static int getBreweryId(String name){
        ArrayList<Document> result;
        int maxId=0;
        openConnection("Beers");
        result=collection.find(eq("Brewery.name",name)).limit(1).into(new ArrayList<>());

        if(result.isEmpty()){
            maxId=getMaxBreweryId()+1;
        }else{
            maxId=result.get(0).get("Brewery",Document.class).getInteger("id");
        }

        closeConnection();
        try {
            return maxId;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private static int getMaxBreweryId() {
        ArrayList<Document> result;

        openConnection("Beers");
        result= collection.find().sort(descending("Brewery.id")).limit(1).into(new ArrayList<>());
        closeConnection();
        try {
            return result.get(0).get("Brewery",Document.class).getInteger("id");
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //#############  AGGREGATION ###########

    public static ArrayList<Document> getBeerOfTheMonth(){

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
        Bson projectFields = project(fields(
                include("beer_name"),
                computed("TotalPurchased", "$total_purchased")));

        try {
            collection.aggregate(Arrays.asList(unwindOrders, matchDate, unwindOrdersList,
                    groupId, sort, limitResults, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return results;
    }

    public static ArrayList<Document> getCheapestBeersByStyle(String style){

        openConnection("Beers");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson matchStyle = match(eq("style", style));

        Bson groupPrice = group(fields(
                        eq("price", "$price"), eq("beer_id", "$_id")),
                        first("beer_name", "$name"),
                        first("view_count", "$view_count")
        );
        Bson sort = sort(fields(ascending("_id.price"), descending("view_count")));

        Bson limitResults = limit(10);

        Bson projectFields = project(fields
                (computed("Beer Name", "$beer_name"),
                        computed("Price", "$_id.price"),
                 computed("View Count", "$view_count"))
        );

        try {
            collection.aggregate(Arrays.asList(matchStyle, groupPrice, sort, limitResults, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        closeConnection();
        return results;
    }

    public static ArrayList<Document> getMostPopularEachState(){

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
        //Bson limit = limit(15);
        Bson projectFields = project(fields(excludeId(),
                computed("state", "$_id"),
                include("style"),
                computed("MostPopularStyleCount", "$mostPopularStyleCount")
        ));

        try {
            collection.aggregate(Arrays.asList(matchState, groupStateStyle, sort, groupState, sortMostPopular, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return results;
    }

    public static ArrayList<Document> getBuyers(){

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

        closeConnection();
        return results;
    }

    //############ QUERY ADMIN #############

    public static ArrayList<Document> getAvgOrderPrice(){

        openConnection("Users");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson unwindOrders = unwind("$orders");
        Bson match = match(exists("orders", true));
        Bson group = group("$null", avg("avg_cost", "$orders.total_cost"));
        Bson projectFields = project(fields(excludeId(),
                computed("AvgOrderPrice", "$avg_cost")));

        try {
            collection.aggregate(Arrays.asList(unwindOrders, match, group, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return results;
    }

    public static ArrayList<Document> getAvgAge(){

        openConnection("Users");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson group = group("$null", avg("avg_age", "$dob.age"));
        Bson projectFields = project(fields(excludeId(),
                computed("AvgAge", "$avg_age")));

        try {
            collection.aggregate(Arrays.asList(group, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return results;
    }

    public static ArrayList<Document> getGenderDistribution(){

        openConnection("Users");

        ArrayList<Document> results = new ArrayList<>();
        Consumer<Document> createDocuments = doc -> {results.add(doc);};

        Bson group = group("$gender", sum("total", 1));
        Bson projectFields = project(fields(excludeId(),
                computed("Gender", "$_id"),
                computed("Total", "$total"))
        );

        try {
            collection.aggregate(Arrays.asList(group, projectFields)).forEach(createDocuments);
        } catch (Exception e){
            e.printStackTrace();
        }

        closeConnection();
        return results;
    }
}



