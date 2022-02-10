package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.bean.User;
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

    @FXML private void addBeer(ActionEvent actionEvent){

        //ArrayList<User> listBeers=MongoDriver.getBeerOfTheMonth();
        //System.out.println(Arrays.toString(listBeers.toArray()));

    }
}
