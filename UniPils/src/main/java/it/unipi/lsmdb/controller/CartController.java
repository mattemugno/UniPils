package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.persistence.LevelDbDriver;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    @FXML
    Label title;
    @FXML
    VBox cartInfoPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String username = DataSession.getUserLogged();
        title.setText(username + " 's Cart");
        printBeersFromLevel(username);
    }

    private void printBeersFromLevel(String username) {

        Font font = Font.font("Comic Sans", FontWeight.BOLD, 18);
        LevelDbDriver levelDbDriver = new LevelDbDriver();
        List<String> keyList = new ArrayList<>();
        ArrayList<Beer> beer_cart = new ArrayList<>();

        keyList = levelDbDriver.findKeysByPrefix(username);

        for (String key: keyList){
            int beer_id = levelDbDriver.splitKeys(key);
            Beer beer = MongoDriver.getBeerById(beer_id);
            beer_cart.add(beer);
        }

        for (Beer item : beer_cart) {
            double space = 5;
            VBox beer = new VBox(space);
            beer.setMaxWidth(491);
            beer.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText("Beer ID:  " + item.get_id() + "     " + "Beer Name:  " + item.getName());
            title.setFont(font);

            Label details = new Label();
            details.setText("Brewery Name:  " + item.getBrewery_name() + "     " + "Beer Style:  " + item.getStyle());
            details.setFont(font);

            beer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(item.get_id());
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });

            Button button = new Button();
            button.setText("REMOVE BEER");
            button.setOnAction(e -> {
                if(deleteFromLevel(username, item.get_id()))
                    Utils.showInfoAlert("Beer removed successfully");
                else Utils.showErrorAlert("Beer not removed");
            }); //remove from wishlist

            beer.getChildren().addAll(button, title, details);
            cartInfoPane.getChildren().add(beer);
        }
    }

    private boolean deleteFromLevel(String username, int beer_id){

        try {
            LevelDbDriver levelDbDriver = new LevelDbDriver();
            String keyPrefix = username + ":" + beer_id;
            String key = levelDbDriver.findKeysByPrefix(keyPrefix).get(0);
            levelDbDriver.deleteValue(key);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
