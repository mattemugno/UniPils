package it.unipi.lsmdb.persistence;

import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.InfoConfig;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;

public class NeoDriver{

    private Driver driver;
    private final String uri;
    private final String user;
    private final String password;

    private static NeoDriver instance = null;

    private NeoDriver()
    {
        uri = "neo4j://" + InfoConfig.getNeo4jIp() + ":" + InfoConfig.getNeo4jPort();
        this.user = InfoConfig.getNeo4jUsername();
        this.password = InfoConfig.getNeo4jPassword();
        openConnection();
    }

    public static NeoDriver getInstance()
    {
        if (instance == null)
        {
            instance = new NeoDriver();
        }
        return instance;
    }


    private void openConnection() {
        try {
            driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
        }catch (Exception ex){
            System.out.println("Impossible open connection with Neo4j");
        }
    }

    public void closeConnection() {
        try{
            driver.close();
        }catch (Exception ex){
            System.out.println("Impossible close connection with Neo4j");
        }
    }

    public boolean addUser(User user){

        try(Session session= driver.session()){

            session.writeTransaction((TransactionWork<Void>) tx ->{
                tx.run("MERGE (u:User {username:$uname, password:$pwd})",
                Values.parameters(
                        "uname", user.getUsername(),
                        "pwd", user.getPassword()));

                return null;
            });
        }catch(Exception ex){
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
