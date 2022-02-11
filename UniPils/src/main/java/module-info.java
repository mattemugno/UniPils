module it.unipi.lsmdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.neo4j.driver;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens it.unipi.lsmdb.bean to com.fasterxml.jackson.databind;
    exports it.unipi.lsmdb;
    exports it.unipi.lsmdb.controller;
    opens it.unipi.lsmdb.controller to javafx.fxml;

}