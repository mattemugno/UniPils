package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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

public class TrendController implements Initializable {

    @FXML VBox vboxOne;
    @FXML VBox vboxTwo;
    @FXML VBox vboxThree;
    @FXML VBox vboxFour;
    @FXML AnchorPane bar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TextField textField = new TextField();
        textField.setPromptText("Insert style!");
        textField.setOnKeyPressed(event->printCheapestBeerByStyle(event, textField.getText())); //query in alto a dx di mongo
        textField.prefWidth(30);

        HBox hBox = new HBox(textField);
        hBox.setAlignment(Pos.BOTTOM_LEFT);
        hBox.setLayoutX(275);
        hBox.setLayoutY(4);

        bar.getChildren().add(hBox);

        //Mongo in alto
        printMostPopularEachState();

        NeoDriver neo4j = NeoDriver.getInstance();
        //Neo4j in basso
        printMostPopularInWishlists(neo4j);
        printMostInteractedBreweries(neo4j);
    }

    @FXML
    private void printMostPopularEachState() {
        Font font = Font.font("Comic Sans", FontWeight.BOLD, 14);

        ArrayList<Document> beers = MongoDriver.getMostPopularEachState();

        System.out.println(beers);

        for (Document beer : beers) {
            double space = 5;
            VBox b = new VBox(space);
            b.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            String state = beer.getString("state");
            String style = beer.getString("style");
            int tot = beer.getInteger("MostPopularStyleCount");

            Label title = new Label();
            title.setText(state + ": " + style + " with " + tot + " beers crafted");
            title.setFont(font);

            b.getChildren().add(title);
            vboxOne.getChildren().add(b);
        }
    }


    @FXML
    private void printCheapestBeerByStyle(KeyEvent e, String style) {
        if(e.getCode() == KeyCode.ENTER) {
            Font font = Font.font("Comic Sans", FontWeight.BOLD, 14);

            ArrayList<Document> beers = MongoDriver.getCheapestBeersByStyle(style);
            System.out.println(beers);

            for (Document beer : beers) {
                double space = 5;
                VBox b = new VBox(space);
                b.setStyle("-fx-border-style: solid inside;"
                        + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                        + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

                //int price = beer.getInteger("_id");
                String name = beer.getString("Beer Name");
                int tot = beer.getInteger("View Count");
                int price = beer.getInteger("_id.price");

                Label title = new Label();
                title.setText(name  + " with " + tot + " views " + ", price: " + price);
                title.setFont(font);

                b.getChildren().add(title);
                vboxTwo.getChildren().add(b);
            }
        }
    }


    @FXML
    private void printMostPopularInWishlists(NeoDriver neo4j) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  14);
        ArrayList<Beer> beers = neo4j.MostPopularBeerInWishlists();

        for(Beer beer : beers) {
            double space = 5;
            VBox b = new VBox(space);
            b.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText(beer.get_id() + " " + beer.getName() + " is in " + beer.getTot_purchased() + " wishlists");
            title.setFont(font);

            b.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                DataSession.setIdBeerToShow(beer.get_id());
                Utils.changeScene("/it/unipi/lsmdb/profile-beer.fxml", event);
                event.consume();
            });

            b.getChildren().add(title);
            vboxThree.getChildren().add(b);
        }
    }

    @FXML
    private void printMostInteractedBreweries(NeoDriver neo4j) {
        Font font = Font.font("Comic Sans", FontWeight.BOLD,  14);
        ArrayList<String> breweries = neo4j.MostInteractedBreweries();

        for(String brew : breweries) {
            double space = 5;
            VBox b = new VBox(space);
            b.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");

            Label title = new Label();
            title.setText(brew);
            title.setFont(font);

            b.getChildren().add(title);
            vboxFour.getChildren().add(b);
        }
    }
}
