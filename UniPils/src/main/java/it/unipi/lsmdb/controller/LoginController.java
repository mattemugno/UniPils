package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private Label username;
    @FXML private Label password;
    @FXML private TextField usernameText;
    @FXML private TextField passwordText;
    @FXML private Button loginButton;
    @FXML private Button registrationButton;

    @FXML
    private void onClickLogin(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("login_page.fxml", ae);
    }

    @FXML
    private void onClickRegistration(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("registration-page.fxml", ae);
    }


}
