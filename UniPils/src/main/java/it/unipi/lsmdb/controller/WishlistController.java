package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WishlistController implements Initializable {

    @FXML
    VBox container;
    @FXML
    Label title;
    @FXML
    Button cart;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            String usernameLogged = DataSession.getUserLogged();
            title.setText(usernameLogged + " 's Wishlist");
            printBeers(usernameLogged);
    }

    private void printBeers(String username) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        NeoDriver neo4j = NeoDriver.getInstance();
        ArrayList<Beer> beers = neo4j.getUserWishlist(username);
        for (Beer i : beers) {
            double space = 5;
            VBox beer = new VBox(space);
            beer.setMaxWidth(491);
            beer.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            TextArea title = new TextArea();
            title.setText("Beer ID:  " + i.getId() + "     " + "Beer Name:  " + i.getName());
            title.setFont(font);
            title.setDisable(true);

            TextArea details = new TextArea();
            details.setText("Brewery Name:  " + i.getBrewery_name() + "     " + "Beer Style:  " + i.getStyle());
            details.setFont(font);
            details.setDisable(true);
            System.out.println(i.getId());

            /*beer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(i.getId());
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });*/

            Button button= new Button();
            button.setText("REMOVE BEER");
            button.setOnAction(e->removeBeer(e, username, i.getId())); //remove from wishlist

            beer.getChildren().addAll(button, title, details);
            container.getChildren().add(beer);
        }
    }

    private void removeBeer(ActionEvent e, String user, int beer) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.deleteHasInWishlist(user, beer);
        Utils.showInfoAlert("Beer deleted");
        printBeers(user);
    }

    @FXML
    private void addToCart(ActionEvent actionEvent){
        Utils.showInfoAlert("All beers added to cart");
    }


}
