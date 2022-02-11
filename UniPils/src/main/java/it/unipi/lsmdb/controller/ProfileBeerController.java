package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProfileBeerController implements Initializable {

    @FXML private TextField searchBar;
    @FXML private Button wishButton;
    @FXML private Button revButton;
    @FXML private Button cartButton;
    @FXML private VBox revSection;
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
    String lastWishState="ADD TO WISHLIST";

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //manca da fare la barra di ricerca tramite query su neo4j che setta l'id della birra desiderata
        //carico info birra in tutte le label definite
        //int beer_id = DataSession.getIdBeerToShow();
        //Beer beer = MongoDriver.getBeersFromId(beer_id);
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  25);
        int beer_id=288497;
        beerName.setText("This Land Lager");
        beerName.setFont(font);
        brewName.setText("Brewery:  " + "Marshall Brewing Company");
        style.setText("Style: " + "German Helles");
        abv.setText("ABV: " + 4.8 + " %");
        price.setText("Price: " + 10 + " USD");
        vol.setText("Vol. " + 66 + " cl");
        country.setText("Country: " + "US");
        state.setText("State: " + "OK");
        /*beerName.setText(beer.getName());
        brewName.setText(beer.getBrewery_name());
        style.setText(beer.getStyle());
        abv.setText(beer.getAbv() + " %");
        price.setText(beer.getPrice() + " USD");
        vol.setText(beer.getVolume() + " cl");
        country.setText(beer.getCountry());
        state.setText(beer.getState());*/

        showBeerReviews(beer_id);

        if (DataSession.getUserLogged() != null){
            String usernameLogged = DataSession.getUserLogged();
            wishButton.setText(lastWishState);
            if(Objects.equals(wishButton.getText(), "ADD TO WISHLIST"))
                wishButton.setOnAction(e->addWishlist(e, usernameLogged, beer_id));
            else
                wishButton.setOnAction(e->deleteWishlist(e, usernameLogged, beer_id));
            //cartButton.setOnAction();
            if(Objects.equals(revButton.getText(), "POST REVIEW"))
                revButton.setOnAction(e->writeReview(e, usernameLogged, beer_id));
            else
                revButton.setOnAction(e->modifyReview(e,usernameLogged,beer_id));
        }
        else
        { //rendi i bottoni invisibili
            wishButton.setVisible(false);
            revButton.setVisible(false);
            cartButton.setVisible(false);
        }
    }

    private void showBeerReviews(int beer) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        NeoDriver neo4j = NeoDriver.getInstance();
        ArrayList<String> authors = neo4j.getAuthorReview(beer);
        ArrayList<Review> reviews= neo4j.getBeerReviews(beer);
        System.out.println(reviews);
        for(int i=0,j=0;i< reviews.size() && j< authors.size(); i++, j++){
                double space = 5;
                VBox rev = new VBox(space);
                rev.setMaxWidth(491);
                rev.setStyle("-fx-border-style: solid inside;"
                        + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                        + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");
                TextArea author = new TextArea();
                author.setText("Publisher:  " + authors.get(j));
                author.setFont(font);
                //author.setStyle("-fx-text-inner-color: black;");
                author.setDisable(true);
                TextArea comment = new TextArea();
                comment.setText("Text:  " + reviews.get(i).getComment());
                comment.setFont(font);
                comment.setDisable(true);
                TextField score = new TextField();
                score.setText("Score:  " + reviews.get(i).getSc());
                score.setFont(font);
                score.setDisable(true);
                TextField ts = new TextField();
                ts.setText("Publication date:  " + reviews.get(i).getTs());
                ts.setFont(font);
                ts.setDisable(true);
                rev.getChildren().addAll(author,comment, score, ts); //attacco le label alla sezione della singola review
                revSection.getChildren().add(rev); //attacco la singola review al vbox globale
                //VBox.setVgrow(child, Priority.ALWAYS);
            }

    }

    private void writeReview(ActionEvent actionEvent, String usernameLogged, int beer) {
        if(comment.getText() == null || score.getText() == null) {
            Utils.showErrorAlert("You need to compile both fields");
            return;
        }
        Review review = new Review(comment.getText(), Integer.parseInt(score.getText()));
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.addReview(review, usernameLogged, beer);
        //comment.setText(writtenReview.getComment());
        //score.setText(String.valueOf(writtenReview.getScore()));
        revButton.setText("MODIFY REVIEW");
        Utils.changeScene("profile-beer.fxml", actionEvent);
    }

    private void modifyReview(ActionEvent actionEvent, String usernameLogged, int beer) {
        if(comment.getText() == null || score.getText() == null) {
            Utils.showErrorAlert("You need to compile both fields");
            return;
        }
        Review review = new Review(comment.getText(), Integer.parseInt(score.getText()));
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.updateReview(review, usernameLogged, beer);
        revButton.setText("POST REVIEW");
        Utils.changeScene("profile-beer.fxml", actionEvent);
    }

    @FXML
    private void addWishlist(ActionEvent actionEvent, String user, int beer) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.addHasInWishlist(user, beer);
        Utils.showInfoAlert("Added to wishlist");
        lastWishState="REMOVE FROM WISHLIST";
        Utils.changeScene("profile-beer.fxml", actionEvent);
    }

    @FXML
    private void deleteWishlist(ActionEvent actionEvent, String user, int beer) {
        NeoDriver neo4j = NeoDriver.getInstance();
        neo4j.deleteHasInWishlist(user, beer);
        Utils.showInfoAlert("Deleted from wishlist");
        lastWishState="ADD TO WISHLIST";
        Utils.changeScene("profile-beer.fxml", actionEvent);
    }


}
