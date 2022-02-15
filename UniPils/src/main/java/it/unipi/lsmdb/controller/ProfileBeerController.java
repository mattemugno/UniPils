package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.LevelDbDriver;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProfileBeerController implements Initializable {

    Stage stage;
    Scene scene;
    @FXML private TextField searchBar;
    @FXML private Button wishButton;
    @FXML private Button revButton;
    @FXML private Button cartButton;
    @FXML VBox revSection;
    @FXML private AnchorPane beerInfoPane;
    @FXML TextField score;
    @FXML TextArea comment;
    @FXML Label beerName;
    @FXML Label brewName;
    @FXML Label abv;
    @FXML Label vol;
    @FXML Label style;
    @FXML Label price;
    @FXML Label country;
    @FXML Label state;
    @FXML ScrollPane scroll;
    @FXML SplitPane all;
    @FXML VBox vbox;
    @FXML Label AVGscore;

    @FXML
    public static void deleteBeer(ActionEvent ae, int beerId) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.deleteBeer(beerId);
        MongoDriver.deleteBeer(beerId);
        Utils.showInfoAlert("Beer " + beerId + " deleted from both DB");
        Utils.changeScene("/it/unipi/lsmdb/admin-page.fxml", ae);
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        NeoDriver neo4j = NeoDriver.getInstance();
        int beer_id = DataSession.getIdBeerToShow();
        Beer beer = MongoDriver.getBeerById(beer_id);
        MongoDriver.updateBeerViewCount(beer);
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  25);

        beerName.setText(beer.getName());
        beerName.setFont(font);
        brewName.setText("Brewery:  " + beer.getBrewery_name());
        style.setText("Style: " + beer.getStyle());
        abv.setText("ABV: " + beer.getAbv() + " %");
        price.setText("Price: " + beer.getPrice() + " USD");
        vol.setText("Volume: " + beer.getVolume() + " cl");
        country.setText("Country: " + beer.getCountry());
        state.setText("State: " + beer.getState());

        ArrayList<Double> avg=neo4j.getAVGScore(beer.get_id());

        if(!(avg.get(0)==null)){
            AVGscore.setText("AVG score: "+avg.get(0).toString());
        }else{
            AVGscore.setText("No score yet");
        }

        if (Objects.equals(DataSession.getUserLogged(), "admin")) {

            wishButton.setVisible(false);
            revButton.setVisible(false);
            cartButton.setVisible(false);
            score.setDisable(true);
            comment.setDisable(true);
            Button cancel = new Button();
            cancel.setText("DELETE BEER");
            cancel.setOnAction(e -> deleteBeer(e, beer_id));
            cancel.setLayoutX(499.0);
            cancel.setLayoutY(100.0);
            beerInfoPane.getChildren().add(cancel);
        }

        showBeerReviews(beer_id, DataSession.getUserLogged());

        if (DataSession.getUserLogged() != null) {
            String usernameLogged = DataSession.getUserLogged();

            cartButton.setOnAction(e -> addToCart( usernameLogged, beer_id, beer.getName(), beer.getPrice()));

            wishButton.setOnAction(e -> addWishlist( usernameLogged, beer_id));

            revButton.setOnAction(e -> writeReview(e, usernameLogged, beer_id));

        } else {
            Utils.showInfoAlert("Log in/Sign in to have all interactions with beer");
            wishButton.setDisable(true);
            revButton.setDisable(true);
            cartButton.setDisable(true);
            comment.setDisable(true);
            score.setDisable(true);
        }
    }

    @FXML
    private void showBeerReviews(int beer, String user) {

        Font font = Font.font("Comic Sans", FontWeight.BOLD, 18);
        NeoDriver neo4j = NeoDriver.getInstance();
        ArrayList<String> authors = neo4j.getAuthorReview(beer);
        ArrayList<Review> reviews = neo4j.getBeerReviews(beer);

        for (int i = 0, j = 0; i < reviews.size() && j < authors.size(); i++, j++) {
            double space = 5;
            VBox rev = new VBox(space);
            rev.setMaxWidth(491);
            rev.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            if (Objects.equals(user, "admin")) {
                Button del = new Button();
                del.setText("DELETE REVIEW");
                int finalJ = j;
                del.setOnAction(e -> delReview(e, beer, authors.get(finalJ)));
                rev.getChildren().add(del);
            }
            Label author = new Label();
            author.setText("Publisher:  " + authors.get(j));
            author.setFont(font);

            Label comment = new Label();
            comment.setText("Text:  " + reviews.get(i).getComment());
            comment.setFont(font);

            Label score = new Label();
            score.setText("Score:  " + reviews.get(i).getSc());
            score.setFont(font);

            Label ts = new Label();
            ts.setText("Publication date:  " + reviews.get(i).getTs());
            ts.setFont(font);

            rev.getChildren().addAll(author, comment, score, ts); //attacco le label alla sezione della singola review
            vbox.getChildren().add(rev); //attacco la singola review al vbox globale
        }

    }

    @FXML
    private void delReview(ActionEvent e, int beer, String user) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.deleteReview(user, beer);
        Utils.changeScene("profile-beer.fxml", e);
        showBeerReviews(beer, user);
    }

    private void writeReview(ActionEvent actionEvent, String usernameLogged, int beer) {
        if (Objects.equals(comment.getText(), "") || Objects.equals(score.getText(), "")) {
            Utils.showErrorAlert("You need to compile both fields");
            return;
        }
        int num = Integer.parseInt(score.getText());
        if (num > 10 || num < 1) {
            Utils.showErrorAlert("You need to insert a valid score (0 < sc < 11)");
            return;
        }
        Review review = new Review(comment.getText(), Integer.parseInt(score.getText()));
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.addReview(review, usernameLogged, beer);
        Utils.changeScene("profile-beer.fxml", actionEvent);
    }


    @FXML
    private void addWishlist(String user, int beer) {

        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.addHasInWishlist(user, beer);
        Utils.showInfoAlert("Added to wishlist");
        wishButton.setDisable(true);
    }

    @FXML
    public void addToCart(String username, int beer_id, String beer_name, int price) {

        LevelDbDriver levelDbDriver = new LevelDbDriver();
        String keyName = username + ":" + beer_id + ":" + "name";
        String keyPrice = username + ":" + beer_id + ":" + "price";
        String keyQuantity = username + ":" + beer_id + ":" + "quantity";

        try {
            levelDbDriver.put(keyName, beer_name);
            levelDbDriver.put(keyPrice, String.valueOf(price));
            levelDbDriver.put(keyQuantity, String.valueOf(1));

            Utils.showInfoAlert("Added to cart");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorAlert("Unable to add item to cart");
        }
        cartButton.setDisable(true);
    }

}
