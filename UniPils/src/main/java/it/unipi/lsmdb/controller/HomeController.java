package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HomeController implements Initializable {

    @FXML private Parent embeddedMenu;
    @FXML private MenuController menuController;
    @FXML private TextField searchBar;
    @FXML private AnchorPane searchBarContainer;
    @FXML private AnchorPane rightPane;
    @FXML private VBox beersMonth;
    @FXML private VBox mostPurchased;
    @FXML private VBox activeUsers;

    @FXML @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setItems(FXCollections.observableArrayList(
                "Beer Name", "Username", "Brewery Name", "Beer Style", "Beer Id"));
        choiceBox.setValue("Beer Name");


        ChoiceBox filter = new ChoiceBox();
        filter.setItems((FXCollections.observableArrayList(
                "Price", "ABV", "Country", "State")));
        filter.setValue("Price");

        choiceBox.setOnAction(e->disable(e,choiceBox.getValue().toString(), filter));


        TextField textField = new TextField();
        textField.setPromptText("Search here!");
        textField.setOnKeyPressed(event->search(event, (String) choiceBox.getValue(), textField.getText()));

        HBox hBox = new HBox(choiceBox, filter, textField);//Add choiceBox and textField to hBox
        hBox.setAlignment(Pos.BOTTOM_LEFT);//Center HBox
        hBox.setLayoutX(300);
        hBox.setLayoutY(20);

        rightPane.getChildren().addAll(hBox);

        NeoDriver neo4j = NeoDriver.getInstance();
        printBeersMonth();
        printActiveUsers(neo4j);
        printMostPurchased(neo4j);

    }

    @FXML
    private void disable(Event e, String value, ChoiceBox box) {
        if (!Objects.equals(value, "Beer Name"))
            box.setDisable(true);
        else
            box.setDisable(false);
    }

    private void printMostPurchased(NeoDriver neo4j) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        ArrayList<Beer> beers = neo4j.MostPurchasedBeers();

        for(Beer beer : beers){
            double space = 5;
            VBox b = new VBox(space);
            b.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText(beer.get_id() + " " + beer.getName() + " has been purchased " + beer.getTot_purchased() + " times");
            title.setFont(font);

            b.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(beer.get_id());
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });

            b.getChildren().add(title);
            mostPurchased.getChildren().add(b);

        }
    }

    private void printActiveUsers(NeoDriver neo4j) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);

        ArrayList<User> users = neo4j.MostActiveUsers();

        for(User user : users){
            double space = 5;
            VBox u = new VBox(space);
            u.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText(user.getUsername() + ": " + user.getInteractions() + " interactions with UniPils");
            title.setFont(font);

            u.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setUserView(user.getUsername());
                Utils.changeScene("/it/unipi/lsmdb/profile-user.fxml", event);
                event.consume();
            });

            u.getChildren().add(title);
            activeUsers.getChildren().add(u);

        }
    }

    private void printBeersMonth() {
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

            b.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(beer.getInteger("_id"));
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });

            b.getChildren().add(title);
            beersMonth.getChildren().add(b);

        }
    }

    private void search(KeyEvent e, String value, String search) {
        if(e.getCode() == KeyCode.ENTER)
            Utils.changeScene("/it/unipi/lsmdb/search-result.fxml", e, search, value);
    }


}
