package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private Button loginButton;
    @FXML private Label usernameLabel;

    @Override @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (DataSession.getUserLogged() != null){
            //se sono loggato
            String usernameLogged = DataSession.getUserLogged();
            usernameLabel.setText(usernameLogged);
            loginButton.setText("Logout");
            loginButton.setOnAction(this::logoutUser);
        }
        else {
            //se non sono loggato
            usernameLabel.setText("");
            loginButton.setOnAction(this::loginUser);
        }
    }

    @FXML
    private void logoutUser(ActionEvent actionEvent){
        //cancello i dati di sessione e faccio il resfresh della scena
        DataSession.logoutSession();
        Utils.changeScene("menu-page.fxml", actionEvent);
    }

    @FXML
    private void loginUser(ActionEvent actionEvent){
        //vado soltanto alla schermata di login
        Utils.changeScene("login_page.fxml", actionEvent);
    }

    @FXML private void onClickBeers(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("beers-page.fxml", ae);
    }

}



