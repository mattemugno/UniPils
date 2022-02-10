package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private Button beerButton;
    @FXML private Button friendsButton;
    @FXML private Button loginButton;
    @FXML private Button cartButton;
    @FXML private Button wishButton;
    @FXML private Label usernameLabel;

    /*if (DataSession.getUserLogged() == null)
    String usernameLogged = DataSession.getUserLogged().getUsername();
                    usernameLabel.setText(usernameLogged);
                else
                        usernameLabel.setText("");*/

    @Override @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            }
        });*/
        if (DataSession.getUserLogged() != null){
            String usernameLogged = DataSession.getUserLogged();
            usernameLabel.setText(usernameLogged);
            loginButton.setText("Logout");
            loginButton.setOnAction(new EventHandler<ActionEvent>());
        }
        else {
            usernameLabel.setText("");
            loginButton.setText("Login");
        }
    }

    /*@FXML
    private void doLogin() {
        usernameLabel.setText("");
    }

    @FXML
    private void doLogout() {
        String usernameLogged = DataSession.getUserLogged().getUsername();
        usernameLabel.setText(usernameLogged);
    }*/

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



