package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.Beer;
import it.unipi.lsmdb.bean.Review;
import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.bson.Document;
import it.unipi.lsmdb.persistence.MongoDriver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeersController {

    @FXML private Button beerButton;
    @FXML private Button addBeer;

    @FXML private void onClickViewBeers(ActionEvent actionEvent){
        /*NeoDriver neo4j = NeoDriver.getInstance();
        String username ="1160birgerKLEINER";
        String u1 = username;
        String u2 = "3967leviWEBB";
        String u3 = "favalessa";
        int beerid= 214879;
        User user = new User("male","filippo","puccini","kjbvhb@djbu.com","poppo", "vkjdbycf", null, "hjb");
        Review review = new Review("gggg",8);
        Beer beer = new Beer(0000, "moretti","","ita", "chiara", 10,4,50, 2, 777777, "birrificio", "santacruz","",0);
        neo4j.findBeer("American IPA");
        System.out.println(neo4j.SuggestedUsers(u1));*/
        //System.out.println(Arrays.toString(listBeers.toArray()));

    }

    @FXML private void addBeer(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("registration-beer.fxml", ae);

    }

    @FXML private void onClickTest(ActionEvent actionEvent){
        MongoDriver.getUserFromUsername("matteo");
    }
}
