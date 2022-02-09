package it.unipi.lsmdb.persistence;

import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.InfoConfig;
import org.neo4j.driver.*;

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

    public boolean getUsersFromUnique(String uName) {
        return true;
    }
}
