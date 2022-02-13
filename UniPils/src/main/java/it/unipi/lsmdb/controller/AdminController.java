package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    AnchorPane container;
    @FXML Label a1;
    @FXML Label a2;
    @FXML Label a3;
    @FXML Label a4;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setLayoutX(37.0);
        choiceBox.setLayoutY(149.0);
        choiceBox.setItems(FXCollections.observableArrayList(
                "Beer Name", "Username", "Brewery Name", "Beer Style", "Beer Id"));
        choiceBox.setValue("Beer_name");

        TextField textField = new TextField();
        textField.setPromptText("Search here!");
        textField.setOnKeyPressed(event->search(event, (String) choiceBox.getValue(), textField.getText()));

        HBox hBox = new HBox(choiceBox, textField);//Add choiceBox and textField to hBox
        hBox.setAlignment(Pos.CENTER);//Center HBox
        hBox.setLayoutX(400);
        hBox.setLayoutY(380);
        container.getChildren().addAll(hBox);

    }

    private void search(KeyEvent e, String value, String search) {
        if(e.getCode() == KeyCode.ENTER)
            Utils.changeScene("/it/unipi/lsmdb/search-result.fxml", e, search, value);
    }

    @FXML
    private void insertBeer(ActionEvent ae){
        Utils.changeScene("/it/unipi/lsmdb/registration-beer.fxml", ae);
    }

    @FXML
    private void showAnalyticOne(ActionEvent actionEvent){

        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        ArrayList<Document> docs = MongoDriver.getBuyers();
        Document d = docs.get(0);

        int field_one = d.getInteger("numberOfBuyers");
        double field_two = d.getDouble("AvgOrders");

        a1.setText("Total All-Time Buyers: " + field_one + "  " + "Avg num orders per user: " + field_two );
        a1.setFont(font);

        a2.setVisible(false);
        //Utils.changeScene("/it/unipi/lsmdb/admin-page.fxml", actionEvent);

    }

    @FXML
    private void showAnalyticTwo(ActionEvent actionEvent){
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        ArrayList<Document> docs = MongoDriver.getAvgOrderPrice();
        Document d = docs.get(0);

        double field_one = d.getDouble("AvgOrderPrice");

        a1.setText("Avg Price per order:  " + field_one + " USD");
        a1.setFont(font);

        a2.setVisible(false);
    }

    @FXML
    private void showAnalyticThree(ActionEvent actionEvent){
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        ArrayList<Document> docs = MongoDriver.getAvgAge();
        Document d = docs.get(0);

        double field_one = d.getDouble("AvgAge");

        a1.setText("Avg age of registered users is:  " + field_one );
        a1.setFont(font);

        a2.setVisible(false);
    }

    @FXML
    private void showAnalyticFour(ActionEvent actionEvent){
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        ArrayList<Document> docs = MongoDriver.getGenderDistribution();
        Document d1 = docs.get(0);
        Document d2 = docs.get(1);

        String field_one = d1.getString("Gender");
        int field_two = d1.getInteger("Total");

        String field_three = d2.getString("Gender");
        int field_four = d2.getInteger("Total");

        a1.setText("Gender: " + field_one + "  " + "Total users: " + field_two );
        a1.setFont(font);

        a2.setText("Gender: " + field_three + "  " + "Total users: " + field_four );
        a2.setFont(font);
        a2.setVisible(true);
    }

    @FXML
    private void goBack(ActionEvent actionEvent){
        Utils.changeScene("/it/unipi/lsmdb/homepage.fxml", actionEvent);
    }

}
