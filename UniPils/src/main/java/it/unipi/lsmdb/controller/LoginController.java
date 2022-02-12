package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField user;
    @FXML private TextField pass;

    @FXML
    private void onClickLogin(ActionEvent actionEvent) {
        String uName = user.getText();
        String pwd = pass.getText();

            if (uName.equals("") || pwd.equals("")) {
                Utils.showErrorAlert("You have to fill both fields");
                return;
            }

            NeoDriver neo4j = NeoDriver.getInstance();

            if (!neo4j.getUser(uName, pwd)) {
                Utils.showErrorAlert("Username and/or password not valid");
                return;
            }

            DataSession.setUserLogged(uName);
            Utils.changeScene("homepage.fxml", actionEvent);

    }

    @FXML
    private void onClickRegistration(ActionEvent actionEvent){
        ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
        Utils.changeScene("registration-page.fxml", ae);
    }


}
