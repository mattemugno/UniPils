package it.unipi.lsmdb.persistence;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.InfoConfig;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class NeoDriver {

    private static NeoDriver instance = null;
    private final String uri;
    private final String user;
    private final String password;
    private Driver driver;

    private NeoDriver() {
        uri = "bolt://" + InfoConfig.getNeo4jIp() + ":" + InfoConfig.getNeo4jPort();
        this.user = InfoConfig.getNeo4jUsername();
        this.password = InfoConfig.getNeo4jPassword();
        openConnection();
    }

    public static NeoDriver getInstance() {
        if (instance == null) {
            instance = new NeoDriver();
        }
        return instance;
    }

    private void openConnection() {
        try {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
            driver.verifyConnectivity();
        } catch (Exception ex) {
            System.out.println("Impossible open connection with Neo4j");
        }
    }

    public void closeConnection() {
        try {
            driver.close();
        } catch (Exception ex) {
            System.out.println("Impossible close connection with Neo4j");
        }
    }

    //#############         CRUD OPERATIONS         ##############

    //function called in registration case
    public boolean addUser(User user) {

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (u:User {username:$uname, password:$pwd, first_name: $first, last_name: $last})",
                        Values.parameters(
                                "uname", user.getUsername(),
                                "pwd", user.getPassword(),
                                "first", user.getFirst(),
                                "last", user.getLast()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    //function called in login case
    public boolean getUser(String username, String password){

        AtomicBoolean found = new AtomicBoolean(true);

        try (Session session = driver.session()) {

            session.readTransaction( tx -> {
                Result result=tx.run("MATCH (u:User) WHERE u.username=$uName and u.password=$pwd RETURN *",
                        Values.parameters(
                                "uName", username,
                                "pwd", password
                        )
                );
                if (!result.hasNext()) {
                    found.set(false);
                }
                return null;
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return found.get();
    }

    //function called by admin when need to add new beer
    public boolean addBeer(Beer beer) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (b:Beer {name:$beerName, id:$beerId, style:$sty, brewery_name:$breweryName, brewery_id: $breweryId})",
                        Values.parameters(
                                "beerName", beer.getName(),
                                "beerId", beer.get_id(),
                                "sty", beer.getStyle(),
                                "breweryName", beer.getBrewery_name(),
                                "breweryId", beer.getBrewery_id()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addReview(Review review, String username, int beerId){

        //String ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User),(b:Beer)"+
                          "WHERE u.username=$username and b.id=$bid "+
                          "MERGE (u)-[:POSTED]->(r:Review {comment:$text, score:$sc, timestamp:localdatetime()})-[:RELATED]->(b)",
                        Values.parameters(
                                "username", username,
                                "bid", beerId,
                                "text", review.getComment(),
                                "sc", review.getScore()
                                //"ts", ts  //formato diverso da quello del dataset
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<Review> getBeerReviews(int beerId){

        ArrayList<Review> reviews= new ArrayList<>();

        try (Session session = driver.session()) {

            session.writeTransaction( tx -> {
                Result result = tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer)"+
                                "WHERE b.id=$bid "+
                                "RETURN r.comment, r.score, r.timestamp",
                        Values.parameters(
                                "bid", beerId
                        )
                );
                while(result.hasNext()){
                    Record r = result.next();
                    String text = String.valueOf(r.get("r.comment"));
                    Value score = r.get("r.score");
                    Value ts = r.get("r.timestamp");
                    Review rev = new Review(text, score,ts);
                    reviews.add(rev);
                }
                return reviews;


            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return reviews;
    }

    public ArrayList<String> getAuthorReview(int beerId){

        ArrayList<String> authors= new ArrayList<>();

        try (Session session = driver.session()) {

            session.writeTransaction( tx -> {
                Result result = tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer)"+
                                "WHERE b.id=$bid "+
                                "RETURN u.username",
                        Values.parameters(
                                "bid", beerId
                        )
                );
                while(result.hasNext()){
                    Record r = result.next();
                    String user = r.get("u.username").asString();
                    authors.add(user);
                }
                return authors;


            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return authors;
    }

    public ArrayList<Review> getReviewsUser(String username,int beerId){
       ArrayList<Review> reviews= new ArrayList<>();

        try (Session session = driver.session()) {

            session.readTransaction( tx -> {
                Result result = tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer)"+
                                "WHERE u.username=$user and b.id=$beerId "+
                                "RETURN r.comment, r.score, r.timestamp,b.id",
                        Values.parameters(
                                "user",username,
                                "beerId",beerId
                        )
                );
                while(result.hasNext()){
                    Record r = result.next();
                    reviews.add(new Review(r.get("r.comment").asString(),r.get("r.score").asInt(),r.get("r.timestamp").asLocalDateTime(),r.get("b.id").asInt()));
                }
               return reviews;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return reviews;
    }

    public boolean addFollows(String u1, String u2) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u1:User),(u2:User)"+
                          "WHERE u1.username=$un1 and u2.username=$un2 "+
                          "MERGE (u1)-[:FOLLOWS]->(u2)",
                        Values.parameters(
                                "un1", u1,
                                "un2", u2
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<String> getFollowers(String username,int skip,int limit){
        ArrayList<String> followers= new ArrayList<>();
        try (Session session = driver.session()) {

            session.readTransaction(tx -> {
                Result result=tx.run("MATCH (u1:User)-[rel:FOLLOWS]->(u2:User) "+
                                        "WHERE u2.username=$un "+
                                        "RETURN u1.username AS user "+
                                        "order by user asc "+
                                        "skip $toSkip "+
                                        "LIMIT $toLimit ",
                        Values.parameters(
                                "un", username,
                                            "toSkip",skip,
                                            "toLimit",limit
                        )
                );

                while(result.hasNext()){
                    Record r = result.next();
                    followers.add(r.get("user").asString());
                }
                return followers;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return followers;
    }

    public ArrayList<String> getFollower(String username,String follow){
        ArrayList<String> res=new ArrayList<>();
        try (Session session = driver.session()) {

            session.readTransaction(tx -> {
                Result result=tx.run("MATCH (u1:User)-[rel:FOLLOWS]->(u2:User) "+
                                "WHERE u1.username=$un1 and u2.username=$un2  "+
                                "RETURN u1.username AS user",
                        Values.parameters(
                                "un1", username,
                                            "un2",follow
                        )
                );
                if(result.hasNext()){
                    Record r=result.next();
                    res.add(r.get("user").asString());
                }

                return res;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return res;
    }

    public ArrayList<String> getFollowing(String username,int skip,int limit){
        ArrayList<String> followers= new ArrayList<>();
        try (Session session = driver.session()) {

            session.readTransaction(tx -> {
                Result result=tx.run("MATCH (u1:User)-[rel:FOLLOWS]->(u2:User) "+
                                "WHERE u1.username=$un "+
                                "RETURN u2.username AS user "+
                                "order by user asc "+
                                "skip $toSkip "+
                                "LIMIT  $toLimit ",
                        Values.parameters(
                                "un", username,
                                            "toSkip",skip,
                                            "toLimit",limit
                        )
                );

                while(result.hasNext()){
                    Record r = result.next();
                    followers.add(r.get("user").asString());
                }
                return followers;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return followers;
    }

    public boolean deleteFollows(String u1, String u2) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u1:User)-[rel:FOLLOWS]->(u2:User)"+
                                "WHERE u1.username=$un1 and u2.username=$un2 "+
                                "DELETE rel",
                        Values.parameters(
                                "un1", u1,
                                "un2", u2
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addPurchased(String username, int beerId) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User),(b:Beer)"+
                          "WHERE u.username=$username and b.id=$bid "+
                          "MERGE (u)-[:PURCHASED]->(b)",

                        Values.parameters(
                                "username", username,
                                "bid", beerId
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addHasInWishlist(String username, int beerId) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User),(b:Beer)"+
                                "WHERE u.username=$username and b.id=$bid "+
                                "MERGE (u)-[:HAS_IN_WISHLIST]->(b)",

                        Values.parameters(
                                "username", username,
                                "bid", beerId
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteHasInWishlist(String username, int beerId) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User)-[r:HAS_IN_WISHLIST]->(b:Beer)"+
                          "WHERE u.username=$username and b.id=$bid "+
                          "DELETE (r)",

                        Values.parameters(
                                "username", username,
                                "bid", beerId
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateReview(Review review, String username, int beerId){
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer)"+
                          "WHERE u.username=$username and b.id=$bid "+
                          "SET r.comment=$text, r.score=$score",
                        Values.parameters(
                                "username", username,
                                "bid", beerId,
                                "text", review.getComment(),
                                "score", review.getScore()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteUser(String username) {

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User)"+
                          "WHERE u.username=$username "+
                          "DETACH DELETE u",
                        Values.parameters(
                                "username", username
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteBeer(int beerId) {

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (b:Beer)"+
                          "WHERE b.id=$bid "+
                          "DETACH DELETE b",
                        Values.parameters(
                                "bid", beerId
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteReview(String username, int beerId) {

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer)"+
                                "WHERE u.username=$username and b.id=$bid "+
                                "DETACH DELETE r",
                        Values.parameters(
                                "username", username,
                                "bid", beerId
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<Beer> getBeersUser(String username,int skip){
        ArrayList<Beer> beers= new ArrayList<>();

        try(Session session= driver.session()){

            session.readTransaction(tx->{
                Result result = tx.run("MATCH (u:User)-[:PURCHASED]->(b:Beer)" +
                                        "where u.username = $userId "+
                                        "RETURN b.id, b.name, b.style, b.brewery_name "+
                                        "order by b.name asc "+
                                        "skip $toSkip "+
                                        "LIMIT  1 ",
                        Values.parameters("userId", username,"toSkip",skip));

                while(result.hasNext()){
                    Record r= result.next();
                    int id = r.get("b.id").asInt();
                    String beerName = r.get("b.name").asString();
                    String style = r.get("b.style").asString();
                    String brewery_name = r.get("b.brewery_name").asString();
                    Beer beer= new Beer(id, beerName, style, brewery_name);
                    beers.add(beer);
                }
                return beers;
            });

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public ArrayList<Beer> getUserWishlist(String username){
        ArrayList<Beer> beers = new ArrayList<>();

        try (Session session = driver.session()) {

            session.writeTransaction(tx -> {
                Result result=tx.run("MATCH (u:User)-[:HAS_IN_WISHLIST]->(b:Beer)"+
                                "WHERE u.username=$username "+
                                "RETURN b.id, b.name, b.brewery_name, b.style",

                        Values.parameters(
                                "username", username
                        )
                );
                while(result.hasNext()) {
                    Record r = result.next();
                    String name = r.get("b.name").asString();
                    int id = r.get("b.id").asInt();
                    String brew = r.get("b.brewery_name").asString();
                    String style = r.get("b.style").asString();
                    Beer beer = new Beer(id,name,style,brew);
                    beers.add(beer);
                }
                return beers;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public ArrayList<User> getUsername(String username){

        ArrayList<User> users = new ArrayList<>();

        try (Session session = driver.session()) {

            session.readTransaction( tx -> {
                Result result=tx.run("MATCH (u:User) WHERE u.username CONTAINS $pattern " +
                                "RETURN u.username, u.password, u.first_name, u.last_name LIMIT 30",
                        Values.parameters(
                                "pattern", username
                        )
                );
                while(result.hasNext()){
                    Record r = result.next();
                    String uname = r.get("u.username").asString();
                    String pass = r.get("u.password").asString();
                    String f = r.get("u.first_name").asString();
                    String l = r.get("u.last_name").asString();
                    User us = new User(uname,pass,f,l);
                    users.add(us);
                }
                return users;
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return users;
    }

    public ArrayList<Beer> getBeerByName(String pattern){
        ArrayList<Beer> beers= new ArrayList<>();

        try(Session session= driver.session()){

            session.readTransaction(tx->{
                Result result = tx.run("MATCH (b:Beer)" +
                        "where b.name CONTAINS $pattern "+
                        "RETURN b.id, b.name, b.style, b.brewery_name LIMIT 30", Values.parameters("pattern", pattern));

                while(result.hasNext()){
                    Record r= result.next();
                    int id = r.get("b.id").asInt();
                    String beerName = r.get("b.name").asString();
                    String style = r.get("b.style").asString();
                    String brewery_name = r.get("b.brewery_name").asString();
                    Beer b= new Beer(id, beerName, style, brewery_name);
                    beers.add(b);
                }
                return beers;
            });

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public ArrayList<Beer> getBeerByStyle(String pattern){
        ArrayList<Beer> beers= new ArrayList<>();

        try(Session session= driver.session()){

            session.readTransaction(tx->{
                Result result = tx.run("MATCH (b:Beer)" +
                        "where b.style CONTAINS $pattern "+
                        "RETURN b.id, b.name, b.style, b.brewery_name LIMIT 30", Values.parameters("pattern", pattern));

                while(result.hasNext()){
                    Record r= result.next();
                    int id = r.get("b.id").asInt();
                    String beerName = r.get("b.name").asString();
                    String style = r.get("b.style").asString();
                    String brewery_name = r.get("b.brewery_name").asString();
                    Beer b= new Beer(id, beerName, style, brewery_name);
                    beers.add(b);
                }
                return beers;
            });

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public ArrayList<Beer> getBeerByBrew(String pattern){
        ArrayList<Beer> beers= new ArrayList<>();

        try(Session session= driver.session()){

            session.readTransaction(tx->{
                Result result = tx.run("MATCH (b:Beer)" +
                        "where b.brewery_name CONTAINS $pattern "+
                        "RETURN b.id, b.name, b.style, b.brewery_name LIMIT 30", Values.parameters("pattern", pattern));

                while(result.hasNext()){
                    Record r= result.next();
                    int id = r.get("b.id").asInt();
                    String beerName = r.get("b.name").asString();
                    String style = r.get("b.style").asString();
                    String brewery_name = r.get("b.brewery_name").asString();
                    Beer b= new Beer(id, beerName, style, brewery_name);
                    beers.add(b);
                }
                return beers;
            });

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public ArrayList<Beer> getBeerById(int id_beer){
        ArrayList<Beer> beers= new ArrayList<>();

        try(Session session= driver.session()){

            session.readTransaction(tx->{
                Result result = tx.run("MATCH (b:Beer)" +
                        "where b.id=$pattern "+
                        "RETURN b.id, b.name, b.style, b.brewery_name LIMIT 30", Values.parameters("pattern", id_beer));

                while(result.hasNext()){
                    Record r= result.next();
                    int id = r.get("b.id").asInt();
                    String beerName = r.get("b.name").asString();
                    String style = r.get("b.style").asString();
                    String brewery_name = r.get("b.brewery_name").asString();
                    Beer b= new Beer(id, beerName, style, brewery_name);
                    beers.add(b);
                }
                return beers;
            });

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    //###########         COMPLEX QUERIES NEO4J            ##############

    public ArrayList<String> MostPurchasedBeers(){

        ArrayList<String> beers= new ArrayList<>();

        try (Session session = driver.session()) {

            session.readTransaction( tx -> {
                Result result=tx.run("MATCH path=(u:User)-[p:PURCHASED]-(b:Beer)"+
                                "RETURN b.id AS beer_id, b.name AS beer_name, COUNT(p) AS total_purchased "+
                                "ORDER BY total_purchased DESC LIMIT 10"
                );

                while(result.hasNext()){
                    Record r= result.next();
                    int beer_id = r.get("beer_id").asInt();
                    String beer_name = r.get("beer_name").asString();
                    String tot = String.valueOf(r.get("total_purchased"));
                    String row = beer_id + "  " + beer_name + " --> " + tot + " orders ";
                    beers.add(row);
                }
                return beers;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public ArrayList<User> MostActiveUsers(){

        ArrayList<User> users= new ArrayList<>();

        try (Session session = driver.session()) {

            session.readTransaction( tx -> {
                Result result=tx.run("MATCH (u:User)-[r]->()"+
                        "RETURN u.username, COUNT(r) as num_interactions "+
                        "ORDER BY num_interactions DESC LIMIT 10"
                );

                while(result.hasNext()){
                    Record r= result.next();
                    String user = r.get("u.username").asString();
                    int interactions = r.get("num_interactions").asInt();
                    users.add(new User(user, interactions));
                }
                return users;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return users;
    }

    public ArrayList<String> MostPopularBeerInWishlists(){

        ArrayList<String> beers= new ArrayList<>();

        try (Session session = driver.session()) {

            session.readTransaction(tx -> {
                Result result=tx.run("MATCH path=(u:User)-[h:HAS_IN_WISHLIST]-(b:Beer)"+
                        "RETURN b.name AS beer_name, COUNT(h) AS quantity "+
                        "ORDER BY quantity DESC LIMIT 10"
                );

                while(result.hasNext()){
                    Record r= result.next();
                    String beer_name = r.get("beer_name").asString();
                    String tot = String.valueOf(r.get("quantity"));
                    String row = beer_name + " " + tot;
                    beers.add(row);
                }
                return beers;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public boolean MostInteractedBreweries(){
        try (Session session = driver.session()) {

            session.readTransaction((TransactionWork<Void>) tx -> {
                Result result=tx.run("MATCH (u:User)-[r]->(b:Beer)<-[related:RELATED]-(review:Review)-[:POSTED]-(u:User)\n" +
                        "RETURN b.brewery_id, b.brewery_name, u.username,\n" +
                        "(COUNT(r) + COUNT(related)) AS num_interactions\n" +
                        "ORDER BY num_interactions DESC\n" +
                        "LIMIT 10"
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // da collegare al profilo
    public ArrayList<String> SuggestedUsers(String username){

        ArrayList<String> users= new ArrayList<>();

        try (Session session = driver.session()) {

            session.readTransaction( tx -> {
                Result result=tx.run("MATCH (u1:User{username: $username})-[:FOLLOWS]->(u2:User)<-[:FOLLOWS]-(u3:User)\n" +
                                "WHERE NOT EXISTS ((u1)-[:FOLLOWS]-(u3))\n" +
                                "RETURN DISTINCT u3.username as suggest_friend LIMIT 10\n" +
                                "UNION\n" +
                                "MATCH (u4:User{username: $username})-[:PURCHASED]->(b:Beer)<-[:PURCHASED]-(u5:User)\n" +
                                "WHERE NOT EXISTS ((u4)-[:FOLLOWS]-(u5))\n" +
                                "RETURN DISTINCT u5.username as suggest_friend LIMIT 10;",
                        Values.parameters(
                                "username", username
                        )
                );

                while(result.hasNext()){
                    Record r= result.next();
                    String user = r.get("suggest_friend").asString();
                    users.add(user);
                }
                return users;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return users;
    }

    public ArrayList<Beer> SuggestedBeers(String username){

        ArrayList<Beer> beers= new ArrayList<>();

        try (Session session = driver.session()) {

            session.readTransaction( tx -> {
                Result result=tx.run("MATCH (u1:User {username: $username})-[:FOLLOWS]-(u2:User)-[:PURCHASED]->(b:Beer)\n" +
                                "WHERE NOT EXISTS ((u1)-[:PURCHASED]->(b:Beer))\n" +
                                "RETURN b.id as beerId, b.name as beerName LIMIT 10\n" +
                                "UNION\n" +
                                "CALL{\n" +
                                "    MATCH (u:User {username: $username})-[:PURCHASED]->(b:Beer)\n" +
                                "    RETURN u AS u1, b.style AS style, COUNT(b.style) as total\n" +
                                "    ORDER BY total DESC\n" +
                                "    LIMIT 1}\n" +
                                "WITH style,u1\n" +
                                "MATCH (bb:Beer)\n" +
                                "WHERE NOT EXISTS ((u1)-[:PURCHASED]->(bb:Beer)) and bb.style=style\n" +
                                "RETURN bb.id as beerId, bb.name as beerName LIMIT 10;\n",
                        Values.parameters(
                                "username", username
                        )
                );

                while(result.hasNext()){
                    Record r= result.next();
                    Beer row=new Beer(r.get("beerId").asInt(),r.get("beerName").asString());
                    //String beer_id = String.valueOf(r.get("beerId"));
                    //String beer_name = r.get("beerName").asString();
                    //String row = beer_id + " " + beer_name;
                    beers.add(row);
                }
                return beers;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return beers;
    }

    public ArrayList<Double> getAVGScore(int beerID){
        ArrayList<Double> avg=new ArrayList<>();
        try (Session session = driver.session()) {

            session.readTransaction(tx -> {
                Result result=tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer) "+
                                "WHERE b.id=$bid "+
                                "RETURN  avg(r.score) AS mean",
                        Values.parameters(
                                "bid", beerID
                        )
                );
                if(result.hasNext()){
                    Record r = result.next();
                    if(r.get("mean").isNull()){
                        avg.add(null);
                    }else{
                        avg.add(r.get("mean").asDouble());
                    }
                }

                return avg;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return avg;
    }

}
