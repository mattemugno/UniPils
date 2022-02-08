package it.unipi.lsmdb.persistence;


import it.unipi.lsmdb.config.InfoConfig;

public class MongoDriver {

    private static MongoClient myClient;
    private static MongoDatabase db;
    private static MongoCollection<org.bson.Document> collection;

    private static final String connectionString = "mongodb://" + InfoConfig.getMongoFirstIp() + ":" + InfoConfig.getMongoFirstPort() +
            "," + InfoConfig.getMongoSecondIp() + ":" + InfoConfig.getMongoSecondPort() +
            "," + InfoConfig.getMongoThirdIp() + ":" + InfoConfig.getMongoThirdPort() +
            "/?retryWrites=true&w=3&wtimeoutMS=5000&readPreference=nearest";

    private static final ConnectionString uri= new ConnectionString(connectionString);



}
