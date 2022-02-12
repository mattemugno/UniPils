package it.unipi.lsmdb.controller;

import it.unipi.lsmdb.persistence.NeoDriver;
import it.unipi.lsmdb.utils.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Parent embeddedMenu;
    @FXML private MenuController menuController;
    @FXML private TextField searchBar;
    @FXML private AnchorPane searchBarContainer;

    @FXML @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setLayoutX(37.0);
        choiceBox.setLayoutY(149.0);
        choiceBox.setItems(FXCollections.observableArrayList(
                "Beer Name", "Username", "Brewery Name", "Beer Style", "Beer Id"));
        choiceBox.setValue("Beer_name");

        TextField textField = new TextField();
        textField.setPromptText("Search here!");
        textField.setOnKeyPressed(event->search(event, (String) choiceBox.getValue(), textField.getText()));

        HBox hBox = new HBox(choiceBox, textField);//Add choiceBox and textField to hBox
        hBox.setAlignment(Pos.CENTER);//Center HBox
        hBox.setLayoutX(37.0);
        hBox.setLayoutY(149.0);
        searchBarContainer.getChildren().addAll(hBox);

    }

    private void search(KeyEvent e, String value, String search) {
        if(e.getCode() == KeyCode.ENTER)
            Utils.changeScene("/it/unipi/lsmdb/search-result.fxml", e, search, value);
    }

}
