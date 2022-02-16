package it.unipi.lsmdb.utils;

import it.unipi.lsmdb.HelloApplication;
import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Order;
import it.unipi.lsmdb.bean.OrderList;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.controller.SearchResultController;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

    static public void changeScene(String fxmlFile, MouseEvent event) {

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

    static public void changeScene(String fxmlFile, KeyEvent event, String search, String value) {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
        Scene scene = null;
        try {

            scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            SearchResultController controller = fxmlLoader.getController();
            controller.initData(search, value);

            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //*******************************************************************************************
    //                          CONSISTENCY MANAGEMENT
    //*******************************************************************************************

    public static boolean addUser(User u){
        NeoDriver neo4j = NeoDriver.getInstance();
        if(MongoDriver.addUser(u))
        {
            //If neo is ok, perform mongo
            if(!neo4j.addUser(u))
            {
                // if mongo is not ok, remove the previously added recipe
                MongoDriver.deleteUser(u.getUsername());
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
                MongoDriver.deleteBeer(b.get_id());
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

    public static boolean addOrder(String username, Order order){

        NeoDriver neo4j = NeoDriver.getInstance();

        if(MongoDriver.addOrder(username, order)){

            for (OrderList ol : order.getOrderList()) {
                neo4j.addPurchased(username, ol.getBeerId());
            }

            Utils.showInfoAlert("Order successfully added");

            return true;
        }
        showErrorAlert("Unable to confirm order");
        return false;
    }

    public static boolean updateUser(String username, String field, String value){

        NeoDriver neo4j = NeoDriver.getInstance();
        String oldValue = MongoDriver.updateUser(username, field, value);
        if(oldValue != null)
        {
            if(!neo4j.updateUser(username, field, value))
            {
                MongoDriver.updateUser(username, field, oldValue);
                showErrorAlert("Error in updating user");
                return false;
            }
            else
            {
                Utils.showInfoAlert("Field succesfully updated");
                return true;
            }
        } else
            return false;
    }

    public static boolean deleteUser(String username) {
        NeoDriver neo4j = NeoDriver.getInstance();

        if (MongoDriver.deleteUser(username)) {
            if(neo4j.deleteUser(username)) {
                Utils.showInfoAlert("User succesfully deleted");
                return true;
            }else{
                showErrorAlert("Error in deleting user");
                return false;
            }

        }
        showErrorAlert("Error in deleting user");
        return false;
    }

    public static boolean deleteBeer(int beer_id) {
        NeoDriver neo4j = NeoDriver.getInstance();

        if (MongoDriver.deleteBeer(beer_id)) {
            if(neo4j.deleteBeer(beer_id)) {
                Utils.showInfoAlert("Beer succesfully deleted");
                return true;
            }else{
                showErrorAlert("Error in deleting beer");
                return false;
            }

        }
        showErrorAlert("Error in deleting beer");
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

