package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.config.DataSession;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CartController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String username = DataSession.getUserLogged();

    }
}
