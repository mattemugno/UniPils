package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HomeController implements Initializable {

    @FXML private Parent embeddedMenu;
    @FXML private MenuController menuController;
    @FXML private TextField searchBar;
    @FXML private AnchorPane searchBarContainer;
    @FXML private VBox beersMonth;
    @FXML private VBox mostPurchased;
    @FXML private VBox activeUsers;

    @FXML @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setItems(FXCollections.observableArrayList(
                "Beer Name", "Username", "Brewery Name", "Beer Style", "Beer Id"));
        choiceBox.setValue("Beer Name");

        TextField textField = new TextField();
        textField.setPromptText("Search here!");
        textField.setOnKeyPressed(event->search(event, (String) choiceBox.getValue(), textField.getText()));

        HBox hBox = new HBox(choiceBox, textField);//Add choiceBox and textField to hBox
        hBox.setAlignment(Pos.CENTER);//Center HBox
        hBox.setLayoutX(37.0);
        hBox.setLayoutY(149.0);

        searchBarContainer.getChildren().addAll(hBox);

        NeoDriver neo4j = NeoDriver.getInstance();
        printBeersMonth(neo4j);
        printActiveUsers(neo4j);
        printMostPurchased(neo4j);

    }

    private void printMostPurchased(NeoDriver neo4j) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        ArrayList<String> beers = neo4j.MostPurchasedBeers();

        for(String beer : beers){
            double space = 5;
            VBox b = new VBox(space);
            b.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText(beer);
            title.setFont(font);

            /*b.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(Integer.parseInt(beer));
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });*/

            b.getChildren().add(title);
            mostPurchased.getChildren().add(b);

        }
    }

    private void printActiveUsers(NeoDriver neo4j) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);

        ArrayList<String> users = neo4j.MostActiveUsers();

        for(String user : users){
            double space = 5;
            VBox u = new VBox(space);
            u.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText(user);
            title.setFont(font);

            /*b.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(Integer.parseInt(beer));
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });*/

            u.getChildren().add(title);
            activeUsers.getChildren().add(u);

        }
    }

    private void printBeersMonth(NeoDriver neo4j) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);

        ArrayList<Document> beers = MongoDriver.getBeerOfTheMonth();

        for(Document beer : beers){
            double space = 5;
            VBox b = new VBox(space);
            b.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            String field_one = beer.getString("beer_name");
            int field_two = beer.getInteger("TotalPurchased");

            Label title = new Label();
            title.setText(field_one + " purchased " + field_two + " times in the last month");
            title.setFont(font);

            b.getChildren().add(title);
            beersMonth.getChildren().add(b);

        }
    }

    private void search(KeyEvent e, String value, String search) {
        if(e.getCode() == KeyCode.ENTER)
            Utils.changeScene("/it/unipi/lsmdb/search-result.fxml", e, search, value);
    }


}
