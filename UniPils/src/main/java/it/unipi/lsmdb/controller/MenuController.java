package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.config.DataSession;
import it.unipi.lsmdb.persistence.MongoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private Button loginButton;
    @FXML private Hyperlink usernameLabel;


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
            usernameLabel.setText("Welcome");
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

    @FXML private void onClickProfile(ActionEvent actionEvent){
        if (DataSession.getUserLogged() != null) {
            ActionEvent ae = new ActionEvent(actionEvent.getSource(), actionEvent.getTarget());
            DataSession.setUserView(null);
            Utils.changeScene("profile-user.fxml", ae);
        }
    }

    @FXML private void onClickHomePage(ActionEvent actionEvent){
        Utils.changeScene("homepage.fxml", actionEvent);
    }

    @FXML
    private void showWishlist(ActionEvent actionEvent){
        if (DataSession.getUserLogged() != null)
            Utils.changeScene("wishlist-page.fxml", actionEvent);
        else
            Utils.showErrorAlert("You need to login to access this area");
    }


    @FXML
    private void OnlickDebug(){
        MongoDriver.getGenderDistribution();
    }

    @FXML
    private void showCart(ActionEvent ae){
        Utils.changeScene("cart-page.fxml", ae);
    }
}




