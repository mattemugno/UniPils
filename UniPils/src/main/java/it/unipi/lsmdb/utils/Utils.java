package it.unipi.lsmdb.utils;

import it.unipi.lsmdb.HelloApplication;
import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class Utils {

    static public void changeScene(String fxmlFile, ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean addUser(User u){
        NeoDriver neo4j = NeoDriver.getInstance();
        if(MongoDriver.addUser(u))
        {
            //If neo is ok, perform mongo
            if(!neo4j.addUser(u))
            {
                // if mongo is not ok, remove the previously added recipe
                MongoDriver.deleteUser(u);
                showErrorAlert("Error in adding user");
                return false;
            }
            else
            {
                Utils.showInfoAlert("User succesfully added");
                return true;
            }
        } else
            return false;
    }

    public static boolean addBeer(Beer b){
        NeoDriver neo4j = NeoDriver.getInstance();
        if(MongoDriver.addBeer(b))
        {
            //If neo is ok, perform mongo
            if(!neo4j.addBeer(b))
            {
                // if mongo is not ok, remove the previously added recipe
                MongoDriver.deleteBeer(b);
                showErrorAlert("Error in adding beer");
                return false;
            }
            else
            {
                Utils.showInfoAlert("Beer succesfully added");
                return true;
            }
        } else
            return false;
    }

    public static void showErrorAlert(String s){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error message");
        alert.setContentText(s);
        alert.showAndWait();
    }

    public static void showInfoAlert(String s){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Info message");
        alert.setContentText(s);
        alert.showAndWait();
    }

    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
    /*public static void closeApp() {
        Neo4jDriver.getInstance().closeConnection();
    }*/
}

