package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.NeoDriver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    NeoDriver neo4j = NeoDriver.getInstance();

    @FXML private VBox profilePage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String usernameLogged = DataSession.getUserLogged();
        ArrayList<Beer> beers = neo4j.getBeersUser(usernameLogged);
        System.out.println(beers);

        for (Beer b : beers) {
            VBox oneBeer = new VBox();
            oneBeer.setMaxWidth(1000);
            oneBeer.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 10;" +"-fx-border-insets: 10;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #596cc2;");
            HBox box = new HBox();
            //oneBeer.setPrefWidth(50);
            Label nameLab = new Label("Beer name: ");
            Label nameBeer=new Label(b.getName());
            Label styleLab = new Label("Style: ");
            Label style = new Label(b.getStyle());
            Label brewNameLab = new Label("Brewery name: ");
            Label brewName = new Label(b.getBrewery_name());
            box.setPrefWidth(200);
            box.getChildren().addAll(nameLab,nameBeer,styleLab, style,brewNameLab, brewName);
            oneBeer.getChildren().add(box);
            this.getProfilePage().getChildren().add(oneBeer);
        }
    }

    public VBox getProfilePage(){
        return profilePage;
    }


}
