package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class SearchResultController  {

    @FXML
    Label results;
    @FXML
    ScrollPane container;
    @FXML VBox vbox;

    private String search, value;

    @FXML
    public void initData (String s, String v){
        search=s;
        value=v;
        results.setText("Search results for: " + "'" + search + "'");
        show(s,v);
    }

    @FXML
    private void show(String search, String value) {

        NeoDriver neo4j = NeoDriver.getInstance();

        if(Objects.equals(value, "Username")){
            ArrayList<User> users = neo4j.getUsername(search);
            printUsers(users);
        }
        else if(Objects.equals(value, "Beer_name")){
            ArrayList<Beer> beer = neo4j.getBeerByName(search);
            printBeers(beer);
        }
        else if(Objects.equals(value, "Brewery Name")){
            ArrayList<Beer> beer = neo4j.getBeerByBrew(search);
            printBeers(beer);
        }
        else if(Objects.equals(value, "Beer Style")){
            ArrayList<Beer> beer = neo4j.getBeerByStyle(search);
            printBeers(beer);
        }
        else if(Objects.equals(value, "Beer Id")) {
            ArrayList<Beer> beer = neo4j.getBeerById(Integer.parseInt(search));
            printBeers(beer);
        }

    }

    @FXML
    private void printBeers(ArrayList<Beer> beers) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD, 18);

        for (Beer i : beers) {
            double space = 5;
            VBox beer = new VBox(space);
            beer.setMaxWidth(491);
            beer.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText("Beer ID:  " + i.get_id() + "     " + "Beer Name:  " + i.getName());
            title.setFont(font);

            Label details = new Label();
            details.setText("Brewery Name:  " + i.getBrewery_name() + "     " + "Beer Style:  " + i.getStyle());
            details.setFont(font);

            beer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(i.get_id());
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });

            beer.getChildren().addAll(title, details);
            vbox.getChildren().add(beer);
        }
    }

    @FXML
    private void printUsers(ArrayList<User> users) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD, 18);

        for (User i : users) {
            double space = 5;
            VBox user = new VBox(space);
            user.setMaxWidth(491);
            user.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            if(DataSession.getUserLogged()== "admin") {
                Button cancel = new Button();
                cancel.setText("DELETE USER");
                cancel.setOnAction(e -> deleteUser(e, i.getUsername()));
            }

            Label title = new Label();
            title.setText("Username:  " + i.getUsername() );
            title.setFont(font);

            Label details = new Label();
            details.setText("First Name:  " + i.getFirst() + "   " + "Last Name:  " + i.getLast());
            details.setFont(font);

            user.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setUserLogged(i.getUsername());
                Utils.changeScene("/it/unipi/lsmdb/profile-user.fxml", event);
                event.consume();
            });

            user.getChildren().addAll(title, details);
            vbox.getChildren().add(user);
        }
    }

    private void deleteUser(ActionEvent e, String username) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.deleteUser(username);
        MongoDriver.deleteUser(username);
        Utils.showInfoAlert("User " + username + " deleted from both DB");
        Utils.changeScene("/it/unipi/lsmdb/search-result.fxml", e);
        //show();
    }

    @FXML
    private void goHome(ActionEvent ae){
        Utils.changeScene("/it/unipi/lsmdb/homepage.fxml", ae);
    }

}
