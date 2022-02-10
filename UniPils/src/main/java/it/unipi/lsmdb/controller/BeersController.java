package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.User;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.bson.Document;
import it.unipi.lsmdb.persistence.MongoDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeersController {

    @FXML private Button beerButton;
    @FXML private Button addBeer;

    @FXML private void onClickViewBeers(ActionEvent actionEvent){
        ArrayList<User> listBeers=MongoDriver.getUser("matteo");
        //System.out.println(Arrays.toString(listBeers.toArray()));

    }

    @FXML private void addBeer(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("registration-beer.fxml", ae);

    }
}
