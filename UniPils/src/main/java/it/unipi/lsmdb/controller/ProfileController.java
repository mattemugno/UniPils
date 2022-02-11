package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    NeoDriver neo4j = NeoDriver.getInstance();

    @FXML private VBox profilePage;
    @FXML private AnchorPane userDataPane;
    @FXML private VBox beersInfoPane;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String usernameLogged = DataSession.getUserLogged();
        ArrayList<Beer> beers = neo4j.getBeersUser(usernameLogged);

        System.out.println(beers);
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  18);
        for (Beer b : beers) {
            AnchorPane oneBeer = new AnchorPane();
            oneBeer.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 1;" +"-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;");
            VBox boxBeer = new VBox(5);
            boxBeer.setPadding(new Insets(5,5,5,5));

            HBox hb=new HBox();

            Label nameBeer = new Label();
            nameBeer.setText("Beer:  " + b.getName());
            nameBeer.setFont(font);

            Button btn=new Button();
            btn.setText("See more details");
            btn.setFont(font);
            btn.setOnAction(actionEvent -> {
                DataSession.setIdBeerToShow(b.getId());
                Utils.changeScene("profile-beer.fxml", actionEvent);});

            hb.getChildren().addAll(nameBeer,btn);


            Label style = new Label();
            style.setText("Style:  " + b.getStyle());
            style.setFont(font);

            Label brewName = new Label();
            brewName.setText("Brewery:  " + b.getBrewery_name());
            brewName.setFont(font);

            ArrayList<Review> reviews=neo4j.getReviewsUser(usernameLogged,b.getId());
            TextArea comment = new TextArea();
            if(!reviews.isEmpty()){
                comment.setText("Review:  " + reviews.get(0).getComment());
                comment.setFont(font);
                comment.setDisable(true);
            }else{
                comment.setText("No comment");
                comment.setFont(font);
                comment.setDisable(true);
            }

            Separator sep=new Separator();

            boxBeer.getChildren().addAll(hb, style,brewName,comment);
            oneBeer.getChildren().addAll(boxBeer,sep);
            beersInfoPane.getChildren().add(oneBeer);
        }
    }



}
