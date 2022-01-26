package it.unipi.dii;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class GenerateOrderDataset {
    private static final int TOTAL_ORDERS = 100000;

    private static int id_order;
    private static String uuid;
    private static String first_name;
    private static String final_name;
    private static Long beer_id;
    private static String beer_name;
    private static Long beer_price;
    private static int quantity;
    private static int total_cost;

    public static void main(String[] args) throws IOException, ParseException {

        ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("UniPils");
        MongoCollection<Document> beer = database.getCollection("beer");
        MongoCollection<Document> user = database.getCollection("Users");

        FileWriter orders = new FileWriter("/home/matteo/Scrivania/orders.json");

        for (int i = 0; i < TOTAL_ORDERS; i++) {
            id_order = i;
            getUserParameters(user);
            StringWriter jsonObjectWriter = getDocument(beer);
            orders.write(jsonObjectWriter.toString());
        }
        orders.close();
    }

    private static void getBeerParameters(MongoCollection<Document> beer) throws IOException, ParseException {

        FileWriter rand_beer = new FileWriter("/home/matteo/Scrivania/beerRand.json");

        beer.aggregate(Arrays.asList(Aggregates.sample(1))).forEach(document -> {
        try {
            rand_beer.write(document.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
        rand_beer.close();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("/home/matteo/Scrivania/beerRand.json"));
        JSONObject item = (JSONObject) obj;

        beer_id = (Long) item.get("id");
        beer_name = (String) item.get("name");
        beer_price = (Long) item.get("price");
        quantity = getRand();
        total_cost += (beer_price*quantity);
    }

    private static void getUserParameters(MongoCollection<Document> user) throws IOException, ParseException {

        FileWriter rand_user = new FileWriter("/home/matteo/Scrivania/userRand.json");

        user.aggregate(Arrays.asList(Aggregates.sample(1))).forEach(document -> {
            try {
                rand_user.write(document.toJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        rand_user.close();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("/home/matteo/Scrivania/userRand.json"));
        JSONObject person = (JSONObject) obj;

        JSONObject login = (JSONObject) person.get("login");
        uuid = (String) login.get("uuid");

        JSONObject name = (JSONObject) person.get("name");
        first_name = (String) name.get("first");
        final_name = (String) name.get("last");
    }

    public static String generateCardNumber(int length){
        Random rnd = new Random();
        int counter = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while(counter < length){
            int generate = rnd.nextInt(9);
            stringBuffer.append(generate);
            counter++;
        }
        return stringBuffer.toString();
    }

    public static String generateRandExpDate(){
        Random random = new Random();
        int minDay = (int) LocalDate.of(2022, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2030, 12, 31).toEpochDay();
        long randomDay = minDay + random.nextInt(maxDay - minDay);

        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);
        return randomBirthDate.toString();
    }

    public static String generateRandDeliveryDate(){
        Random random = new Random();
        int minDay = (int) LocalDate.of(2022, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2022, 6, 30).toEpochDay();
        long randomDay = minDay + random.nextInt(maxDay - minDay);

        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);
        return randomBirthDate.toString();
    }

    public static double getFeedback(double rangeMin, double rangeMax){
        double random = ThreadLocalRandom.current().nextDouble(rangeMin, rangeMax);
        return round(random, 1);
    }

    private static int getRand() {
        DistributedRandomNumberGenerator drng = new DistributedRandomNumberGenerator();
        drng.addNumber(1, 0.50d);
        drng.addNumber(2, 0.20d);
        drng.addNumber(3, 0.10d);
        drng.addNumber(4, 0.10d);
        drng.addNumber(5, 0.05d);
        drng.addNumber(6, 0.01d);
        drng.addNumber(7, 0.01d);
        drng.addNumber(8, 0.01d);
        drng.addNumber(9, 0.01d);
        drng.addNumber(10, 0.01d);

        return drng.getDistributedRandomNumber();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private static StringWriter getDocument(MongoCollection<Document> beer) throws IOException, ParseException {
        JsonFactory factory = new JsonFactory();
        StringWriter jsonObjectWriter = new StringWriter();
        JsonGenerator generator = factory.createGenerator(jsonObjectWriter);
        generator.useDefaultPrettyPrinter();

        generator.writeStartObject();

            generator.writeFieldName("id_order");
            generator.writeNumber(id_order);

            generator.writeFieldName("user");
            generator.writeStartObject();
            generator.writeFieldName("user_id");
            generator.writeString(uuid);
            generator.writeFieldName("first_name");
            generator.writeString(first_name);
            generator.writeFieldName("final_name");
            generator.writeString(final_name);
            generator.writeFieldName("card_number");
            generator.writeString(generateCardNumber(16));
            generator.writeFieldName("CVV");
            generator.writeString(generateCardNumber(3));
            generator.writeFieldName("expiration_date");
            generator.writeString(generateRandExpDate());
            generator.writeEndObject();

            generator.writeFieldName("order_list");
            generator.writeStartArray();
                int list = getRand();
                for (int i = 0; i < list; i++) {
                    getBeerParameters(beer);
                    generator.writeStartObject();
                        generator.writeFieldName("beer_id");
                        generator.writeNumber(beer_id);
                        generator.writeFieldName("beer_name");
                        generator.writeString(beer_name);
                        generator.writeFieldName("beer_price");
                        generator.writeNumber(beer_price);
                        generator.writeFieldName("quantity");
                        generator.writeNumber(quantity);
                    generator.writeEndObject();
                }
            generator.writeEndArray();

            generator.writeFieldName("delivery_date");
            generator.writeString(generateRandDeliveryDate());
            generator.writeFieldName("feedback");
            generator.writeNumber(getFeedback(1, 5));
            generator.writeFieldName("total_cost");
            generator.writeNumber(total_cost);
            total_cost = 0;
        generator.close();

        return jsonObjectWriter;
    }
}

