package it.unipi.lsmdb.persistence;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.InfoConfig;
import org.neo4j.driver.*;

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
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

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
            closeConnection();
            return false;
        }
        if(!found.get())
            return false;
        closeConnection();
        return true;
    }

    public boolean addBeer(Beer beer) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (b:Beer {name:$beerName, id:$beerId, style=$sty, brewery_name:$breweryName, brewery_id: $breweryId})",
                        Values.parameters(
                                "beerName", beer.getName(),
                                "beerId", beer.getId(),
                                "sty", beer.getStyle(),
                                "breweryName", beer.getBrewery_name(),
                                "breweryId", beer.getBrewery_id()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean addReview(Review review, User user, Beer beer){
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User),(b:Beer)"+
                          "WHERE u.username=$username and b.id=$bid"+
                          "MERGE (u:User)-[:POSTED]->(r:Review {comment:$text, score:$sc, timestamp:$ts})-[:RELATED]->(b:Beer)",
                        Values.parameters(
                                "username", user.getUsername(),
                                "bid", beer.getId(),
                                "comment", review.getComment(),
                                "score", review.getScore(),
                                "timestamp", Values.ofLocalDateTime()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean addFollows(User u1, User u2) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u1:User),(u2:User)"+
                          "WHERE u1.username=$un1 and u2.username=$un2"+
                          "MERGE (u1)-[:FOLLOWS]->(u2)",
                        Values.parameters(
                                "un1", u1.getUsername(),
                                "un2", u2.getUsername()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean deleteFollows(User u1, User u2) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u1:User),(u2:User)"+
                                "WHERE u1.username=$un1 and u2.username=$un2"+
                                "DELETE (u1)-[:FOLLOWS]->(u2)",
                        Values.parameters(
                                "un1", u1.getUsername(),
                                "un2", u2.getUsername()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean addPurchased(User user, Beer beer) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User),(b:Beer)"+
                          "WHERE u.username=$username and b.id=$bid"+
                          "MERGE (u)-[:PURCHASED]->(b)",

                        Values.parameters(
                                "username", user.getUsername(),
                                "bid", beer.getId()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean addHasInWishlist(User user, Beer beer) {
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User),(b:Beer)"+
                                "WHERE u.username=$username and b.id=$bid"+
                                "MERGE (u)-[:HAS_IN_WISHLIST]->(b)",

                        Values.parameters(
                                "username", user.getUsername(),
                                "bid", beer.getId()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean updateReview(Review review, User user, Beer beer){
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer)"+
                          "WHERE u.username=$username and b.id=$bid"+
                          "SET r.comment=$text, r.score=$score",
                        Values.parameters(
                                "username", user.getUsername(),
                                "bid", beer.getId(),
                                "text", review.getComment(),
                                "score", review.getScore()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean deleteUser(User user) {

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User)"+
                          "WHERE u.username=$username"+
                          "DETACH DELETE u",
                        Values.parameters(
                                "username", user.getUsername()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean deleteBeer(Beer beer) {

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (b:Beer)"+
                          "WHERE b.id=bid"+
                          "DETACH DELETE b",
                        Values.parameters(
                                "bid", beer.getId()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean deleteReview(User user, Beer beer, Review review) {

        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User)-[:POSTED]-(r:Review)-[:RELATED]->(b:Beer)"+
                                "WHERE u.username=$username and b.id=$bid"+
                                "DETACH DELETE r",
                        Values.parameters(
                                "username", user.getUsername(),
                                "bid", beer.getId()
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean findBeer(String searchInput){
        try (Session session = driver.session()) {

            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (b:Beer)"+
                          "WHERE b.name=$field or b.style=$field or b.brewery_name=$field or b.id=$field or b.brewery_id=$field"+
                          "RETURN b.id, b.name, b.style, b.brewery_name",
                        Values.parameters(
                                "field", searchInput
                        )
                );

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

}
