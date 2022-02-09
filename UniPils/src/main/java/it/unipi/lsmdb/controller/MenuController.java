package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MenuController {

    @FXML private Button beerButton;
    @FXML private Button friendsButton;
    @FXML private Button loginButton;
    @FXML private Button cartButton;
    @FXML private Button wishButton;
    @FXML private Label username;

    @FXML
    private void onClickLogin(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("login_page.fxml", ae);
    }

    @FXML private void onClickBeers(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("beers-page.fxml", ae);
    }


}

